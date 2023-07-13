package com.github.lzyzsd.jsbridge

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Message
import android.view.View
import android.webkit.*
import android.webkit.WebStorage.QuotaUpdater
import androidx.annotation.RequiresApi

open class BridgeWebChromeClient(val bridgeWebView: BridgeWebView) : WebChromeClient() {
    private var webChromeClient: WebChromeClient? = null
    fun setWebChromeClient(webChromeClient: WebChromeClient?) {
        if (webChromeClient != null) {
            this.webChromeClient = webChromeClient
        }
    }

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        if (webChromeClient != null) {
            webChromeClient!!.onProgressChanged(view, newProgress)
        }
        bridgeWebView.onProgressChanged(view, newProgress)

    }

    override fun onReceivedTitle(view: WebView, title: String) {
        if (webChromeClient != null) {
            webChromeClient!!.onReceivedTitle(view, title)
        }

        bridgeWebView.onReceivedTitle(view, title)
    }

    override fun onReceivedIcon(view: WebView, icon: Bitmap) {
        if (webChromeClient != null) {
            webChromeClient!!.onReceivedIcon(view, icon)
        }
    }

    override fun onReceivedTouchIconUrl(view: WebView, url: String, precomposed: Boolean) {
        if (webChromeClient != null) {
            webChromeClient!!.onReceivedTouchIconUrl(
                view,
                url,
                precomposed
            )
        }
    }

    override fun onShowCustomView(view: View, callback: CustomViewCallback) {
        if (webChromeClient != null) {
            webChromeClient!!.onShowCustomView(view, callback)
        }
    }

    override fun onShowCustomView(
        view: View,
        requestedOrientation: Int,
        callback: CustomViewCallback
    ) {
        if (webChromeClient != null) {
            webChromeClient!!.onShowCustomView(
                view,
                requestedOrientation,
                callback
            )
        }
    }

    override fun onHideCustomView() {
        if (webChromeClient != null) webChromeClient!!.onHideCustomView()
    }

    override fun onCreateWindow(
        view: WebView,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message
    ): Boolean {
        return if (webChromeClient != null) {
            webChromeClient!!.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
        } else {
            super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
        }
    }

    override fun onRequestFocus(view: WebView) {
        if (webChromeClient != null) {
            webChromeClient!!.onRequestFocus(view)
        }
    }

    override fun onCloseWindow(window: WebView) {
        if (webChromeClient != null) {
            webChromeClient!!.onCloseWindow(window)
        }
    }

    override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
        return if (webChromeClient != null) {
            webChromeClient!!.onJsAlert(view, url, message, result)
        } else {
            super.onJsAlert(view, url, message, result)
        }
    }

    override fun onJsConfirm(
        view: WebView,
        url: String,
        message: String,
        result: JsResult
    ): Boolean {
        return if (webChromeClient != null) {
            webChromeClient!!.onJsConfirm(view, url, message, result)
        } else {
            super.onJsConfirm(view, url, message, result)
        }
    }

    override fun onJsBeforeUnload(
        view: WebView,
        url: String,
        message: String,
        result: JsResult
    ): Boolean {
        return if (webChromeClient != null) {
            webChromeClient!!.onJsBeforeUnload(view, url, message, result)
        } else {
            super.onJsBeforeUnload(view, url, message, result)
        }
    }

    override fun onExceededDatabaseQuota(
        url: String,
        databaseIdentifier: String,
        quota: Long,
        estimatedDatabaseSize: Long,
        totalQuota: Long,
        quotaUpdater: QuotaUpdater
    ) {
        if (webChromeClient != null) {
            webChromeClient!!.onExceededDatabaseQuota(
                url,
                databaseIdentifier,
                quota,
                estimatedDatabaseSize,
                totalQuota,
                quotaUpdater
            )
        }
    }

    override fun onReachedMaxAppCacheSize(
        requiredStorage: Long,
        quota: Long,
        quotaUpdater: QuotaUpdater
    ) {
        if (webChromeClient != null) {
            webChromeClient!!.onReachedMaxAppCacheSize(
                requiredStorage,
                quota,
                quotaUpdater
            )
        }
    }

    override fun onGeolocationPermissionsShowPrompt(
        origin: String,
        callback: GeolocationPermissions.Callback
    ) {
        if (webChromeClient != null) {
            webChromeClient!!.onGeolocationPermissionsShowPrompt(
                origin,
                callback
            )
        }
    }

    override fun onGeolocationPermissionsHidePrompt() {
        if (webChromeClient != null) {
            webChromeClient!!.onGeolocationPermissionsHidePrompt()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onPermissionRequest(request: PermissionRequest) {
        if (webChromeClient != null) {
            webChromeClient!!.onPermissionRequest(request)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onPermissionRequestCanceled(request: PermissionRequest) {
        if (webChromeClient != null) {
            webChromeClient!!.onPermissionRequestCanceled(request)
        }
    }

    override fun onJsTimeout(): Boolean {
        return if (webChromeClient != null) {
            webChromeClient!!.onJsTimeout()
        } else {
            super.onJsTimeout()
        }
    }

    override fun onConsoleMessage(message: String, lineNumber: Int, sourceID: String) {
        if (webChromeClient != null) {
            webChromeClient!!.onConsoleMessage(
                message,
                lineNumber,
                sourceID
            )
        }
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
        return if (webChromeClient != null) {
            webChromeClient!!.onConsoleMessage(consoleMessage)
        } else super.onConsoleMessage(consoleMessage)
    }

    override fun getDefaultVideoPoster(): Bitmap? {
        return if (webChromeClient != null) {
            webChromeClient!!.defaultVideoPoster
        } else {
            super.getDefaultVideoPoster()
        }
    }

    override fun getVideoLoadingProgressView(): View? {
        return if (webChromeClient != null) {
            webChromeClient!!.videoLoadingProgressView
        } else {
            super.getVideoLoadingProgressView()
        }
    }

    override fun getVisitedHistory(callback: ValueCallback<Array<String>>) {
        if (webChromeClient != null) {
            webChromeClient!!.getVisitedHistory(callback)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onShowFileChooser(
        webView: WebView,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        return if (webChromeClient != null) {
            webChromeClient!!.onShowFileChooser(webView, filePathCallback, fileChooserParams)
        } else {

            if (bridgeWebView.mOnShowFileChooserListener != null) {
                return bridgeWebView.mOnShowFileChooserListener!!.onShowFileChooser(
                    webView,
                    filePathCallback,
                    fileChooserParams
                )
            }
            super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
        }
    }

    override fun onJsPrompt(
        view: WebView,
        url: String,
        message: String,
        defaultValue: String,
        result: JsPromptResult
    ): Boolean {
        return if (webChromeClient != null) {
            webChromeClient!!.onJsPrompt(view, url, message, defaultValue, result)
        } else {
            super.onJsPrompt(view, url, message, defaultValue, result)
        }
    }
}