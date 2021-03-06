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
        // ????????????????????????????????????WebView???????????????
        ws.loadWithOverviewMode = false
        // ??????????????????
        ws.saveFormData = true
        // ????????????????????????????????????????????????????????????
        ws.setSupportZoom(true)
        ws.builtInZoomControls = false
        ws.displayZoomControls = false
        // ??????????????????
        ws.setAppCacheEnabled(true)
        // ??????????????????
        ws.cacheMode = WebSettings.LOAD_DEFAULT
        // setDefaultZoom  api19?????????
        // ??????????????????????????????????????????
        ws.useWideViewPort = true
        // ?????????
        setInitialScale(100)
        // ??????WebView??????JavaScript?????????????????????false???
        ws.javaScriptEnabled = true
        //  ???????????????????????????????????????
        ws.blockNetworkImage = false
        // ??????localStorage???????????????
        ws.domStorageEnabled = true
        // ??????????????????
        ws.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        // WebView?????????????????????(??????????????????????????????)
        ws.setSupportMultipleWindows(true)
        // webview???5.0?????????????????????????????????,https???????????????http??????,?????????????????????MIXED_CONTENT_ALWAYS_ALLOW
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.mixedContentMode = WebSettings.LOAD_NORMAL
        }
        ws.textZoom = 100
        this.webViewClient = NativeWebViewClient()
        setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        //debug ??????????????????webView ????????????
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
     * [view] ????????????????????????
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