(function(){
    if(window.WebViewJavascriptBridge && window.WebViewJavascriptBridge.inited){
        return "false";
    }
    var nativeCallJsHandlers = {};
    var nativeReturnJsHandlers = {};
    var uniqueId = 1;
    function init(defaultHandler){
        if(WebViewJavascriptBridge.defaultHandler){
            console.log('WebViewJavascriptBridge.init called twice');
            return;
        }
        WebViewJavascriptBridge.defaultHandler = defaultHandler;
        WebViewJavascriptBridge.inited = true;
    }
    function registerHandler(handlerName, handler){
        if(handlerName && handler){ nativeCallJsHandlers[handlerName] = handler; }
    }

    function removeHandler(handlerName, handler) {
        delete nativeCallJsHandlers[handlerName];
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
            var callbackId = 'cb_' + (uniqueId++) + '_' + new Date().getTime();
            nativeReturnJsHandlers[callbackId] = responseCallback;
            message.callbackId = callbackId;
        }
        AndroidBridge.callFromWeb(JSON.stringify(message));
    }

     // 发送
    function send(data, responseCallback) {
       callHandler('send', data, responseCallback);
    }
    function _dispatchMessage(messageJSON){
        setTimeout(function() {
            console.log(messageJSON);
            var message = JSON.parse(messageJSON);
            console.log(message.handlerName + "  json");

            if(message.responseId){
                var handler = nativeReturnJsHandlers[message.responseId];
                if(handler){
                    try {
                       if(message.responseData && message.responseData.constructor == String){
                          message.responseData = JSON.parse(message.responseData);
                       }
                    } catch (exception) {
                       console.log("WebViewJavascriptBridge: WARNING: javascript handler threw.", message, exception);
                    }
                    handler(message.responseData);
                    delete nativeReturnJsHandlers[message.responseId];
                }
            } else if(message.handlerName){
                var handler = nativeCallJsHandlers[message.handlerName];
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
                } catch (exception) {
                    console.log("WebViewJavascriptBridge: WARNING: javascript handler threw.", message, exception);
                }
                handler(message.data, responseCallback);
            }
        });
    }
    var WebViewJavascriptBridge = window.WebViewJavascriptBridge = {
        init: init, registerHandler: registerHandler, callHandler: callHandler,send: send, _dispatchMessage: _dispatchMessage
    };
//    document.dispatchEvent(new Event('WebViewJavascriptBridgeReady'));

    var readyEvent = document.createEvent('Events');
    var jobs = window.WVJBCallbacks || [];
    readyEvent.initEvent('WebViewJavascriptBridgeReady');
    readyEvent.bridge = WebViewJavascriptBridge;
    window.WVJBCallbacks = [];
    jobs.forEach(function (job) {
         job(WebViewJavascriptBridge)
    });
    document.dispatchEvent(readyEvent);

})();