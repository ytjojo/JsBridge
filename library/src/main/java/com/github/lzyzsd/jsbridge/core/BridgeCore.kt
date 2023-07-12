package com.github.lzyzsd.jsbridge.core

import android.content.Context
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.webkit.WebSettings
import android.webkit.WebView
import com.github.lzyzsd.jsbridge.BridgeUtil
import java.io.File

object BridgeCore {
    const val CALLBACK_ID_FORMAT = "JAVA_CB_%s"
    const val UNDERLINE_STR = "_"

    //	public final static String JAVASCRIPT_STR = "javascript:";
    const val JAVA_SCRIPT = "android_bridge_script.js"

    const val SCRIPT_DISPATCH_MESSAGE =
        "(function(){window.WebViewJavascriptBridge && WebViewJavascriptBridge._dispatchMessage(%s);}())"

    // 导航栏脚本 - 点击返回
    const val SCRIPT_CLICK_BACK = "document.dispatchEvent(new Event(\"back\", {cancelable: true}));"
    var isDebug = false

    var assetScript = ""
    val handler = Handler(Looper.getMainLooper())


    fun getBridgeJavascript(context: Context): String {

        if (assetScript.isNullOrEmpty()) {
            assetScript = BridgeUtil.assetFile2Str(context, JAVA_SCRIPT)
        }
        return assetScript
    }

    fun runOnUiThread(runnable: Runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run()
        } else {
            handler.post { runnable.run() }
        }
    }


    fun onResume(webView: WebView) {
        webView.onResume()
        webView.resumeTimers()
    }

    fun onPause(webView: WebView) {
        webView.pauseTimers()
        webView.onPause()
    }

    fun onDestroy(webView: WebView) {
        val parent = webView.parent
        if (parent is ViewGroup) {
            parent.removeView(webView)
        }
        webView.stopLoading()
        // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
        webView.settings.javaScriptEnabled = false
        webView.clearHistory()
        webView.clearView()
        webView.removeAllViews()
        webView.destroy()
    }

    fun loadUrl(webView: WebView, url: String?) {
        webView?.loadUrl(url!!)
    }

    fun reload(webView: WebView) {
        webView?.reload()
    }

    fun loadUrl(
        webView: WebView,
        url: String?,
        additionalHttpHeaders: Map<String?, String?>?
    ) {
        webView?.loadUrl(url!!, additionalHttpHeaders!!)
    }

    fun loadData(webView: WebView, data: String?) {
        webView?.loadData(Uri.encode(data, "utf-8"), "text/html", "utf-8")
    }

    fun loadDataWithBaseURL(
        webView: WebView,
        baseUrl: String?,
        data: String?,
        failUrl: String?
    ) {
        webView?.loadDataWithBaseURL(baseUrl, data!!, "text/html", "utf-8", failUrl)
    }

    fun clearCache(context: Context?) {
        if (context == null) {
            return
        }
        val webView = WebView(context.applicationContext)
        webView.clearCache(true)
        webView.clearHistory()
        webView.clearFormData()
        // 清除 Cookie
        if (CookieSyncManager.getInstance() == null) {
            CookieSyncManager.createInstance(context)
        }
        val cm = CookieManager.getInstance()
        cm.removeAllCookie()
        // 清除数据库缓存
        try {
            context.deleteDatabase("webview.db")
            context.deleteDatabase("webviewCache.db")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        deleteFile(File(context.cacheDir.absolutePath + "/webviewCache"))
        // appcache
        deleteFile(File(context.filesDir.absolutePath + "/webcache"))
    }

    private fun deleteFile(file: File?) {
        if (file != null && file.exists()) {
            if (file.isFile) {
                file.delete()
            } else if (file.isDirectory) {
                val files = file.listFiles()
                for (i in files.indices) {
                    deleteFile(files[i])
                }
            }
            file.delete()
        }
    }


    fun config(webView: WebView) {
        val settings = webView.settings
        val context = webView.context
        settings.setCacheMode(WebSettings.LOAD_DEFAULT)

        // 远程调试
        val info: ApplicationInfo = context.getApplicationInfo()
        WebView.setWebContentsDebuggingEnabled( info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0)
        // 接受第三方cookie
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        // 5.0以上开启混合模式加载
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW)
        // 自动加载图片
        settings.setLoadsImagesAutomatically(true)
        // 适应屏幕
        settings.setLoadWithOverviewMode(true)
        settings.setUseWideViewPort(true)
        // 允许执行 js
        settings.setJavaScriptEnabled(true)
        // 允许SessionStorage/LocalStorage存储
        settings.setDomStorageEnabled(true)
        // 禁用放缩
        // 禁用放缩
        settings.setSupportZoom(false)
        settings.setDisplayZoomControls(false)
        settings.setBuiltInZoomControls(false)
        // 禁用文字缩放
        settings.setTextZoom(100)
        // app cache
        settings.setAppCacheEnabled(true)
        settings.setAppCachePath(context.getCacheDir().getAbsolutePath() + "/webviewCache")
        // 允许WebView使用File协议
        settings.setAllowFileAccess(true)
        // 当webview调用requestFocus时为webview设置节点
        settings.setNeedInitialFocus(true)
        // 设置编码格式
        settings.setDefaultTextEncodingName("UTF-8")
        // video 标签 自动播放
        settings.setMediaPlaybackRequiresUserGesture(false)

        settings.setJavaScriptCanOpenWindowsAutomatically(true)

        settings.setAllowContentAccess(true)
        settings.setDatabaseEnabled(true)
        settings.setSavePassword(false)
        settings.setSaveFormData(false)
        settings.setBlockNetworkImage(false)


        //****配置权限,允许H5页面使用GPS权限
        val gpsDB = "/data/data/" + context.getPackageName() + "/databases/"
        //设置定位的数据库路径
        //设置定位的数据库路径
        settings.setGeolocationEnabled(true)
        settings.setGeolocationDatabasePath(gpsDB)


        // 下载监听
//        webView.setDownloadListener()
    }


}