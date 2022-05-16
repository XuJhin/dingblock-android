package cool.dingstock.monitor.mine.monitor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.constant.AccountConstant
import cool.dingstock.appbase.constant.MonitorConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.home.bp.ClueProductBean
import cool.dingstock.appbase.entity.bean.monitor.MonitorCenterRegions
import cool.dingstock.appbase.entity.bean.monitor.MonitorProductBean
import cool.dingstock.appbase.entity.event.im.EventBlockGoods
import cool.dingstock.appbase.entity.event.monitor.EventChannelFilter
import cool.dingstock.appbase.entity.event.monitor.EventMonitorRuleSetting
import cool.dingstock.appbase.entity.event.monitor.EventRefreshMonitorPage
import cool.dingstock.appbase.entity.event.monitor.MonitorEventSource
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.visual
import cool.dingstock.appbase.mvp.lazy.VmLazyFragment
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.refresh.DcWhiteRefreshHeader
import cool.dingstock.appbase.router.ExternalRouter
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.util.setSvgColorRes
import cool.dingstock.appbase.viewmodel.HomeIndexViewModel
import cool.dingstock.appbase.widget.CommonEmptyView
import cool.dingstock.appbase.widget.commondialog.CommonDialog
import cool.dingstock.appbase.widget.consecutivescroller.ConsecutiveScrollerLayout
import cool.dingstock.appbase.widget.recyclerview.adapter.BaseRVAdapter
import cool.dingstock.appbase.widget.recyclerview.item.BaseEmpty
import cool.dingstock.appbase.widget.recyclerview.item.BaseViewHolder
import cool.dingstock.foundation.ext.getColorByRes
import cool.dingstock.lib_base.stroage.ConfigSPHelper
import cool.dingstock.lib_base.util.StringUtils
import cool.dingstock.monitor.R
import cool.dingstock.monitor.adapter.item.MonitorCenterRegionItem
import cool.dingstock.monitor.adapter.item.MonitorCouponItemBinder
import cool.dingstock.monitor.adapter.item.MonitorProductItemBinder
import cool.dingstock.monitor.center.MonitorCenterViewModel
import cool.dingstock.monitor.center.MonitorContentFragment
import cool.dingstock.monitor.databinding.ViewDataEmptyMonitorRegionBinding
import cool.dingstock.monitor.databinding.ViewEmptyMonitorRegionBinding
import cool.dingstock.uicommon.calendar.dialog.ComparisonPriceDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.libpag.PAGFile
import org.libpag.PAGView

class MonitorMineFragment : VmLazyFragment<MonitorMineVM>() {
    private lateinit var rvRecommend: RecyclerView
    private lateinit var rv: RecyclerView
    private lateinit var allEmpty: View

    private lateinit var allEmptyTv: TextView
    private lateinit var notLoginLin: View
    private lateinit var loginTv: TextView
    private lateinit var layoutParent: ConsecutiveScrollerLayout
    private lateinit var layoutMonitorRegion: LinearLayout
    private lateinit var offlineTv: TextView
    private lateinit var managerTv: TextView
    private lateinit var channelNameTv: TextView
    private lateinit var maintainTv: TextView
    private lateinit var ruleTv: TextView
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var filterIv: View
    private lateinit var pagView: PAGView
    private var topManagerIvAlpha: ((Float) -> Unit)? = null

    private var isHideOnlineShieldDialog = false
    private var isHideOfflineShieldDialog = false

    fun setTopManagerIvAlpha(alpha: (Float) -> Unit) {
        topManagerIvAlpha = alpha
    }

    private val monitorCenterListAdapter by lazy {
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

    private var recommendAdapter: BaseRVAdapter<MonitorCenterRegionItem> =
        BaseRVAdapter<MonitorCenterRegionItem>()

    private lateinit var mainActivityModel: HomeIndexViewModel
    private var isNeedRefresh = false
    private var monitorCenterViewModel: MonitorCenterViewModel? = null

    override fun getLayoutId(): Int = R.layout.fragment_monitor_mine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)

    }

    override fun onLazy() {
        showLoadingView()
        viewModel.checkIsLoginAndRefresh()
    }

    override fun reload() {
        super.reload()
        viewModel.checkIsLoginAndRefresh()
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        checkRefresh()
    }

    override fun onPause() {
        super.onPause()
        pauseEmptyAnim()
    }

    override fun onResume() {
        super.onResume()
        resumeEmptyAnim()
    }

    fun resumeEmptyAnim() {
        pagView.isVisible = true
    }

    fun pauseEmptyAnim() {
        if (pagView.isPlaying) {
//            pagView.
//            emptyAnim.playAnimation()
//        }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun initVariables(
        rootView: View?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        mainActivityModel = ViewModelProvider(requireActivity())[HomeIndexViewModel::class.java]
        parentFragment?.let {
            monitorCenterViewModel = ViewModelProvider(it)[MonitorCenterViewModel::class.java]
        }
        isHideOnlineShieldDialog = ConfigSPHelper.getInstance()
            .getBoolean(MonitorConstant.DataParam.MONITOR_SHIELD_HINT_ENABLE)
        isHideOfflineShieldDialog = ConfigSPHelper.getInstance()
            .getBoolean(MonitorConstant.DataParam.OFFLINE_MONITOR_SHIELD_HINT_ENABLE)
        initView()
        initPageTitle()
        initAdapter()
        initObserve()
    }

    override fun onStatusViewErrorClick() {
        super.onStatusViewErrorClick()
        showLoadingView()
        viewModel.refresh()
    }

    override fun initListeners() {
        refreshLayout.setOnRefreshListener { viewModel.refresh() }
        allEmptyTv.setOnClickListener {
            UTHelper.commonEvent(
                UTConstant.Monitor.EnterSource_AllChannelP,
                "Source",
                "我的监控tab_添加监控"
            )
            routeToDefaultSubscribe()
            UTHelper.commonEvent(UTConstant.Monitor.MyMonitorP_click_AddMonitor)
        }
        loginTv.setOnClickListener {
            routeToLogin()
        }

        monitorCenterListAdapter.loadMoreModule.setOnLoadMoreListener {
            viewModel.subscribedNexFeeds()
        }
        managerTv.setOnShakeClickListener {
            routeToOnLineSubscribe()
            UTHelper.commonEvent(UTConstant.Monitor.MyMonitorP_click_AddMonitor)
        }
        rootView?.findViewById<View>(R.id.filter_layer)?.setOnShakeClickListener {
            UTHelper.commonEvent(UTConstant.Monitor.MonitorP_click_ClassicMode_icon)
            mainActivityModel.drawerOpenMonitor.postValue(true)
        }
        layoutParent.setOnVerticalScrollChangeListener { _, scrollY, _, _ ->
            val alpha = if (scrollY > 0 && scrollY < rv.top) {
                scrollY / rv.top.toFloat()
            } else if (scrollY >= rv.top) {
                1f
            } else {
                0f
            }
            topManagerIvAlpha?.invoke(alpha)
            println("scrollY: ${rv.top - scrollY}, alpha: $alpha")
        }
    }

    private fun initView() {
        layoutParent = rootView.findViewById(R.id.layout_parent)
        layoutMonitorRegion = rootView.findViewById(R.id.layout_monitor_region)
        offlineTv = rootView.findViewById(R.id.offline_tv)
        rvRecommend = rootView.findViewById(R.id.rv_recommend)
        rv = rootView.findViewById(R.id.monitor_fragment_rv)
        refreshLayout = rootView.findViewById(R.id.monitor_fragment_refresh)
        allEmpty = rootView.findViewById(R.id.all_empty_lin)
//        emptyAnim = rootView.findViewById(R.id.empty_anim)
        pagView = rootView.findViewById(R.id.pag_empty_anim)
        allEmptyTv = rootView.findViewById(R.id.all_empty_tv)
        notLoginLin = rootView.findViewById(R.id.not_login_lin)
        loginTv = rootView.findViewById(R.id.login_tv)
        managerTv = rootView.findViewById(R.id.manager_tv)
        channelNameTv = rootView.findViewById(R.id.channel_name_tv)
        maintainTv = rootView.findViewById(R.id.maintain_tv)
        ruleTv = rootView.findViewById(R.id.rule_tv)
        filterIv = rootView.findViewById(R.id.filter_iv)
        context?.let { DcWhiteRefreshHeader(it) }?.let { refreshLayout.setRefreshHeader(it) }
        val pagFile = PAGFile.Load(requireContext().assets, "monitor_empty_animator.pag")
        val numTexts = pagFile.numTexts()
        if (numTexts < 0) {
            return
        }
        for (index in 0 until numTexts) {
            val netTextData = pagFile.getTextData(index)
            if (index % 2 == 0) {
                netTextData.fillColor = getColorByRes(R.color.color_pag_content)
            } else {
                netTextData.fillColor = getColorByRes(R.color.color_pag_title)
            }
            pagFile.replaceText(index, netTextData)
        }
        pagView.setRepeatCount(0)
        pagView.composition = pagFile
    }

    private fun initObserve() {
        //是否未登录
        viewModel.isLoginLiveData.observe(this) {
            showNotLoginView(it)
        }
        //订阅 是否所有都为空
        viewModel.stateAllEmpty.observe(this) {
            showAllEmptyView(it)
            finishRefresh()
        }
        //订阅列表为空
//        viewModel.stateEmpty.observe(this, { aBoolean ->
//            if (aBoolean) {
//                showSubscribedEmpty()
//            }
//        })
        //当监控地区为空时，加载空地区的头部
        viewModel.monitorRegionStatus.observe(this) { status ->
            when (status) {
                MonitorMineVM.MonitorDateStatus.RegionNull -> {
                    rvRecommend.visibility = View.VISIBLE
                    offlineTv.hide(true)
                    showRecommendNull()
                }
                MonitorMineVM.MonitorDateStatus.RegionEmpty -> {
                    rvRecommend.visibility = View.VISIBLE
                    offlineTv.hide(true)
                    showMonitorDateEmpty()
                }
                MonitorMineVM.MonitorDateStatus.RegionNotEmpty ->
                    rvRecommend.visibility = View.VISIBLE
                else -> Unit
            }
        }
        //加载更多失败
        viewModel.loadMoreFailed.observe(this) { aBoolean ->
            if (aBoolean) {
                hideEmptyView()
                monitorCenterListAdapter.loadMoreModule.loadMoreFail()
            }
        }
        //监控中心加载更多数据
        viewModel.loadMoreMonitorLiveData.observe(this) {
            if (it.isEmpty()) {
                monitorCenterListAdapter.loadMoreModule.loadMoreEnd()
            } else {
                setItemList(it, false)
                monitorCenterListAdapter.loadMoreModule.loadMoreComplete()
            }
        }
        //监控中心数据
        viewModel.monitorLiveData.observe(this) {
            finishRefresh()
            it?.let { setItemList(it, true) }
        }

        viewModel.monitorRegionsLiveData.observe(this) { monitorCenterRegions ->
            val viewList: MutableList<MonitorCenterRegionItem> = ArrayList()
            for (item in monitorCenterRegions) {
                viewList.add(MonitorCenterRegionItem(item))
            }
            if (viewList.size > 0) {
                val entity = MonitorCenterRegions()
                entity.isAdd = true
                viewList.add(MonitorCenterRegionItem(entity))
            }
            offlineTv.hide(viewList.size == 0)
            recommendAdapter.clearAllItemView()
            recommendAdapter.addItemViewList(viewList)
            recommendAdapter.hideEmptyView()
            recommendAdapter.notifyDataSetChanged()
            layoutParent.requestLayout()
        }
        viewModel.subChannelLiveData.observe(this) {
            mainActivityModel.monitorEnableShowDrawerLiveData.postValue(it)
            filterIv.visual(it)
            channelNameTv.visual(it)
            (parentFragment as? MonitorContentFragment)?.hideTopRightIcon(!it)
            if (it) {
                val emptyView =
                    LayoutInflater.from(context)
                        .inflate(R.layout.view_empty_monitor_goods, null, false)
                monitorCenterListAdapter.setEmptyView(emptyView)
                managerTv.hide(false)
            } else {
                val emptyView =
                    LayoutInflater.from(context)
                        .inflate(R.layout.view_empty_monitor_subscribe, null, false)
                val tvAddMonitor = emptyView.findViewById<TextView>(R.id.tv)
                tvAddMonitor.text = "添加线上监控"
                tvAddMonitor.setOnClickListener {
                    routeToOnLineSubscribe() //跳转线上
                }
                monitorCenterListAdapter.setEmptyView(emptyView)
                managerTv.hide(true)
            }
        }
        viewModel.reset2AllLiveData.observe(this) {
            channelNameTv.text = "全部频道"
            maintainTv.hide()
            ruleTv.hide()
        }
        viewModel.priceListLiveData.observe(this) {
            comparisonPriceDialog.setData(it)
            comparisonPriceDialog.show(childFragmentManager, "comparisonPriceDialog")
        }
    }


    private fun initAdapter() {
        initRecommendAdapter()
        rv.setHasFixedSize(false)
        initMonitorAdapter()
        rv.layoutManager = LinearLayoutManager(activity)
        rv.adapter = monitorCenterListAdapter
    }

    private fun initRecommendAdapter() {
        recommendAdapter.setOnItemViewClickListener { item, _, _ ->
            if (item.data.isAdd) {
                routeToUnLinSubscribe()
                return@setOnItemViewClickListener
            }
            DcRouter(MonitorConstant.Uri.MONITOR_REGIONS)
                .dialogBottomAni()
                .appendParams(hashMapOf(MonitorConstant.UriParam.FILTER_ID to item.data.id))
                .start()
            UTHelper.commonEvent(UTConstant.Monitor.MyMonitorP_click_AreaList)
        }
        rvRecommend.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvRecommend.adapter = recommendAdapter
    }

    private fun initMonitorAdapter() {

        monitorCenterListAdapter.setEmptyView(CommonEmptyView(requireContext()))
        productItemBinder.clickListener = object : MonitorProductItemBinder.ItemClickListener {
            override fun onClickShowMore(position: Int) {
                clickMore(position)
            }

            override fun onClickShowCount(position: Int) {
                val bean = monitorCenterListAdapter.data[position] as MonitorProductBean
                //val realPosition = productAdapter.headerLayoutCount + position
                bean.isExpand = !bean.isExpand
                monitorCenterListAdapter.notifyDataItemChanged(position)
            }

            override fun onItemClick(position: Int, time: String) {
                val bean = monitorCenterListAdapter.data[position] as MonitorProductBean
                UTHelper.monitorContent(
                    bean.objectId,
                    bean.title,
                    bean.channel?.name,
                    time
                )
                UTHelper.commonEvent(UTConstant.Monitor.MyMonitorP_click_ChannelList_CommodityJump)
                ExternalRouter.route(bean.link)
            }

            override fun loading(forceShow: Boolean) {
                if (forceShow) {
                    showLoadingDialog()
                } else {
                    hideLoadingDialog()
                }
            }

            override fun onDateClick(channelId: String, name: String) {
                DcRouter(MonitorConstant.Uri.MONITOR_DETAIL)
                    .appendParams(hashMapOf(MonitorConstant.UriParam.CHANNEL_ID to channelId))
                    .start()
                UTHelper.commonEvent(UTConstant.Monitor.MyMonitorP_click_ChannelList_Channel)
                UTHelper.commonEvent(
                    UTConstant.Monitor.ChannelP_ent,
                    "source",
                    "监控竖列表",
                    "ChannelName",
                    name
                )
            }

            override fun onSearchPriceClick(productId: String) {
                viewModel.searchPrice(productId)
            }

        }

        couponItemBinder.itemClickListener = object : MonitorCouponItemBinder.ClickListener {
            override fun onItemClick(link: String) {
                val user = user
                if (user != null) {
                    DcRouter(link).start()
                }
            }

            override fun onChanelBarClick(channelId: String) {

                val user = user
                if (user != null) {
                    DcRouter(MonitorConstant.Uri.MONITOR_DETAIL)
                        .putUriParameter(MonitorConstant.UriParam.CHANNEL_ID, channelId)
                        .start()
                }
            }
        }
        monitorCenterListAdapter.addItemBinder(ClueProductBean::class.java, couponItemBinder)
        monitorCenterListAdapter.addItemBinder(MonitorProductBean::class.java, productItemBinder)

        rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = monitorCenterListAdapter
        }
    }

    private fun initPageTitle() {
        mainActivityModel.monitorNoticeScroll.observe(this, object : Observer<Int> {
            override fun onChanged(integer: Int) {
                if (integer != 1 || !pageVisible) {
                    Log.e("scroll", "监控页面不可见")
                    return
                }
                tryScrollToTop()
            }
        })
    }

    private fun setItemList(monitorProductBeanList: List<Any>, isRefresh: Boolean) {
        if (isRefresh) {
            hideLoadingView()
            monitorCenterListAdapter.setList(monitorProductBeanList)
        } else {
            monitorCenterListAdapter.addData(monitorProductBeanList)
            monitorCenterListAdapter.loadMoreModule.loadMoreComplete()
        }
    }

    private fun tryScrollToTop() {
        Log.e("scroll", "scroll monitor")
        rv.stopScroll()
        layoutParent.scrollTo(0, 0)
        viewModel.refresh()
    }

    private fun clickMore(position: Int) {
        val bean = monitorCenterListAdapter.data[position] as MonitorProductBean
        if (null == AccountHelper.getInstance().user) {
            showToastShort("用户未登录")
            return
        }
        bean.let {
            if (bean.blocked) {
                bizAction(it.bizId, false, position)
            } else {
                val isHideDialog =
                    if (bean.channel?.isOffline == true) isHideOfflineShieldDialog else isHideOnlineShieldDialog
                if (!isHideDialog) {
                    bean.bizId?.let { id ->
                        context?.let { it1 ->
                            CommonDialog.Builder(
                                it1,
                                if (it.channel?.isOffline == true) bean.channel?.name.plus("监控频道中 屏蔽该商品只能屏蔽商品所属城市的该商品")
                                else "屏蔽该商品只能屏蔽当前频道该商品，非全部频道屏蔽",
                                "我知道了",
                                "不再提示",
                                onCancelClickListener = {
                                    ConfigSPHelper.getInstance()
                                        .save(
                                            if (bean.channel?.isOffline == true) MonitorConstant.DataParam.OFFLINE_MONITOR_SHIELD_HINT_ENABLE
                                            else MonitorConstant.DataParam.MONITOR_SHIELD_HINT_ENABLE,
                                            true
                                        )
                                    if (bean.channel?.isOffline == true) {
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
                    }
                } else {
                    bizAction(it.bizId, true, position)
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
    private fun bizAction(bizId: String?, blocked: Boolean, position: Int) {
        if (StringUtils.isEmpty(bizId)) {
            return
        }
        val bean: MonitorProductBean = monitorCenterListAdapter.data[position] as MonitorProductBean
        viewModel.bizAction(bizId ?: "", blocked, success = {
            bean.blocked = blocked
            showToastShort(if (blocked) "屏蔽成功" else "取消屏蔽成功")
            monitorCenterListAdapter.notifyItemChanged(position)
        }) { msg -> showToastShort(msg) }
    }

    /**
     * 监控地区为空
     */
    private fun showRecommendNull() {
        //这里每次都变化是应为 这个 adapter的showEmpty会显示上一次的。不会更新。所以暴力的从新new
        initRecommendAdapter()
        rvRecommend.adapter = recommendAdapter
        val recommendEmpty = object : BaseEmpty<String, ViewEmptyMonitorRegionBinding>("") {
            override fun getLayoutId(viewType: Int): Int {
                return R.layout.view_empty_monitor_region
            }

            override fun onSetViewsData(holder: BaseViewHolder) {
                holder.itemView.findViewById<View>(R.id.btn_line)
                    ?.setOnClickListener {
                        //跳转线下
                        routeToUnLinSubscribe()
                    }
                holder.itemView.findViewById<ImageView>(R.id.add_iv)
                    .setSvgColorRes(R.drawable.monitor_svg_add, R.color.color_text_absolutely_white)
            }
        }
        recommendAdapter.showEmptyView(recommendEmpty)
    }

    /**
     * 监控地区 发售为空为空
     */
    private fun showMonitorDateEmpty() {
        //这里每次都变化是应为 这个 adapter的showEmpty会显示上一次的。不会更新。所以暴力的从新new
        initRecommendAdapter()
        rvRecommend.adapter = recommendAdapter
        val recommendEmpty = object : BaseEmpty<String, ViewDataEmptyMonitorRegionBinding>("") {
            override fun getLayoutId(viewType: Int): Int {
                return R.layout.view_data_empty_monitor_region
            }

            override fun onSetViewsData(holder: BaseViewHolder) {
                holder.itemView.findViewById<LinearLayout>(R.id.layout_no_sale_sneakers)
                    .setOnClickListener {
                        //跳转线下
                        routeToUnLinSubscribe()
                    }
            }
        }
        recommendAdapter.showEmptyView(recommendEmpty)
    }

//    /**
//     * 显示订阅为空
//     */
//    private fun showSubscribedEmpty() {
//        hideLoadingView()
//        finishRefresh()
//        monitorCenterListAdapter.loadMoreModule.loadMoreComplete()
//        val previousSize: Int = monitorCenterListAdapter.data.size
//        monitorCenterListAdapter.notifyItemRangeRemoved(0, previousSize)
//        monitorCenterListAdapter.data.clear()
//        monitorCenterListAdapter.isUseEmpty = true
//    }

    private fun showAllEmptyView(isAllEmpty: Boolean) {
        allEmpty.hide(!isAllEmpty)
//        emptyAnim.playAnimation()
        pagView.play()
        layoutParent.hide(isAllEmpty)
        //显示所有为空也不显示黑色背景
        monitorCenterViewModel?.minContentIsBlackTheme = !isAllEmpty

    }

    private fun showNotLoginView(isLogin: Boolean) {
        if (!isLogin) {
            (parentFragment as? MonitorContentFragment)?.hideTopRightIcon(!isLogin)
            mainActivityModel.monitorEnableShowDrawerLiveData.postValue(false)
        }
        notLoginLin.hide(isLogin)
        refreshLayout.isEnabled = isLogin
        if (!isLogin) {
            layoutParent.hide(true)
            //这里没有登陆一定不显示黑色背景，如果登陆了在获取是否有监控的时候还会在判断一次
            monitorCenterViewModel?.minContentIsBlackTheme = false
            allEmpty.hide(true)
            pagView.stop()
            pagView.progress = 0.0
//            emptyAnim.cancelAnimation()
//            emptyAnim.progress = 0f
        }
    }

    private fun finishRefresh() {
        if (refreshLayout.isRefreshing) {
            refreshLayout.finishRefresh()
        }
    }

    fun routeToOnLineSubscribe() {
        if (LoginUtils.isLoginAndRequestLogin(requireContext())) {
            DcRouter(MonitorConstant.Uri.MONITOR_MENU_ON_LINE).start()
            UTHelper.commonEvent(
                UTConstant.Monitor.EnterSource_AllChannelP,
                "Source",
                "我的监控tab_添加线上监控"
            )
            UTHelper.commonEvent(UTConstant.Monitor.MyMonitorP_click_AddOnlineMonitor)
        }
    }

    private fun routeToUnLinSubscribe() {
        if (LoginUtils.isLoginAndRequestLogin(requireContext())) {
            DcRouter(MonitorConstant.Uri.MONITOR_MENU_OFFLINE)
                .start()
            UTHelper.commonEvent(
                UTConstant.Monitor.EnterSource_AllChannelP,
                "Source",
                "我的监控tab_添加线下监控"
            )
            UTHelper.commonEvent(UTConstant.Monitor.MyMonitorP_click_AddOfflineMonitor)
        }
    }

    private fun routeToDefaultSubscribe() {
        if (LoginUtils.isLoginAndRequestLogin(requireContext())) {
            DcRouter(MonitorConstant.Uri.MONITOR_MENU_ON_LINE).start()
        }
    }

    private fun routeToLogin() {
        DcRouter(AccountConstant.Uri.INDEX).start()
    }

    private fun checkRefresh() {
        if (isNeedRefresh) {
            isNeedRefresh = false
            viewModel.checkIsLoginAndRefresh()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshPage(eventMonitor: EventRefreshMonitorPage?) {
        when (eventMonitor?.eventSource) {
            //这里没法监听到Fragment 在ViewPager滑动回来，所以暴力刷新
            MonitorEventSource.RECOMMEND -> {
                isNeedRefresh = true
                checkRefresh()
            }
            else -> {
                isNeedRefresh = true
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChannelFilter(eventMonitor: EventChannelFilter) {
        //筛选
        if (eventMonitor.isAll) {
            maintainTv.hide()
            ruleTv.hide()
        }
        ruleTv.hide(!eventMonitor.customRuleEffective)
        maintainTv.hide(!eventMonitor.maintaining)
        channelNameTv.text = eventMonitor.channelName
        viewModel.filterChannelId = eventMonitor.channelId
        viewModel.refreshFeeds()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateChannel(ruleSetting: EventMonitorRuleSetting) {
        if (viewModel.filterChannelId == ruleSetting.channelId) {
            ruleTv.hide(ruleSetting.keywordSetting != 1 && ruleSetting.sizeSetting != 1)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun goodsBlockStatusChange(eventBlockGoods: EventBlockGoods) {
        for ((i, item) in monitorCenterListAdapter.data.withIndex()) {
            if (item is MonitorProductBean && item.bizId == eventBlockGoods.bizId) {
                item.blocked = eventBlockGoods.blocked
                monitorCenterListAdapter.notifyDataItemChanged(i)
                break
            }
        }
    }

    companion object {
        fun getInstance(): MonitorMineFragment {
            return MonitorMineFragment()
        }
    }
}