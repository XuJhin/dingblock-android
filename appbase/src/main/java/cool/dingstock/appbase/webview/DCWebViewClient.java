package cool.dingstock.appbase.webview;

import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import java.util.ArrayList;

import cool.dingstock.appbase.delegate.DCWebViewControllerDelegate;
import cool.dingstock.appbase.mvp.BaseActivity;
import cool.dingstock.appbase.mvp.DCActivityManager;
import cool.dingstock.appbase.router.DcRouterUtils;
import cool.dingstock.appbase.webview.delegate.OverrideUrlDelegate;
import cool.dingstock.lib_base.util.Logger;


public class DCWebViewClient extends WebViewClient {

    ArrayList<OverrideUrlDelegate> delegates = new ArrayList<>();

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Logger.d("onPageFinished  -- ");
        hideLoading(view);
        if (!(view instanceof DCWebView)) {
            return;
        }
        DCWebView dcWebView = (DCWebView) view;
        DCWebViewControllerDelegate controller = dcWebView.getControllerDelegate();
        if (null != controller) {
            controller.onWebViewLoadingFinish();
        }
    }


    public void registerDelegate(OverrideUrlDelegate delegate) {
        delegates.add(delegate);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        Logger.d("shouldOverrideUrlLoading url=" + url);
        if (url.startsWith("mqqapi:")
                || url.startsWith("weixin:")
                || url.startsWith("tel:")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            BaseActivity topActivity = DCActivityManager.getInstance().getTopActivity();
            if (null == topActivity) {
                return super.shouldOverrideUrlLoading(webView, url);
            }
            topActivity.startActivity(intent);
            return true;
        }
        try {
            if (DcRouterUtils.isScheme(Uri.parse(url))) {
                Logger.d("拦截  ： shouldOverrideUrlLoading  url=" + url);
                return true;
            }
        } catch (Exception e) {
        }

        for (OverrideUrlDelegate delegate : delegates) {
            if (delegate.shouldOverrideUrlLoading(webView, url)) {
                return true;
            }
        }

        return super.shouldOverrideUrlLoading(webView, url);
    }

    @Override
    public void onReceivedError(WebView webView, int i, String s, String s1) {
        super.onReceivedError(webView, i, s, s1);
        Logger.e("onReceivedError");
    }

    //    @Override
    public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
        super.onReceivedSslError(webView, sslErrorHandler, sslError);
        Logger.e("onReceivedSslError");
    }

    @Override
    public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
        super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
        Logger.e("onReceivedHttpError");
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        Logger.e("error=" + error.toString());
        hideLoading(view);
    }

    private void hideLoading(WebView view) {
        if (!(view instanceof DCWebView)) {
            return;
        }
        DCWebView dcWebView = (DCWebView) view;
        DCWebViewControllerDelegate controller = dcWebView.getControllerDelegate();
        if (null != controller) {
            controller.hideLoadingView();
        }
    }
}
