package com.github.lzyzsd.jsbridge

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Message
import android.view.KeyEvent
import android.webkit.*
import androidx.annotation.RequiresApi

open class BridgeWebViewClient(val bridgeWebView: BridgeWebView) : WebViewClient() {
    private var webViewClient: WebViewClient? = null
    fun setWebViewClient(webViewClient: WebViewClient?) {
        this.webViewClient = webViewClient
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
        if (webViewClient != null) {
            return webViewClient!!.shouldOverrideUrlLoading(view, url)
        }
        return if (interceptUrl(url)) true else super.shouldOverrideUrlLoading(view, url)
    }

    private fun interceptUrl(url: String?): Boolean {


//        try {
//            url = URLDecoder.decode(url, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        Log.i(TAG, "shouldOverrideUrlLoading, url = " + url);
        return false
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest?): Boolean {
        return if (webViewClient != null) webViewClient!!.shouldOverrideUrlLoading(
            view,
            request
        ) else super.shouldOverrideUrlLoading(
            view,
            request
        )
    }

    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
        if (webViewClient != null) webViewClient!!.onPageStarted(view, url, favicon)


        bridgeWebView.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView, url: String?) {
        if (webViewClient != null) webViewClient!!.onPageFinished(view, url)
        bridgeWebView.onPageFinished(view, url)

    }

    override fun onLoadResource(view: WebView, url: String?) {
        if (webViewClient != null) webViewClient!!.onLoadResource(view, url)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onPageCommitVisible(view: WebView, url: String?) {
        if (webViewClient != null) webViewClient!!.onPageCommitVisible(view, url)
    }

    override fun shouldInterceptRequest(view: WebView, url: String?): WebResourceResponse? {
        return if (webViewClient != null) {
            webViewClient!!.shouldInterceptRequest(view, url)
        } else super.shouldInterceptRequest(view, url)
    }

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        return if (webViewClient != null) {
            webViewClient!!.shouldInterceptRequest(view, request)
        } else super.shouldInterceptRequest(view, request)
    }

    override fun onTooManyRedirects(view: WebView, cancelMsg: Message, continueMsg: Message) {
        if (webViewClient != null) {
            webViewClient!!.onTooManyRedirects(view, cancelMsg, continueMsg)
        } else {
            super.onTooManyRedirects(view, cancelMsg, continueMsg)
        }
    }

    override fun onReceivedError(
        view: WebView,
        errorCode: Int,
        description: String,
        failingUrl: String
    ) {
        if (webViewClient != null) {
            webViewClient!!.onReceivedError(view, errorCode, description, failingUrl)
        } else {
            super.onReceivedError(view, errorCode, description, failingUrl)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest?,
        error: WebResourceError
    ) {
        if (webViewClient != null) {
            webViewClient!!.onReceivedError(view, request, error)
        } else {
            super.onReceivedError(view, request, error)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onReceivedHttpError(
        view: WebView,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse
    ) {
        if (webViewClient != null) {
            webViewClient!!.onReceivedHttpError(view, request, errorResponse)
        } else {
            super.onReceivedHttpError(view, request, errorResponse)
        }
    }

    override fun onFormResubmission(view: WebView, dontResend: Message, resend: Message) {
        if (webViewClient != null) {
            webViewClient!!.onFormResubmission(view, dontResend, resend)
        } else {
            super.onFormResubmission(view, dontResend, resend)
        }
    }

    override fun doUpdateVisitedHistory(view: WebView, url: String, isReload: Boolean) {
        if (webViewClient != null) {
            webViewClient!!.doUpdateVisitedHistory(view, url, isReload)
        } else {
            super.doUpdateVisitedHistory(view, url, isReload)
        }
    }

    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        if (webViewClient != null) {
            webViewClient!!.onReceivedSslError(view, handler, error)
        } else {
            super.onReceivedSslError(view, handler, error)
        }
    }

    override fun onReceivedClientCertRequest(view: WebView, request: ClientCertRequest) {
        if (webViewClient != null) {
            webViewClient!!.onReceivedClientCertRequest(view, request)
        } else {
            super.onReceivedClientCertRequest(view, request)
        }
    }

    override fun onReceivedHttpAuthRequest(
        view: WebView,
        handler: HttpAuthHandler,
        host: String,
        realm: String
    ) {
        if (webViewClient != null) {
            webViewClient!!.onReceivedHttpAuthRequest(view, handler, host, realm)
        } else {
            super.onReceivedHttpAuthRequest(view, handler, host, realm)
        }
    }

    override fun shouldOverrideKeyEvent(view: WebView, event: KeyEvent): Boolean {
        return if (webViewClient != null) {
            webViewClient!!.shouldOverrideKeyEvent(view, event)
        } else super.shouldOverrideKeyEvent(view, event)
    }

    override fun onUnhandledKeyEvent(view: WebView, event: KeyEvent) {
        if (webViewClient != null) {
            webViewClient!!.onUnhandledKeyEvent(view, event)
        } else {
            super.onUnhandledKeyEvent(view, event)
        }
    }

    override fun onScaleChanged(view: WebView, oldScale: Float, newScale: Float) {
        if (webViewClient != null) {
            webViewClient!!.onScaleChanged(view, oldScale, newScale)
        } else {
            super.onScaleChanged(view, oldScale, newScale)
        }
    }

    override fun onReceivedLoginRequest(
        view: WebView,
        realm: String,
        account: String?,
        args: String
    ) {
        if (webViewClient != null) {
            webViewClient!!.onReceivedLoginRequest(view, realm, account, args)
        } else {
            super.onReceivedLoginRequest(view, realm, account, args)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
        return if (webViewClient != null) {
            webViewClient!!.onRenderProcessGone(view, detail)
        } else super.onRenderProcessGone(view, detail)
    }

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    override fun onSafeBrowsingHit(
        view: WebView,
        request: WebResourceRequest?,
        threatType: Int,
        callback: SafeBrowsingResponse?
    ) {
        if (webViewClient != null) {
            webViewClient!!.onSafeBrowsingHit(view, request, threatType, callback)
        } else {
            super.onSafeBrowsingHit(view, request, threatType, callback)
        }
    }

    companion object {
        const val TAG = "BridgeWebViewClient"
    }
}