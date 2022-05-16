package cool.dingstock.home.ui.home

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.sankuai.waimai.router.Router
import cool.dingstock.appbase.adapter.MyFragmentPageAdapter
import cool.dingstock.appbase.constant.*
import cool.dingstock.appbase.entity.bean.home.HomeCategoryBean
import cool.dingstock.appbase.entity.bean.home.HomeData
import cool.dingstock.appbase.entity.bean.home.HomeItem
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.helper.HomeSuggestHelper
import cool.dingstock.appbase.mvp.BaseFragment
import cool.dingstock.appbase.mvp.lazy.CacheLzyFragment
import cool.dingstock.appbase.net.api.home.HomeHelper
import cool.dingstock.appbase.serviceloader.IOverseaServer
import cool.dingstock.appbase.serviceloader.ISeriesListFragmentServer
import cool.dingstock.appbase.serviceloader.ISneakersFragmentServer
import cool.dingstock.appbase.serviceloader.ITideFragmentServer
import cool.dingstock.appbase.storage.NetDataCacheHelper
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.ut.UTUtils
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.util.isWhiteMode
import cool.dingstock.appbase.util.setSvgColor
import cool.dingstock.appbase.viewmodel.HomeIndexViewModel
import cool.dingstock.appbase.widget.NoScrollViewPager
import cool.dingstock.appbase.widget.tablayout.HomeTabScaleImgAdapter
import cool.dingstock.appbase.widget.tablayout.HomeTabScaleImgAdapter.TabTitleEntity
import cool.dingstock.home.R
import cool.dingstock.home.databinding.HomeFragmentLayoutBinding
import cool.dingstock.home.ui.dingchao.HomeDingChaoFragment
import cool.dingstock.home.ui.dingchao.HomeDingChaoFragment.OnAppBarScrollListener
import cool.dingstock.home.ui.fashion.index.HomeFashionFragment
import cool.dingstock.home.ui.h5.HomeH5Fragment
import cool.dingstock.lib_base.stroage.ConfigSPHelper
import cool.dingstock.lib_base.util.AppUtils
import cool.dingstock.lib_base.util.CollectionUtils
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.lib_base.widget.card.touch.ItemTouchHelperCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import kotlin.math.abs

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/8  15:27
 */
class HomeIndexFragment : CacheLzyFragment<HomeIndexFragmentVm, HomeFragmentLayoutBinding>() {
    var tabLayout: MagicIndicator? = null
    var viewPager: NoScrollViewPager? = null
    var searchIv: ImageView? = null
    var tableLayoutLin: FrameLayout? = null
    var bgV: View? = null
    var tabBgV: View? = null
    var fakeBar: View? = null
    private val fragmentList: MutableList<BaseFragment> = java.util.ArrayList()
    private var mCategoryList: ArrayList<HomeCategoryBean>? = null
    private var isLoadFinish = false
    private var mPreviousUrl: Uri? = null
    private var homeIndexViewModel: HomeIndexViewModel? = null
    var homeDingChaoFragment: HomeDingChaoFragment? = null
    var categoryList: ArrayList<HomeCategoryBean?> = ArrayList()
    private var needHideSearchPosition: MutableSet<Int> = hashSetOf()
    var commonNavigator: CommonNavigator? = null
    private val titleStartY = 0f
    private var headTabCanScroll = true
    var titleList: ArrayList<TabTitleEntity>? = null
    var selIndex = 0
    var longOutObserver: Observer<Boolean>? = null

    private val tabPageAdapter by lazy {
        HomeTabScaleImgAdapter(titleList).apply {
            setStartAndEndTitleSize(16f, 21f)
            setNormalColor(getCompatColor(R.color.color_text_black3))
            setSelectedColor(getCompatColor(R.color.color_text_black1))
            setPadding(SizeUtils.dp2px(10f))
            setIndicatorColor(
                getCompatColor(R.color.color_text_black1),
                10.dp.toInt(),
                SizeUtils.dp2px(1.5f)
            )
            setTabSelectListener { index: Int ->
                selIndex = index
                homeIndexViewModel!!.homeSelIndex = index

                if (headTabCanScroll) {
                    viewPager!!.currentItem = index
                    UTHelper.homeLevel1Tab(titleList?.get(index)?.title)
                }
            }
        }
    }

    private val brandTabPageAdapter by lazy {
        HomeTabScaleImgAdapter(titleList).apply {
            setStartAndEndTitleSize(16f, 21f)
            setNormalColor(getCompatColor(R.color.color_text_white_a60))
            setSelectedColor(getCompatColor(R.color.white))
            setPadding(SizeUtils.dp2px(10f))
            setIndicatorColor(
                getCompatColor(R.color.white),
                10.dp.toInt(),
                SizeUtils.dp2px(1.5f)
            )
            setTabSelectListener { index: Int ->
                selIndex = index
                homeIndexViewModel!!.homeSelIndex = index

                if (headTabCanScroll) {
                    viewPager!!.currentItem = index
                    UTHelper.homeLevel1Tab(titleList?.get(index)?.title)
                }
            }
        }
    }

    override fun reload() {
        super.reload()
        for (fragment in fragmentList) {
            fragment.reload()
        }
    }

    override fun initVariables(
        rootView: View?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        homeIndexViewModel = ViewModelProvider(requireActivity())[HomeIndexViewModel::class.java]
        tabLayout = rootView?.findViewById(R.id.home_fragment_tablayout)
        viewPager = rootView?.findViewById(R.id.home_fragment_vp)
        searchIv = rootView?.findViewById(R.id.search_iv)
        searchIv?.setSvgColor(
            R.drawable.search_svg_search,
            getCompatColor(R.color.color_text_black1)
        )
        tableLayoutLin = rootView?.findViewById(R.id.home_fragment_tablayout_lin)
        tableLayoutLin?.bringToFront()
        bgV = rootView?.findViewById(R.id.bgV)
        tabBgV = rootView?.findViewById(R.id.tabBgV)
        fakeBar = rootView?.findViewById(R.id.fake_status_bar1)
        context?.let {
            setTopHeight(SizeUtils.getStatusBarHeight(it))
        }
        initObserver()
        showLoadingView()
    }

    override fun initListeners() {
        searchIv?.setOnClickListener {
            DcRouter(SearchConstant.Uri.SEARCH_INDEX).start()
            //埋点
            UTHelper.commonEvent(UTUtils.SEARCH)
        }
    }

    override fun loadCache() {
        var data = NetDataCacheHelper.getData(HomeIndexFragmentVm.cacheKey, HomeData::class.java)
        val lastVersion = ConfigSPHelper.getInstance().getString("lastVersion")
        context?.let {
            val currentVersion = AppUtils.getVersionName(it)
            if (currentVersion != lastVersion) {
                ConfigSPHelper.getInstance().save("lastVersion", currentVersion)
                data = null
            }
        }
        if (data != null) {
            loadCacheSuccess = true
            //加载数据
            val categories: List<HomeCategoryBean?>? = data?.categories
            val categoryList = viewModel.addDefaultCategory(categories)
            HomeHelper.getInstance().cacheHomeData = data //将数据缓存，传递给HomeDinChaoFramgent
            setTabDataList(categoryList)
            setViewPagerData(categoryList, data?.items)
            hideLoadingView()
        }
    }

    override fun onLazy() {
        showLoadingView()
        viewModel.loadData()
    }

    override fun onStatusViewErrorClick() {
        super.onStatusViewErrorClick()
        showLoadingView()
        viewModel.loadData()
    }

    override fun onResume() {
        super.onResume()
        if (homeIndexViewModel?.homeCurrentShowTab == HomeIndexViewModel.IndexTopTab.Fashion) {
            activity?.let {
                if (it.isWhiteMode()) StatusBarUtil.setDarkMode(it)
            }
        }
//        switchTab()
    }

    override fun onPause() {
        super.onPause()
        if (homeIndexViewModel?.homeCurrentShowTab == HomeIndexViewModel.IndexTopTab.Fashion) {
            activity?.let {
                if (it.isWhiteMode()) StatusBarUtil.setLightMode(it)
            }
        }
    }

    private fun initObserver() {
        viewModel.viewPagerDataLiveData.observe(viewLifecycleOwner) { homeData ->
            homeData.categories?.let {
                setViewPagerData(it, homeData.items)
            }
        }
        viewModel.tabDataListLiveData.observe(viewLifecycleOwner) {
            setTabDataList(it)
        }
        longOutObserver = Observer<Boolean> { viewPager?.currentItem = 0 }
        longOutObserver?.let {
            homeIndexViewModel?.loginOut?.observeForever(it)
        }
    }

    private fun setTabDataList(categoryList: ArrayList<HomeCategoryBean>?) {
        if (CollectionUtils.isEmpty(categoryList)) {
            return
        }
        this.categoryList.clear()
        this.categoryList.addAll(categoryList!!)
        runOnUiThread {
            setTabItemView(categoryList)
        }
    }

    private fun setViewPagerData(categoryList: ArrayList<HomeCategoryBean>, homeItems: HomeItem?) {
        if (CollectionUtils.isEmpty(categoryList) || null == homeItems) {
            return
        }
        mCategoryList = categoryList
        fragmentList.clear()
        for (position in categoryList.indices) {
            val homeCategoryBean = categoryList[position]
            val type = homeCategoryBean.type
            if (position == 0) {
                homeDingChaoFragment = HomeDingChaoFragment()
                setListener()
                fragmentList.add(homeDingChaoFragment!!)
                continue
            }
            if (TextUtils.isEmpty(type)) {
                continue
            }
            var fragment: BaseFragment? = null
            when (type) {
                HomeBusinessConstant.CategoryType.SNEAKERS -> {
                    fragment =
                        Router.getService(
                            ISneakersFragmentServer::class.java,
                            CalendarConstant.ServerLoader.SNEAKERS_FRAGMENT
                        )?.getSneakersFragment(homeCategoryBean, true)
                    if (fragment == null) {
                        fragment = HomeH5Fragment()
                    }
                }
                HomeBusinessConstant.CategoryType.STREET_WEAR -> {
                    fragment = HomeFashionFragment.getInstance(homeCategoryBean)
                }
                HomeBusinessConstant.CategoryType.H5 -> {
                    val instance = HomeH5Fragment.getInstance(homeCategoryBean)
                    instance?.setNeedMargeTop(true)
                    fragment = instance
                }
                HomeBusinessConstant.CategoryType.OVERSEA -> {
                    fragment =
                        Router.getService(
                            IOverseaServer::class.java,
                            ShopConstant.ServerLoader.OVERSEA_HOME_FRAGMENT
                        )?.getOverseaFragment()
                    needHideSearchPosition.add(position)
                }
                HomeBusinessConstant.CategoryType.TIDE_PLAY -> {
                    fragment =
                        Router.getService(
                            ITideFragmentServer::class.java,
                            TideConstant.ServerLoader.TIDE_FRAGMENT
                        )?.getTideHomeIndexFragment()
                }
                HomeBusinessConstant.CategoryType.SHOES -> {
                    fragment =
                        Router.getService(
                            ISeriesListFragmentServer::class.java,
                            ShoesConstant.ServerLoader.SERIES_LIST_FRAGMENT
                        )?.getSeriesListFragment()
                }
                else -> {
                }
            }
            if (null != fragment) {
                fragmentList.add(fragment)
            }
        }
        val fragmentPageAdapter = MyFragmentPageAdapter(childFragmentManager, fragmentList)
        viewPager!!.adapter = fragmentPageAdapter
        viewPager!!.offscreenPageLimit = fragmentList.size
        isLoadFinish = true
        switchTab()
        viewPager!!.setCurrentItem(homeIndexViewModel!!.homeSelIndex, false)
    }

    private fun setTabItemView(categoryList: ArrayList<HomeCategoryBean>) {
        if (commonNavigator != null) {
            return
        }
        commonNavigator = CommonNavigator(context)
        titleList = ArrayList()
        for ((name, _, _, _, iconUrl, tip) in categoryList) {
            val entity = TabTitleEntity()
            entity.title = name
            entity.imgUrl = iconUrl
            entity.tip = tip
            titleList?.add(entity)
        }

        commonNavigator?.adapter = tabPageAdapter
        tabLayout!!.navigator = commonNavigator
        viewPager!!.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                tabLayout!!.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (position == 0) {
                    val percent = 1 - positionOffset
                    bgV?.alpha = percent
                    tabBgV?.alpha = percent
                    fakeBar?.alpha = percent
                }
            }

            override fun onPageSelected(position: Int) {
                tabLayout!!.onPageSelected(position)
                homeIndexViewModel!!.homeSelIndex = position
                utHomeTabClick(position)
                onTabSelected(position)
                if (needHideSearchPosition.contains(position)) {
                    hideSearchIcon(true)
                } else {
                    hideSearchIcon(false)
                }
                bgV?.hide(position != 0)
                tabBgV?.hide(position != 0)
                fakeBar?.hide(position != 0)
            }

            override fun onPageScrollStateChanged(state: Int) {
                tabLayout!!.onPageScrollStateChanged(state)
            }
        })
    }

    private fun setTopHeight(otherHeight: Int) {
        fakeBar?.apply {
            layoutParams.height = otherHeight
        }
        rootView?.findViewById<LinearLayout>(R.id.title_ll)
            ?.setPadding(0, otherHeight - 7.dp.toInt(), 0, 0)
        tabBgV?.apply {
            (layoutParams as FrameLayout.LayoutParams).height =
                SizeUtils.dp2px(44f) + otherHeight
        }
    }

    private fun hideSearchIcon(b: Boolean) {
        if (b) {
            searchIv?.visibility = View.GONE
        } else {
            searchIv?.visibility = View.VISIBLE
        }
    }

    private fun onTabSelected(position: Int) {
        try {
            searchIv?.setSvgColor(
                R.drawable.search_svg_search,
                getCompatColor(R.color.color_text_black1)
            )
//            tableLayoutLin?.setBackgroundColor(getCompatColor(R.color.common_activity_bg))
            commonNavigator?.adapter = tabPageAdapter
            when (mCategoryList?.get(position)?.type) {
                HomeBusinessConstant.CategoryType.HOME -> {
                    homeIndexViewModel?.homeCurrentShowTab = HomeIndexViewModel.IndexTopTab.Home
                }
                HomeBusinessConstant.CategoryType.SNEAKERS -> {
                    homeIndexViewModel?.homeCurrentShowTab =
                        HomeIndexViewModel.IndexTopTab.SneakersCalendar
                }
                HomeBusinessConstant.CategoryType.STREET_WEAR -> {
                    homeIndexViewModel?.homeCurrentShowTab =
                        HomeIndexViewModel.IndexTopTab.Fashion
                    searchIv?.setSvgColor(
                        R.drawable.search_svg_search,
                        Color.WHITE
                    )
//                    tableLayoutLin?.setBackgroundColor(getCompatColor(R.color.transparent))
                    if (context?.isWhiteMode() == true) commonNavigator?.adapter =
                        brandTabPageAdapter
                }
                HomeBusinessConstant.CategoryType.OVERSEA -> {
                    homeIndexViewModel?.homeCurrentShowTab =
                        HomeIndexViewModel.IndexTopTab.Oversea
                }
                HomeBusinessConstant.CategoryType.TIDE_PLAY -> {
                    homeIndexViewModel?.homeCurrentShowTab =
                        HomeIndexViewModel.IndexTopTab.TidePlay
                }
                HomeBusinessConstant.CategoryType.SHOES -> {
                    homeIndexViewModel?.homeCurrentShowTab =
                        HomeIndexViewModel.IndexTopTab.SHOES
                }
                HomeBusinessConstant.CategoryType.H5 -> {
                    homeIndexViewModel?.homeCurrentShowTab =
                        HomeIndexViewModel.IndexTopTab.H5
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun setListener() {
        homeDingChaoFragment?.onAppBarScrollListener = object : OnAppBarScrollListener {
            override fun onAppBarScroll(f: Int, appbarHeight: Int) {
                context?.let {
                    val height = tableLayoutLin!!.height
                    val currentCanScroll = abs(f) <= appbarHeight - height
                    if (headTabCanScroll != currentCanScroll) {
                        headTabCanScroll = currentCanScroll
                        if (viewPager?.isNoScroll != !headTabCanScroll) {
                            if (!headTabCanScroll) {
                                lifecycleScope.launchWhenResumed {
                                    launch(Dispatchers.IO) {
                                        HomeSuggestHelper.uploadHomeFeeds()
                                    }
                                }
                            }
                        }
                        viewPager!!.isNoScroll = !headTabCanScroll

                        tabLayout!!.isEnabled = headTabCanScroll
                        val viewPager = homeDingChaoFragment!!.viewPager
                        if (viewPager != null) {
                            viewPager.isNoScroll = headTabCanScroll
                        }
                    }
                    when {
                        abs(f) >= appbarHeight - height + SizeUtils.getStatusBarHeight(it) -> { //可以开始向上滑动了
                            tableLayoutLin!!.y =
                                titleStartY - (abs(f) - (appbarHeight - height)) + SizeUtils.getStatusBarHeight(
                                    it
                                )
                            fakeBar!!.bringToFront()
                        }
                        abs(f) < appbarHeight - height -> {
                            tableLayoutLin!!.y = titleStartY
                            tableLayoutLin!!.bringToFront()
                        }
                        else -> {
                            tableLayoutLin!!.bringToFront()
                        }
                    }
                }
            }
        }
        homeDingChaoFragment?.onStateChangedListener =
            object : HomeDingChaoFragment.OnCardTouchChangeListener {
                override fun onCardTouchChange(state: ItemTouchHelperCallback.DragStateEnum) {
                    viewPager!!.isNoScroll = state == ItemTouchHelperCallback.DragStateEnum.DRAGING
                }
            }
    }

    private fun utHomeTabClick(index: Int) {
        if (CollectionUtils.isEmpty(categoryList)) {
            return
        }
        val homeCategoryBean = categoryList[index]
        if (index == 0) {
            UTHelper.commonEvent(UTConstant.Home.HOME_TOP_TAB_FIRST)
        } else {
            UTHelper.commonEvent(UTConstant.Home.HOME_TOP_TAB + homeCategoryBean?.type)
        }
    }

    private fun switchTab() {
        if (null != mPreviousUrl && mPreviousUrl === uri) {
            return
        }
        if (uri == null) {
            return
        }
        val categoryId = uri.getQueryParameter(HomeConstant.UriParam.KEY_CATEGORY_ID)
        if (TextUtils.isEmpty(categoryId) || CollectionUtils.isEmpty(mCategoryList) && !isLoadFinish) {
            return
        }
        var targetIndex = -1
        for (homeCategoryBean in mCategoryList!!) {
            if (categoryId == homeCategoryBean.type) {
                targetIndex = mCategoryList?.indexOf(homeCategoryBean) ?: 0
                break
            }
        }
        viewPager!!.offscreenPageLimit = mCategoryList?.size ?: 0
        if (targetIndex >= 0) {
            viewPager!!.currentItem = targetIndex
        }
        mPreviousUrl = uri
    }

    override fun switchPages(uri: Uri?) {
        super.switchPages(uri)
        val categoryId = uri?.getQueryParameter(HomeConstant.UriParam.KEY_CATEGORY_ID)
        if (TextUtils.isEmpty(categoryId) || CollectionUtils.isEmpty(mCategoryList) && !isLoadFinish) {
            return
        }
        if (categoryId != HomeBusinessConstant.CategoryType.HOME && homeIndexViewModel?.homeIsShowRocket == true) {
            homeIndexViewModel?.scrollToTop()
        }
        viewBinding.root.post {
            var targetIndex = -1
            for (homeCategoryBean in mCategoryList!!) {
                if (categoryId == homeCategoryBean.type) {
                    targetIndex = mCategoryList?.indexOf(homeCategoryBean) ?: 0
                    break
                }
            }
            if (targetIndex >= 0) {
                viewPager?.setCurrentItem(targetIndex, false)
                fragmentList[targetIndex].switchPages(uri)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        longOutObserver?.let {
            homeIndexViewModel?.loginOut?.removeObserver(it)
        }
    }
}