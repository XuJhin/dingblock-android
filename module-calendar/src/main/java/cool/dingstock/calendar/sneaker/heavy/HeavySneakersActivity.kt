package cool.dingstock.calendar.sneaker.heavy

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.adapter.CommonLoopVpAdapter
import cool.dingstock.appbase.constant.*
import cool.dingstock.appbase.customerview.CommonNavigatorNew
import cool.dingstock.appbase.entity.bean.home.*
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.getColorRes
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.helper.MonitorRemindHelper
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.share.ShareParams
import cool.dingstock.appbase.share.ShareType
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.*
import cool.dingstock.appbase.widget.commondialog.CommonDialog
import cool.dingstock.appbase.widget.dialog.common.DcTitleDialog
import cool.dingstock.appbase.widget.recyclerview.adapter.BaseRVAdapter
import cool.dingstock.appbase.widget.tablayout.TabScaleAdapter
import cool.dingstock.calendar.R
import cool.dingstock.calendar.adapter.footer.ProductDetailFooter
import cool.dingstock.calendar.databinding.ActivityHeavySneakersBinding
import cool.dingstock.calendar.item.HeavySneakerCardView
import cool.dingstock.imagepicker.utils.PPermissionUtils
import cool.dingstock.lib_base.json.JSONHelper
import cool.dingstock.lib_base.util.*
import cool.dingstock.uicommon.adapter.StickyHeaderDecoration
import cool.dingstock.uicommon.calendar.dialog.ComparisonPriceDialog
import cool.dingstock.uicommon.helper.AnimationDrawHelper
import cool.dingstock.uicommon.product.dialog.SmsRegistrationDialog
import cool.dingstock.uicommon.product.item.HomeRaffleDetailItem
import cool.mobile.account.share.ShareDialog
import cool.mobile.account.share.ShareHelper
import net.lucode.hackware.magicindicator.FragmentContainerHelper
import net.lucode.hackware.magicindicator.MagicIndicator
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [HomeConstant.Path.HEAVY]
)
class HeavySneakersActivity : VMBindingActivity<HeavySneakersVm, ActivityHeavySneakersBinding>() {
    private val PERMISSIONS_CALENDAR = arrayOf(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR
    )
    private var mRequestCode = 101
    var recommendList: ArrayList<CalenderProductEntity>? = null
    val animationDrawHelper = AnimationDrawHelper()

    var postion = 0
    lateinit var raffleRv: RecyclerView
    lateinit var magicIndicator: MagicIndicator
    lateinit var storeCountTv: TextView
    lateinit var storeCountLayer: View
    private val loopVpAdapter by lazy { CommonLoopVpAdapter(arrayListOf()) }
    private var layoutManager: LinearLayoutManager? = null
    private var decoration: StickyHeaderDecoration? = null
    private var rvAdapter: BaseRVAdapter<HomeRaffleDetailItem>? = null
    private var mHomeProductDetailList: List<HomeProductDetail>? = null
    private var containerHelper: FragmentContainerHelper? = null
    private var scrollToPosition = 0
    private var canScroll = false
    private var isUserTriggers = false
    private var lastPos = 0
    private var lastSectionHeight = 0
    private var productId: String? = null
    private var mHomeProduct: HomeProduct? = null
    private var mActionHomeRaffle: HomeRaffle? = null
    private var currentPosition = 0
    private var isFirstLoad = true
    private val smsRegistrationDialog by lazy {
        SmsRegistrationDialog()
    }
    private val comparisonPriceDialog by lazy {
        ComparisonPriceDialog()
    }

    override fun setSystemStatusBar() {
        super.setSystemStatusBar()
        StatusBarUtil.transparentStatus(this)
        StatusBarUtil.setNavigationBarColor(
            this,
            getCompatColor(R.color.calendar_bottom_action_bg_color)
        )
    }

    override fun fakeStatusView(): View? {
        return viewBinding.fakeStatusBar
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        initView()
        initObserver()
        initTitleBar()
        recommendList =
            intent.getParcelableArrayListExtra<CalenderProductEntity>(CalendarConstant.DataParam.HEAVY_SNEAKER_LIST)
        if (recommendList == null || recommendList?.size == 0) {
            //这里做loading
            viewModel.loadHeavy()
            return
        }
        initMinView()
    }

    private fun initMinView() {
        postion = intent.getIntExtra(CalendarConstant.DataParam.HEAVY_SNEAKER_LIST_POSITION, 0)
        currentPosition = postion
        initRaffleList()
        initViewPager()
        setViewPagerData()
    }

    private fun initView() {
        raffleRv = findViewById(R.id.raffleRv)
        magicIndicator = findViewById(R.id.magicIndicator)
        storeCountTv = findViewById(R.id.store_count_tv)
        storeCountLayer = findViewById(R.id.store_count_layer)
    }


    override fun initListeners() {
        viewBinding.bottomActionBar.homeProductDetailCommentLayer.setOnShakeClickListener {
            if (mHomeProduct == null) {
                return@setOnShakeClickListener
            }
            DcRouter(HomeConstant.Uri.REGION_COMMENT)
                .dialogBottomAni()
                .putUriParameter(CircleConstant.UriParams.ID, productId)
                .putExtra(
                    CalendarConstant.DataParam.KEY_PRODUCT,
                    JSONHelper.toJson(mHomeProduct?.sketch())
                )
                .start()
        }
        viewBinding.bottomActionBar.homeProductDetailLikeLayer.setOnShakeClickListener {
            if (!LoginUtils.isLoginAndRequestLogin(this)) {
                return@setOnShakeClickListener
            }
            if (mHomeProduct == null) {
                return@setOnShakeClickListener
            }
            viewModel.productAction(
                if (mHomeProduct!!.liked) ServerConstant.Action.RETRIEVE_LIKE else ServerConstant.Action.LIKE,
                productId
                    ?: ""
            )
        }
        viewBinding.bottomActionBar.homeProductDetailDislikeLayer.setOnShakeClickListener {
            if (!LoginUtils.isLoginAndRequestLogin(this)) {
                return@setOnShakeClickListener
            }
            if (mHomeProduct == null) {
                return@setOnShakeClickListener
            }

            viewModel.productAction(
                if (mHomeProduct!!.disliked) ServerConstant.Action.RETRIEVE_DISLIKE else ServerConstant.Action.DISLIKE,
                productId
                    ?: ""
            )
        }
        viewBinding.homeProductDetailBackIcon.setOnClickListener {
            finish()
        }
        viewBinding.homeProductDetailShareFra.setOnShakeClickListener {
            if (mHomeProduct == null) {
                return@setOnShakeClickListener
            }
            UTHelper.commonEvent(UTConstant.BP.HotSaleP_click_Share)
            shareWxMiniProgram()
        }
        viewBinding.titleBar.setRightOnClickListener { v: View? -> shareWxMiniProgram() }
        viewBinding.appBar.addOnOffsetChangedListener(OnOffsetChangedListener { appbar: AppBarLayout, verticalOffset: Int ->
            if (null == viewBinding.titleBar) {
                return@OnOffsetChangedListener
            }
            val totalScrollRange = appbar.totalScrollRange
            if (totalScrollRange == 0) {
                viewBinding.titleBar.visibility = View.VISIBLE
                viewBinding.fakeStatusBar.setBackgroundColor(Color.TRANSPARENT)
                return@OnOffsetChangedListener
            }
            val percent = Math.abs(verticalOffset) / totalScrollRange
            if (percent == 1) {
                viewBinding.titleBar.visibility = View.VISIBLE
                viewBinding.fakeStatusBar.setBackgroundColor(getColorRes(R.color.white))
            } else {
                if (null != viewBinding.titleBar) {
                    viewBinding.titleBar.clearAnimation()
                    viewBinding.titleBar.visibility = View.INVISIBLE
                    viewBinding.fakeStatusBar.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        })
        viewBinding.raffleRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (canScroll) {
                    canScroll = false
                    moveToPosition(scrollToPosition)
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
                    val position = layoutManager!!.findFirstVisibleItemPosition()
                    if (lastPos != position) {
                        switchTab(lastPos, position)
                        Logger.d("onScrolled  lastPos != position ------------")
                    }
                    lastPos = position
                }
            }
        })
    }


    private fun initObserver() {
        viewModel.goodDetailLiveData.observe(this) {
            DcRouter(HomeConstant.Uri.BP_GOODS_DETAIL)
                .putExtra(HomeConstant.UriParam.KEY_GOOD_DETAIL, it)
                .start()
        }
        viewModel.raffleLiveData.observe(this) {
            onRaffleLoadSuccess(it)
        }
        viewModel.productInfoLiveData.observe(this) {
            viewBinding.bottomActionBar.homeProductDetailCommentTxt.text =
                it.commentCount.toString()
            refreshLikeAndDisLike(it)
            mHomeProduct = it
        }
        viewModel.likeLiveData.observe(this) {
            updateProductView(it)
        }
        viewModel.heavyListLiveData.observe(this) {
            recommendList = it
            initMinView()
        }
        viewModel.priceListLiveData.observe(this) {
            comparisonPriceDialog.setData(it)
            comparisonPriceDialog.show(supportFragmentManager, "comparisonPriceDialog")
        }
    }

    private fun initTitleBar() {
        viewBinding.titleBar.title = getString(R.string.heavy_title)
        viewBinding.titleBar.setLineVisibility(false)
        val iv = ImageView(this)
        iv.setSvgColorRes(R.drawable.ic_share_icon, R.color.series_details_btn_bg)
        viewBinding.titleBar.setRightView(iv)
    }

    private fun initBg(color: Int) {
        val gd =
            GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(color, resources.getColor(R.color.color_gray))
            )
        viewBinding.bgV.background = gd
    }

    private fun initRaffleList() {
        decoration = object : StickyHeaderDecoration(0) {
            override fun getHeaderName(pos: Int): String {
                val itemView = rvAdapter!!.getItemView(pos) ?: return ""
                val headerName = itemView.data?.headerName ?: return ""
                mHomeProductDetailList?.let {
                    for (homeProductDetail in mHomeProductDetailList!!) {
                        if (headerName == homeProductDetail.region!!.name && mHomeProductDetailList?.indexOf(
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
                context,
                cool.dingstock.appbase.R.color.color_text_black1
            )
        )
        decoration?.setTextSize(SizeUtils.sp2px(14f))
        decoration?.setTextPaddingLeft(24)
        rvAdapter = BaseRVAdapter()
        layoutManager = LinearLayoutManager(context)
        raffleRv.layoutManager = layoutManager
        raffleRv.adapter = rvAdapter
        decoration?.let {
            raffleRv.addItemDecoration(it)
        }
    }

    private fun initViewPager() {
        viewBinding.viewPager.pageMargin = 15.dp.toInt()
        viewBinding.viewPager.adapter = loopVpAdapter
        viewBinding.viewPager.offscreenPageLimit = 2
        viewBinding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                try {
                    currentPosition = position % loopVpAdapter.viewList.size
                    recommendList?.get(currentPosition)?.let {
                        viewModel.loadRaffle(it)
                        storeCountTv.text = "${it.raffleCount}条发售"
                        storeCountLayer.hide((it.raffleCount ?: 0) == 0)
                        productId = it.id ?: ""
                        initBg(Color.parseColor(it.color ?: "#000000"))
                    }
                } catch (e: Exception) {
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (!isFirstLoad) {
//            animationDrawHelper.isFisrtShow = false
            recommendList?.get(currentPosition)?.let {
                viewModel.loadRaffle(it)
                productId = it.id ?: ""
            }
        }
        isFirstLoad = false
    }

    private fun setViewPagerData() {
        if (recommendList == null) {
            return
        }
        val list = arrayListOf<CalenderProductEntity>()
        list.addAll(recommendList!!)
        while (recommendList!!.size < 5) {
            recommendList?.addAll(list)
        }
        for (entity in recommendList!!) {
            val cardView = HeavySneakerCardView(context, entity)
            cardView.setOnClickListener {
                UTHelper.commonEvent(
                    UTConstant.SaleDetails.SaleDetailsP_source,
                    "position",
                    "热门发售页"
                )
                DcRouter(HomeConstant.Uri.DETAIL)
                    .putUriParameter(HomeConstant.UriParam.KEY_PRODUCT_ID, entity.id)
                    .start()
            }
            cardView.setOnSearchPriceClick {
                UTHelper.commonEvent(UTConstant.Calendar.SaleDetailsP_click_DWprice)
                val user = getAccount()
                if (user != null) {
                    viewModel.searchPrice(entity.id)
                }
            }
            loopVpAdapter.viewList.add(cardView)
        }
        loopVpAdapter.notifyDataSetChanged()
        val i = loopVpAdapter.viewList.size * 100 + postion
        viewBinding.viewPager.currentItem = i
    }

    private fun updateProductView(action: String?) {
        if (null == mHomeProduct) {
            return
        }
        when (action) {
            ServerConstant.Action.RETRIEVE_LIKE, ServerConstant.Action.LIKE -> {
                var likeCount = mHomeProduct!!.likeCount
                mHomeProduct!!.likeCount = if (mHomeProduct!!.liked) --likeCount else ++likeCount
                mHomeProduct!!.liked = !mHomeProduct!!.liked
                if (mHomeProduct!!.disliked && mHomeProduct!!.liked) {
                    mHomeProduct!!.disliked = false
                    mHomeProduct!!.dislikeCount = mHomeProduct!!.dislikeCount - 1
                }
                refreshLikeAndDisLike(mHomeProduct)
            }
            ServerConstant.Action.RETRIEVE_DISLIKE, ServerConstant.Action.DISLIKE -> {
                var dislikeCount = mHomeProduct!!.dislikeCount
                mHomeProduct!!.dislikeCount =
                    if (mHomeProduct!!.disliked) --dislikeCount else ++dislikeCount
                mHomeProduct!!.disliked = !mHomeProduct!!.disliked
                if (mHomeProduct!!.liked && mHomeProduct!!.disliked) {
                    mHomeProduct!!.liked = false
                    mHomeProduct!!.likeCount = mHomeProduct!!.likeCount - 1
                }
                refreshLikeAndDisLike(mHomeProduct)
            }
            else -> {
            }
        }
    }

    private fun refreshLikeAndDisLike(homeProduct: HomeProduct?) {
        if (null == homeProduct) {
            return
        }
        viewBinding.bottomActionBar.homeProductDetailLikeIcon.isSelected = homeProduct.liked
        viewBinding.bottomActionBar.homeProductDetailDislikeIcon.isSelected = homeProduct.disliked
        viewBinding.bottomActionBar.homeProductDetailLikeTxt.isSelected = homeProduct.liked
        viewBinding.bottomActionBar.homeProductDetailDislikeTxt.isSelected = homeProduct.disliked
        viewBinding.bottomActionBar.homeProductDetailLikeTxt.text = homeProduct.likeCount.toString()
        viewBinding.bottomActionBar.homeProductDetailDislikeTxt.text =
            homeProduct.dislikeCount.toString()
    }

    private fun onRaffleLoadSuccess(it: List<HomeProductDetail>) {
        mHomeProductDetailList = it
        rvAdapter?.clearAllItemView()
        rvAdapter?.clearAllSectionViews()
        rvAdapter?.clearAllSectionHeadView()
        val titleList: ArrayList<String> = ArrayList()
        if (mHomeProductDetailList == null || mHomeProductDetailList?.size == 0) {
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

        for (homeProductDetail in mHomeProductDetailList!!) {
            val region = homeProductDetail.region ?: continue
            val itemList: MutableList<HomeRaffleDetailItem> = ArrayList()
            val raffles = homeProductDetail.raffles ?: continue
            for (i in raffles.indices) {
                val homeRaffle = raffles[i]
                homeRaffle.headerName = region.name
                val homeRaffleItem = HomeRaffleDetailItem(homeRaffle)
                homeRaffleItem.setSource("热门发售")
                homeRaffleItem.setAnimationDrawHelper(animationDrawHelper)
                homeRaffleItem.setLast(i == raffles.size - 1)
                homeRaffleItem.setStart(i == 0)
                homeRaffleItem.setShowWhere(HomeRaffleDetailItem.ShowWhere.PRODUCT_DETAILS)
                homeRaffleItem.setMListener(object : HomeRaffleDetailItem.ActionListener {
                    override fun onFlashClick(homeRaffle: HomeRaffle) {
                        val user = getAccount()
                        if (user != null) {
                            viewModel.parseLink(homeRaffle.bpLink!!)
                        }
                    }

                    override fun onShareClick(homeRaffle: HomeRaffle) {
                        showShareView(homeRaffle)
                    }

                    override fun onSmsClick(homeRaffle: HomeRaffle) {
                        if (LoginUtils.isLoginAndRequestLogin(context)) {
                            if (System.currentTimeMillis() < homeRaffle.smsStartAt) {
                                CommonDialog.Builder(this@HeavySneakersActivity)
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
                                        val user = getAccount()
                                        if (user != null) {
                                            requestCalendarPermission(homeRaffle)
                                        }
                                    }
                                    .builder()
                                    .show()
                                return
                            }
                            if (homeRaffle.showOldPage == true) {
                                val user = getAccount()
                                if (user != null) {
                                    routerToSms(homeRaffle)
                                }
                            } else {
                                openChooseDialog(homeRaffle)
                            }
                        }
                    }

                    override fun onAlarmClick(homeRaffle: HomeRaffle, pos: Int) {
                        val user = getAccount()
                        if (user != null) {
                            val monitorRemindHelper =
                                MonitorRemindHelper(AlarmFromWhere.HEAVY_DETAIL, pos, "")

                            val remindDesc = monitorRemindHelper.generateRemindMsg(
                                viewModel.productName,
                                homeRaffle.brand?.name ?: "",
                                homeRaffle.notifyDate,
                                homeRaffle.method ?: "",
                                homeRaffle.price ?: "",
                            )

                            if (homeRaffle.reminded) {
                                monitorRemindHelper.cancelRemind(
                                    context,
                                    homeRaffle.id ?: "",
                                    remindDesc
                                )
                            } else {
                                monitorRemindHelper.setRemind(
                                    context,
                                    supportFragmentManager,
                                    homeRaffle.id ?: "",
                                    remindDesc,
                                    homeRaffle.notifyDate
                                )
                            }
                        }
                    }
                })
                itemList.add(homeRaffleItem)
            }
            rvAdapter!!.addItemViewList(itemList)
            titleList.add(region.name ?: "")
            //space footer
            if (it.indexOf(homeProductDetail) == it.size - 1) {
                lastSectionHeight += SizeUtils.dp2px(130f) * homeProductDetail.raffles!!.size
                if (it.size > 1) {
                    lastSectionHeight += SizeUtils.dp2px(40f)
                }
                raffleRv.post {
                    rvAdapter!!.addFootView(ProductDetailFooter(SizeUtils.dp2px(80f)))
                }
            }
        }
        //tab
        setTab(titleList)
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
                        smsRegistrationDialog.show(supportFragmentManager, "smsRegistrationDialog")
                    } else {
                        ToastUtil.getInstance()._short(context, it.msg)
                    }
                }, {
                    hideLoadingDialog()
                    ToastUtil.getInstance()._short(context, it.message)
                })
        }
    }


    private fun setTab(titleList: List<String>) {
        val commonNavigator = CommonNavigatorNew(context)
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
        val (_, raffleList) = mHomeProductDetailList!![tabIndex]
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
        val firstItem = layoutManager!!.findFirstVisibleItemPosition()
        // 最后一个可见的view的位置
        val lastItem = layoutManager!!.findLastVisibleItemPosition()
        if (position <= firstItem) {
            // 如果跳转位置firstItem 之前(滑出屏幕的情况)，就smoothScrollToPosition可以直接跳转，
            raffleRv.smoothScrollToPosition(position)
        } else if (position <= lastItem) {
            // 跳转位置在firstItem 之后，lastItem 之间（显示在当前屏幕），smoothScrollBy来滑动到指定位置
            val top = raffleRv.getChildAt(position - firstItem).top - SizeUtils.dp2px(40f)
            raffleRv.smoothScrollBy(0, top)
        } else {
            raffleRv.smoothScrollToPosition(position)
            scrollToPosition = position
            canScroll = true
        }
    }

    private fun switchTab(lastPos: Int, position: Int) {
        val lastPosName = rvAdapter!!.getItemView(lastPos).data?.headerName
        val positionName = rvAdapter!!.getItemView(position).data?.headerName
        if (lastPosName != positionName) {
            Logger.d("lastPosName=$lastPosName positionName=$positionName")
            for (homeProductDetail in mHomeProductDetailList!!) {
                if (homeProductDetail.region!!.name == positionName) {
                    containerHelper!!.handlePageSelected(
                        mHomeProductDetailList!!.indexOf(
                            homeProductDetail
                        )
                    )
                }
            }
        }
    }

    private fun showShareView(homeRaffle: HomeRaffle?) {
        if (null == homeRaffle || null == mHomeProduct) {
            return
        }
        val content = String.format(
            getString(R.string.raffle_share_content_template),
            homeRaffle.brand!!.name,
            mHomeProduct?.name
        )
        val type = ShareType.Link
        val params = ShareParams()
        params.title = getString(R.string.raffle_share_title)
        params.content = content
        params.imageUrl = mHomeProduct?.imageUrl
        params.link =
            if (TextUtils.isEmpty(homeRaffle.shareLink)) homeRaffle.link else homeRaffle.shareLink
        type.params = params
        val shareDialog = ShareDialog(context)
        shareDialog.shareType = type
        shareDialog.show()
    }

    private fun shareWxMiniProgram() {
        // TODO: 2020/11/6 优化分享
        if (null == mHomeProduct) {
            return
        }
        mHomeProduct?.let {
            val path = "pages/start/start?detailId=" + mHomeProduct!!.objectId
            val title = it.name + "有新的发售信息"
            val content = it.name + "有新的发售信息"
            val imageUrl = it.imageUrl ?: ""
            val shareHelper = ShareHelper()
            shareHelper.shareToMiniProgram(path, title, content, imageUrl, context)
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

    private fun showTimingDialog(smsStartAt: Long, homeRaffle: HomeRaffle) {
        val niceDate = TimeUtils.formatTimestampCustom(smsStartAt, "MM月dd日 HH:mm")
        val messageContent = "抽签将在$niceDate 开始,请稍后再试"
        DcTitleDialog.Builder(this)
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

    private fun requestCalendarPermission(homeRaffle: HomeRaffle) {
        mActionHomeRaffle = homeRaffle
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_CALENDAR, ++mRequestCode)
        } else {
            remind()
        }
    }

    private fun remind() {
        if (null == mHomeProduct || null == mActionHomeRaffle) {
            return
        }
        val brand = mActionHomeRaffle!!.brand
        val place = if (null == brand) "" else brand.name!!
        val releaseDateString = mActionHomeRaffle!!.releaseDateString
        val method = mActionHomeRaffle!!.method
        val des = """
            ${mHomeProduct?.name}
            $place
            $releaseDateString
            $method
            """.trimIndent()
        //如果后端设置了短信时间并且未到达短信开始时间,将短信时间设置到提醒日历
        val notifyData: Long = if (mActionHomeRaffle!!.smsStartAt > System.currentTimeMillis()) {
            mActionHomeRaffle!!.smsStartAt
        } else {
            TimeUtils.format2Mill(mActionHomeRaffle!!.notifyDate)
        }
        val addSuccess = CalendarReminderUtils
            .addCalendarEvent(this, des, "", notifyData)
        showToastShort(if (addSuccess) R.string.home_setup_success else R.string.home_setup_failed)
        mActionHomeRaffle = null
    }

    override fun moduleTag(): String = ModuleConstant.CALENDAR

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshAlarm(event: AlarmRefreshEvent) {
        if (event.fromWhere === AlarmFromWhere.HEAVY_DETAIL) {
            rvAdapter!!.getItemView(event.pos).data?.reminded = event.isLightUpAlarm
            rvAdapter!!.notifyItemChanged(event.pos)
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //添加日历事件申请权限被拒绝后
        grantResults.forEach { permissionState ->
            if (permissionState == PackageManager.PERMISSION_DENIED) {
                context?.let {
                    CommonDialog.Builder(it)
                        .content("需要获得授权访问您的日历")
                        .confirmTxt("前往授权")
                        .cancelTxt("取消")
                        .onConfirmClick {
                            val routeHelper = PPermissionUtils(context)
                            routeHelper.gotoPermissionSet()
                        }
                        .builder()
                        .show()
                }
                return
            }
        }
    }
}