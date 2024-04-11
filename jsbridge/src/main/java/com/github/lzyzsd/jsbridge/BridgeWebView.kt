package com.github.lzyzsd.jsbridge

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.webkit.*
import com.github.lzyzsd.jsbridge.core.BridgeCore
import com.github.lzyzsd.jsbridge.core.BridgeCore.isDebug
import com.github.lzyzsd.jsbridge.core.BridgeInjector
import com.github.lzyzsd.jsbridge.core.MessageDispatcher
import java.lang.ref.WeakReference

@SuppressLint("SetJavaScriptEnabled")
open class BridgeWebView : WebView, IWebViewInterface {
    private var mWebViewClient: BridgeWebViewClient? = null
    private var mChromeClient: BridgeWebChromeClient? = null

    private val mOnPageLoadListeners = ArrayList<OnPageLoadListener>()

    private val mFixOnProgressChangedListeners = ArrayList<OnProgressChangedListener>()
    private val mOnReceiveTitleListeners = ArrayList<OnReceiveTitleListener>()

    private val mOnScrollChangedListeners = ArrayList<OnScrollChangedListener>()

    var mOnShowFileChooserListener: OnShowFileChooserListener? = null

    val bridgeInjector by lazy {
        BridgeInjector(this)
    }

    val messageDispatcher by lazy {
        MessageDispatcher(this)
    }

    private val mActivity by lazy {
        context2Activity(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        init()
    }

    constructor(context: Context?) : super(context!!) {
        init()
    }

    private fun init() {
        val applicationInfo: ApplicationInfo = context.getApplicationInfo()
        BridgeCore.isDebugByApplicationInfo = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        clearCache(true)
        settings.useWideViewPort = true
        //		webView.getSettings().setLoadWithOverviewMode(true);
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.javaScriptEnabled = true
        //        mContent.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        settings.javaScriptCanOpenWindowsAutomatically = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isDebug) {
            setWebContentsDebuggingEnabled(true)
        }
        mWebViewClient = BridgeWebViewClient(this)
        mChromeClient = BridgeWebChromeClient(this)
        this.addJavascriptInterface(
            AndroidJavascriptInterface(messageDispatcher),
            "AndroidBridge"
        )
        super.setWebViewClient(mWebViewClient!!)
        super.setWebChromeClient(mChromeClient!!)
    }

    override fun setWebViewClient(client: WebViewClient) {
        mWebViewClient!!.setWebViewClient(client)
    }

    override fun setWebChromeClient(client: WebChromeClient?) {
        mChromeClient!!.setWebChromeClient(client)
    }

    /**
     * call javascript registered handler
     * 调用javascript处理程序注册
     *
     * @param handlerName handlerName
     * @param data        data
     * @param callBack    OnBridgeCallback
     */
    override fun callHandler(handlerName: String, data: Any?, callBack: OnBridgeCallback?) {
        messageDispatcher.callHandler(handlerName, data, callBack)
    }

    /**
     * register handler,so that javascript can call it
     * 注册处理程序,以便javascript调用它
     *
     * @param handlerName handlerName
     * @param handler     BridgeHandler
     */
    override fun registerHandler(handlerName: String, handler: BridgeHandler) {
        messageDispatcher.registerHandler(handlerName, handler)
    }


    /**
     * unregister handler
     *
     * @param handlerName
     */
    override fun unregisterHandler(handlerName: String) {
        messageDispatcher.unregisterHandler(handlerName)
    }

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        bridgeInjector.onProgressChanged(view, newProgress)

    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        bridgeInjector.onPageStarted(view, url, favicon)
        mOnPageLoadListeners.forEach {
            it.onPageStarted(view, url, favicon)
        }
        // 阻断网络图片下载
        view!!.settings.blockNetworkImage = true
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        bridgeInjector.onPageFinished(view, url)

        mOnPageLoadListeners.forEach {
            it.onPageFinished(view, url)
        }
        view!!.settings.blockNetworkImage = false
    }

    override fun onReceivedTitle(view: WebView, title: String) {
        mOnReceiveTitleListeners.forEach {
            it.onReceivedTitle(view, title)
        }
    }

    override fun activity(): Activity? {
        return mActivity
    }


    override fun destroy() {
        messageDispatcher.onDestory()
        mOnReceiveTitleListeners.clear()
        mOnPageLoadListeners.clear()
        mFixOnProgressChangedListeners.clear()
        super.destroy()
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        mOnScrollChangedListeners.forEach {
            it.onScrollChanged(this,l,t,oldl,oldt)
        }
    }

    companion object {
        fun context2Activity(context: Context?): Activity? {
            var context = context
            while (context != null) {
                if (context is Activity) {
                    return context
                }
                val activity = getActivityFromDecorContext(context)
                if (activity != null) return activity
                context = if (context is ContextWrapper) {
                    context.baseContext
                } else {
                    break
                }
            }
            return null
        }

        private fun getActivityFromDecorContext(context: Context?): Activity? {
            if (context == null) return null
            if (context.javaClass.name == "com.android.internal.policy.DecorContext") {
                try {
                    val mActivityContextField =
                        context.javaClass.getDeclaredField("mActivityContext")
                    mActivityContextField.isAccessible = true
                    return (mActivityContextField[context] as WeakReference<Activity?>).get()
                } catch (ignore: Exception) {
                }
            }
            return null
        }
    }

    fun addOnPageLoadListener(l: OnPageLoadListener) {
        mOnPageLoadListeners.add(l)
    }
    fun removeOnPageLoadListener(l: OnPageLoadListener) {
        mOnPageLoadListeners.remove(l)
    }

    fun addOnScrollChangedListener(l: OnScrollChangedListener) {
        mOnScrollChangedListeners.add(l)
    }
    fun removeOnScrollChangedListener(l: OnScrollChangedListener) {
        mOnScrollChangedListeners.remove(l)
    }
    fun addOnProgressChangedListener(l: OnProgressChangedListener) {
        mFixOnProgressChangedListeners.add(l)
    }

    fun removeOnProgressChangedListener(l: OnProgressChangedListener) {
        mFixOnProgressChangedListeners.remove(l)
    }




    fun getFixProgressListeners(): ArrayList<OnProgressChangedListener> {
        return mFixOnProgressChangedListeners
    }

    fun addOnReceiveTitleListener(l: OnReceiveTitleListener) {
        mOnReceiveTitleListeners.add(l)
    }


    fun removeOnReceiveTitleListener(l: OnReceiveTitleListener) {
        mOnReceiveTitleListeners.remove(l)
    }
}

interface OnPageLoadListener {

    fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?)


    fun onPageFinished(view: WebView?, url: String?)


}

interface OnScrollChangedListener {


    fun onScrollChanged(view: WebView, left: Int, top: Int, oldLeft: Int, oldTop: Int)

}

interface OnProgressChangedListener {
    fun onProgressChanged(view: WebView, newProgress: Int)
}

interface OnReceiveTitleListener {
    fun onReceivedTitle(view: WebView, title: String)
}

interface OnShowFileChooserListener {
    fun onShowFileChooser(
        webView: WebView,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ): Boolean
}
