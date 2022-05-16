package cool.dingstock.appbase.webview

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.ValueCallback
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.lljjcoder.citywheel.CityConfig
import com.lljjcoder.style.citypickerview.CityPickerView
import com.sankuai.waimai.router.annotation.RouterUri
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import cool.dingstock.appbase.R
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.constant.WebviewConstant
import cool.dingstock.appbase.delegate.DCWebViewControllerDelegate
import cool.dingstock.appbase.entity.event.update.EventUserVipChange
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.activity.VMActivity
import cool.dingstock.appbase.util.DcUrlUtils
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.util.isDarkMode
import cool.dingstock.appbase.util.setSvgColor
import cool.dingstock.appbase.webview.DCWebView.OnScrollChangedCallback
import cool.dingstock.appbase.webview.DCWebView.RefreshStateListener
import cool.dingstock.appbase.webview.bridge.ActionBrideEvent
import cool.dingstock.appbase.webview.bridge.OnNativeActionCallback
import cool.dingstock.appbase.webview.overridedelegate.DHGameDelegate
import cool.dingstock.appbase.widget.IconTextView
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.lib_base.util.StringUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.net.MalformedURLException
import java.net.URL


@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [WebviewConstant.Path.INDEX, WebviewConstant.Path.INDEX1, WebviewConstant.Path.INDEX2]
)
open class DCWebViewActivity : VMActivity<BaseViewModel>(), DCWebViewControllerDelegate {
    var webView: DCWebView? = null
    lateinit var cLayoutTitleBar: LinearLayout
    lateinit var iconBack: AppCompatImageView
    lateinit var tvTitle: IconTextView
    lateinit var fakeStatusView: View
    lateinit var swipeRefreshLayout: SmartRefreshLayout
    lateinit var rightTxtTv: TextView
    var mPicker: CityPickerView? = null
    var pageIsFullScreen = false
    private var url: String? = null
    private var mTitle: String? = ""
    private var forbiddenRefresh = false
    override fun moduleTag(): String {
        return "webview"
    }

    override fun getLayoutId(): Int {
        return R.layout.webview_activity_index
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        webView = findViewById(R.id.webView_wv)
        cLayoutTitleBar = findViewById(R.id.layout_title_bar)
        iconBack = findViewById(R.id.webview_iv_back)
        tvTitle = findViewById(R.id.webview_tv_title)
        fakeStatusView = findViewById(R.id.view_fake_status)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        rightTxtTv = findViewById(R.id.webview_tv_right)
        rightTxtTv.visibility = View.GONE
        showLoadingView()
        url = getQueryParameter(WebviewConstant.UriParam.URL)
        val urlFragment = uri.fragment
        mTitle = uri.getQueryParameter(WebviewConstant.UriParam.TITLE)
        forbiddenRefresh =
            uri.getBooleanQueryParameter(WebviewConstant.UriParam.NO_PULL_REFRESH, false)
        val isFullScreen = getQueryParameter(WebviewConstant.UriParam.IS_FULL_SCREEN)
        var titleBgColor = DcUrlUtils.getParam(url, WebviewConstant.UriParam.TITLE_BG_COLOR)
        var titleTxtColor = DcUrlUtils.getParam(url, WebviewConstant.UriParam.TXT_BG_COLOR)
        val backIconColor = getQueryParameter(WebviewConstant.UriParam.BACK_ICON_COLO)
        if (this.isDarkMode) {
            titleBgColor = "0E0E0E"
            titleTxtColor = "E6FFFFFF"
            iconBack.setSvgColor(R.drawable.ic_icon_nav_back, Color.parseColor("#FFFFFF"))
        } else {
            try {
                Color.parseColor("#$titleBgColor")
            } catch (e: Exception) {
                titleBgColor = "ffffff"
            }
            try {
                Color.parseColor("#$titleTxtColor")
            } catch (e: Exception) {
                titleTxtColor = "333333"
            }
            if (!StringUtils.isEmpty(backIconColor)) {
                try {
                    val color = Color.parseColor("#$backIconColor")
                    iconBack.setSvgColor(R.drawable.ic_icon_nav_back, color)
                } catch (e: Exception) {
                    iconBack.setSvgColor(R.drawable.ic_icon_nav_back, Color.parseColor("#000000"))
                }
            } else {
                iconBack.setSvgColor(R.drawable.ic_icon_nav_back, Color.parseColor("#000000"))
            }
        }
        titleBgColor = "#$titleBgColor"
        titleTxtColor = "#$titleTxtColor"
        if (TextUtils.isEmpty(url)) {
            Logger.e("WebView url is empty !!!")
            showToastLong("url is empty")
            finish()
            return
        }
        webView?.controllerDelegate = this
        // WebView
        if (!TextUtils.isEmpty(urlFragment)) {
            url = "$url#$urlFragment"
        }
        pageIsFullScreen = isFullScreen != null && isFullScreen == "true"
        url?.let { webView?.loadUrl(it) }
        tvTitle.text = if (!TextUtils.isEmpty(mTitle)) mTitle else ""
        if (pageIsFullScreen) {
            tvTitle.visibility = View.INVISIBLE
            fakeStatusView.visibility = View.VISIBLE
            val layoutParams = fakeStatusView.layoutParams
            layoutParams.height = SizeUtils.getStatusBarHeight(this)
            fakeStatusView.layoutParams = layoutParams
            cLayoutTitleBar.setBackgroundResource(R.color.transparent)
            fullScreen()
            //            initScroll();
        } else {
            StatusBarUtil.setColor(this, Color.parseColor(titleBgColor), 0)
            fakeStatusView.visibility = View.GONE
            tvTitle.visibility = View.VISIBLE
            cLayoutTitleBar.setBackgroundColor(Color.parseColor(titleBgColor))
            tvTitle.setTextColor(Color.parseColor(titleTxtColor))
            val layoutParams = swipeRefreshLayout.layoutParams as FrameLayout.LayoutParams
            layoutParams.topMargin = SizeUtils.getMeasuredHeight(cLayoutTitleBar)
            swipeRefreshLayout.layoutParams = layoutParams
        }
        swipeRefreshLayout.setOnRefreshListener(OnRefreshListener {
            webView?.let {
                it.clearCache(true)
                it.clearCache(false)
                it.reload()
            }
            swipeRefreshLayout.finishRefresh()
        })
        swipeRefreshLayout.isEnabled = !forbiddenRefresh
        webView?.setRefreshStateListener(object : RefreshStateListener {
            override fun refreshState(enable: Boolean) {
                swipeRefreshLayout.isEnabled = enable && !forbiddenRefresh
            }
        })
        webView?.webChromeClient = object : DCChromeClient() {
            fun openFileChooser(valueCallback: ValueCallback<Uri>, s: String, s1: String) {
                val intent = Intent()
                intent.action = Intent.ACTION_PICK
                mValueCallback = valueCallback
                intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                intent.type = "image/*"
                startActivityForResult(Intent.createChooser(intent, "选择图片"), CHOSE_IMG)
            }

            override fun onShowFileChooser(
                webView: WebView,
                valueCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                val intent = Intent()
                intent.action = Intent.ACTION_PICK
                mValuesCallback = valueCallback
                intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                intent.type = "image/*"
                startActivityForResult(Intent.createChooser(intent, "选择图片"), CHOSE_IMG)
                return true
            }
        }
        //设置重定向问题
        setWebViewOverride()
        EventBus.getDefault().register(this)
    }

    var mValueCallback: ValueCallback<Uri>? = null
    var mValuesCallback: ValueCallback<Array<Uri>>? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOSE_IMG) {
            if (resultCode == RESULT_OK && data != null) {
                val result = data.data
                if (mValuesCallback != null) {
                    mValuesCallback?.onReceiveValue(arrayOf(result!!))
                }
                if (mValueCallback != null) {
                    mValueCallback?.onReceiveValue(result)
                }
            } else {
                if (mValuesCallback != null) {
                    mValuesCallback?.onReceiveValue(null)
                }
                if (mValueCallback != null) {
                    mValueCallback?.onReceiveValue(null)
                }
            }
            mValuesCallback = null
            mValueCallback = null
        }
    }

    private fun setWebViewOverride() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val webViewClient = webView?.webViewClient
            if (webViewClient is DCWebViewClient) {
                webViewClient.registerDelegate(DHGameDelegate(this))
            }
        } else {
        }

    }

    @Deprecated("(用于实现滑动渐变, 但是ui觉得不够美观 ， 现在取消)")
    private fun initScroll() {
        val totalDistance = SizeUtils.dp2px(100f)
        webView?.setOnScrollChangedCallback(object : OnScrollChangedCallback {
            override fun onScroll(dx: Int, dy: Int) {
                when {
                    dy > totalDistance -> {
                        cLayoutTitleBar.setBackgroundColor(Color.argb(255, 255, 255, 255))
                        iconBack.alpha = 1.0f
                        tvTitle.visibility = View.VISIBLE
                    }
                    dy <= 0 -> {
                        tvTitle.visibility = View.INVISIBLE
                        cLayoutTitleBar.setBackgroundResource(R.color.transparent)
                    }
                    else -> {
                        val scale = dy.toFloat() / totalDistance
                        val alpha = (scale * 255).toInt()
                        tvTitle.alpha = alpha.toFloat()
                        cLayoutTitleBar.setBackgroundColor(Color.argb(alpha, 255, 255, 255))
                    }
                }
                Log.e("webview", dy.toString() + "")
            }
        })
    }

    private fun fullScreen() {
        StatusBarUtil.setTranslucentForImageView(this, 0, null)
        setSystemStatusBarMode()
    }

    override fun initListeners() {
        Logger.i("initListeners ")
        iconBack.setOnClickListener {
            if (webView != null) {
                if (webView?.canGoBack() == true) {
                    webView?.goBack()
                } else {
                    setWebViewBack()
                }
            } else {
                finish()
            }
        }
        rightTxtTv.setOnClickListener {
            if (webView != null) {
                webView?.onRightClick(object : OnNativeActionCallback {
                    override fun onCallback(actionEvent: ActionBrideEvent) {}
                })
            }
        }
    }

    private fun setWebViewBack() {
        webView?.onBackClickAction(object : OnNativeActionCallback {
            override fun onCallback(actionEvent: ActionBrideEvent) {
                try {
                    if (java.lang.Boolean.parseBoolean(actionEvent.result.toString())) {
                        return
                    }
                } catch (e: Exception) {
                }
                finish()
            }
        })
    }

    override fun onDetachedFromWindow() {
        releaseWebView()
        super.onDetachedFromWindow()
    }

    override fun onDestroy() {
        releaseWebView()
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    private fun releaseWebView() {
        webView?.let {
            it.release()
            val parent = webView!!.parent
            if (parent != null) {
                (parent as ViewGroup).removeView(webView)
            }
            it.stopLoading()
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            it.settings.javaScriptEnabled = false
            it.clearHistory()
            it.clearView()
            it.removeAllViews()
            try {
                it.destroy()
            } catch (ex: Throwable) {
            } finally {
                webView = null
            }
        }
    }

    //如果有上一级url返回到上一级url
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView != null && webView!!.canGoBack()) {
            webView?.goBack() // 返回前一个页面
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun showErrorView() {
        super.showErrorView()
        setOnErrorViewClick { v: View? ->
            hideErrorView()
            showLoadingView()
            webView?.reload()
        }
    }

    val titleBarTile: CharSequence?
        get() {
            if (null == tvTitle) {
                Logger.w("The titlebar is empty.")
                return null
            }
            return tvTitle!!.text.toString()
        }

    fun titleBarVisibility(visibility: Boolean) {
        runOnUiThread {
            if (null == tvTitle) {
                Logger.w("The titlebar is empty.")
                return@runOnUiThread
            }
            tvTitle!!.visibility = if (visibility) View.VISIBLE else View.GONE
        }
    }

    override fun setTitleBarTitle(title: String) {
        runOnUiThread {
            tvTitle?.text = title
        }
    }

    override fun setRightTxt(text: String) {
        runOnUiThread {
            rightTxtTv.text = text
            if (text != null && text != "") {
                rightTxtTv.visibility = View.VISIBLE
            } else {
                rightTxtTv.visibility = View.GONE
            }
        }
    }

    override fun getDCActivity() = this

    override fun getCityPickerView(): CityPickerView {
        val titleColor: String
        val titleBgColor: String
        val lineColor: String
        if (this.isDarkMode) {
            titleColor = "#E6FFFFFF"
            titleBgColor = "#1D1D1D"
            lineColor = "#0DFFFFFF"
        } else {
            titleColor = "#25262A"
            titleBgColor = "#FFFFFF"
            lineColor = "#EAECEF"
        }
        if (mPicker == null) {
            mPicker = CityPickerView()
            mPicker!!.init(this)
            val cityConfig = CityConfig.Builder()
                .setLineColor(lineColor)
                .title(" ") //标题
                .titleTextSize(20) //标题文字大小
                .titleTextColor(titleColor) //标题文字颜  色
                .titleBackgroundColor(titleBgColor) //标题栏背景色
                .confirTextColor(titleColor) //确认按钮文字颜色
                .confirmText("确认") //确认按钮文字
                .confirmTextSize(16) //确认按钮文字大小
                .cancelText("请选择地区") //取消按钮文字
                .cancelTextColor(titleColor)
                .cancelTextSize(20) //取消按钮文字大小
                .setCityWheelType(CityConfig.WheelType.PRO_CITY_DIS) //显示类，只显示省份一级，显示省市两级还是显示省市区三级
                .showBackground(true) //是否显示半透明背景
                .visibleItemsCount(4) //显示item的数量
                .province("四川省") //默认显示的省份
                .city("成都市") //默认显示省份下面的城市
                .district("武侯区") //默认显示省市下面的区县数据
                .provinceCyclic(true) //省份滚轮是否可以循环滚动
                .cityCyclic(true) //城市滚轮是否可以循环滚动
                .districtCyclic(true) //区县滚轮是否循环滚动
                .setCustomItemLayout(R.layout.account_choose_address_item) //自定义item的布局
                .setCustomItemTextViewId(R.id.item_city_name_tv) //自定义item布局里面的textViewid
                .drawShadows(true) //滚轮不显示模糊效果
                .setLineHeigh(0) //中间横线的高度
                .setShowGAT(true) //是否显示港澳台数据，默认不显示
                .build()
            mPicker!!.setConfig(cityConfig)
        }
        return mPicker!!
    }

    override fun showCityPickerView() {
        runOnUiThread {
            mPicker?.showCityPicker()
        }
    }

    override fun setTitleBarLeft(needHidden: Boolean?, backBtnColor: String?) {
        if (needHidden == true) {
            iconBack.visibility = View.GONE
        }
        backBtnColor?.let {
            iconBack.setSvgColor(R.drawable.ic_icon_nav_back, Color.parseColor(it))
        }
    }

    override fun onWebViewLoadingFinish() {}
    fun errorView(visibility: Boolean, retryUrl: String?) {
        runOnUiThread {
            if (visibility) {
                showErrorView()
            } else {
                hideErrorView()
            }
        }
    }

    // TODO get Uri 要重写
    fun getQueryParameter(key: String?): String? {
        val query = uri.encodedQuery ?: return null
        val encodedKey = Uri.encode(key, null)
        val length = query.length
        var start = 0
        do {
            val nextAmpersand = query.indexOf('&', start)
            val end = if (nextAmpersand != -1) nextAmpersand else length
            var separator = query.indexOf('=', start)
            if (separator > end || separator == -1) {
                separator = end
            }
            if (separator - start == encodedKey.length
                && query.regionMatches(start, encodedKey, 0, encodedKey.length)
            ) {
                return if (separator == end) {
                    ""
                } else {
                    isDecodeUrl(query.substring(separator + 1, end))
                }
            }

            // Move start to end of name.
            start = if (nextAmpersand != -1) {
                nextAmpersand + 1
            } else {
                break
            }
        } while (true)
        return null
    }

    // TODO get Uri 要重写
    fun getQueryParameter(uri: Uri, key: String?): String? {
        val query = uri.encodedQuery ?: return null
        val encodedKey = Uri.encode(key, null)
        val length = query.length
        var start = 0
        do {
            val nextAmpersand = query.indexOf('&', start)
            val end = if (nextAmpersand != -1) nextAmpersand else length
            var separator = query.indexOf('=', start)
            if (separator > end || separator == -1) {
                separator = end
            }
            if (separator - start == encodedKey.length
                && query.regionMatches(start, encodedKey, 0, encodedKey.length)
            ) {
                return if (separator == end) {
                    ""
                } else {
                    isDecodeUrl(query.substring(separator + 1, end))
                }
            }

            // Move start to end of name.
            start = if (nextAmpersand != -1) {
                nextAmpersand + 1
            } else {
                break
            }
        } while (true)
        return null
    }

    private fun isDecodeUrl(urlString: String): String {
        return try {
            URL(urlString)
            Logger.d("This url: ", urlString, " is valid, so return this.")
            urlString
        } catch (e: MalformedURLException) {
            val decodeUrl = Uri.decode(urlString)
            Logger.d("This url: ", urlString, " is invalid, so decode this: ", decodeUrl)
            decodeUrl
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun vipReceived(eventUserVipChange: EventUserVipChange?) {
        webView!!.onVipReceived(object : OnNativeActionCallback {
            override fun onCallback(actionEvent: ActionBrideEvent) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        webView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView!!.onPause()
    }

    companion object {
        private const val CHOSE_IMG = 0x1010101
    }
}