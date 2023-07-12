package com.github.lzyzsd.jsbridge

import android.webkit.JavascriptInterface
import android.util.Log
import com.github.lzyzsd.jsbridge.core.BridgeCore

class AndroidJavascriptInterface(
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