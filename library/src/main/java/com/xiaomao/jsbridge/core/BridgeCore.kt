package com.xiaomao.jsbridge.core

import android.os.Handler
import android.os.Looper

object BridgeCore {
    const val CALLBACK_ID_FORMAT = "JAVA_CB_%s"
    const val UNDERLINE_STR = "_"

    const val SCRIPT_DISPATCH_MESSAGE =
        "(function(){window.WebViewJavascriptBridge && WebViewJavascriptBridge._dispatchMessage('%s');}())"
    var isDebug = false
    val handler = Handler(Looper.getMainLooper())



    fun getBridgeJavascript():String{
        return ""
    }

    fun runOnUiThread(runnable: Runnable){
        if(Looper.myLooper() == Looper.getMainLooper()){
            runnable.run()
        }else{
            handler.post { runnable }
        }
    }


}