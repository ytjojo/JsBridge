package com.xiaomao.jsbridge;


import android.app.Activity;

public interface WebViewJavascriptBridge {
	
	void sendToWeb(String data);

	void sendToWeb(String data, OnBridgeCallback responseCallback);

	void sendToWeb(String function, Object... values);

	void onResponseFromWeb(String data);

	void callFromWeb(String data);

	boolean isDestory();



}

