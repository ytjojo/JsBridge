package com.xiaomao.jsbridge.core;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;

class BridgeWebChromeClient extends WebChromeClient {
    private WebChromeClient webChromeClient = null;

    public void setWebChromeClient(WebChromeClient webChromeClient) {

        if (webChromeClient != null) {
            this.webChromeClient = webChromeClient;
        }

    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {

        if (webChromeClient != null) {

            webChromeClient.onProgressChanged(view, newProgress);
        }


    }

    @Override
    public void onReceivedTitle(WebView view, String title) {

        if (webChromeClient != null) {

            webChromeClient.onReceivedTitle(view, title);
        }
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        if (webChromeClient != null) {

            webChromeClient.onReceivedIcon(view, icon);
        }
    }

    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        if (webChromeClient != null)
            webChromeClient.onReceivedTouchIconUrl(view, url, precomposed);
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        if (webChromeClient != null)
            webChromeClient.onShowCustomView(view, callback);
    }


    @Override
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        if (webChromeClient != null)
            webChromeClient.onShowCustomView(view, requestedOrientation, callback);
    }

    @Override
    public void onHideCustomView() {
        if (webChromeClient != null)
            webChromeClient.onHideCustomView();
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

        if (webChromeClient != null) {
            return webChromeClient.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        } else {
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }

    }

    @Override
    public void onRequestFocus(WebView view) {
        if (webChromeClient != null) {

            webChromeClient.onRequestFocus(view);
        }

    }

    @Override
    public void onCloseWindow(WebView window) {
        if (webChromeClient != null) {
            webChromeClient.onCloseWindow(window);
        }
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

        if (webChromeClient != null) {
            return webChromeClient.onJsAlert(view, url, message, result);
        } else {
            return super.onJsAlert(view, url, message, result);
        }

    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        if (webChromeClient != null) {
            return webChromeClient.onJsConfirm(view, url, message, result);
        } else {
            return super.onJsConfirm(view, url, message, result);
        }

    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {

        if (webChromeClient != null) {
            return webChromeClient.onJsBeforeUnload(view, url, message, result);
        } else {
            return super.onJsBeforeUnload(view, url, message, result);
        }


    }

    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
        if (webChromeClient != null)
            webChromeClient.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
    }

    @Override
    public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
        if (webChromeClient != null)
            webChromeClient.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        if (webChromeClient != null)
            webChromeClient.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        if (webChromeClient != null)
            webChromeClient.onGeolocationPermissionsHidePrompt();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPermissionRequest(PermissionRequest request) {
        if (webChromeClient != null)
            webChromeClient.onPermissionRequest(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPermissionRequestCanceled(PermissionRequest request) {
        if (webChromeClient != null)
            webChromeClient.onPermissionRequestCanceled(request);
    }

    @Override
    public boolean onJsTimeout() {
        if (webChromeClient != null)
            return webChromeClient.onJsTimeout();
        else {
            return super.onJsTimeout();
        }
    }

    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        if (webChromeClient != null)
            webChromeClient.onConsoleMessage(message, lineNumber, sourceID);
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if (webChromeClient != null) {
            return webChromeClient.onConsoleMessage(consoleMessage);
        }
        return super.onConsoleMessage(consoleMessage);

    }

    @Override
    public Bitmap getDefaultVideoPoster() {
        if (webChromeClient != null) {
            return webChromeClient.getDefaultVideoPoster();
        } else {
            return super.getDefaultVideoPoster();
        }

    }

    @Override
    public View getVideoLoadingProgressView() {
        if (webChromeClient != null) {
            return webChromeClient.getVideoLoadingProgressView();
        } else {
            return super.getVideoLoadingProgressView();
        }

    }

    @Override
    public void getVisitedHistory(ValueCallback<String[]> callback) {
        if (webChromeClient != null) {
            webChromeClient.getVisitedHistory(callback);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

        if (webChromeClient != null) {
            return webChromeClient.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        } else {
            return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        }

    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        if (webChromeClient != null) {
            return webChromeClient.onJsPrompt(view, url, message, defaultValue, result);
        } else {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
    }

}
