package cool.dingstock.appbase.webview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import com.lljjcoder.style.citypickerview.CityPickerView
import cool.dingstock.appbase.BuildConfig
import cool.dingstock.appbase.R
import cool.dingstock.appbase.delegate.DCWebViewControllerDelegate
import cool.dingstock.appbase.webview.bridge.IBridgeModule
import cool.dingstock.appbase.webview.bridge.OnNativeActionCallback
import cool.dingstock.appbase.webview.bridge.x5.XWebView
import cool.dingstock.appbase.webview.delegate.RouteModuleDelegate
import cool.dingstock.appbase.webview.delegate.ViewModuleDelegate
import cool.dingstock.appbase.webview.module.*
import java.lang.ref.WeakReference

class DCWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int=0
) : XWebView(
    context, attrs
), ViewModuleDelegate,
    RouteModuleDelegate,
    AbsListView.OnScrollListener {
    var viewModule: ViewModule? = null
    var controllerDelegate: DCWebViewControllerDelegate? = null
    var refreshStateListener: RefreshStateListener? = null
    private val startY = 0
    private var preEnable = false

    override fun createModules(): List<IBridgeModule> {
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
    override fun setupWebView() {
        val ws = settings
        with(ws) {
            // 网页内容的宽度是否可大于WebView控件的宽度
            loadWithOverviewMode = false
            // 保存表单数据
            saveFormData = true
            // 是否应该支持使用其屏幕缩放控件和手势缩放
            setSupportZoom(true)
            builtInZoomControls = false
            displayZoomControls = false
            // 启动应用缓存
            setAppCacheEnabled(true)
            // 设置缓存模式
            cacheMode = WebSettings.LOAD_DEFAULT
            // setDefaultZoom  api19被弃用
            // 设置此属性，可任意比例缩放。
            useWideViewPort = true
            // 不缩放
            setInitialScale(100)
            javaScriptEnabled = true
            //  页面加载好以后，再放开图片
            blockNetworkImage = false
            // 使用localStorage则必须打开
            domStorageEnabled = true
            // 排版适应屏幕
            layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
            // WebView是否新窗口打开(加了后可能打不开网页)
            setSupportMultipleWindows(true)
            // webview从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启。MIXED_CONTENT_ALWAYS_ALLOW
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mixedContentMode = WebSettings.LOAD_NORMAL
            }
            textZoom = 100
        }
        this.requestFocus(View.FOCUS_DOWN)
        this.requestFocusFromTouch()
        this.isEnabled = true
        this.webViewClient = DCWebViewClient()
//        this.webViewClientExtension = X5WebViewEventHandler(this)
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

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        if (clampedX) {
            if (scrollableParent == null) {
                scrollableParent = findViewParentIfNeeds(this, 10)
            }
            if (null != scrollableParent) {
                scrollableParent!!.requestDisallowInterceptTouchEvent(false)
            }
        }
        preEnable = preEnable && scrollY == 0
        if (refreshStateListener != null) {
//            refreshStateListener.refreshState(clampedY&&startY==0);
            refreshStateListener!!.refreshState(clampedY && preEnable)
        }
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
    }
//    fun tbs_onOverScrolled(
//        scrollX: Int, scrollY: Int,
//        clampedX: Boolean, clampedY: Boolean, view: View?
//    ) {
//        if (clampedX) {
//            if (scrollableParent == null) {
//                scrollableParent = findViewParentIfNeeds(this, 10)
//            }
//            if (null != scrollableParent) {
//                scrollableParent!!.requestDisallowInterceptTouchEvent(false)
//            }
//        }
//        preEnable = preEnable && scrollY == 0
//        if (refreshStateListener != null) {
////            refreshStateListener.refreshState(clampedY&&startY==0);
//            refreshStateListener!!.refreshState(clampedY && preEnable)
//        }
//        super_onOverScrolled(scrollX, scrollY, clampedX, clampedY)
//    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            if (scrollableParent == null) {
                scrollableParent = findViewParentIfNeeds(this, 10)
            }
            if (null != scrollableParent) {
                scrollableParent!!.requestDisallowInterceptTouchEvent(true)
            }
        }
        if (refreshStateListener != null && event?.action == MotionEvent.ACTION_DOWN) {
            preEnable = true
            refreshStateListener?.refreshState(false)
        }
        return super.onTouchEvent(event)
    }

    override fun computeScroll() {
        super.computeScroll()
    }

    override fun overScrollBy(
        deltaX: Int,
        deltaY: Int,
        scrollX: Int,
        scrollY: Int,
        scrollRangeX: Int,
        scrollRangeY: Int,
        maxOverScrollX: Int,
        maxOverScrollY: Int,
        isTouchEvent: Boolean
    ): Boolean {
        return super.overScrollBy(
            deltaX,
            deltaY,
            scrollX,
            scrollY,
            scrollRangeX,
            scrollRangeY,
            maxOverScrollX,
            maxOverScrollY,
            isTouchEvent
        )
    }

    private var scrollableParent: ViewGroup? = null
    private fun findViewParentIfNeeds(tag: View, depth: Int): ViewGroup? {
        if (depth < 0) {
            return null
        }
        val parent = tag.parent ?: return null
        return if (parent is ViewGroup) {
            if (canScrollHorizontal(parent)) {
                parent
            } else {
                findViewParentIfNeeds(parent, depth - 1)
            }
        } else null
    }

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
        controllerDelegate?.dcActivity?.showToastShort(message)
    }

    override fun showLoadingDialog() {
        if (!hasActivity()) {
            return
        }
        controllerDelegate?.dcActivity?.showLoadingDialog()
    }

    override fun hideLoadingDialog() {
        if (!hasActivity()) {
            return
        }
        controllerDelegate?.dcActivity?.hideLoadingDialog()
    }

    override fun finish() {
        if (!hasActivity()) {
            return
        }
        controllerDelegate!!.dcActivity.finish()
    }

    override fun setRightTxt(text: String) {
        controllerDelegate?.setRightTxt(text)
    }

    override fun getCityPicker(): CityPickerView {
        return controllerDelegate!!.cityPickerView
    }

    override fun showCityPickerView() {
        controllerDelegate?.showCityPickerView()
    }

    /**
     * routeModuleDelegate
     */
    override fun route(url: String) {
        if (!hasActivity()) {
            return
        }
        controllerDelegate?.dcActivity?.DcRouter(url)?.start()
    }

    override fun setOnScrollChangeListener(l: OnScrollChangeListener?) {
        super.setOnScrollChangeListener(l)
    }


    fun setOnScrollChangedCallback(
        onScrollChangedCallback: OnScrollChangedCallback?
    ) {
        mOnScrollChangedCallback = onScrollChangedCallback
    }

    override fun onScrollChanged(
        l: Int, t: Int, oldl: Int,
        oldt: Int
    ) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (mOnScrollChangedCallback != null) {
            mOnScrollChangedCallback?.onScroll(l - oldl, t)
        }
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
        controllerDelegate?.setTitleBarLeft(needHidden, backBtnColor)
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
            viewModule?.onBackClick(callback!!)
        }
    }

    fun onRightClick(callback: OnNativeActionCallback?) {
        if (viewModule != null) {
            viewModule?.onRightClick(callback!!)
        }
    }

    override fun onResume() {
        if (viewModule != null) {
            viewModule?.onResume()
        }
    }

    override fun onPause() {
        if (viewModule != null) {
            viewModule?.onPause()
        }
    }

    fun onVipReceived(callback: OnNativeActionCallback?) {
        if (viewModule != null) {
            viewModule?.onVipReceived(callback!!)
        }
    }
}