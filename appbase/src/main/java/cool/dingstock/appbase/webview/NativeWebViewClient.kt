package cool.dingstock.appbase.webview

import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.webkit.*
import cool.dingstock.appbase.mvp.DCActivityManager
import cool.dingstock.appbase.router.DcRouterUtils
import cool.dingstock.appbase.webview.delegate.OverrideUrlDelegate
import cool.dingstock.appbase.webview.system.SystemWebView
import cool.dingstock.lib_base.util.Logger

class NativeWebViewClient : WebViewClient() {
    var delegates = ArrayList<OverrideUrlDelegate>()
    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        Logger.d("onPageFinished  -- ")
        hideLoading(view)
        if (view !is SystemWebView) {
            return
        }
        val controller = view.controllerDelegate
        controller?.onWebViewLoadingFinish()
    }

    fun registerDelegate(delegate: OverrideUrlDelegate) {
        delegates.add(delegate)
    }

    override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
        Logger.d("shouldOverrideUrlLoading url=$url")
        if (url.startsWith("mqqapi:")
            || url.startsWith("weixin:")
            || url.startsWith("tel:")
        ) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            val topActivity = DCActivityManager.getInstance().topActivity
                ?: return super.shouldOverrideUrlLoading(webView, url)
            topActivity.startActivity(intent)
            return true
        }
        try {
            if (DcRouterUtils.isScheme(Uri.parse(url))) {
                Logger.d("拦截  ： shouldOverrideUrlLoading  url=$url")
                return true
            }
        } catch (e: Exception) {
        }
        for (delegate in delegates) {
            if (delegate.shouldOverrideUrlLoading(webView, url)) {
                return true
            }
        }
        return super.shouldOverrideUrlLoading(webView, url)
    }

    override fun onReceivedError(webView: WebView, i: Int, s: String, s1: String) {
        super.onReceivedError(webView, i, s, s1)
        Logger.e("onReceivedError")
    }

    //    @Override
    override fun onReceivedSslError(
        webView: WebView,
        sslErrorHandler: SslErrorHandler,
        sslError: SslError
    ) {
        super.onReceivedSslError(webView, sslErrorHandler, sslError)
        Logger.e("onReceivedSslError")
    }

    override fun onReceivedHttpError(
        webView: WebView,
        webResourceRequest: WebResourceRequest,
        webResourceResponse: WebResourceResponse
    ) {
        super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse)
        Logger.e("onReceivedHttpError")
    }

    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceError
    ) {
        super.onReceivedError(view, request, error)
        Logger.e("error=$error")
        hideLoading(view)
    }

    private fun hideLoading(view: WebView) {
        if (view !is SystemWebView) {
            return
        }
        val controller = view.controllerDelegate
        controller?.hideLoadingView()
    }
}
