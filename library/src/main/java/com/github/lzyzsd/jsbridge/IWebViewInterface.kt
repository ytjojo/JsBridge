package com.github.lzyzsd.jsbridge

import android.app.Activity
import android.graphics.Bitmap
import android.webkit.WebView

interface IWebViewInterface {
    // 对外开放的 注测交互事件
    fun registerHandler(handlerName: String, handler: BridgeHandler)

    // 对外开放的 执行js侧代码
    fun callHandler(handlerName: String, data: String?, callBack: OnBridgeCallback?)
    fun unregisterHandler(handlerName: String)

    fun onProgressChanged(view: WebView, newProgress:Int )

    fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?)

    fun onPageFinished(view: WebView?, url: String?)

    fun onReceivedTitle(view: WebView, title: String)

    fun activity():Activity?
}