package com.xiaomao.jsbridge;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import com.google.gson.Gson;
import com.xiaomao.jsbridge.core.BridgeCore;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressLint("SetJavaScriptEnabled")
public class BridgeWebView extends WebView {

    private final int URL_MAX_CHARACTER_NUM = 2097152;
    private Map<String, OnBridgeCallback> mCallbacks = new ArrayMap<>();

    private Map<String, BridgeHandler> messageHandlers = new HashMap<>();

    private List<Object> mStartupRequests = new ArrayList<>();

    private BridgeWebViewClient mClient;

    private long mUniqueId = 0;

    private boolean mJSLoaded = false;

    private Gson mGson;

    private Activity mActivity;

    public BridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BridgeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BridgeWebView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mActivity = context2Activity(getContext());
        clearCache(true);
        getSettings().setUseWideViewPort(true);
//		webView.getSettings().setLoadWithOverviewMode(true);
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        getSettings().setJavaScriptEnabled(true);
//        mContent.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && BridgeCore.INSTANCE.isDebug()) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        mClient = new BridgeWebViewClient();
        super.setWebViewClient(mClient);
    }

    public void setGson(Gson gson) {
        mGson = gson;
    }

    public boolean isJSLoaded() {
        return mJSLoaded;
    }

    public Map<String, OnBridgeCallback> getCallbacks() {
        return mCallbacks;
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        mClient.setWebViewClient(client);
    }


    public void onLoadStart() {
        mJSLoaded = false;
    }

    public void onLoadFinished() {
        mJSLoaded = true;
        if (mStartupRequests != null) {
            for (Object message : mStartupRequests) {
                dispatchMessage(message);
            }
            mStartupRequests = null;
        }
    }

    @Override
    public void sendToWeb(String data) {
        sendToWeb(data, (OnBridgeCallback) null);
    }

    @Override
    public void sendToWeb(String data, OnBridgeCallback responseCallback) {
        doSend(null, data, responseCallback);
    }

    /**
     * call javascript registered handler
     * 调用javascript处理程序注册
     *
     * @param handlerName handlerName
     * @param data        data
     * @param callBack    OnBridgeCallback
     */
    public void callHandler(String handlerName, String data, OnBridgeCallback callBack) {
        doSend(handlerName, data, callBack);
    }


    @Override
    public void sendToWeb(String function, Object... values) {
        // 必须要找主线程才会将数据传递出去 --- 划重点

        String messageJson = String.format(function, values);
        String javascriptCommand = String.format(BridgeCore.SCRIPT_DISPATCH_MESSAGE, messageJson);
        // 必须要找主线程才会将数据传递出去 --- 划重点
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            this.evaluateJavascript(javascriptCommand, null);
        } else {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    evaluateJavascript(javascriptCommand, null);
                }
            });
        }
    }

    @Override
    public void onResponseFromWeb(String data) {

        try {
            JSResponse jsResponse = mGson.fromJson(data, JSResponse.class)
            String responseId = jsResponse.responseId;
            if (!TextUtils.isEmpty(responseId)) {
                // js response
                OnBridgeCallback onBridgeCallback = mCallbacks.remove(responseId);
                if (onBridgeCallback != null) {
                    onBridgeCallback.onCallBack(jsResponse.responseData);

                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void callFromWeb(String data) {
        try {
            JSRequest jsRequest = mGson.fromJson(data, JSRequest.class);
            if (TextUtils.isEmpty(jsRequest.handlerName)) {
                return;
            }
            BridgeHandler bridgeHandler = messageHandlers.remove(jsRequest.handlerName);
            if (bridgeHandler != null) {
                final String callbackId = jsRequest.callbackId;
                bridgeHandler.handler(jsRequest.data, new OnBridgeCallback() {
                    @Override
                    public void onCallBack(String data) {

                        if (!TextUtils.isEmpty(callbackId)) {
                            JSRequest jsRequest = new JSRequest();
                            jsRequest.callbackId = callbackId;
                            jsRequest.data = data;
                            dispatchMessage(jsRequest);
                            sendResponse(data, callbackId);

                        }

                    }
                });

            }

        } catch (Exception e) {

        }

    }

    /**
     * 保存message到消息队列
     *
     * @param handlerName      handlerName
     * @param data             data
     * @param responseCallback OnBridgeCallback
     */
    private void doSend(String handlerName, Object data, OnBridgeCallback responseCallback) {
        if (!(data instanceof String) && mGson == null) {
            return;
        }
        JSRequest request = new JSRequest();
        if (data != null) {
            request.data = data instanceof String ? (String) data : mGson.toJson(data);
        }
        if (responseCallback != null) {
            String callbackId = String.format(BridgeUtil.CALLBACK_ID_FORMAT, (++mUniqueId) + (BridgeUtil.UNDERLINE_STR + SystemClock.elapsedRealtime()));
            mCallbacks.put(callbackId, responseCallback);
            request.callbackId = callbackId;
        }
        if (!TextUtils.isEmpty(handlerName)) {
            request.handlerName = handlerName;
        }
        queueMessage(request);
    }

    /**
     * list<message> != null 添加到消息集合否则分发消息
     *
     * @param message Message
     */
    private void queueMessage(Object message) {
        if (mStartupRequests != null) {
            mStartupRequests.add(message);
        } else {
            dispatchMessage(message);
        }
    }

    /**
     * 分发message 必须在主线程才分发成功
     *
     * @param message Message
     */
    private void dispatchMessage(Object message) {
        if (mGson == null) {
            return;
        }
        String messageJson = mGson.toJson(message);
        //escape special characters for json string  为json字符串转义特殊字符

        // 系统原生 API 做 Json转义，没必要自己正则替换，而且替换不一定完整
        messageJson = JSONObject.quote(messageJson);
        String javascriptCommand = String.format(BridgeCore.SCRIPT_DISPATCH_MESSAGE, messageJson);
        // 必须要找主线程才会将数据传递出去 --- 划重点
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            this.evaluateJavascript(javascriptCommand, null);
        } else {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    evaluateJavascript(javascriptCommand, null);
                }
            });
        }
    }

    public void sendResponse(Object data, String callbackId) {
        if (!(data instanceof String) && mGson == null) {
            return;
        }
        if (!TextUtils.isEmpty(callbackId)) {
            final JSResponse response = new JSResponse();
            response.responseId = callbackId;
            response.responseData = data instanceof String ? (String) data : mGson.toJson(data);
            if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                dispatchMessage(response);
            } else {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dispatchMessage(response);
                    }
                });
            }

        }
    }

    @Override
    public void destroy() {
        super.destroy();
        mCallbacks.clear();
    }


    public static Activity context2Activity(Context context) {
        while (context != null) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            Activity activity = getActivityFromDecorContext(context);
            if (activity != null) return activity;
            if (context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
            } else {
                break;
            }
        }
        return null;
    }

    @Nullable
    private static Activity getActivityFromDecorContext(@Nullable Context context) {
        if (context == null) return null;
        if (context.getClass().getName().equals("com.android.internal.policy.DecorContext")) {
            try {
                Field mActivityContextField = context.getClass().getDeclaredField("mActivityContext");
                mActivityContextField.setAccessible(true);
                //noinspection ConstantConditions,unchecked
                return ((WeakReference<Activity>) mActivityContextField.get(context)).get();
            } catch (Exception ignore) {
            }
        }
        return null;
    }


}
