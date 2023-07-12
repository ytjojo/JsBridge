package com.github.lzyzsd.jsbridge.core

import android.graphics.Bitmap
import android.os.Looper
import android.os.SystemClock
import android.text.TextUtils
import android.webkit.ValueCallback
import android.webkit.WebView
import com.github.lzyzsd.jsbridge.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.json.JSONObject

class MessageDispatcher(val webView: WebView) : WebViewJavascriptBridge, OnPageLoadListener {

    private val mCallbacks: HashMap<String, OnBridgeCallback?> = HashMap()

    private val messageHandlers: HashMap<String, BridgeHandler?> = HashMap()

    private val mStartupRequests: ArrayList<Any> = ArrayList()

    private var mUniqueId: Long = 0L
    private var isDestoryed = false

    private var isJSLoaded = false
    var defaultHandler: BridgeHandler = object : BridgeHandler {
        override fun handler(data: String?, function: OnBridgeCallback?) {
        }

    }


    val mActivity by lazy {
        BridgeWebView.context2Activity(webView.context)
    }
    var mGson = GsonBuilder().create()
    override fun onWebViewJavascriptBridgeReady() {
        if (mStartupRequests.isNotEmpty()) {
            for (message in mStartupRequests) {
                dispatchMessage(message)
            }
            mStartupRequests.clear()
        }

        isJSLoaded = true
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
    fun callHandler(handlerName: String?, data: Any?, callBack: OnBridgeCallback?) {
        doSend(handlerName, data, callBack)
    }


    override fun sendWithFunctionToWeb(function: String?,callback: ValueCallback<String>?, vararg values: Any?) {
        // 必须要找主线程才会将数据传递出去 --- 划重点

        var jsCommand = String.format(function!!, *values)

        if (isDestory()) {
            return
        }
        // 必须要找主线程才会将数据传递出去 --- 划重点
        if (Thread.currentThread() === Looper.getMainLooper().thread) {

            webView.evaluateJavascript(jsCommand,null)
        } else {
            BridgeCore.runOnUiThread(Runnable {
                if (isDestory()) {
                    return@Runnable
                }
                webView.evaluateJavascript(jsCommand,null)
            })
        }
    }

    override fun onResponseFromWeb(data: String?) {
        if (isDestory()) {
            return
        }
        try {
//            val jsResponse: JSResponse = mGson.fromJson<JSResponse>(data, JSResponse::class.java)
            val jsElement = mGson.fromJson<JsonElement>(data, JsonElement::class.java)
            var responseData: String? = null
            var responseId: String? = null

            if (jsElement is JsonObject) {
                responseId = jsElement.get("responseId")?.asString

                val dataElement = jsElement.get("responseData")
                if (dataElement != null) {
                    if (dataElement.isJsonPrimitive) {
                        responseData = dataElement.asString
                    } else {
                        responseData = mGson.toJson(dataElement)
                    }
                }
            }
            if (!TextUtils.isEmpty(responseId)) {
                // js response
                val onBridgeCallback: OnBridgeCallback? = mCallbacks.remove(responseId)
                onBridgeCallback?.onCallBack(responseData)
            }
        } catch (e: Exception) {
        }
    }

    override fun callFromWeb(data: String?) {
        if (isDestory()) {
            return
        }
        try {
//            val jsRequest: JSRequest = mGson.fromJson<JSRequest>(data, JSRequest::class.java)
            val jsElement = mGson.fromJson<JsonElement>(data, JsonElement::class.java)
            var handlerName: String? = ""
            var data: String? = null
            var callbackId: String? = null

            if (jsElement is JsonObject) {
                handlerName = jsElement.get("handlerName")?.asString
                callbackId = jsElement.get("callbackId")?.asString

                val dataElement = jsElement.get("data")
                if (dataElement != null) {
                    if (dataElement.isJsonPrimitive) {
                        data = dataElement.asString
                    } else {
                        data = mGson.toJson(dataElement)
                    }
                }
            }

            var bridgeHandler: BridgeHandler? = defaultHandler
            if (!TextUtils.isEmpty(handlerName)) {
                bridgeHandler = messageHandlers.get(handlerName)
            }
            if (bridgeHandler == null) {
                bridgeHandler = defaultHandler
            }

            if (bridgeHandler != null) {
                val callbackId = callbackId
                bridgeHandler.handler(data) { dataFromNative ->
                    if (!TextUtils.isEmpty(callbackId)) {
                        sendResponseToWeb(dataFromNative, callbackId)
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    override fun isDestory(): Boolean {

        return isDestoryed || mActivity?.isFinishing == true || mActivity?.isDestroyed == true
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
        if (!isJSLoaded) {
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
        if (isDestory()) {
            return
        }
        var messageJson: String? = if (message is String) message else mGson.toJson(message)
        //escape special characters for json string  为json字符串转义特殊字符

        // 系统原生 API 做 Json转义，没必要自己正则替换，而且替换不一定完整
        messageJson = JSONObject.quote(messageJson)
        val javascriptCommand = String.format(BridgeCore.SCRIPT_DISPATCH_MESSAGE, messageJson)
        // 必须要找主线程才会将数据传递出去 --- 划重点
        if (Looper.myLooper() === Looper.getMainLooper()) {
            webView.evaluateJavascript(javascriptCommand, null)
        } else {
            BridgeCore.runOnUiThread(Runnable {
                if (isDestory()) {
                    return@Runnable
                }
                webView.evaluateJavascript(
                    javascriptCommand,
                    null
                )
            })
        }
    }

    fun sendResponseToWeb(data: Any?, callbackId: String?) {
        if (data !is String && mGson == null) {
            return
        }
        if (!TextUtils.isEmpty(callbackId) && !isDestory()) {
            val response = JSResponse()
            response.responseId = callbackId
            response.responseData = if (data is String) data else mGson.toJson(data)
            if (Thread.currentThread() === Looper.getMainLooper().thread) {
                dispatchMessage(response)
            } else {
                BridgeCore.runOnUiThread(Runnable {
                    if (isDestory()) {
                        return@Runnable
                    }
                    dispatchMessage(response)
                })
            }
        }
    }

    fun onDestory() {
        isDestoryed = true
        mCallbacks.clear()
        messageHandlers.clear()
    }

    fun registerHandler(handlerName: String?, handler: BridgeHandler?) {
        if (handler != null && !handlerName.isNullOrEmpty()) {
            // 添加至 Map<String, BridgeHandler>
            messageHandlers[handlerName!!] = handler
        }
    }

    fun unregisterHandler(handlerName: String?) {
        if (!handlerName.isNullOrEmpty()) {
            messageHandlers.remove(handlerName)
        }
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        isJSLoaded = false

    }

    override fun onPageFinished(view: WebView?, url: String?) {
    }

}