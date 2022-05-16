package cool.dingstock.appbase.webview

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.webkit.*
import android.widget.FrameLayout
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.R
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.constant.WebviewConstant
import cool.dingstock.appbase.databinding.ActivityDcDefaultWebViewBinding
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.util.StatusBarUtil


@RouterUri(
        scheme = RouterConstant.SCHEME,
        host = RouterConstant.HOST,
        path = [WebviewConstant.Path.DEFAULT]
)
class DcDefaultWebViewActivity : VMBindingActivity<BaseViewModel, ActivityDcDefaultWebViewBinding>() {

    private var videoFullView: FrameLayout? = null// 全屏时视频加载view
    private var xCustomView: View? = null

    override fun moduleTag(): String {
        return "webView"
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        StatusBarUtil.transparentStatus(this)
        val url = intent?.data?.getQueryParameter(WebviewConstant.UriParam.URL) ?: ""
        val title = intent?.data?.getQueryParameter("title")
        if (TextUtils.isEmpty(url)) {
            finish()
            return
        }
        videoFullView = findViewById(R.id.video_fullView)
        viewBinding.commonTitlebarTitleTv.text = title
        val ws = viewBinding.webViewWv.settings
        ws.javaScriptEnabled = true
        // 网页内容的宽度是否可大于WebView控件的宽度
        // 网页内容的宽度是否可大于WebView控件的宽度
        ws.loadWithOverviewMode = false
        // 保存表单数据
        // 保存表单数据
        ws.saveFormData = true
        // 是否应该支持使用其屏幕缩放控件和手势缩放
        // 是否应该支持使用其屏幕缩放控件和手势缩放
        ws.setSupportZoom(true)
        ws.builtInZoomControls = false
        ws.displayZoomControls = false
        // 启动应用缓存
        ws.setAppCacheEnabled(true)
        // 设置缓存模式
        // setDefaultZoom  api19被弃用
        // 设置此属性，可任意比例缩放。
        ws.useWideViewPort = true
        // 不缩放
        // 告诉WebView启用JavaScript执行。默认的是false。
        //  页面加载好以后，再放开图片
        ws.blockNetworkImage = false
        // 使用localStorage则必须打开
        ws.domStorageEnabled = true
        // 排版适应屏幕
        ws.layoutAlgorithm = android.webkit.WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        // WebView是否新窗口打开(加了后可能打不开网页)
        ws.setSupportMultipleWindows(true)
        // webview从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启。MIXED_CONTENT_ALWAYS_ALLOW
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.mixedContentMode = WebSettings.LOAD_NORMAL
        }
        showLoadingView()
        with(viewBinding.webViewWv){
            requestFocus()
            isEnabled=true
        }
        viewBinding.webViewWv.loadUrl(url)
        viewBinding.webViewWv.webViewClient = object : android.webkit.WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                hideLoadingView()
            }
        }

        viewBinding.webViewWv.webChromeClient = object : WebChromeClient() {
            // 拦截全屏调用的方法
            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                super.onShowCustomView(view, callback)
                viewBinding.webViewWv.visibility = View.INVISIBLE
                // 如果一个视图已经存在，那么立刻终止并新建一个
                if (xCustomView != null) {
                    callback.onCustomViewHidden()
                    return
                }
                view.visibility = View.VISIBLE
                videoFullView?.addView(view)
                xCustomView = view
                xCustomView!!.visibility = View.VISIBLE
                videoFullView?.visibility = View.VISIBLE
            }

            override fun onHideCustomView() {
                super.onHideCustomView()
                if (xCustomView == null) {
                    // 不是全屏播放状态
                    return
                }
                xCustomView!!.visibility = View.GONE
                videoFullView?.removeView(xCustomView)
                xCustomView = null
                videoFullView?.visibility = View.GONE
                viewBinding.webViewWv.visibility = View.VISIBLE
            }
        }
    }

    override fun initListeners() {
        viewBinding.commonTitlebarLeftTxt.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        CookieSyncManager.createInstance(this)
//        val cookieManager = CookieManager.getInstance()
//        cookieManager.removeAllCookie()
//        CookieSyncManager.getInstance().sync()


        val cookieManager = CookieManager.getInstance()

        cookieManager.removeAllCookies {

        }
        cookieManager.flush()
        viewBinding.apply {
            webViewWv.webChromeClient = null
            webViewWv.settings.javaScriptEnabled = false
            webViewWv.clearCache(true)
            webViewWv.destroy()
        }
    }
}