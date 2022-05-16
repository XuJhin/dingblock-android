package cool.dingstock.calendar.ui.pager

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.permissionx.guolindev.PermissionX
import cool.dingstock.appbase.constant.*
import cool.dingstock.appbase.customerview.CommonNavigatorNew
import cool.dingstock.appbase.entity.bean.account.DcLoginUser
import cool.dingstock.appbase.entity.bean.home.*
import cool.dingstock.appbase.ext.azDp
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.visual
import cool.dingstock.appbase.helper.MonitorRemindHelper
import cool.dingstock.appbase.mvp.lazy.VmBindingLazyFragment
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.share.ShareParams
import cool.dingstock.appbase.share.ShareType
import cool.dingstock.appbase.util.CalendarReminderUtils
import cool.dingstock.appbase.util.ClipboardHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.widget.commondialog.CommonDialog
import cool.dingstock.appbase.widget.dialog.common.DcTitleDialog
import cool.dingstock.appbase.widget.recyclerview.adapter.BaseRVAdapter
import cool.dingstock.appbase.widget.tablayout.TabScaleAdapter
import cool.dingstock.calendar.R
import cool.dingstock.calendar.databinding.CalendarFragmentHeavyReleaseGoodsBinding
import cool.dingstock.foundation.ext.tintColorRes
import cool.dingstock.lib_base.json.JSONHelper
import cool.dingstock.lib_base.util.*
import cool.dingstock.uicommon.adapter.StickyHeaderDecoration
import cool.dingstock.uicommon.calendar.dialog.ComparisonPriceDialog
import cool.dingstock.uicommon.helper.AnimationDrawHelper
import cool.dingstock.uicommon.product.dialog.SmsRegistrationDialog
import cool.dingstock.uicommon.product.item.HomeRaffleDetailItem
import cool.mobile.account.share.ShareDialog
import net.lucode.hackware.magicindicator.FragmentContainerHelper
import net.lucode.hackware.magicindicator.MagicIndicator
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CalendarFragmentHeavyRaffle :
    VmBindingLazyFragment<CalendarFragmentHeavyGoodVM, CalendarFragmentHeavyReleaseGoodsBinding>() {
    private var dialog: ComparisonPriceDialog? = null
    private var goodId: String? = null
    private val titleList: MutableList<String> = arrayListOf()
    private var canScroll = false
    private var decoration: StickyHeaderDecoration? = null
    private var rvAdapter: BaseRVAdapter<HomeRaffleDetailItem>? = null
    private var lastSectionHeight = 0
    private var isUserTriggers = false
    private var lastPos = 0
    private val animationDrawHelper = AnimationDrawHelper()
    private lateinit var magicIndicator: MagicIndicator
    private var containerHelper: FragmentContainerHelper? = null
    private val smsRegistrationDialog by lazy {
        SmsRegistrationDialog()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshAlarm(event: AlarmRefreshEvent) {
        if (event.fromWhere === AlarmFromWhere.HEAVY_DETAIL) {
            rvAdapter!!.getItemView(event.pos).data?.reminded = event.isLightUpAlarm
            rvAdapter!!.notifyItemChanged(event.pos)
        }
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    private lateinit var rvLayoutManager: LinearLayoutManager

    override fun initListeners() {
    }

    override fun onLazy() {
        Logger.d("请求数据 ${goodId}")
        viewModel.fetchGoodInfo()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        goodId = arguments?.getString(GOOD_ID)
        viewModel.goodId = goodId
    }

    override fun initVariables(
        rootView: View?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        initViewAndEvent()
        observerDataChange()
    }


    private fun initRaffleList() {
        decoration = object : StickyHeaderDecoration(90.azDp.toInt()) {
            override fun getHeaderName(pos: Int): String {
                val itemView = rvAdapter!!.getItemView(pos) ?: return ""
                val headerName = itemView.data?.headerName ?: return ""
                viewModel.dataResult.value?.raffles?.let {
                    for (homeProductDetail in it) {
                        if (headerName == homeProductDetail.region!!.name && it.indexOf(
                                homeProductDetail
                            ) == 0
                        ) {
                            return ""
                        }
                    }
                }
                return headerName
            }
        }
        decoration?.setHeaderHeight(SizeUtils.dp2px(40f))
        decoration?.setHeaderContentColor(getCompatColor(R.color.color_gray))
        decoration?.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                cool.dingstock.appbase.R.color.color_text_black1
            )
        )
        decoration?.setTextSize(SizeUtils.sp2px(14f))
        decoration?.setTextPaddingLeft(24)
        rvAdapter = BaseRVAdapter()
        rvLayoutManager = LinearLayoutManager(context)
        viewBinding.rvRaffle.apply {
            layoutManager = rvLayoutManager
            adapter = rvAdapter
            addItemDecoration(decoration!!)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (canScroll) {
                        canScroll = false
                        moveToPosition(viewModel.scrollToPosition)
                    }
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        isUserTriggers = true
                        Logger.d(
                            "onScrollStateChanged",
                            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) "SCROLL_STATE_DRAGGING" else "SCROLL_STATE_IDLE"
                        )
                    } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        isUserTriggers = false
                        Logger.d(
                            "onScrollStateChanged",
                            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) "SCROLL_STATE_DRAGGING" else "SCROLL_STATE_IDLE"
                        )
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (isUserTriggers) {
                        val position = rvLayoutManager!!.findFirstVisibleItemPosition()
                        if (lastPos != position) {
                            switchTab(lastPos, position)
                            Logger.d("onScrolled  lastPos != position ------------")
                        }
                        lastPos = position
                    }
                }
            })
        }
    }

    private fun switchTab(lastPos: Int, position: Int) {
        val lastPosName = rvAdapter!!.getItemView(lastPos).data?.headerName
        val positionName = rvAdapter!!.getItemView(position).data?.headerName
        if (lastPosName != positionName) {
            Logger.d("lastPosName=$lastPosName positionName=$positionName")
            for (homeProductDetail in viewModel.dataResult.value?.raffles!!) {
                if (homeProductDetail.region!!.name == positionName) {
                    containerHelper!!.handlePageSelected(
                        viewModel.dataResult.value?.raffles!!.indexOf(
                            homeProductDetail
                        )
                    )
                }
            }
        }
    }

    private fun initViewAndEvent() {
        dialog =
            parentFragmentManager.findFragmentByTag("ComparisonPriceDialog") as ComparisonPriceDialog?
        if (dialog == null) {
            dialog = ComparisonPriceDialog()
        }
        initRaffleList()
        viewBinding.ivClose.setOnClickListener {
            requireActivity().finish()
        }
        magicIndicator = viewBinding.magicIndicator
        with(viewBinding.layerBottom) {
            layerLike.setOnClickListener(View.OnClickListener { v: View? ->
                viewModel.dataResult.value?.product?.let {
                    val user: DcLoginUser? = getDcAccount()
                    if (user != null) {
                        viewModel.productAction(if (it.liked) ServerConstant.Action.RETRIEVE_LIKE else ServerConstant.Action.LIKE)
                    }
                }
            })

            layerDislike.setOnClickListener(View.OnClickListener { v: View? ->
                viewModel.dataResult.value?.product?.let {
                    val user: DcLoginUser? = getDcAccount()
                    if (user != null) {
                        viewModel.productAction(if (it.disliked) ServerConstant.Action.RETRIEVE_DISLIKE else ServerConstant.Action.DISLIKE)
                    }
                }
            })
            //评论
            layoutComment.setOnClickListener(View.OnClickListener { v: View? ->
                viewModel.dataResult.value?.product?.let {
                    val map = java.util.HashMap<String?, String?>()
                    map[CircleConstant.UriParams.ID] = it.objectId
                    DcRouter(HomeConstant.Uri.REGION_COMMENT)
                        .dialogBottomAni()
                        .appendParams(map)
                        .putExtra(
                            CalendarConstant.DataParam.KEY_PRODUCT,
                            JSONHelper.toJson(it.sketch())
                        )
                        .start()
                }
            })
        }
        with(viewBinding.productVideo) {
            moreFra.setOnClickListener(View.OnClickListener { view: View? ->
                if (viewModel.dataResult.value == null) {
                    return@OnClickListener
                }
                val map: HashMap<String?, String?> = hashMapOf()
                map[PostConstant.UriParams.ID] = viewModel.dataResult.value?.product?.talkId ?: ""
                DcRouter(PostConstant.Uri.MORE_VIEW)
                    .appendParams(map)
                    .start()
            })
        }
        with(viewBinding.layerGoodExtras) {
            homeProductDetailProductSkuLayer.setOnClickListener {

            }
            layerDeal.setOnClickListener {
                DcUriRequest(requireContext(), CircleConstant.Uri.DEAL_DETAILS)
                    .putUriParameter(CircleConstant.UriParams.ID, viewModel.goodId)
                    .start()
            }
            raffleInfoLayerMarketPrice.setOnClickListener {
                val user = AccountHelper.getInstance().user
                if (null == user) {
                    DcRouter(AccountConstant.Uri.INDEX)
                        .start()
                    return@setOnClickListener
                }
                showLoadingDialog()
                viewModel.priceList(viewModel.goodId!!)
            }
        }
    }

    private fun showSmsDialog(homeRaffle: HomeRaffle?) {
        if (homeRaffle == null) {
            return
        }
        val user = getDcAccount()
        if (user == null) {
            return
        }
        if (System.currentTimeMillis() < homeRaffle.smsStartAt) {
            CommonDialog.Builder(requireContext())
                .content(
                    "短信抽签未开始\n短信抽签将于${
                        TimeUtils.formatTimestampS2NoYea2r(
                            homeRaffle.smsStartAt
                        )
                    }开始，请稍后再试～"
                )
                .confirmTxt("提醒我")
                .cancelTxt("取消")
                .onConfirmClick {
                    requestCalendarPermission(homeRaffle)
                }
                .builder()
                .show()
            return
        }
        if (homeRaffle.showOldPage == true) {
            routerToSms(homeRaffle)
        } else {
            openChooseDialog(homeRaffle)
        }
    }

    private fun openChooseDialog(homeRaffle: HomeRaffle?) {
        homeRaffle?.sms?.let { homeRaffleSmsBean ->
            //可以改到viewModel中去
            showLoadingDialog()
            viewModel.calendarApi.smsInputTemplate(homeRaffle.id!!)
                .subscribe({
                    hideLoadingDialog()
                    if (!it.err && it.res != null) {
                        smsRegistrationDialog.setSmsData(
                            homeRaffle.id!!,
                            homeRaffleSmsBean,
                            it.res!!
                        )
                        smsRegistrationDialog.show(parentFragmentManager, "smsRegistrationDialog")
                    } else {
                        ToastUtil.getInstance()._short(context, it.msg)
                    }
                }, {
                    hideLoadingDialog()
                    ToastUtil.getInstance()._short(context, it.message)
                })
        }
    }

    private fun routerToSms(homeRaffle: HomeRaffle) {
        val smsStartAt = homeRaffle.smsStartAt
        if (smsStartAt > System.currentTimeMillis()) {
            showTimingDialog(smsStartAt, homeRaffle)
        } else {
            DcRouter(HomeConstant.Uri.SMS_EDIT)
                .putExtra(CalendarConstant.DataParam.KEY_SMS, homeRaffle.sms)
                .start()
        }
    }

    private fun requestCalendarPermission(homeRaffle: HomeRaffle) {
        PermissionX.init(this)
            .permissions(
                listOf(
                    Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.READ_CALENDAR
                )
            )
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    remind(homeRaffle)
                }
            }
    }


    private fun remind(homeRaffle: HomeRaffle) {
        if (null == viewModel.dataResult.value?.product) {
            return
        }
        val brand = homeRaffle.brand
        val place = if (null == brand) "" else brand.name!!
        val releaseDateString = homeRaffle.releaseDateString
        val method = homeRaffle.method
        val des = """
            ${viewModel.dataResult.value?.product?.name}
            $place
            $releaseDateString
            $method
            """.trimIndent()
        //如果后端设置了短信时间并且未到达短信开始时间,将短信时间设置到提醒日历
        val notifyData: Long = if (homeRaffle.smsStartAt > System.currentTimeMillis()) {
            homeRaffle.smsStartAt
        } else {
            TimeUtils.format2Mill(homeRaffle.notifyDate)
        }
        val addSuccess = CalendarReminderUtils
            .addCalendarEvent(requireContext(), des, "", notifyData)
        showToastShort(if (addSuccess) R.string.home_setup_success else R.string.home_setup_failed)
    }

    private fun showShareView(homeRaffle: HomeRaffle?) {
        if (null == homeRaffle || null == viewModel.dataResult.value?.product) {
            return
        }
        val content = String.format(
            getString(R.string.raffle_share_content_template),
            homeRaffle.brand!!.name,
            viewModel.dataResult.value?.product?.name
        )
        val type = ShareType.Link
        val params = ShareParams()
        params.title = getString(R.string.raffle_share_title)
        params.content = content
        params.imageUrl = viewModel.dataResult.value?.product?.imageUrl
        params.link =
            if (TextUtils.isEmpty(homeRaffle.shareLink)) homeRaffle.link else homeRaffle.shareLink
        type.params = params
        val shareDialog = ShareDialog(requireContext())
        shareDialog.shareType = type
        shareDialog.show()
    }

    private fun showTimingDialog(smsStartAt: Long, homeRaffle: HomeRaffle) {
        val niceDate = TimeUtils.formatTimestampCustom(smsStartAt, "MM月dd日 HH:mm")
        val messageContent = "抽签将在$niceDate 开始,请稍后再试"
        DcTitleDialog.Builder(requireContext())
            .title("短信抽签还未开始")
            .content(messageContent)
            .cancelTxt("取消")
            .confirmTxt("提醒我")
            .onConfirmClick {
                requestCalendarPermission(homeRaffle)
            }
            .builder()
            .show()
    }

    protected fun getDcAccount(): DcLoginUser? {
        val user = AccountHelper.getInstance().user
        if (null == user) {
            DcRouter(AccountConstant.Uri.INDEX)
                .start()
            return null
        }
        return user
    }


    private fun onRaffleLoadSuccess(mHomeProductDetailList: List<HomeProductDetail>) {
        rvAdapter?.apply {
            clearAllItemView()
            clearAllSectionViews()
            clearAllSectionHeadView()
        }
        val titleList: ArrayList<String> = ArrayList()
        if (mHomeProductDetailList.isEmpty()) {
            rvAdapter?.showEmptyView("已结束")
            return
        }
        var isEmpty = true
        for (hpd in mHomeProductDetailList!!) {
            if ((hpd.raffles?.size ?: 0) > 0) {
                isEmpty = false
                break
            }
        }
        if (isEmpty) {
            rvAdapter?.showEmptyView("已结束")
            return
        }
        rvAdapter?.hideEmptyView()
        for (homeProductDetail in mHomeProductDetailList) {
            val region = homeProductDetail.region ?: continue
            val itemList: MutableList<HomeRaffleDetailItem> = ArrayList()
            val raffles = homeProductDetail.raffles ?: continue
            for (i in raffles.indices) {
                val homeRaffle = raffles[i]
                homeRaffle.headerName = region.name
                val homeRaffleItem = HomeRaffleDetailItem(homeRaffle)
                with(homeRaffleItem) {
                    setSource("热门发售")
                    setAnimationDrawHelper(animationDrawHelper)
                    setLast(i == raffles.size - 1)
                    setStart(i == 0)
                    setShowWhere(HomeRaffleDetailItem.ShowWhere.PRODUCT_DETAILS)
                    setMListener(object : HomeRaffleDetailItem.ActionListener {
                        override fun onFlashClick(homeRaffle: HomeRaffle) {
                            val user = getDcAccount()
                            if (user != null) {
                                viewModel.parseLink(homeRaffle.bpLink!!)
                            }
                        }

                        override fun onShareClick(homeRaffle: HomeRaffle) {
                            showShareView(homeRaffle)
                        }

                        override fun onSmsClick(homeRaffle: HomeRaffle) {
                            if (LoginUtils.isLoginAndRequestLogin(requireContext())) {
                                if (System.currentTimeMillis() < homeRaffle.smsStartAt) {
                                    CommonDialog.Builder(requireContext())
                                        .content(
                                            "短信抽签未开始\n短信抽签将于${
                                                TimeUtils.formatTimestampS2NoYea2r(
                                                    homeRaffle.smsStartAt
                                                )
                                            }开始，请稍后再试～"
                                        )
                                        .confirmTxt("提醒我")
                                        .cancelTxt("取消")
                                        .onConfirmClick {
                                            val user = getDcAccount()
                                            if (user != null) {
                                                requestCalendarPermission(homeRaffle)
                                            }
                                        }
                                        .builder()
                                        .show()
                                    return
                                }
                                if (homeRaffle.showOldPage == true) {
                                    val user = getDcAccount()
                                    if (user != null) {
                                        routerToSms(homeRaffle)
                                    }
                                } else {
                                    openChooseDialog(homeRaffle)
                                }
                            }
                        }

                        override fun onAlarmClick(homeRaffle: HomeRaffle, pos: Int) {
                            val user = getDcAccount()
                            if (user != null) {
                                val monitorRemindHelper =
                                    MonitorRemindHelper(AlarmFromWhere.HEAVY_DETAIL, pos, "")

                                val remindDesc = monitorRemindHelper.generateRemindMsg(
                                    viewModel.dataResult.value?.product?.name ?: "",
                                    homeRaffle.brand?.name ?: "",
                                    homeRaffle.notifyDate,
                                    homeRaffle.method ?: "",
                                    homeRaffle.price ?: "",
                                )

                                if (homeRaffle.reminded) {
                                    monitorRemindHelper.cancelRemind(
                                        requireContext(),
                                        homeRaffle.id ?: "",
                                        remindDesc
                                    )
                                } else {
                                    monitorRemindHelper.setRemind(
                                        requireContext(),
                                        childFragmentManager,
                                        homeRaffle.id ?: "",
                                        remindDesc,
                                        homeRaffle.notifyDate
                                    )
                                }
                            }
                        }
                    })
                }
                itemList.add(homeRaffleItem)
            }
            rvAdapter!!.addItemViewList(itemList)
            titleList.add(region.name ?: "")
            //space footer
            if (mHomeProductDetailList.indexOf(homeProductDetail) == raffles.size - 1) {
                lastSectionHeight += SizeUtils.dp2px(130f) * homeProductDetail.raffles!!.size
                if (raffles.size > 1) {
                    lastSectionHeight += SizeUtils.dp2px(40f)
                }
            }
        }
    }

    private fun observerDataChange() {
        viewModel.dataResult.observe(this, Observer {
            it.product?.let { homeProduct ->
                setupBottomBarInfo(homeProduct)
                setInfo(homeProduct)
            }
            it.raffles?.let { raffleList ->
                setupRaffleList(raffleList)
            }
        })
        viewModel.goodDetailLiveData.observe(this) {
            DcRouter(HomeConstant.Uri.BP_GOODS_DETAIL)
                .putExtra(HomeConstant.UriParam.KEY_GOOD_DETAIL, it)
                .start()
        }

        viewModel.priceLivedata.observe(this, Observer {
            dialog?.setData(it)
            dialog?.show(parentFragmentManager, "ComparisonPriceDialog")
        })

        viewModel.likeLiveData.observe(this) {
            updateProductView(it)
        }
    }

    private fun updateProductView(action: String?) {

        viewModel.dataResult.value?.product?.let { mHomeProduct ->
            when (action) {
                ServerConstant.Action.RETRIEVE_LIKE, ServerConstant.Action.LIKE -> {
                    var likeCount = mHomeProduct.likeCount
                    mHomeProduct.likeCount = if (mHomeProduct.liked) --likeCount else ++likeCount
                    mHomeProduct.liked = !mHomeProduct.liked
                    if (mHomeProduct.disliked && mHomeProduct.liked) {
                        mHomeProduct.disliked = false
                        mHomeProduct.dislikeCount = mHomeProduct.dislikeCount - 1
                    }
                    setupBottomBarInfo(mHomeProduct)
                }
                ServerConstant.Action.RETRIEVE_DISLIKE, ServerConstant.Action.DISLIKE -> {
                    var dislikeCount = mHomeProduct.dislikeCount
                    mHomeProduct.dislikeCount =
                        if (mHomeProduct.disliked) --dislikeCount else ++dislikeCount
                    mHomeProduct.disliked = !mHomeProduct.disliked
                    if (mHomeProduct.liked && mHomeProduct.disliked) {
                        mHomeProduct.liked = false
                        mHomeProduct.likeCount = mHomeProduct.likeCount - 1
                    }
                    setupBottomBarInfo(mHomeProduct)
                }
                else -> {
                }
            }
        }
    }


    private fun setupRaffleList(raffles: List<HomeProductDetail>) {
        titleList.clear()
        raffles.forEach { homeProductDetail ->
            homeProductDetail.region?.let { region ->
                titleList.add(region.name!!)
            }
        }
        onRaffleLoadSuccess(raffles)
        setupTab(titleList)
    }

    private fun setupTab(titleList: MutableList<String>) {
        val commonNavigator = CommonNavigatorNew(requireContext())
        val tabPageAdapter = TabScaleAdapter(titleList)
        tabPageAdapter.setStartAndEndTitleSize(14f, 16f)
        tabPageAdapter.setSelectedColor(getCompatColor(R.color.color_text_black1))
        tabPageAdapter.setIndicatorColor(Color.parseColor("#242933"), 0, -1)
        tabPageAdapter.hideIndicator()
        tabPageAdapter.setTabSelectListener { index: Int ->
            containerHelper?.handlePageSelected(index)
            switchToItem(index)
        }
        commonNavigator.setAdapter(tabPageAdapter)
        magicIndicator.navigator = commonNavigator
        containerHelper = FragmentContainerHelper()
        containerHelper?.attachMagicIndicator(magicIndicator)
    }

    private fun switchToItem(tabIndex: Int) {
        val mHomeProductDetailList = viewModel.dataResult.value?.raffles!!
        val (_, raffleList) = mHomeProductDetailList[tabIndex]
        if (CollectionUtils.isEmpty(raffleList)) {
            return
        }
        val homeRaffle = raffleList!![0]
        if (homeRaffle.brand != null) {
            Logger.d("switchToItem to tabIndex=" + tabIndex + " firstItem=" + homeRaffle.brand!!.name)
        }
        for (homeRaffleItem in rvAdapter!!.itemList) {
            if (homeRaffle === homeRaffleItem.data) {
                moveToPosition(rvAdapter!!.itemList.indexOf(homeRaffleItem))
            }
        }
    }

    private fun moveToPosition(position: Int) {
        // 第一个可见的view的位置
        val firstItem = rvLayoutManager.findFirstVisibleItemPosition()
        // 最后一个可见的view的位置
        val lastItem = rvLayoutManager.findLastVisibleItemPosition()
        if (position <= firstItem) {
            // 如果跳转位置firstItem 之前(滑出屏幕的情况)，就smoothScrollToPosition可以直接跳转，
            viewBinding.rvRaffle.smoothScrollToPosition(position)
        } else if (position <= lastItem) {
            // 跳转位置在firstItem 之后，lastItem 之间（显示在当前屏幕），smoothScrollBy来滑动到指定位置
            val top =
                viewBinding.rvRaffle.getChildAt(position - firstItem).top - SizeUtils.dp2px(40f)
            viewBinding.rvRaffle.smoothScrollBy(0, top)
        } else {
            viewBinding.rvRaffle.smoothScrollToPosition(position)
            viewModel.scrollToPosition = position
            canScroll = true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setInfo(homeProduct: HomeProduct) {
        with(viewBinding) {
            tvGoodTitle.text = homeProduct.name
            if (homeProduct.raffleCount <= 0) {
                storeCountLayer.hide(true)
            } else {
                storeCountLayer.visual(true)
                tvRaffleCounts.text = "${homeProduct.raffleCount}条发售"
            }

        }
        with(viewBinding.layerGoodExtras) {
            //发售价格
            homeProductDetailProductPriceTxt.text = homeProduct.price
            //市场价格
            if (homeProduct.marketPrice.isNullOrEmpty()) {
                marketPriceTv.text = "-"
                marketPriceLeftIcon.hide(true)
            } else {
                marketPriceLeftIcon.hide(false)
                marketPriceTv.text = homeProduct.marketPrice
            }
            //交易数目
            if (homeProduct.dealCount == null || homeProduct.dealCount == 0) {
                layerDeal.hide(true)
                dealTv.text = "-"
            } else {
                layerDeal.hide(false)
                dealTv.text = "${homeProduct.dealCount}条"
            }
            //货号
            homeProductDetailProductSkuTxt.text = "${homeProduct.sku}"
            homeProductDetailProductSkuLayer.setOnLongClickListener {
                homeProduct.sku?.let { sku ->
                    ClipboardHelper.showMenu(
                        requireContext(),
                        sku,
                        homeProductDetailProductSkuTxt
                    ) {
                        runOnUiThread {
                            showToastShort("复制货号成功")
                        }
                    }
                }
                true
            }
        }
    }

    private fun setupBottomBarInfo(homeProduct: HomeProduct) {
        with(viewBinding.layerBottom) {
            tvComment.text = "${homeProduct.commentCount}"
            tvLike.text = "${homeProduct.likeCount}"
            tvDislike.text = "${homeProduct.dislikeCount}"
            tvLike.setTextColor(getCompatColor(R.color.color_text_black3))
            tvDislike.setTextColor(getCompatColor(R.color.color_text_black3))
            if (homeProduct.liked) {
                tvLike.setTextColor(getCompatColor(R.color.color_red))
                tvDislike.setTextColor(getCompatColor(R.color.color_text_black3))
                ivLike.tintColorRes(R.color.color_red)
                ivDislike.tintColorRes(R.color.color_text_black3)
            } else {
                tvLike.setTextColor(getCompatColor(R.color.color_text_black3))
                ivLike.tintColorRes(R.color.color_text_black3)
            }
            if (homeProduct.disliked) {
                ivDislike.tintColorRes(R.color.color_green)
                tvDislike.setTextColor(getCompatColor(R.color.color_green))
                tvLike.setTextColor(getCompatColor(R.color.color_text_black3))
                ivLike.tintColorRes(R.color.color_text_black3)
            } else {
                tvDislike.setTextColor(getCompatColor(R.color.color_text_black3))
                ivDislike.tintColorRes(R.color.color_text_black3)
            }

        }
    }

    companion object {
        private const val GOOD_ID = "good_id"
        fun instant(goodId: String): CalendarFragmentHeavyRaffle {
            return CalendarFragmentHeavyRaffle().apply {
                arguments = Bundle().apply {
                    putString(GOOD_ID, goodId)
                }
            }
        }
    }
}