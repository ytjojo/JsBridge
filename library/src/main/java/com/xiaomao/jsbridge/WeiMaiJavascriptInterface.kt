package com.xiaomao.jsbridge

import com.xiaomao.jsbridge.IWebViewInterface
import android.webkit.JavascriptInterface
import android.app.Activity
import android.util.Log
import com.xiaomao.jsbridge.core.BridgeCore

class WeiMaiJavascriptInterface(
    private val webViewJavascriptBridge: WebViewJavascriptBridge,
) {
    /**
     * 新容器交互通道
     */
    @JavascriptInterface
    fun callFromWeb(data: String) {
        if (BridgeCore.isDebug) {
            Log.w("WebView1", "postMessage: $data")
        }
        if(webViewJavascriptBridge.isDestory){
            return
        }
        BridgeCore.runOnUiThread { webViewJavascriptBridge.callFromWeb(data) }
    }

    @JavascriptInterface
    fun onResponseFromWeb(data: String) {
        if (BridgeCore.isDebug) {
            Log.w("WebView1", "postMessage: $data")
        }

        if(webViewJavascriptBridge.isDestory){
            return
        }
        BridgeCore.runOnUiThread { webViewJavascriptBridge.onResponseFromWeb(data) }
    }
}