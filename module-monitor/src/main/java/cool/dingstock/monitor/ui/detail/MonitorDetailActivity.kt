package cool.dingstock.monitor.ui.detail

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.*
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.constant.*
import cool.dingstock.appbase.entity.bean.home.bp.ClueProductBean
import cool.dingstock.appbase.entity.bean.monitor.ChannelInfoEntity
import cool.dingstock.appbase.entity.bean.monitor.MonitorProductBean
import cool.dingstock.appbase.entity.event.im.EventBlockGoods
import cool.dingstock.appbase.entity.event.monitor.*
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.refresh.DcWhiteRefreshHeader
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.*
import cool.dingstock.appbase.widget.CommonEmptyView
import cool.dingstock.appbase.widget.commondialog.CommonDialog
import cool.dingstock.lib_base.stroage.ConfigSPHelper
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.monitor.R
import cool.dingstock.monitor.adapter.item.MonitorCouponItemBinder
import cool.dingstock.monitor.adapter.item.MonitorProductItemBinder
import cool.dingstock.monitor.databinding.ActivityMonitorDetailBinding
import cool.dingstock.monitor.dialog.MonitorTypeSelectDialog
import cool.dingstock.uicommon.calendar.dialog.ComparisonPriceDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference
import java.lang.reflect.Field
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.min

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [MonitorConstant.Path.MONITOR_DETAIL]
)
class MonitorDetailActivity :
    VMBindingActivity<MonitorDetailViewModel, ActivityMonitorDetailBinding>() {
    var timeHandler: TimeHandler? = null
    private var timer: Timer? = null
    private var createAt: Long = 0L
    private var timerTask: TimerTask? = null

    //用于暂停定时器
    var pausedTimer: Boolean = false
    private var isSubscribe: Boolean = false
    private var field: Field? = null
    var channelId = ""
    private var isHideOnlineShieldDialog = false
    private var isHideOfflineShieldDialog = false
    private val productAdapter by lazy {
        DcBaseBinderAdapter(arrayListOf())
    }

    private val productItemBinder by lazy {
        MonitorProductItemBinder()
    }

    private val couponItemBinder by lazy {
        MonitorCouponItemBinder()
    }

    private val comparisonPriceDialog: ComparisonPriceDialog by lazy {
        ComparisonPriceDialog()
    }

    private val monitorTypeSelectDialog by lazy {
        MonitorTypeSelectDialog(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        StatusBarUtil.setDarkMode(this)
        isHideOnlineShieldDialog = ConfigSPHelper.getInstance()
            .getBoolean(MonitorConstant.DataParam.MONITOR_SHIELD_HINT_ENABLE)
        isHideOfflineShieldDialog = ConfigSPHelper.getInstance()
            .getBoolean(MonitorConstant.DataParam.OFFLINE_MONITOR_SHIELD_HINT_ENABLE)
        initBundleData()
        initStatusBar()
        timeHandler = TimeHandler(this)
        showLoadingView()
        viewModel.refresh()
        initView()
        initRvAdapter()
        finishRefresh()
        hideLoadingView()
        updateUI()
        initScroll()
        viewBinding.refreshLayout.apply {
            setRefreshHeader(DcWhiteRefreshHeader(context))
            setOnRefreshListener {
                viewModel.refresh()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateChannel(ruleSetting: EventMonitorRuleSetting) {
        viewModel.refresh()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateMonitorCities(event: EventRefreshMonitorOfflineCities) {
        viewModel.refresh()
    }

    override fun moduleTag(): String {
        return ModuleConstant.MONITOR
    }

    @SuppressLint("SoonBlockedPrivateApi")
    private fun fixTask() {
        try {
            field = TimerTask::class.java.getDeclaredField("state")
            field?.isAccessible = true
            field?.set(timerTask, 0)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        timerTask?.cancel()
        timer = null
        timerTask = null
        timeHandler = null
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    private fun initStatusBar() {
        StatusBarUtil.setTranslucentForImageView(this, 0, null)
        StatusBarUtil.setDarkMode(this)
    }

    private fun initScroll() {
        val totalDistance = SizeUtils.dp2px(100f)
        val topBarColor = getCompatColor(R.color.white)
        val iconBack = getCompatColor(R.color.color_text_black1)
        if (isWhiteMode()) {
            viewBinding.titleBar.setLeftIconColorRes(R.color.white)
        }
        if (isWhiteMode()) {
            viewBinding.monitorDetailBg.hide(false)
        }
        viewBinding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val scrollY = verticalOffset.absoluteValue
            val scale = scrollY.toFloat() / totalDistance
            val alpha = (scale * 255).toInt()
            val alphaColor =
                Color.argb(
                    min(alpha, 255),
                    topBarColor.red,
                    topBarColor.green,
                    topBarColor.blue
                )
            val backColor = Color.argb(
                min(alpha, 255),
                iconBack.red,
                iconBack.green,
                iconBack.blue
            )
            Logger.d("Monitor Detail alpha ${alpha} scrolly ${scrollY}")
            viewBinding.layoutTop.setBackgroundColor(alphaColor)
            if (isWhiteMode()) {
                viewBinding.titleBar.setLeftIconColor(backColor)
            }
            when {
                scale > 1 -> {
                    if (isWhiteMode()) {
                        viewBinding.monitorDetailBg.hide(true)
                        StatusBarUtil.setLightMode(this@MonitorDetailActivity)
                        viewBinding.refreshLayout.isEnabled = false
                        viewBinding.layoutMonitorUnsubBg.alpha = 0f
                        viewBinding.layoutMonitorUnsubTv.setTextColor(getCompatColor(R.color.color_text_black1))
                        viewBinding.titleBar.setTitleAlpha(1f)
                        viewBinding.titleBar.setLeftIconColorRes(R.color.color_text_black1)
                        viewBinding.ivIsSlient.setImageResource(R.drawable.selected_monitor_shield_dark)
                        viewBinding.monitorNoticeBg.setBackgroundColor(
                            getCompatColor(
                                R.color.transparent
                            )
                        )
                    }
                }
                scale <= 0 -> {
                    viewBinding.titleBar.setLeftIconColorRes(R.color.color_text_absolutely_white)
                    if (isWhiteMode()) {
                        viewBinding.refreshLayout.isEnabled = true
                        viewBinding.layoutMonitorUnsubBg.alpha = 1f
                        viewBinding.layoutMonitorUnsubTv.setTextColor(
                            getCompatColor(R.color.color_text_absolutely_white)
                        )
                        viewBinding.ivIsSlient.setImageResource(R.drawable.selected_monitor_shield_blue)
                        viewBinding.titleBar.setTitleAlpha(0f)
                        viewBinding.monitorNoticeBg.setBackgroundResource(R.drawable.monitor_shape_translucent)
                    }
                }
                else -> {
                    if (isWhiteMode()) {
                        viewBinding.titleBar.setLeftIconColorInt(alphaColor)
                        viewBinding.layoutMonitorUnsubBg.alpha = 1f - scale
                        viewBinding.monitorNoticeBg.alpha = 1f - scale
                        viewBinding.titleBar.setTitleAlpha(scale)
                        viewBinding.layoutMonitorUnsubTv.setTextColor(
                            ColorUtils.getColorChanges(
                                getCompatColor(R.color.color_text_black1),
                                getCompatColor(R.color.white),
                                1 - scale
                            )
                        )
                    }
                }
            }
//            when {
//                scrollY > totalDistance -> {
//                    if (isWhiteMode()) {
//                        viewBinding.monitorDetailBg.hide(true)
//                        StatusBarUtil.setLightMode(this@MonitorDetailActivity)
//                        viewBinding.refreshLayout.isEnabled = false
//                        viewBinding.layoutMonitorUnsubBg.alpha = 0f
//                        viewBinding.layoutMonitorUnsubTv.setTextColor(
//                            ContextCompat.getColor(
//                                context,
//                                R.color.color_text_black1
//                            )
//                        )
//                        viewBinding.titleBar.setTitleAlpha(1f)
//                        viewBinding.layoutTop.setBackgroundColor(whiteColor)
//                        viewBinding.titleBar.setLeftIconColorRes(R.color.color_text_black1)
//                        viewBinding.ivIsSlient.setImageResource(R.drawable.selected_monitor_shield_dark)
//                        viewBinding.layoutRightIcon.setBackgroundColor(
//                            ContextCompat.getColor(
//                                context,
//                                R.color.transparent
//                            )
//                        )
//                    } else {
//                        with(viewBinding) {
//                            titleBar.apply {
//                                setLeftIconColorRes(R.color.color_text_black1)
//                                setTitleColor(R.color.color_text_black1)
//                                setTitleAlpha(1f)
//                                setBackgroundColor(resources.getColor(R.color.color_gray))
//                            }
//                        }
//                    }
//                }
//                scrollY <= 0 -> {
//                    if (isWhiteMode()) {
//                        viewBinding.monitorDetailBg.hide(false)
//                    }
//                    StatusBarUtil.setDarkMode(this@MonitorDetailActivity)
//                    viewBinding.refreshLayout.isEnabled = true
//                    viewBinding.layoutMonitorUnsubBg.alpha = 1f
//                    viewBinding.layoutMonitorUnsubTv.setTextColor(
//                        ContextCompat.getColor(
//                            context,
//                            R.color.color_text_black1
//                        )
//                    )
//                    viewBinding.ivIsSlient.setImageResource(R.drawable.selected_monitor_shield_blue)
//                    viewBinding.titleBar.setTitleAlpha(0f)
//                    viewBinding.layoutTop.setBackgroundResource(R.color.transparent)
//                    viewBinding.titleBar.setLeftIconColorRes(R.color.color_text_black1)
//                    viewBinding.layoutRightIcon.setBackgroundResource(R.drawable.shape_monitor_notice)
//                }
//                else -> {
//                    viewBinding.monitorDetailBg.hide(true)
//                    viewBinding.refreshLayout.isEnabled = false
//                    val scale = scrollY.toFloat() / totalDistance
//                    val alpha = (scale * 255).toInt()
//                    val bgColor = resources.getColor(R.color.color_gray)
//                    val alphaColor = Color.argb(alpha, bgColor.red, bgColor.green, bgColor.blue)
//                    viewBinding.layoutTop.setBackgroundColor(alphaColor)
//                    viewBinding.titleBar.setBackgroundColor(alphaColor)
//                    if (isWhiteMode()) {
//                        viewBinding.titleBar.setLeftIconColorInt(alphaColor)
//                        viewBinding.layoutMonitorUnsubBg.alpha = 1f - scale
//                        viewBinding.titleBar.setTitleAlpha(scale)
//                        viewBinding.layoutMonitorUnsubTv.setTextColor(
//                            ColorUtils.getColorChanges(
//                                ContextCompat.getColor(context, R.color.color_text_black1),
//                                ContextCompat.getColor(context, R.color.white),
//                                1 - scale
//                            )
//                        )
//                    }
//                }
//            }
        })
    }

    override fun fakeStatusView(): View? {
        return viewBinding.fakeLayout.fakeStatusBar
    }

    private fun updateUI() {
        productAdapter.loadMoreModule.apply {
            isAutoLoadMore = true
            isEnableLoadMoreIfNotFullPage = true
            preLoadNumber = 5
            setOnLoadMoreListener {
                viewModel.loadMore()
            }
        }
        viewModel.apply {
            //更新列表数据
            couponListLiveData.observe(this@MonitorDetailActivity) {
                hideLoadingView()
                finishRefresh()
                productAdapter.setList(it)
            }
            //更新加载更多时的数据
            couponLoadMoreMonitorLiveData.observe(this@MonitorDetailActivity) {
                finishRefresh()
                if (it.isNullOrEmpty()) {
                    finishLoadMore()
                } else {
                    productAdapter.addData(it)
                    productAdapter.loadMoreModule.loadMoreComplete()
                }
            }
            //更新列表数据
            listLiveData.observe(this@MonitorDetailActivity) {
                hideLoadingView()
                finishRefresh()
                productAdapter.setList(it)
            }
            //更新加载更多时的数据
            loadMoreMonitorLiveData.observe(this@MonitorDetailActivity) {
                finishRefresh()
                if (it.isNullOrEmpty()) {
                    finishLoadMore()
                } else {
                    productAdapter.addData(it)
                    productAdapter.loadMoreModule.loadMoreComplete()
                }
            }
            //设置顶部监控次数数据
            channelInfoLiveData.observe(this@MonitorDetailActivity) {
                finishRefresh()
                hideLoadingView()
                setChannelInfo(it)
            }
            monitorToastLiveData.observe(this@MonitorDetailActivity) {
                ToastUtil.getInstance()
                    .makeTextAndShow(this@MonitorDetailActivity, it, Toast.LENGTH_SHORT)
            }
            routeData.observe(this@MonitorDetailActivity) {
                DcRouter(it).start()
                val user = AccountHelper.getInstance().user
                UTHelper.payPage(if (user?.vipValidity == null) "normal" else "vip")
            }
            dismissDialog.observe(this@MonitorDetailActivity) {
                if (it) {
                    hideLoadingDialog()
                } else {
                    hideLoadingDialog()
                }
            }
            priceListLiveData.observe(this@MonitorDetailActivity) {
                comparisonPriceDialog.setData(it)
                comparisonPriceDialog.show(supportFragmentManager, "comparisonPriceDialog")
            }
        }
    }


    private fun finishLoadMore() {
        productAdapter.loadMoreModule.apply {
            loadMoreComplete()
            loadMoreEnd(false)
        }
    }

    private fun finishRefresh() {
        viewBinding.refreshLayout.finishRefresh()
    }

    private fun getData(): Map<String, String> {
        val duration = System.currentTimeMillis() - createAt
        val days = duration / (1000 * 60 * 60 * 24)
        val hours = duration % (1000 * 60 * 60 * 24) / (1000 * 60 * 60)
        val minutes = duration % (1000 * 60 * 60) / (1000 * 60)
        val seconds = duration % (1000 * 60) / 100
        val perSecond = seconds / 10.0
        return mapOf("hours" to "$hours", "minutes" to "$minutes", "perSecond" to "$perSecond")
    }

    private fun setDuration() {
        val date = getData()
        if (isSubscribe) {
            val sb = SpannableStringBuilder()
            fun append(sb: SpannableStringBuilder, key: String, unit: String) {
                val spHours = SpannableString(" ${date[key]}")
                spHours.setSpan(
                    RelativeSizeSpan(1.4f),
                    0,
                    spHours.length,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                )
                spHours.setSpan(
                    StyleSpan(android.graphics.Typeface.BOLD),
                    0,
                    spHours.length,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                )
                sb.append(spHours)
                sb.append(unit)
            }
            sb.append("已监控")
            append(sb, "hours", "小时")
            append(sb, "minutes", "分")
            append(sb, "perSecond", "秒")
            viewBinding.tvMonitorContinueDur.text = sb
        }
    }

    private fun setChannelInfo(channelInfo: ChannelInfoEntity?) {
        hideLoadingView()
        channelInfo?.let {

            val isHideMonitorCity = it.customCitiesCountStr.isNullOrEmpty()
            val isHideMonitorType = it.customCateCountStr.isNullOrEmpty()

            monitorTypeSelectDialog.setFilter(it.customCate)

            viewBinding.apply {
                llMonitorCities.hide(isHideMonitorCity)
                if (!isHideMonitorCity) {
                    tvMonitorCities.text = it.customCitiesCountStr
                }
                llMonitorType.hide(isHideMonitorType)
                if (!isHideMonitorType) {
                    tvMonitorType.text = it.customCateCountStr
                }
            }

            viewBinding.ivChannelIcon.load(it.iconUrl)
            if (TextUtils.isEmpty(channelInfo.desc)) {
                viewBinding.tvMonitorDescribe.hide(true)
            } else {
                viewBinding.tvMonitorDescribe.hide(false)
            }
            viewBinding.tvMonitorDescribe.text = channelInfo.desc ?: ""
            createAt = it.subscribedAt
            isSubscribe = false
            updateMonitorState(it.subscribed)
            executeAnim(channelInfo.subscribed)
            if (channelInfo.subscribed) {
                executeTimer()
                setSubjectMonitor(channelInfo.feedCount)
            } else {
                setUnSubjectMonitor(channelInfo.feedCount)
            }
            viewBinding.layoutMonitorUnsubTv.setOnClickListener {
                val user = getAccount() ?: return@setOnClickListener
                UTHelper.commonEvent(
                    UTConstant.Monitor.ChannelP_click_MonitorButton,
                    "ChannelName",
                    channelInfo.name
                )

                viewModel.switchMonitorSilent(this, channelInfo.id, false, false)
                channelInfo.isSilent = false
                viewBinding.ivIsSlient.isSelected = channelInfo.isSilent

                viewBinding.clMonitorConfig.hide(true)
                viewBinding.tvMaintain.hide(true)
                viewBinding.tvMonitorChannelName.text = channelInfo.name
                if (channelInfo.restricted && LoginUtils.getCurrentUser()?.isVip() == false) {
                    DcUriRequest(this, MonitorConstant.Uri.MONITOR_DIALOG_VIP)
                        .dialogCenterAni()
                        .putUriParameter(CommonConstant.UriParams.SOURCE, "频道主页监控按钮")
                        .start()
                    return@setOnClickListener
                }
                viewModel.switchMonitorState(channelInfo.id, !channelInfo.subscribed, success = {
                    EventBus.getDefault().post(EventRefreshMonitorPage(MonitorEventSource.DETAIL))
                    EventBus.getDefault()
                        .post(EventChangeMonitorState(channelInfo.id, channelInfo.subscribed, null))
                })
                if (channelInfo.subscribed) {
                    setUnSubjectMonitor(channelInfo.feedCount)
                }
                channelInfo.subscribed = !channelInfo.subscribed
                setNoticeState(channelInfo)
            }
            viewBinding.layoutMonitorSuspensionTv.setOnClickListener {
                if (channelInfo.restricted && LoginUtils.getCurrentUser()?.isVip() == false) {
                    DcUriRequest(this, MonitorConstant.Uri.MONITOR_DIALOG_VIP)
                        .putUriParameter(CommonConstant.UriParams.SOURCE, "频道主页监控按钮")
                        .dialogCenterAni()
                        .start()
                    return@setOnClickListener
                }
                viewModel.switchMonitorState(channelInfo.id, !channelInfo.subscribed, success = {
                    EventBus.getDefault().post(EventRefreshMonitorPage(MonitorEventSource.DETAIL))
                    EventBus.getDefault()
                        .post(EventChangeMonitorState(channelInfo.id, channelInfo.subscribed, null))
                })
                if (!channelInfo.subscribed) {
                    createAt = System.currentTimeMillis()
                    setSubjectMonitor(channelInfo.feedCount)
                }
                channelInfo.subscribed = !channelInfo.subscribed
                viewBinding.clMonitorConfig.hide(false)
                setNoticeState(channelInfo)
            }
            setNoticeState(it)
        }
    }

    private fun setNoticeState(entity: ChannelInfoEntity) {
        val currentUser = LoginUtils.getCurrentUser()
        currentUser?.let {
            viewBinding.layoutRightIcon.visibility =
                if (it.isVip() && entity.subscribed) View.VISIBLE else View.GONE
        }
        viewBinding.ivIsSlient.isSelected = entity.isSilent
        viewBinding.layoutRightIcon.setOnClickListener {
            UTHelper.commonEvent(UTConstant.Monitor.ChannelP_click_Bells)
            viewModel.switchMonitorSilent(this, entity.id, !entity.isSilent, true)
            entity.isSilent = !entity.isSilent
            viewBinding.ivIsSlient.isSelected = entity.isSilent
        }

        viewBinding.clMonitorConfig.hide(!entity.subscribed)
        if (entity.customRuleEffective) {
            viewBinding.tvRules.text = "自定义规则·生效中"
        } else {
            viewBinding.tvRules.text = "自定义规则"
        }

        if (entity.subscribed) {//已监控
            if (!entity.maintaining) {
                viewBinding.tvMaintain.hide(true)
                viewBinding.tvMonitorChannelName.text = entity.name
            } else {
                viewBinding.tvMaintain.hide(false)
                dealMonitorTitle(entity)
            }
        } else {
            viewBinding.tvMaintain.hide(true)
            viewBinding.tvMonitorChannelName.text = entity.name
        }
    }

    private fun dealMonitorTitle(entity: ChannelInfoEntity) {
        val title = "维护中".plus("  ").plus(entity.name)
        val len = title.length
        val labelLength = "维护中".length
        val styleTitle = SpannableStringBuilder(title)
        styleTitle.setSpan(
            TextAppearanceSpan(context, R.style.mText_style3),
            0,
            labelLength,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        styleTitle.setSpan(
            TextAppearanceSpan(context, R.style.mText_style4),
            labelLength + 1,
            len,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        viewBinding.tvMonitorChannelName.text = styleTitle
    }

    private fun executeAnim(subscribed: Boolean) {
        isSubscribe = subscribed
        viewBinding.ivAnim.playAnimation()
        viewBinding.ivAnim.repeatCount = Int.MAX_VALUE
    }

    private fun executeTimer() {
        timerTask = object : TimerTask() {
            override fun run() {
                if (!pausedTimer) {
                    val message: Message? = timeHandler?.obtainMessage(1) ?: null
                    message?.let { timeHandler?.sendMessage(message) }
                }
            }
        }
        fixTask()
        if (timer == null) {
            timer = Timer()
            timer?.schedule(timerTask, 0, 100)
        } else {
            timer?.schedule(timerTask, 0, 100)
        }
    }

    /**
     * 取消监控
     */
    private fun setUnSubjectMonitor(feedCount: Int) {
        pausedTimer = true
        updateMonitorState(false)
        viewBinding.tvMonitorTips.visibility = View.GONE
        val sb = SpannableStringBuilder()
        sb.append("已成功检测")
        val spNum = SpannableString("$feedCount")
        spNum.setSpan(RelativeSizeSpan(1.4f), 0, spNum.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        spNum.setSpan(
            StyleSpan(android.graphics.Typeface.BOLD),
            0,
            spNum.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        sb.append(spNum)
        sb.append("次")
        viewBinding.tvMonitorContinueDur.text = sb
        isSubscribe = false
        switchAnimState()
    }

    /**
     * 监控中
     */
    private fun setSubjectMonitor(feedCount: Int) {
        viewBinding.tvMonitorTips.visibility = View.VISIBLE
        viewBinding.tvMonitorTips.text = "已成功监控${feedCount}次"
        updateMonitorState(true)
        pausedTimer = false
        isSubscribe = true
        switchAnimState()
        executeTimer()
    }

    private fun updateMonitorState(subscribed: Boolean) {
        viewBinding.layoutMonitorSuspensionTv.hide(subscribed)
        viewBinding.layoutMonitorUnsubFra.hide(!subscribed)
    }

    private fun initRvAdapter() {
        productItemBinder.clickListener = object : MonitorProductItemBinder.ItemClickListener {
            override fun onClickShowMore(position: Int) {
                clickMore(position)
            }

            override fun onClickShowCount(position: Int) {
                val bean = productAdapter.data[position] as MonitorProductBean
                bean.isExpand = !bean.isExpand
                productAdapter.notifyDataItemChanged(position)
            }

            override fun onItemClick(position: Int, time: String) {
                val bean = productAdapter.data[position] as MonitorProductBean
                UTHelper.monitorContent(
                    bean.objectId,
                    bean.title,
                    bean.channel?.name,
                    time
                )
                bean.link?.let { DcRouter(it).start() }
                UTHelper.commonEvent(
                    UTConstant.Monitor.ChannelP_click_ProductList, "ProductName", bean.title
                        ?: ""
                )
            }

            override fun loading(forceShow: Boolean) {
                if (forceShow) {
                    showLoadingDialog()
                } else {
                    hideLoadingDialog()
                }
            }

            override fun onDateClick(channelId: String, name: String) {
                //not use at here
            }

            override fun onSearchPriceClick(productId: String) {
                viewModel.searchPrice(productId)
            }

        }
        couponItemBinder.showChanel = false
        couponItemBinder.itemClickListener = object : MonitorCouponItemBinder.ClickListener {
            override fun onItemClick(link: String) {
                val user = getAccount()
                if (user != null) {
                    DcRouter(link).start()
                }
            }

            override fun onChanelBarClick(channelId: String) {

            }
        }
        productAdapter.addItemBinder(ClueProductBean::class.java, couponItemBinder)
        productAdapter.addItemBinder(MonitorProductBean::class.java, productItemBinder)

        viewBinding.rvChannelDetail.apply {
            layoutManager = LinearLayoutManager(this@MonitorDetailActivity)
            adapter = productAdapter
        }
        val tv = CommonEmptyView(this)
        tv.setText("暂无监控数据")
        productAdapter.setEmptyView(tv)
    }

    private fun initView() {
        viewBinding.titleBar.apply {
            setBackgroundResource(R.color.transparent)
            title = ""
            setLeftIconColorRes(R.color.color_text_black1)
            setTitleColor(R.color.color_text_black1)
            setLineVisibility(false)
            setTitleAlpha(0f)
        }
        if (isDarkMode()) {
            viewBinding.monitorDetailTopBg.visibility = View.INVISIBLE
            viewBinding.monitorDetailBg.visibility = View.INVISIBLE
        } else {
            viewBinding.monitorDetailTopBg.visibility = View.VISIBLE
            viewBinding.monitorDetailBg.visibility = View.VISIBLE
        }
        viewBinding.layoutMonitorUnsubTv.isSelected = false
        viewBinding.layoutTop.setBackgroundResource(R.color.transparent)
        initViewShow()
    }

    private fun initViewShow() {
        val user = getAccount()
        val noVipHeaderView =
            LayoutInflater.from(this).inflate(R.layout.monitor_no_vip_layout, null, false)

        noVipHeaderView.findViewById<View>(R.id.iv_vip).setOnClickListener {
            UTHelper.commonEvent(UTConstant.Mine.VipP_ent, "source", "频道主页监控按钮")
            MobileHelper.getInstance().getCloudUrlAndRouter(
                this,
                MineConstant.VIP_CENTER,
                UTConstant.Monitor.ChannelP_click_OpenVip
            )
        }
        productAdapter.removeAllHeaderView()
        if (user == null) {
            productAdapter.addHeaderView(noVipHeaderView)
            return
        }
        if (!user.isVip()) {
            productAdapter.addHeaderView(noVipHeaderView)
            return
        }
    }

    /**
     * 切换动画状态
     */
    private fun switchAnimState() {
        if (isSubscribe) {
            //通过状态来控制播放暂停
            viewBinding.ivAnim.playAnimation()
        } else {
            viewBinding.ivAnim.cancelAnimation()
            viewBinding.ivAnim.progress = 0f
        }
    }

    /**
     * 获取参数
     */
    override fun initBundleData() {
        channelId = uri.getQueryParameter(MonitorConstant.UriParam.CHANNEL_ID).toString()
        channelId.let { viewModel.setChannelId(channelId) }
    }

    override fun initListeners() {
        monitorTypeSelectDialog.setOnConfirmClick {
            val selectTypeList = arrayListOf<String>()
            it.forEach { cateEntity ->
                if (cateEntity.isSelect) {
                    selectTypeList.add(cateEntity.name)
                }
            }
            viewModel.upDateMonitorType(channelId, selectTypeList)
        }
        viewBinding.apply {
            llRules.setOnShakeClickListener {
                UTHelper.commonEvent(UTConstant.Monitor.ChannelP_click_CustomizeMonitor)
                DcRouter(MonitorConstant.Uri.MONITOR_SETTING_RULE)
                    .putUriParameter("channel_id", channelId)
                    .putUriParameter(
                        "channel_name",
                        viewBinding.tvMonitorChannelName.text.toString()
                    )
                    .start()
            }
            llMonitorCities.setOnShakeClickListener {
                UTHelper.commonEvent(UTConstant.Monitor.ChannelP_click_City)
                DcRouter(MonitorConstant.Uri.MONITOR_CITY)
                    .putUriParameter(MonitorConstant.UriParam.CHANNEL_ID, channelId)
                    .start()
            }
            llMonitorType.setOnShakeClickListener {
                UTHelper.commonEvent(UTConstant.Monitor.ChannelP_click_Category)
                monitorTypeSelectDialog.show()
            }
        }
    }

    class TimeHandler internal constructor(val activity: MonitorDetailActivity) : Handler() {
        private var mWeakReference: WeakReference<MonitorDetailActivity> = WeakReference(activity)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val monitorActivity: MonitorDetailActivity? = mWeakReference.get()
            when (msg.what) {
                1 -> {
                    monitorActivity?.setDuration()
                }
            }
        }
    }

    private fun clickMore(position: Int) {
        val bean = productAdapter.data[position] as MonitorProductBean
        if (null == AccountHelper.getInstance().user) {
            showToastShort("用户未登录")
            return
        }
        if (bean.blocked) {
            bean.bizId?.let { bizAction(it, false, position) }
        } else {
            val isHideDialog =
                if (viewModel.isOffline) isHideOfflineShieldDialog else isHideOnlineShieldDialog
            if (!isHideDialog) {
                bean.bizId?.let { id ->
                    CommonDialog.Builder(
                        this,
                        if (viewModel.isOffline) bean.channel?.name.plus("监控频道中 屏蔽该商品只能屏蔽商品所属城市的该商品")
                        else "屏蔽该商品只能屏蔽当前频道该商品，非全部频道屏蔽",
                        "我知道了",
                        "不再提示",
                        onCancelClickListener = {
                            ConfigSPHelper.getInstance()
                                .save(
                                    if (viewModel.isOffline) MonitorConstant.DataParam.OFFLINE_MONITOR_SHIELD_HINT_ENABLE
                                    else MonitorConstant.DataParam.MONITOR_SHIELD_HINT_ENABLE,
                                    true
                                )
                            if (viewModel.isOffline) {
                                isHideOfflineShieldDialog = true
                            } else {
                                isHideOnlineShieldDialog = true
                            }
                            UTHelper.commonEvent(
                                UTConstant.Monitor.MonitorP_ShieldingRemin,
                                "operating",
                                "不再提示"
                            )
                            bizAction(id, true, position)
                        },
                        onConfirmClickListener = {
                            UTHelper.commonEvent(
                                UTConstant.Monitor.MonitorP_ShieldingRemin,
                                "operating",
                                "我知道了"
                            )
                            bizAction(id, true, position)
                        }
                    ).builder().show()
                }
            } else {
                bean.bizId?.let { it ->
                    bizAction(it, true, position)
                }
            }
        }
    }

    /**
     * 屏蔽当前商品
     *
     * @param bizId
     * @param blocked
     * @param position
     */
    private fun bizAction(bizId: String, blocked: Boolean, position: Int) {
        if (productAdapter.data.isNullOrEmpty()) {
            return
        }
        val bean = productAdapter.data[position] as MonitorProductBean
        viewModel.bizAction(bizId, blocked, success = {
            bean.blocked = blocked
            showToastShort(if (blocked) "屏蔽成功" else "取消屏蔽成功")
            try {
                productAdapter.notifyItemChanged(position + productAdapter.headerLayoutCount)
                EventBus.getDefault().post(EventBlockGoods(bizId, blocked))
            } catch (e: java.lang.Exception) {
            }
        }) { msg -> showToastShort(msg) }
    }
}