package com.github.lzyzsd.jsbridge;


import android.webkit.ValueCallback;

public interface WebViewJavascriptBridge {
	
	void sendToWeb(String data);

	void sendToWeb(String data, OnBridgeCallback responseCallback);

	void sendWithFunctionToWeb(String function,ValueCallback<String> valueCallback, Object... values );

	void onResponseFromWeb(String data);

	void callFromWeb(String data);

	boolean isDestory();


	void onWebViewJavascriptBridgeReady();



}

