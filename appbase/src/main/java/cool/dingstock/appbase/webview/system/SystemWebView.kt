package cool.dingstock.appbase.webview.system

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import com.lljjcoder.style.citypickerview.CityPickerView
import cool.dingstock.appbase.BuildConfig
import cool.dingstock.appbase.R
import cool.dingstock.appbase.delegate.DCWebViewControllerDelegate
import cool.dingstock.appbase.webview.NativeWebViewClient
import cool.dingstock.appbase.webview.bridge.IBridgeModule
import cool.dingstock.appbase.webview.bridge.OnNativeActionCallback
import cool.dingstock.appbase.webview.bridge.android.NativeJsBridge
import cool.dingstock.appbase.webview.delegate.RouteModuleDelegate
import cool.dingstock.appbase.webview.delegate.ViewModuleDelegate
import cool.dingstock.appbase.webview.module.*
import java.lang.ref.WeakReference


class SystemWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr), ViewModuleDelegate, RouteModuleDelegate,
    AbsListView.OnScrollListener {
    lateinit var jsBridge: NativeJsBridge
    var viewModule: ViewModule? = null
    var controllerDelegate: DCWebViewControllerDelegate? = null
    var refreshStateListener: RefreshStateListener? = null
    private val startY = 0
    private var preEnable = false

    init {
        initBridge()
    }

    private fun initBridge() {
        jsBridge = NativeJsBridge(this)
        createModules().let { modules ->
            modules.forEach {
                jsBridge.registerModule(it)
            }
        }
        addJavascriptInterface(jsBridge, NativeJsBridge.BridgeName)
        setupWebView()
    }


    private fun createModules(): List<IBridgeModule> {
        viewModule = ViewModule(this, context = WeakReference(context), bridge = jsBridge);
        val list = ArrayList<IBridgeModule>();
        list.add(AccountModule(context = WeakReference(context), bridge = jsBridge))
        list.add(PayModule(context = WeakReference(context), bridge = jsBridge));
        list.add(CommonModel(context = WeakReference(context), bridge = jsBridge));
        list.add(viewModule!!);
        list.add(RouteModule(this, context = WeakReference(context), bridge = jsBridge));
        return list;
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setupWebView() {
        val ws = settings
        // 网页内容的宽度是否可大于WebView控件的宽度
        ws.loadWithOverviewMode = false
        // 保存表单数据
        ws.saveFormData = true
        // 是否应该支持使用其屏幕缩放控件和手势缩放
        ws.setSupportZoom(true)
        ws.builtInZoomControls = false
        ws.displayZoomControls = false
        // 启动应用缓存
        ws.setAppCacheEnabled(true)
        // 设置缓存模式
        ws.cacheMode = WebSettings.LOAD_DEFAULT
        // setDefaultZoom  api19被弃用
        // 设置此属性，可任意比例缩放。
        ws.useWideViewPort = true
        // 不缩放
        setInitialScale(100)
        // 告诉WebView启用JavaScript执行。默认的是false。
        ws.javaScriptEnabled = true
        //  页面加载好以后，再放开图片
        ws.blockNetworkImage = false
        // 使用localStorage则必须打开
        ws.domStorageEnabled = true
        // 排版适应屏幕
        ws.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        // WebView是否新窗口打开(加了后可能打不开网页)
        ws.setSupportMultipleWindows(true)
        // webview从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启。MIXED_CONTENT_ALWAYS_ALLOW
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.mixedContentMode = WebSettings.LOAD_NORMAL
        }
        ws.textZoom = 100
        this.webViewClient = NativeWebViewClient()
        setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        //debug 模式下面开启webView 调试功能
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (BuildConfig.DEBUG) {
                setWebContentsDebuggingEnabled(true)
            }
        }
    }

    @JvmName("setRefreshStateListener1")
    fun setRefreshStateListener(refreshStateListener: RefreshStateListener?) {
        this.refreshStateListener = refreshStateListener
    }

    private var mOnScrollChangedCallback: OnScrollChangedCallback? = null

    /**
     * [view] 是否可以横向滑动
     */
    private fun canScrollHorizontal(view: View): Boolean {
        return view.canScrollHorizontally(100) || view.canScrollHorizontally(-100)
    }

    private fun hasActivity(): Boolean {
        return null != controllerDelegate && null != controllerDelegate!!.dcActivity
    }

    /**
     * ViewModuleDelegate
     */
    override fun showToast(message: String) {
        if (!hasActivity()) {
            return
        }
        controllerDelegate!!.dcActivity.showToastShort(message)
    }

    override fun showLoadingDialog() {
        if (!hasActivity()) {
            return
        }
        controllerDelegate!!.dcActivity.showLoadingDialog()
    }

    override fun hideLoadingDialog() {
        if (!hasActivity()) {
            return
        }
        controllerDelegate!!.dcActivity.hideLoadingDialog()
    }

    override fun finish() {
        if (!hasActivity()) {
            return
        }
        controllerDelegate!!.dcActivity.finish()
    }

    override fun setRightTxt(text: String) {
        controllerDelegate!!.setRightTxt(text)
    }

    override fun getCityPicker(): CityPickerView {
        return controllerDelegate!!.cityPickerView
    }

    override fun showCityPickerView() {
        controllerDelegate!!.showCityPickerView()
    }

    /**
     * routeModuleDelegate
     */
    override fun route(url: String) {
        if (!hasActivity()) {
            return
        }
        controllerDelegate!!.dcActivity.DcRouter(url).start()
    }


    override fun onScrollChanged(
        l: Int, t: Int, oldl: Int,
        oldt: Int
    ) {
        super.onScrollChanged(l, t, oldl, oldt)
    }

    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
    override fun onScroll(
        view: AbsListView,
        firstVisibleItem: Int,
        visibleItemCount: Int,
        totalItemCount: Int
    ) {
    }

    override fun setTitleBar(needHidden: Boolean?, backBtnColor: String?) {
        controllerDelegate!!.setTitleBarLeft(needHidden, backBtnColor)
    }

    /**
     * Impliment in the activity/fragment/view that you want to listen to the webview
     */
    interface OnScrollChangedCallback {
        fun onScroll(dx: Int, dy: Int)
    }

    interface RefreshStateListener {
        fun refreshState(enable: Boolean)
    }

    fun onBackClickAction(callback: OnNativeActionCallback?) {
        if (viewModule != null) {
            viewModule!!.onBackClick(callback!!)
        }
    }

    fun onRightClick(callback: OnNativeActionCallback?) {
        if (viewModule != null) {
            viewModule!!.onRightClick(callback!!)
        }
    }

    override fun onResume() {
        if (viewModule != null) {
            viewModule!!.onResume()
        }
    }

    override fun onPause() {
        if (viewModule != null) {
            viewModule!!.onPause()
        }
    }

    fun onVipReceived(callback: OnNativeActionCallback?) {
        if (viewModule != null) {
            viewModule!!.onVipReceived(callback!!)
        }
    }
}