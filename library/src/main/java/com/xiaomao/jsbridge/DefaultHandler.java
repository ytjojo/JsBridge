package com.xiaomao.jsbridge;

public class DefaultHandler implements BridgeHandler{

	@Override
	public void handler(String data, OnBridgeCallback function) {
		if(function != null){
			function.onCallBack("DefaultHandler response data.");
		}
	}

}
