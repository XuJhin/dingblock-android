package cool.dingstock.tide.index

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import com.youth.banner.loader.ImageLoaderInterface
import cool.dingstock.appbase.adapter.LoadMoreBinderAdapter
import cool.dingstock.appbase.constant.CalendarConstant
import cool.dingstock.appbase.constant.CircleConstant
import cool.dingstock.appbase.constant.HomeConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.common.DateBean
import cool.dingstock.appbase.entity.bean.tide.GoodItem
import cool.dingstock.appbase.entity.bean.tide.TideItemEntity
import cool.dingstock.appbase.entity.bean.tide.TideSectionsEntity
import cool.dingstock.appbase.ext.azDp
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.mvp.lazy.VmBindingLazyFragment
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.CalendarReminderUtils
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.viewmodel.HomeIndexViewModel
import cool.dingstock.appbase.widget.stickyheaders.StickyHeaderLayoutManager
import cool.dingstock.lib_base.json.JSONHelper
import cool.dingstock.lib_base.util.CollectionUtils
import cool.dingstock.lib_base.util.ScreenUtils
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.lib_base.widget.tabs.DcTabLayout
import cool.dingstock.tide.R
import cool.dingstock.tide.adapter.TideSectionAdapter
import cool.dingstock.tide.adapter.viewholder.OnTideItemActionClickListener
import cool.dingstock.tide.databinding.FragmentTideHomeIndexBinding
import cool.dingstock.tide.dialog.TideDealInfoFilterDialog
import cool.dingstock.tide.dialog.TideFilterDialog
import cool.dingstock.tide.item.RecommendItemBinder
import cool.dingstock.uicommon.widget.MonthTabViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("NotifyDataSetChanged")
class TideHomeIndexFragment :
    VmBindingLazyFragment<TideHomeIndexVM, FragmentTideHomeIndexBinding>() {

    private var mRequestCode = 101
    private var mTideItemEntity: TideItemEntity? = null
    private var remTideList: ArrayList<TideSectionsEntity>? = arrayListOf()
    private var isScrolled = false

    private val homeIndexViewModel by lazy {
        ViewModelProvider(requireActivity())[HomeIndexViewModel::class.java]
    }

    companion object {
        private val PERMISSIONS_CALENDAR = arrayOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        )

        @JvmStatic
        fun newInstance() =
            TideHomeIndexFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    private val recommendAdapter: LoadMoreBinderAdapter by lazy {
        LoadMoreBinderAdapter()
    }

    private val recommendItemBinder: RecommendItemBinder by lazy {
        RecommendItemBinder()
    }

    private val sectionAdapter by lazy {
        TideSectionAdapter()
    }
    private val stickyHeaderLayoutManager by lazy {
        StickyHeaderLayoutManager()
    }
    var currentIndex = -1
    private val tideFilterDialog by lazy {
        TideFilterDialog(requireContext())
    }

    private val dealFilterDialog by lazy {
        TideDealInfoFilterDialog(requireContext())
    }

    override fun onStatusViewErrorClick() {
        super.onStatusViewErrorClick()
        viewModel.loadData(isFirstLoad = true, needRefreshTab = true)
    }

    override fun onLazy() {
        viewBinding.refreshLayout.isEnabled = true
        viewModel.getMonthList()
        viewModel.loadData(isFirstLoad = true, needRefreshTab = true)
    }


    override fun initVariables(
        rootView: View?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        viewBinding.refreshLayout.isEnabled = true
        viewBinding.refreshLayout.apply {
            val lp = layoutParams as FrameLayout.LayoutParams
            lp.topMargin = SizeUtils.dp2px(40f) + SizeUtils.getStatusBarHeight(requireContext())
            layoutParams = lp
        }
        initView()
        initObserver()
    }


    override fun initListeners() {
        viewBinding.tabLayout.addOnTabSelectedListener(object : DcTabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: DcTabLayout.Tab?) {
                tab?.customView?.let { v ->
                    context?.let {
                        val tabVh = MonthTabViewHolder(it, v)
                        tabVh.setSelect(true)
                    }
                }
                //切换数据
                if (currentIndex != -1 && currentIndex != tab?.position) {
                    currentIndex = tab?.position ?: viewModel.currentMonthIndex
                    viewModel.currentMonthIndex = tab?.position ?: viewModel.currentMonthIndex
                    //重置只看订阅按钮
                    viewBinding.onlySubscribeIv.isSelected = false
                    viewModel.loadData(isFirstLoad = false, needRefreshTab = false)
                    UTHelper.commonEvent(
                        UTConstant.Tide.CalendarCW_click_Month,
                        "month",
                        "${currentIndex + 1}"
                    )
                }

            }

            override fun onTabUnselected(tab: DcTabLayout.Tab?) {
                tab?.customView?.let { v ->
                    context?.let {
                        val tabVh = MonthTabViewHolder(it, v)
                        tabVh.setSelect(false)
                    }
                }
            }

            override fun onTabReselected(tab: DcTabLayout.Tab?) {
            }

        })

        viewBinding.filterLayer.setOnShakeClickListener {
            getTabType()?.let {
                UTHelper.commonEvent(UTConstant.Tide.CalendarCW_click_Screen, "type", it)
            }
            tideFilterDialog.show()
        }

        viewBinding.platformLayer.setOnShakeClickListener {
            UTHelper.commonEvent(UTConstant.Tide.CalendarCW_Plick_Platform)
            tideFilterDialog.show()
        }

        viewBinding.dealInfoLayer.setOnShakeClickListener {
            UTHelper.commonEvent(UTConstant.Tide.CalendarCW_Plick_Trade)
            dealFilterDialog.show()
        }

        tideFilterDialog.setOnConfirmClick {
            //加载数据
//            filterAdapter.notifyDataSetChanged()
            //颜色变化
            checkFilterState()
            viewModel.reLoadData(viewBinding.onlySubscribeIv.isSelected)
        }

        dealFilterDialog.setOnConfirmClick {
            checkDealInfoState()
            viewModel.reLoadData(viewBinding.onlySubscribeIv.isSelected)
        }
        viewBinding.refreshLayout.setOnRefreshListener {
            viewModel.reLoadData(viewBinding.onlySubscribeIv.isSelected)
        }
        viewBinding.onlySubscribeLayer.setOnShakeClickListener {
            getTabType()?.let {
                UTHelper.commonEvent(UTConstant.Tide.CalendarCW_click_Subscribe, "type", it)
            }
            context?.let { cnx ->
                if (!LoginUtils.isLoginAndRequestLogin(cnx)) {
                    return@setOnShakeClickListener
                }
                viewBinding.onlySubscribeIv.isSelected =
                    !viewBinding.onlySubscribeIv.isSelected
                viewModel.reLoadData(viewBinding.onlySubscribeIv.isSelected)
            }
        }

        sectionAdapter.listener = object : OnTideItemActionClickListener {
            override fun onLookClick(entity: TideItemEntity) {
                getTabType()?.let {
                    UTHelper.commonEvent(UTConstant.Tide.CalendarCW_click_Card, "type", it)
                }
                context?.let { cnx ->
                    if (!LoginUtils.isLoginAndRequestLogin(cnx)) {
                        return
                    }
                    if (!TextUtils.isEmpty(entity.linkUrl)) {
                        DcRouter(entity.linkUrl ?: "").start()
                    } else {
                        ToastUtil.getInstance()._short(cnx, "暂无购买链接")
                    }
                }

            }


            override fun onSubscribeClick(view: View, entity: TideItemEntity) {
                context?.let { cnx ->
                    if (!LoginUtils.isLoginAndRequestLogin(cnx)) {
                        return
                    }
                    viewModel.subscribeChange(entity)
                    requestCalendarPermission(entity)
                }
            }

            override fun onComment(view: View, entity: TideItemEntity) {
                var type = CalendarConstant.CommentType.TIDE
                if (viewModel.tabLiveData.value?.size ?: 0 >= 2) {
                    type = getTypeId()
                }
                DcRouter(HomeConstant.Uri.REGION_COMMENT)
                    .putUriParameter(CircleConstant.UriParams.ID, entity.id)
                    .putUriParameter(
                        CalendarConstant.UrlParam.COMMENT_TYPE, type
                    )
                    .dialogBottomAni()
                    .putExtra(
                        CalendarConstant.DataParam.KEY_PRODUCT,
                        JSONHelper.toJson(entity.sketch())
                    )
                    .start()
            }

            override fun onLikeClick(view: View, entity: TideItemEntity, refreshDislike: Boolean) {
                viewModel.tideApi.favoredTideProduct(entity.id, !entity.liked).subscribe({
                    if (!it.err) {
                        entity.liked = !entity.liked
                        if (entity.liked) {
                            entity.likeCount++
                            if (entity.dislikeCount > 0 && refreshDislike) {
                                entity.dislikeCount--
                            }
                            entity.disliked = false
                        } else {
                            if (entity.likeCount > 0) {
                                entity.likeCount--
                            }
                        }
                        sectionAdapter.notifyDataSetChanged()
                    }
                }, {})
            }

            override fun onDisLikeClick(view: View, entity: TideItemEntity, refreshLike: Boolean) {
                viewModel.tideApi.disLikeTideProduct(entity.id, !entity.disliked).subscribe({
                    if (!it.err) {
                        entity.disliked = !entity.disliked
                        if (entity.disliked) {
                            entity.dislikeCount++
                            if (entity.likeCount > 0 && refreshLike) {
                                entity.likeCount--
                            }
                            entity.liked = false
                        } else {
                            if (entity.dislikeCount > 0) {
                                entity.dislikeCount--
                            }
                        }
                        sectionAdapter.notifyDataSetChanged()
                    }
                }, {})
            }
        }

        recommendItemBinder.onClickActionListener =
            object : RecommendItemBinder.OnClickActionListener {
                override fun onItemClick(id: String) {
                    if (!isScrolled) {
                        moveTideRVTo(id)
                    }
                }
            }

        viewBinding.tideRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                isScrolled = newState != RecyclerView.SCROLL_STATE_IDLE
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val isTop =
                    (stickyHeaderLayoutManager.getFirstVisibleItemViewHolder(true)?.positionInSection == 0
                            && stickyHeaderLayoutManager.getFirstVisibleItemViewHolder(true)?.section == 0)
                            || sectionAdapter.data.isNullOrEmpty()
                Log.e("isTop", isTop.toString())
                viewBinding.refreshLayout.isEnabled = isTop
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        viewBinding.tab1.setOnShakeClickListener {
            selType(0, true)
        }
        viewBinding.tab2.setOnShakeClickListener {
            selType(1, true)
        }

    }

    private fun getTypeId() =
        (viewModel.tabLiveData.value?.get(viewModel.currentTabIndex.value ?: 0)?.id
            ?: CalendarConstant.CommentType.TIDE)


    private fun initView() {
        viewBinding.apply {
            tideRv.adapter = sectionAdapter
            tideRv.layoutManager = stickyHeaderLayoutManager
            tideRv.itemAnimator?.changeDuration = 0

            rvRecommendGoods.adapter = recommendAdapter
            context?.let { cnx ->
                rvRecommendGoods.layoutManager =
                    LinearLayoutManager(cnx, LinearLayoutManager.HORIZONTAL, false)
            }

            tideBanner.apply {
                layoutParams = layoutParams.apply {
                    height = ((ScreenUtils.getScreenWidth(context) - 15.azDp) * 0.2f).toInt()
                }
                setImageLoader(object : ImageLoaderInterface<ShapeableImageView> {
                    override fun displayImage(
                        context: Context?,
                        path: Any?,
                        imageView: ShapeableImageView?
                    ) {
                        if (imageView != null) {
                            this@TideHomeIndexFragment.context?.let {
                                Glide.with(it).load(path).into(imageView)
                            }
                        }
                    }

                    override fun createImageView(context: Context?): ShapeableImageView {
                        return ShapeableImageView(context).apply {
                            shapeAppearanceModel = ShapeAppearanceModel.builder()
                                .setAllCorners(CornerFamily.ROUNDED, 8.dp)
                                .build()
                        }
                    }
                })
                setOnBannerListener { index ->
                    viewModel.bannerData.value?.get(index)?.let {
                        UTHelper.commonEvent(
                            "${UTConstant.Tide.CalendarCW_click_NFTbanner_}$index",
                            "id",
                            it.id
                        )
                        if (it.targetId.isNullOrEmpty()) {
                            it.targetUrl?.let { url ->
                                DcRouter(url).start()
                            }
                        } else {
                            if (!isScrolled) {
                                moveTideRVTo(it.targetId!!)
                            }
                        }
                    }
                }
            }
        }
        initScroll()
        recommendAdapter.addItemBinder(GoodItem::class.java, recommendItemBinder)
    }

    private fun initObserver() {
        viewModel.monthListData.observe(this) {
            setTabData(it, viewModel.currentMonthIndex)
        }
        viewModel.filterResultListLiveData.observe(this) {
            viewModel.filterList.clear()
            viewModel.filterList.addAll(it)
            tideFilterDialog.setFilter(it)
            checkFilterState()
        }
        viewModel.dealFilterResultListLiveData.observe(this) {
            viewModel.dealFilterList.clear()
            viewModel.dealFilterList.addAll(it)
            dealFilterDialog.setFilter(it)
            checkDealInfoState()
        }

        viewModel.goodItemListLiveData.observe(this) {
            viewBinding.hotTitleLl.hide(it.isNullOrEmpty())
            viewBinding.rvRecommendGoods.hide(it.isNullOrEmpty())
            recommendAdapter.setList(it)
        }

        viewModel.bannerData.observe(this) {
            if (it.isNullOrEmpty()) {
                viewBinding.tideBannerCard.isVisible = false
            } else {
                viewBinding.tideBannerCard.isVisible = true
                viewBinding.tideBanner.run {
                    setImages(it.map { banner -> banner.imageUrl })
                    start()
                }
            }
        }

        viewModel.tideItemListLiveData.observe(this) {
            sectionAdapter.setData(it)
            remTideList = it
            viewBinding.emptyView.hide(it.size != 0)
            var scrollIndex = 0
            kotlin.run out@{
                it.forEachIndexed { _, tideSectionsEntity ->
                    if (tideSectionsEntity.select == true) {
                        //滚动到这个位置
                        return@out
                    } else {
                        //因为title会被当作两个。所以需要加2
                        scrollIndex += ((tideSectionsEntity.tides?.size ?: 0) + 2)
                    }
                }
            }
            stickyHeaderLayoutManager.scrollToPosition(scrollIndex)
            scrollListener.setTotal()
        }
        viewModel.refreshLayoutLiveData.observe(this) {
            viewBinding.refreshLayout.finishRefresh()
        }
        viewModel.subChangeLiveData.observe(this) {
            sectionAdapter.notifyDataSetChanged()
        }
        homeIndexViewModel.tideScrollTop.observe(viewLifecycleOwner) {
            if (!it || !super.getPageVisible()) {
                return@observe
            }
            tryToScrollTop()
        }
        viewModel.currentTabIndex.observe(this) { index ->
            when (index) {
                0 -> {
                    viewBinding.tab1.isSelected = true
                    viewBinding.tab2.isSelected = false
                    viewBinding.tab1.setBackgroundResource(R.drawable.tide_tab_title_bg)
                    viewBinding.tab2.background = null
                }
                else -> {
                    viewBinding.tab1.isSelected = false
                    viewBinding.tab2.isSelected = true
                    viewBinding.tab1.background = null
                    viewBinding.tab2.setBackgroundResource(R.drawable.tide_tab_title_bg)
                }

            }
        }
        viewModel.tabLiveData.observe(this) {
            if (it.size >= 2) {
                viewBinding.tabLayer.hide(false)
                viewBinding.tab1.text = it[0].name
                viewBinding.tab2.text = it[1].name
                selType(0, false)
            } else {
                viewBinding.tabLayer.hide(true)
                viewModel.currentTabIndex.value = 0
            }
        }
    }

    private fun selType(index: Int, loadData: Boolean) {
        if (viewModel.currentTabIndex.value == index) {
            return
        }
        viewModel.currentTabIndex.value = index
        with(viewBinding) {
            //清空筛选项
            onlySubscribeIv.isSelected = false
            filterIv.isSelected = false
            filterTv.isSelected = false
            platformTv.isSelected = false
            platformIv.imageTintList = null
            dealInfoTv.isSelected = false
            dealInfoIv.imageTintList = null
            viewModel.filterList.clear()
            viewModel.dealFilterList.clear()
            when (getTypeId()) {
                CalendarConstant.CommentType.TIDE -> {
                    if (loadData) {
                        UTHelper.commonEvent(UTConstant.Tide.CalendarCW_click_Bar, "type", "实物潮玩")
                    }
                    tideFilterDialog.setFilterTitle("筛选")
                    filterLayer.isVisible = true
                    line1.isVisible = true
                    platformLayer.isVisible = false
                    dealInfoLayer.isVisible = false
                }
                CalendarConstant.CommentType.DIGITAL -> {
                    if (loadData) {
                        UTHelper.commonEvent(UTConstant.Tide.CalendarCW_click_Bar, "type", "数字藏品")
                    }
                    tideFilterDialog.setFilterTitle("平台")
                    filterLayer.isVisible = false
                    line1.isVisible = false
                    platformLayer.isVisible = true
                    dealInfoLayer.isVisible = true
                }
            }
        }

        if (loadData) {
            viewModel.loadData(isFirstLoad = false, needRefreshTab = false)
        }
    }

    private fun tryToScrollTop() {
        viewBinding.tideRv.stopScroll()
        stickyHeaderLayoutManager.scrollToPosition(0)
        scrollListener.reset()
        val layoutParams = viewBinding.layoutAppBar.layoutParams ?: return
        val behavior = (layoutParams as CoordinatorLayout.LayoutParams).behavior
        if (behavior is AppBarLayout.Behavior) {
            if (behavior.topAndBottomOffset == 0) {
                return
            } else {
                behavior.topAndBottomOffset = 0
            }
        }
    }

    private fun initScroll() {
        viewBinding.tideRv.addOnScrollListener(scrollListener)
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        private var totalY = 0
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            totalY += dy
            homeIndexViewModel.tideIsSHowRocket = totalY > SizeUtils.dp2px(270f)
        }

        fun reset() {
            totalY = 0
        }

        fun setTotal() {
            viewBinding.tideRv.post {
                this.totalY = stickyHeaderLayoutManager.getFirstVisibleItemViewHolder(true)?.run {
                    (layoutPosition - (section + 1) * 2) * itemView.measuredHeight + section * 40.azDp.toInt()
                } ?: 0
                homeIndexViewModel.tideIsSHowRocket = totalY > SizeUtils.dp2px(270f)
            }
        }
    }

    private fun setTabData(dateBeanList: List<DateBean?>, currentIndex: Int) {
        if (CollectionUtils.isEmpty(dateBeanList)) {
            return
        }
        for (dateBean in dateBeanList) {
            val tab = viewBinding.tabLayout.newTab()
            tab.setCustomView(R.layout.common_month_tab)
            tab.customView?.let {
                context?.let { cnx ->
                    val tabVh = MonthTabViewHolder(cnx, it)
                    tabVh.tabTitle?.text = "${dateBean?.month ?: ""}"
                    tabVh.tabMonth?.text = "月"
                    viewBinding.tabLayout.addTab(tab)
                }

            }
        }
//      开始选中
        lifecycleScope.launch(Dispatchers.Main) {
            viewBinding.tabLayout.getTabAt(currentIndex)?.select(true)
            this@TideHomeIndexFragment.currentIndex = currentIndex
        }
//        ThreadPoolHelper.getInstance().runOnUiThread({
//            viewBinding.tabLayout.getTabAt(currentIndex)?.select(true)
//            this.currentIndex = currentIndex
//        }, 100)
    }


    private fun checkFilterState() {
        var isHaveFilter = false
        kotlin.run out@{
            viewModel.filterList.forEach {
                if (!it.isAll && it.isSelected) {
                    isHaveFilter = true
                    return@out
                }
            }
        }
        when (getTypeId()) {
            CalendarConstant.CommentType.TIDE -> {
                viewBinding.filterIv.isSelected = isHaveFilter
                viewBinding.filterTv.isSelected = isHaveFilter
            }
            CalendarConstant.CommentType.DIGITAL -> {
                viewBinding.platformTv.isSelected = isHaveFilter
                viewBinding.platformIv.imageTintList =
                    if (isHaveFilter) ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.color_orange
                    ) else null
            }
        }
    }

    private fun checkDealInfoState() {
        var isHaveFilter = false
        kotlin.run out@{
            viewModel.dealFilterList.forEach {
                if (!it.isAll && it.isSelected) {
                    isHaveFilter = true
                    return@out
                }
            }
        }
        viewBinding.dealInfoTv.isSelected = isHaveFilter
        viewBinding.dealInfoIv.imageTintList = if (isHaveFilter) ContextCompat.getColorStateList(
            requireContext(),
            R.color.color_orange
        ) else null
    }


    private fun requestCalendarPermission(tideItemEntity: TideItemEntity) {
        mTideItemEntity = tideItemEntity
        context?.let { cnx ->
            if (ActivityCompat.checkSelfPermission(
                    cnx,
                    Manifest.permission.WRITE_CALENDAR
                ) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
                    cnx,
                    Manifest.permission.READ_CALENDAR
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    PERMISSIONS_CALENDAR,
                    ++mRequestCode
                )
            } else {
                remind()
            }
        }

    }

    private fun moveTideRVTo(id: String) {
        var ansPos = 0
        remTideList?.forEachIndexed { index, entity ->
            ansPos += 2 //一个标题算两个item
            entity.tides?.forEachIndexed { index2, bean ->
                if (bean.id == id) {
                    stickyHeaderLayoutManager.scrollToPosition(ansPos - 1)
                    scrollListener.setTotal()
                    viewBinding.tideRv.post {
                        sectionAdapter.data[index].tides?.get(index2)?.isPlayAnimator = true
                        sectionAdapter.notifySectionItemChanged(index, index2)
                    }
                    getTabType()?.let {
                        UTHelper.commonEvent(
                            UTConstant.Tide.CalendarCW_click_Hotsale,
                            "title",
                            sectionAdapter.data[index].tides?.get(index2)?.title,
                            "type",
                            it
                        )
                    }
                    return
                }
                ansPos += 1
            }
        }


    }


    private fun getTabType(): String? {
        if (viewModel.currentTabIndex.value ?: 0 < viewModel.tabLiveData.value?.size ?: 0) {
            return viewModel.tabLiveData.value?.get(viewModel.currentTabIndex.value ?: 0)?.name
        }
        return null
    }

    private fun remind() {
        if (null == mTideItemEntity) {
            return
        }
        val des =
            ("\"${mTideItemEntity?.title ?: ""}\"\n${mTideItemEntity?.company ?: ""}").trimIndent()
        //如果后端设置了短信时间并且未到达短信开始时间,将短信时间设置到提醒日历

        if (mTideItemEntity!!.isSubscribe) {
            context?.let { cnx ->
                CalendarReminderUtils
                    .deleteCalendarEvent(cnx, des)
            }
        } else {
            context?.let { cnx ->
                CalendarReminderUtils
                    .addCalendarEvent(
                        cnx, des, "",
                        mTideItemEntity?.saleDate ?: System.currentTimeMillis()
                    )
            }
        }

        mTideItemEntity = null
    }

}