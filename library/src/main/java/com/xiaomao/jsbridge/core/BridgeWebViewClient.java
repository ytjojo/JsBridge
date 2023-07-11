package com.xiaomao.jsbridge.core;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.view.KeyEvent;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SafeBrowsingResponse;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


class BridgeWebViewClient extends WebViewClient {

    public static final String TAG = "BridgeWebViewClient";
    private WebViewClient webViewClient = null;

    public void setWebViewClient(WebViewClient webViewClient) {
        this.webViewClient = webViewClient;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (webViewClient != null) {
            return webViewClient.shouldOverrideUrlLoading(view, url);
        }
        return interceptUrl(url) ? true : super.shouldOverrideUrlLoading(view, url);
    }

    private boolean interceptUrl(String url) {


//        try {
//            url = URLDecoder.decode(url, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        Log.i(TAG, "shouldOverrideUrlLoading, url = " + url);

        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (webViewClient != null)
            return webViewClient.shouldOverrideUrlLoading(view, request);
    }


    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (webViewClient != null)
            webViewClient.onPageStarted(view, url, favicon);



    }


    @Override
    public void onPageFinished(WebView view, String url) {
        if (webViewClient != null)

            webViewClient.onPageFinished(view, url);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        if (webViewClient != null)
            webViewClient.onLoadResource(view, url);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onPageCommitVisible(WebView view, String url) {
        if (webViewClient != null)
            webViewClient.onPageCommitVisible(view, url);
    }


    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (webViewClient != null) {
            return webViewClient.shouldInterceptRequest(view, url);
        }
        return super.shouldInterceptRequest(view, url);
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (webViewClient != null) {
            return webViewClient.shouldInterceptRequest(view, request);
        }
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public void onTooManyRedirects(WebView view, android.os.Message cancelMsg, android.os.Message continueMsg) {
        if (webViewClient != null) {
            webViewClient.onTooManyRedirects(view, cancelMsg, continueMsg);
        } else {
            super.onTooManyRedirects(view, cancelMsg, continueMsg);
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if (webViewClient != null) {
            webViewClient.onReceivedError(view, errorCode, description, failingUrl);
        } else {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        if ( webViewClient != null) {
            webViewClient.onReceivedError(view, request, error);
        } else {
            super.onReceivedError(view, request, error);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        if (webViewClient != null) {
            webViewClient.onReceivedHttpError(view, request, errorResponse);
        } else {
            super.onReceivedHttpError(view, request, errorResponse);
        }

    }

    @Override
    public void onFormResubmission(WebView view, android.os.Message dontResend, android.os.Message resend) {
        if (webViewClient != null) {
            webViewClient.onFormResubmission(view, dontResend, resend);
        } else {
            super.onFormResubmission(view, dontResend, resend);
        }

    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        if (webViewClient != null) {
            webViewClient.doUpdateVisitedHistory(view, url, isReload);
        } else {
            super.doUpdateVisitedHistory(view, url, isReload);
        }
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        if (webViewClient != null) {
            webViewClient.onReceivedSslError(view, handler, error);
        } else {
            super.onReceivedSslError(view, handler, error);
        }
    }

    @Override
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        if (webViewClient != null) {
            webViewClient.onReceivedClientCertRequest(view, request);
        } else {
            super.onReceivedClientCertRequest(view, request);
        }
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        if (webViewClient != null) {
            webViewClient.onReceivedHttpAuthRequest(view, handler, host, realm);
        } else {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        if (webViewClient != null) {
            return webViewClient.shouldOverrideKeyEvent(view, event);
        }
        return super.shouldOverrideKeyEvent(view, event);
    }

    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        if (webViewClient != null) {
            webViewClient.onUnhandledKeyEvent(view, event);
        } else {
            super.onUnhandledKeyEvent(view, event);
        }
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        if (webViewClient != null) {
            webViewClient.onScaleChanged(view, oldScale, newScale);
        } else {
            super.onScaleChanged(view, oldScale, newScale);
        }
    }

    @Override
    public void onReceivedLoginRequest(WebView view, String realm, @Nullable String account, String args) {
        if (webViewClient != null) {
            webViewClient.onReceivedLoginRequest(view, realm, account, args);
        } else {
            super.onReceivedLoginRequest(view, realm, account, args);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
        if ( webViewClient != null) {
            return webViewClient.onRenderProcessGone(view, detail);
        }
        return super.onRenderProcessGone(view, detail);
    }

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    @Override
    public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {
        if ( webViewClient != null) {
            webViewClient.onSafeBrowsingHit(view, request, threatType, callback);
        } else {
            super.onSafeBrowsingHit(view, request, threatType, callback);
        }
    }
}