package com.github.lzyzsd.jsbridge.core

import android.graphics.Bitmap
import android.os.Looper
import android.util.Log
import android.webkit.WebView
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.OnPageLoadListener
import com.github.lzyzsd.jsbridge.OnProgressChangedListener

class BridgeInjector(val bridgeWebview: BridgeWebView): OnPageLoadListener,
    OnProgressChangedListener {

    var mProgress = 0
    var isJSLoaded = false



    override fun onProgressChanged(view: WebView, newProgress: Int) {
        // 进度拉快. 80很容易, 100却很难
        val progress = Math.min(100, newProgress * 5 / 4)
        if (mProgress != progress) {
            mProgress = progress

            bridgeWebview.getFixProgressListeners().forEach {
                it.onProgressChanged(view, mProgress)
            }

            if (newProgress >= 75) {
                injectJsBridge(bridgeWebview)
            }
            if (BridgeCore.isDebug) {
                Log.d("WeiMaiWebViewHelper: ", "onProgressChanged: $mProgress")
            }
        }


    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        isJSLoaded = false
    }


    override fun onPageFinished(view: WebView?, url: String?) {
        bridgeWebview.messageDispatcher.onWebViewJavascriptBridgeReady()
    }


    private fun injectJsBridge(webView: WebView) {
        webView.evaluateJavascript(
            BridgeCore.getBridgeJavascript(webView.context)
        ) { value ->
            if (value == "true") {

                if (Looper.myLooper() == Looper.getMainLooper()) {
                    onWebViewJavascriptBridgeReady()
                } else {
                    BridgeCore.runOnUiThread {
                        onWebViewJavascriptBridgeReady()
                    }
                }

            } else {

            }
        }

    }

    fun onWebViewJavascriptBridgeReady() {
        isJSLoaded = true
    }

}

