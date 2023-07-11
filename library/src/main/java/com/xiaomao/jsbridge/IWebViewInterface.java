package com.xiaomao.jsbridge;

import android.app.Activity;
import android.webkit.WebView;

public interface IWebViewInterface {


    WebView getWebView();
    // 设置 WeiMaiWebPageInterface
    // 执行 js 脚本
    void executeJavaScript(String command, OnBridgeCallback callback);
    // 对外开放的 注测交互事件
    void registerHandler(String handlerName, BridgeHandler handler);
    // 对外开放的 执行js侧代码
    void callHandler(String handlerName, String data,  BridgeHandler callBackFunction);
    // js端调用交互后最终都会执行此方法. 实现类需要在这里进行分发
    void onHandlerBridgeMessage(String data);
    // 注入jsbridge 脚本
    void onRequireBridge(WebView webView, boolean debug);

    void finishPage();

    Activity getActivity();
}
