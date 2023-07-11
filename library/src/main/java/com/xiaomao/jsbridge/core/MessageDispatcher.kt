package com.xiaomao.jsbridge.core

import android.os.Looper
import android.os.SystemClock
import android.text.TextUtils
import android.webkit.WebView
import com.google.gson.GsonBuilder
import com.xiaomao.jsbridge.*
import org.json.JSONObject

class MessageDispatcher(val webView:WebView) :WebViewJavascriptBridge{

    private val mCallbacks: HashMap<String, OnBridgeCallback?> = HashMap()

    private val messageHandlers: HashMap<String, BridgeHandler?> = HashMap()

    private val mStartupRequests: ArrayList<Any> = ArrayList()

    private var mUniqueId: Long = 0L
    private var isDestoryed = false

    val mActivity by lazy {
        BridgeWebView.context2Activity(webView.context)
    }
    var mGson = GsonBuilder().create()
    fun onWebViewJavascriptBridgeReady() {
        if (mStartupRequests.isNotEmpty()) {
            for (message in mStartupRequests) {
                dispatchMessage(message)
            }
            mStartupRequests.clear()
        }
    }

    override fun sendToWeb(data: String?) {
        sendToWeb(data, null as OnBridgeCallback?)
    }

    override fun sendToWeb(data: String?, responseCallback: OnBridgeCallback?) {
        doSend(null, data, responseCallback)
    }

    /**
     * call javascript registered handler
     * 调用javascript处理程序注册
     *
     * @param handlerName handlerName
     * @param data        data
     * @param callBack    OnBridgeCallback
     */
    fun callHandler(handlerName: String?, data: String?, callBack: OnBridgeCallback?) {
        doSend(handlerName, data, callBack)
    }


    override fun sendToWeb(function: String?, vararg values: Any?) {
        // 必须要找主线程才会将数据传递出去 --- 划重点
        val messageJson = String.format(function!!, *values)
        val javascriptCommand = String.format(BridgeCore.SCRIPT_DISPATCH_MESSAGE, messageJson)
        // 必须要找主线程才会将数据传递出去 --- 划重点
        if (Thread.currentThread() === Looper.getMainLooper().thread) {
            webView.evaluateJavascript(javascriptCommand, null)
        } else {
            mActivity.runOnUiThread(Runnable { webView.evaluateJavascript(javascriptCommand, null) })
        }
    }

    override fun onResponseFromWeb(data: String?) {
        try {
            val jsResponse: JSResponse = mGson.fromJson<JSResponse>(data, JSResponse::class.java)
            val responseId = jsResponse.responseId
            if (!TextUtils.isEmpty(responseId)) {
                // js response
                val onBridgeCallback: OnBridgeCallback? = mCallbacks.remove(responseId)
                if (onBridgeCallback != null) {
                    onBridgeCallback.onCallBack(jsResponse.responseData)
                }
            }
        } catch (e: Exception) {
        }
    }

    override fun callFromWeb(data: String?) {
        try {
            val jsRequest: JSRequest = mGson.fromJson<JSRequest>(data, JSRequest::class.java)
            if (TextUtils.isEmpty(jsRequest.handlerName)) {
                return
            }
            val bridgeHandler: BridgeHandler? = messageHandlers.remove(jsRequest.handlerName)
            if (bridgeHandler != null) {
                val callbackId = jsRequest.callbackId
                bridgeHandler.handler(jsRequest.data) { data ->
                    if (!TextUtils.isEmpty(callbackId)) {
                        val jsRequest = JSRequest()
                        jsRequest.callbackId = callbackId
                        jsRequest.data = data
                        dispatchMessage(jsRequest)
                        sendResponse(data, callbackId)
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    override fun isDestory(): Boolean {

       return isDestoryed || mActivity.isFinishing || mActivity.isDestroyed
    }

    /**
     * 保存message到消息队列
     *
     * @param handlerName      handlerName
     * @param data             data
     * @param responseCallback OnBridgeCallback
     */
    private fun doSend(handlerName: String?, data: Any?, responseCallback: OnBridgeCallback?) {
        if (data !is String && mGson == null) {
            return
        }
        val request = JSRequest()
        if (data != null) {
            request.data = if (data is String) data else mGson.toJson(data)
        }
        if (responseCallback != null) {
            val callbackId = String.format(
                BridgeCore.CALLBACK_ID_FORMAT,
                (++mUniqueId).toString() + (BridgeCore.UNDERLINE_STR + SystemClock.elapsedRealtime())
            )
            mCallbacks.put(callbackId, responseCallback)
            request.callbackId = callbackId
        }
        if (!TextUtils.isEmpty(handlerName)) {
            request.handlerName = handlerName
        }
        queueMessage(request)
    }

    /**
     * list<message> != null 添加到消息集合否则分发消息
     *
     * @param message Message
    </message> */
    private fun queueMessage(message: Any) {
        if (mStartupRequests != null) {
            mStartupRequests.add(message)
        } else {
            dispatchMessage(message)
        }
    }

    /**
     * 分发message 必须在主线程才分发成功
     *
     * @param message Message
     */
    private fun dispatchMessage(message: Any) {
        if (mGson == null) {
            return
        }
        var messageJson: String? = mGson.toJson(message)
        //escape special characters for json string  为json字符串转义特殊字符

        // 系统原生 API 做 Json转义，没必要自己正则替换，而且替换不一定完整
        messageJson = JSONObject.quote(messageJson)
        val javascriptCommand = String.format(BridgeCore.SCRIPT_DISPATCH_MESSAGE, messageJson)
        // 必须要找主线程才会将数据传递出去 --- 划重点
        if (Thread.currentThread() === Looper.getMainLooper().thread) {
            webView.evaluateJavascript(javascriptCommand, null)
        } else {
            mActivity.runOnUiThread(Runnable { webView.evaluateJavascript(javascriptCommand, null) })
        }
    }

    fun sendResponse(data: Any?, callbackId: String?) {
        if (data !is String && mGson == null) {
            return
        }
        if (!TextUtils.isEmpty(callbackId)) {
            val response = JSResponse()
            response.responseId = callbackId
            response.responseData = if (data is String) data else mGson.toJson(data)
            if (Thread.currentThread() === Looper.getMainLooper().thread) {
                dispatchMessage(response)
            } else {
                mActivity.runOnUiThread(Runnable { dispatchMessage(response) })
            }
        }
    }

    fun onDestory(){
        isDestoryed = true
        mCallbacks.clear()
        messageHandlers.clear()
    }

}