(function(){
    if(window.WebViewJavascriptBridge){return "false";}
    var nativeCallJsHandlers = {};
    var nativeReturnJsHandlers = {};
    var uniqueId = 1;
    function init(defaultHandler){
        if(WebViewJavascriptBridge.defaultHandler){
            console.log('WebViewJavascriptBridge.init called twice');
            return;
        }
        WebViewJavascriptBridge.defaultHandler = defaultHandler;
    }
    function registerHandler(handlerName, handler){
        if(handlerName && handler){ WebViewJavascriptBridge.nativeCallJsHandlers[handlerName] = handler; }
    }
    function callHandler(){
        var handlerName; var data; var responseCallback; var item;
        for(var i = 0; i < arguments.length; i++){
            item = arguments[i];
            if (item.constructor == String){
                handlerName = item;
            } else if (item.constructor == Object) {
                data = item;
            } else if (item.constructor == Function) {
                responseCallback = item;
            }
        }
        var message = {handlerName: handlerName, data: data};
        if(responseCallback){
            var callbackId = 'cb_' + (WebViewJavascriptBridge.uniqueId++) + '_' + new Date().getTime();
            WebViewJavascriptBridge.nativeReturnJsHandlers[callbackId] = responseCallback;
            message.callbackId = callbackId;
        }
        AndroidBridge.callFromWeb(JSON.stringify(message));
    }
    function _dispatchMessage(messageJSON){
        setTimeout(function() {
            var message = JSON.parse(messageJSON);
            if(message.responseId){
                var handler = WebViewJavascriptBridge.nativeReturnJsHandlers[message.responseId];
                if(handler){
                    if(message.responseData && message.responseData.constructor == String){
                        message.responseData = JSON.parse(message.responseData);
                    }
                    handler(message.responseData);
                    delete WebViewJavascriptBridge.nativeReturnJsHandlers[message.responseId];
                }
            } else if(message.handlerName){
                var handler = WebViewJavascriptBridge.nativeCallJsHandlers[message.handlerName];
                if(!handler){ handler = WebViewJavascriptBridge.defaultHandler;}
                var responseCallback;
                if(message.callbackId){
                    var callbackResponseId = message.callbackId;
                    responseCallback = function(responseData) {
                        AndroidBridge.onResponseFromWeb(JSON.stringify({responseId: callbackResponseId,responseData: responseData}));
                    };
                }
                try {
                    if(message.data && message.data.constructor == String){
                        message.data = JSON.parse(message.data);
                    }
                    handler(message.data, responseCallback);
                } catch (exception) {
                    console.log("WebViewJavascriptBridge: WARNING: javascript handler threw.", message, exception);
                }
            }
        });
    }
    var WebViewJavascriptBridge = window.WebViewJavascriptBridge = {
        init: init, registerHandler: registerHandler, callHandler: callHandler, _dispatchMessage: _dispatchMessage
    };
    document.dispatchEvent(new Event('WebViewJavascriptBridgeReady'));
    // if(AndroidBridge){ AndroidBridge.postMessage('WebViewJavascriptBridgeReady'); }
})();