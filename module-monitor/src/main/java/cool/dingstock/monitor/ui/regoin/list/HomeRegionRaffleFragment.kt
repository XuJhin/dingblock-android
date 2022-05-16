package cool.dingstock.monitor.ui.regoin.list

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.permissionx.guolindev.PermissionX
import cool.dingstock.appbase.constant.CalendarConstant
import cool.dingstock.appbase.constant.HomeConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.home.*
import cool.dingstock.appbase.helper.MonitorRemindHelper
import cool.dingstock.appbase.lazy.LazyFragment
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.share.ShareParams
import cool.dingstock.appbase.share.ShareType
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.CalendarReminderUtils
import cool.dingstock.appbase.util.LocationHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.OnLocationResultListener
import cool.dingstock.appbase.widget.commondialog.CommonDialog
import cool.dingstock.appbase.widget.dialog.common.DcTitleDialog
import cool.dingstock.appbase.widget.recyclerview.adapter.BaseRVAdapter
import cool.dingstock.lib_base.util.CollectionUtils
import cool.dingstock.lib_base.util.TimeUtils
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.monitor.R
import cool.dingstock.uicommon.calendar.dialog.ComparisonPriceDialog
import cool.dingstock.uicommon.product.dialog.SmsRegistrationDialog
import cool.dingstock.uicommon.product.item.HomeRegionRaffleItem
import cool.mobile.account.share.ShareDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 关注地区商品列表
 */
class HomeRegionRaffleFragment : LazyFragment() {
    @JvmField
    var recyclerView: RecyclerView? = null

    @JvmField
    var refreshLayout: SwipeRefreshLayout? = null

    private val comparisonPriceDialog by lazy {
        ComparisonPriceDialog()
    }

    private var regionId: String = ""
    private var viewModel: RaffleFragmentListViewModel? = null
    private var rvAdapter: BaseRVAdapter<HomeRegionRaffleItem>? = null

    override fun getLayoutId(): Int {
        return R.layout.home_fragment_region_raffle
    }

    override fun initVariables(
        rootView: View?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        EventBus.getDefault().register(this)
        viewModel = ViewModelProvider(this)[RaffleFragmentListViewModel::class.java]
        startLocation()
        initBundleData()
        initView(rootView)
        showLoadingView()
        initRv()
        syncData()
    }

    private fun startLocation() {
        PermissionX.init(this)
            .permissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, "定位权限是此功能正常运行所依赖的权限，请授予", "明白", "取消")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "是否前往设置页开启定位权限", "前往", "取消")
            }
            .request { allGranted, _, _ ->
                if (allGranted) {
                    LocationHelper(requireContext(), object : OnLocationResultListener {
                        override fun onLocationResult(location: Location?) {
                            viewModel?.updateLocation(location)
                        }

                        override fun onLocationChange(location: Location?) {
                            viewModel?.updateLocation(location)
                        }

                        override fun onLocationFail() {
                        }
                    }).bind(this).startLocation()
                } else {
                    showToastShort("无法获取定位权限")
                }
            }
    }

    private fun syncData() {
        viewModel?.apply {
            goodDetailLiveData.observe(this@HomeRegionRaffleFragment) {
                context?.let { it1 ->
                    DcUriRequest(it1, HomeConstant.Uri.BP_GOODS_DETAIL)
                        .putExtra(HomeConstant.UriParam.KEY_GOOD_DETAIL, it)
                        .start()
                }
            }
            finishRefreshData.observe(this@HomeRegionRaffleFragment) {
                refreshLayout?.isRefreshing = false
            }
            hideLoading.observe(this@HomeRegionRaffleFragment) {
                hideLoadingView()
                hideLoadingDialog()
            }
            errorLiveData.observe(this@HomeRegionRaffleFragment) {
                hideLoadingView()
                showErrorView(it)
            }
            listData.observe(this@HomeRegionRaffleFragment) {
                setRaffleData(it)
                hideLoadingView()
                hideLoadingDialog()
            }
            priceListLiveData.observe(this@HomeRegionRaffleFragment) {
                comparisonPriceDialog.setData(it)
                comparisonPriceDialog.show(childFragmentManager, "comparisonPriceDialog")
            }
        }
    }

    private fun initBundleData() {
        val arguments = arguments
        if (null != arguments) {
            regionId = arguments.getString(KEY_ID, "")
            viewModel?.initRegionId(regionId)
        }
    }

    private fun initView(rootView: View?) {
        recyclerView = rootView!!.findViewById(R.id.home_fragment_region_raffle_rv)
        refreshLayout = rootView.findViewById(R.id.home_fragment_region_raffle_refresh)
    }

    override fun initListeners() {
        setOnErrorViewClick(View.OnClickListener {
            showLoadingView()
            refreshData(false)
        })
        refreshLayout?.setOnRefreshListener { refreshData(true) }
    }

    private fun refreshData(isRefresh: Boolean) {
        viewModel?.getRegionShopList(isRefresh)
    }

    override fun lazyInit() {
        Log.e("lazyInit", "$regionId,lazyInit")
        refreshData(false)
    }

    private fun initRv() {
        rvAdapter = BaseRVAdapter()
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = rvAdapter
    }

    private fun setRaffleData(data: List<HomeRegionRaffleBean?>) {
        if (CollectionUtils.isEmpty(data)) {
            rvAdapter?.showEmptyView("该地区目前没有发售哦~")
            return
        }
        val itemList: MutableList<HomeRegionRaffleItem> = ArrayList()
        for (regionRaffleBean in data) {
            val regionRaffleItem = HomeRegionRaffleItem(regionRaffleBean)
            regionRaffleItem.utEventId = UTConstant.Home.CLICK_GOOD_LIST
            regionRaffleItem.utActionId = UTConstant.Home.FocusAreaP_click_OtherOperations
            regionRaffleItem.utJumpEventId = UTConstant.Home.CLICK_GOOD_ROUTE
            regionRaffleItem.setActionListener(object : HomeRegionRaffleItem.ActionListener {
                override fun onFlashClick(homeProduct: HomeProduct?, homeRaffle: HomeRaffle) {
                    val user = getUser()
                    if (user != null) {
                        viewModel?.parseLink(homeRaffle.bpLink!!)
                    }
                    UTHelper.commonEvent(UTConstant.Home.CLICK_GOOD_LIST, "source", "关注地区")
                }

                override fun onSearchPriceClick(homeProduct: HomeProduct?) {
                    viewModel?.searchPrice(homeProduct?.objectId)
                }

                override fun onShareClick(homeProduct: HomeProduct?, homeRaffle: HomeRaffle) {
                    showShareView(homeProduct, homeRaffle)
                }

//                override fun onSmsClick(homeProduct: HomeProduct, homeRaffle: HomeRaffle) {
//                    UTHelper.commonEvent(UTConstant.SaleDetails.SaleDetailsP_source, "position", "关注地区")
//
////                    UTHelper.commonEvent(UTConstant.SaleDetails.SaleDetailsP_click_SMS_icon, "source", "关注地区")
//
//                    val user = getUser()
//                    if (user != null) {
//                        routerToSms(homeProduct, homeRaffle)
//                    }
//                }


                override fun onSmsClick(homeProduct: HomeProduct?, homeRaffle: HomeRaffle) {
                    if (!context?.let { LoginUtils.isLoginAndRequestLogin(it) }!!) {
                        return
                    }
                    if (homeProduct == null) return
                    if (System.currentTimeMillis() < homeRaffle.smsStartAt) {
                        CommonDialog.Builder(requireContext())
                            .content("短信抽签未开始\n短信抽签将于${TimeUtils.formatTimestampS2NoYea2r(homeRaffle.smsStartAt)}开始，请稍后再试～")
                            .confirmTxt("提醒我")
                            .cancelTxt("取消")
                            .onConfirmClick {
                                loadDataToCalender(homeProduct, homeRaffle)
                            }
                            .builder()
                            .show()
                        return
                    }
                    if (homeRaffle.showOldPage == true) {
                        val user = getUser()
                        if (user != null) {
                            routerToSms(homeProduct, homeRaffle)
                        }
                    } else {
                        openChooseDialog(homeRaffle)
                    }
                }


                override fun onAlarmClick(
                    homeProduct: HomeProduct?,
                    homeRaffle: HomeRaffle,
                    pos: Int
                ) {
//                    UTHelper.commonEvent(UTConstant.SaleDetails.SaleDetailsP_click_AlarmClock_icon, "position", "关注地区")
                    if (homeProduct == null) return
                    val user = getUser()
                    if (user != null) {
                        context?.let {
                            val monitorRemindHelper =
                                MonitorRemindHelper(
                                    AlarmFromWhere.CARE_ADDRESS,
                                    pos,
                                    homeProduct.objectId ?: ""
                                )
                            val remindMsg = monitorRemindHelper.generateRemindMsg(
                                homeProduct.name ?: "",
                                homeRaffle.brand?.name ?: "",
                                homeRaffle.notifyDate,
                                homeRaffle.method ?: "",
                                homeRaffle.price ?: "",
                            )
                            if (homeRaffle.reminded) {
                                monitorRemindHelper.cancelRemind(
                                    it,
                                    homeRaffle.id ?: "",
                                    remindMsg
                                )
                            } else {
                                monitorRemindHelper.setRemind(
                                    it,
                                    this@HomeRegionRaffleFragment.childFragmentManager,
                                    homeRaffle.id ?: "",
                                    remindMsg,
                                    homeRaffle.notifyDate
                                )
                            }
                        }
                    }
                }
            })
            itemList.add(regionRaffleItem)
        }
        rvAdapter?.setItemViewList(itemList)
    }

    private val smsRegistrationDialog by lazy {
        SmsRegistrationDialog()
    }

    private fun openChooseDialog(homeRaffle: HomeRaffle?) {
        homeRaffle?.sms?.let { homeRaffleSmsBean ->
            //可以改到viewModel中去
            showLoadingDialog()
            homeRaffle.id?.let {
                viewModel?.calendarApi?.smsInputTemplate(it)
                    ?.subscribe({ request ->
                        hideLoadingDialog()
                        if (!request.err && request.res != null) {
                            homeRaffle.id?.let { it1 ->
                                smsRegistrationDialog.setSmsData(
                                    it1,
                                    homeRaffleSmsBean,
                                    request.res!!
                                )
                            }
                            activity?.let { it1 ->
                                smsRegistrationDialog.show(
                                    it1.supportFragmentManager,
                                    "smsRegistrationDialog"
                                )
                            }
                        } else {
                            ToastUtil.getInstance()._short(context, request.msg)
                        }
                    }, { e ->
                        hideLoadingDialog()
                        ToastUtil.getInstance()._short(context, e.message)
                    })
            }
        }
    }


    private fun routerToSms(homeProduct: HomeProduct, homeRaffle: HomeRaffle) {
        val smsStartAt = homeRaffle.smsStartAt
        if (smsStartAt > System.currentTimeMillis()) {
            showTimingDialog(smsStartAt, homeRaffle, homeProduct)
        } else {
            Router(HomeConstant.Uri.SMS_EDIT)
                .putExtra(CalendarConstant.DataParam.KEY_SMS, homeRaffle.sms)
                .start()
        }
    }

    /**
     * 将通知写入日历
     *
     * @param homeProduct
     * @param homeRaffle
     */
    private fun loadDataToCalender(homeProduct: HomeProduct, homeRaffle: HomeRaffle) {
        PermissionX.init(this)
            .permissions(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, "日历权限是此功能正常运行所依赖的权限，请授予", "明白", "取消")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "是否前往设置页开启日历权限", "前往", "取消")
            }
            .request { allGranted, _, _ ->
                if (allGranted) {
                    remind(homeRaffle, homeProduct)
                } else {
                    showToastShort("获取日历权限失败")
                }
            }
    }

    private fun remind(mActionHomeRaffle: HomeRaffle, mHomeProduct: HomeProduct) {
        val brand = mActionHomeRaffle.brand
        val place = if (null == brand) "" else brand.name
        val releaseDateString = mActionHomeRaffle.releaseDateString
        val method = mActionHomeRaffle.method
        val des = """
            ${mHomeProduct.name}
            $place
            $releaseDateString
            $method
            """.trimIndent()
        val addSuccess = CalendarReminderUtils.addCalendarEvent(
            context,
            des,
            "",
            TimeUtils.format2Mill(mActionHomeRaffle.notifyDate)
        )
        showToastShort(if (addSuccess) R.string.home_setup_success else R.string.home_setup_failed)
    }

    private fun showTimingDialog(
        smsStartAt: Long,
        homeRaffle: HomeRaffle,
        homeProduct: HomeProduct
    ) {
        val niceDate = TimeUtils.formatTimestampCustom(smsStartAt, "MM月dd日 HH:mm")
        val messageContent = "抽签将在$niceDate 开始,请稍后再试"
        DcTitleDialog.Builder(requireContext())
            .title("短信抽签还未开始")
            .content(messageContent)
            .cancelTxt("取消")
            .confirmTxt("提醒我")
            .onConfirmClick {
                loadDataToCalender(homeProduct, homeRaffle)
            }
            .builder()
            .show()
    }

    private fun showShareView(mHomeProduct: HomeProduct?, homeRaffle: HomeRaffle?) {
        if (null == homeRaffle || null == mHomeProduct) {
            return
        }
        val content = String.format(
            getString(R.string.raffle_share_content_template),
            homeRaffle.brand?.name,
            mHomeProduct.name
        )

        val type = ShareType.Link
        val params = ShareParams()
        params.title = getString(R.string.raffle_share_title)
        params.content = content
        params.imageUrl = mHomeProduct.imageUrl
        params.link =
            if (TextUtils.isEmpty(homeRaffle.shareLink)) homeRaffle.link else homeRaffle.shareLink
        type.params = params

        val shareDialog = ShareDialog(requireContext())
        shareDialog.shareType = type
        shareDialog.show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshAlarm(event: AlarmRefreshEvent) {
        if (event.fromWhere === AlarmFromWhere.CARE_ADDRESS) {
            rvAdapter!!.allItemList.forEachIndexed { index, homeRegionRaffleItem ->
                if (homeRegionRaffleItem.data.header?.objectId == event.productId) {
                    homeRegionRaffleItem.data.data?.get(event.pos)?.reminded = event.isLightUpAlarm
                    rvAdapter!!.notifyItemChanged(index)
                    return
                }
            }
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    companion object {
        const val KEY_ID = "region_id"
        fun newInstance(id: String?): HomeRegionRaffleFragment {
            val fragment = HomeRegionRaffleFragment()
            val args = Bundle()
            args.putString(KEY_ID, id)
            fragment.arguments = args
            return fragment
        }
    }
}