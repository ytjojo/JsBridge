package com.xiaomao.jsbridge.core

import android.graphics.Bitmap
import android.os.Looper
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebView
import com.xiaomao.jsbridge.BridgeWebView

class BridgeInjector(val webView: BridgeWebView) {

    var mProgress = 0
    var isJSLoaded = false

    val mOnProgressChangedListeners = ArrayList<OnProgressChangedListener>()


    fun onProgressChanged( view:WebView, newProgress:Int ) {
        // 进度拉快. 80很容易, 100却很难
        val progress = Math.min(100, newProgress * 5 / 4)
        if (mProgress != progress) {
            mProgress = progress

            mOnProgressChangedListeners.forEach {
                it.onProgressChanged(view,mProgress)
            }

            if ( newProgress >= 75) {
                injectJsBridge(webView)
            }
            if (BridgeCore.isDebug) {
                Log.d("WeiMaiWebViewHelper: ", "onProgressChanged: $mProgress")
            }
        }


    }

    fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {

        isJSLoaded = false
    }


    fun onPageFinished(view: WebView?, url: String?) {
        if(isJSLoaded){

        }
    }


    private fun injectJsBridge(webView: WebView){
        val activity = BridgeWebView.context2Activity(webView.context)
        webView.evaluateJavascript(BridgeCore.getBridgeJavascript(),object : ValueCallback<String> {
            override fun onReceiveValue(value: String?) {
                if(value == "true"){

                    if(Looper.myLooper() == Looper.getMainLooper()){
                        onWebViewJavascriptBridgeReady()
                    }else{
                        activity.runOnUiThread{
                            onWebViewJavascriptBridgeReady()
                        }
                    }

                }else{

                }
            }
        })

    }

    fun onWebViewJavascriptBridgeReady(){
        isJSLoaded = true
    }

}

interface OnProgressChangedListener{
    fun onProgressChanged( view:WebView, newProgress:Int )
}