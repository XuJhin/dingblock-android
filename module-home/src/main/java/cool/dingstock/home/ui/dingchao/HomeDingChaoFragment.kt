package cool.dingstock.home.ui.dingchao

import android.annotation.SuppressLint
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.shuyu.gsyvideoplayer.GSYVideoManager
import cool.dingstock.appbase.adapter.VPLessViewAdapter
import cool.dingstock.appbase.config.AppSpConstant
import cool.dingstock.appbase.constant.CircleConstant
import cool.dingstock.appbase.constant.HomeConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.customerview.BetterTouchFrameLayout
import cool.dingstock.appbase.customerview.CommonNavigatorNew
import cool.dingstock.appbase.entity.bean.ApiResult
import cool.dingstock.appbase.entity.bean.home.FollowCountEntity
import cool.dingstock.appbase.entity.bean.home.HomeData
import cool.dingstock.appbase.entity.bean.home.HomePostExtraTabBean
import cool.dingstock.appbase.entity.bean.topic.TalkTopicEntity
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.helper.PopWindowManager
import cool.dingstock.appbase.helper.ShakeHelper
import cool.dingstock.appbase.mvp.BaseFragment
import cool.dingstock.appbase.mvp.lazy.CacheLzyFragment
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.refresh.DcRefreshHeader
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.viewmodel.HomeIndexViewModel
import cool.dingstock.appbase.widget.MyTouchSmartRefreshLayout
import cool.dingstock.appbase.widget.NoScrollViewPager
import cool.dingstock.appbase.widget.tablayout.TabScaleAdapter
import cool.dingstock.home.R
import cool.dingstock.home.ViewTouchBindHelper
import cool.dingstock.home.databinding.HomeFragmentDingchaoLayoutBinding
import cool.dingstock.home.utils.CardViewHelper
import cool.dingstock.home.widget.card.HomeTouchCardView
import cool.dingstock.lib_base.stroage.ConfigSPHelper
import cool.dingstock.lib_base.util.ActivityUtils
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.lib_base.widget.card.touch.ItemTouchHelperCallback
import cool.dingstock.post.helper.preVideoPosition
import cool.dingstock.post.helper.preVideoUrl
import cool.dingstock.post.list.HomePostListFragment
import cool.dingstock.post.list.PostConstant
import cool.dingstock.post.list.deal.HomeDealPostListFragment
import cool.dingstock.post.list.followed.HomeFollowedPostListFragment
import cool.dingstock.post.list.recommend.HomeRecommendListFragment
import cool.dingstock.post.list.nearby.HomeNearbyFragment
import cool.dingstock.post.list.topic.HomeTopicPostListFragment
import cool.dingstock.uicommon.widget.SignViewDialog
import net.lucode.hackware.magicindicator.MagicIndicator
import kotlin.math.absoluteValue

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/8  17:45
 */
class HomeDingChaoFragment :
        CacheLzyFragment<HomeDingChaoFragmentVm, HomeFragmentDingchaoLayoutBinding>() {
    private var refreshLayout: MyTouchSmartRefreshLayout? = null
    private var fabBtn: ImageView? = null
    private var appBarLayout: AppBarLayout? = null
    private var postTabs: MagicIndicator? = null
    var viewPager: NoScrollViewPager? = null
    var signView: SignViewDialog? = null
    var topClickHelper: View? = null
    var tabIv: ImageView? = null
    private var homeIndexViewModel: HomeIndexViewModel? = null
    var rvDataLiveDataObserver: Observer<HomeData>? = null
    var refreshLiveDataObserver: Observer<Boolean>? = null
    private var betterTouchFrameLayout: BetterTouchFrameLayout? = null
    var onAppBarScrollListener: OnAppBarScrollListener? = null
    var onStateChangedListener: OnCardTouchChangeListener? = null
    var homeItemFragment: HomeItemFragment? = null
    val viewTouchBindHeler by lazy {
        ViewTouchBindHelper()
    }

    private val shakeHelper by lazy {
        ShakeHelper(requireContext())
    }

    /**
     * 是否需要通过触摸事件触发 tab的点击事件
     *
     * */
    var needTopClickHelper = false

    /**
     * 是否正在拖动card,拖动时禁止下滑
     *
     * */
    private var isSwiping = false

    /**
     * 当前选中的Tab
     */
    private var currentTab: Int = 0
    private val fragmentList: MutableList<BaseFragment> = ArrayList()
    private var pageAdapter: VPLessViewAdapter? = null
    private val tabNameList: MutableList<String> = ArrayList()
    private var tabPageAdapter: TabScaleAdapter? = null
    private var commonNavigator: CommonNavigatorNew? = null
    private var homeTouchCardView: HomeTouchCardView? = null
    var parentActivityVisible = false
    var hasJumpFollow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.e("onCreate")
        initStatusView()
        CardViewHelper.initSize()
        showLoadingView()
    }

    override fun reload() {
        super.reload()
        refreshData()
        //如果是跳转我的登录就不能马上请求homeConfig
        if (homeIndexViewModel?.isLoginReturn != true) {
            PopWindowManager.checkReturnHomeOrRefreshPopWindow(requireContext(), false)
        }
    }

    override fun ignoreUt(): Boolean {
        return true
    }

    override fun initVariables(
            rootView: View?,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ) {
        homeIndexViewModel =
                ViewModelProvider(requireActivity())[HomeIndexViewModel::class.java]
        rootView?.let {
            initViewAndEvent(rootView)
        }
        initObserver()
        refreshLayout?.isEnabled = false
        appBarLayout?.bringToFront()
        pageAdapter = VPLessViewAdapter(childFragmentManager, fragmentList)
        viewPager?.apply {
            adapter = pageAdapter
            currentItem = homeIndexViewModel?.homeDCSelIndex ?: 0
            offscreenPageLimit = 3
        }
        asyncScroll()

        refreshLayout?.apply {
            val lp = layoutParams as FrameLayout.LayoutParams
            lp.topMargin = SizeUtils.dp2px(40f) + SizeUtils.getStatusBarHeight(requireContext())
            layoutParams = lp
        }
        topClickHelper?.layoutParams?.height =
                SizeUtils.dp2px(40f) + SizeUtils.getStatusBarHeight(requireContext())
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showPushDialog()
    }

    fun showPushDialog() {
//        val pushCheckHelper = PushCheckHelper()
//        pushCheckHelper.with(requireActivity())
//            .checkPushPermission()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListeners() {
        refreshLayout?.setRefreshHeader(object : DcRefreshHeader(requireContext()) {
            override fun onStateChanged(
                    refreshLayout: RefreshLayout,
                    oldState: RefreshState,
                    newState: RefreshState
            ) {
                super.onStateChanged(refreshLayout, oldState, newState)
                homeTouchCardView?.setEnableDrag(newState == RefreshState.None)
            }
        })
        homeTouchCardView?.itemTouchHelperCallback?.setListener { state ->
            refreshLayout?.isEnabled = state == ItemTouchHelperCallback.DragStateEnum.STATIC
            refreshLayout?.isNoTouch = state == ItemTouchHelperCallback.DragStateEnum.DRAGING
//            viewPager?.isNoScroll = state == ItemTouchHelperCallback.DragStateEnum.DRAGING
            onStateChangedListener?.onCardTouchChange(state)
        }
        refreshLayout?.setOnRefreshListener {
            refreshData()
            PopWindowManager.checkReturnHomeOrRefreshPopWindow(requireContext(), false)
        }
        appBarLayout?.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout: AppBarLayout, i: Int ->
            val enable = i >= 0 && !isSwiping
            refreshLayout?.isEnabled = enable
            (refreshLayout?.refreshHeader as? View)?.hide(!enable)
            onAppBarScrollListener?.onAppBarScroll(i, appBarLayout.height)
            needTopClickHelper = appBarLayout.height - i.absoluteValue < 40.dp
            homeIndexViewModel?.homeIsShowRocket = i.absoluteValue >= appBarLayout.height
        })
        fabBtn?.setOnClickListener(View.OnClickListener {
            if (user == null) {
                return@OnClickListener
            }
            onPublishClick()
        })
        topClickHelper?.setOnClickListener {
            Logger.e("topClickHelper")
        }
        var startX = 0f
        var startY = 0f
        topClickHelper?.setOnTouchListener { v, ev ->
            Logger.e("setOnTouchListener,${ev.rawX},${ev.rawY}")
            if (!needTopClickHelper) {
                return@setOnTouchListener true
            }
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = ev.x
                    startY = ev.y
                }
                MotionEvent.ACTION_UP -> {
                    if ((ev.x - startX).absoluteValue < 3.dp && (ev.y - startY).absoluteValue < 3.dp) {
                        checkPostTab(ev)
                    }
                }
            }
            return@setOnTouchListener true
        }
        shakeHelper.setStartListener {
            //            摇一摇触发
            Logger.e("shakeHelper", "触发")
            viewModel.sign()
        }
    }

    override fun loadCache() {
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        Logger.e("PopWindowManager", "onFragmentResume")
        checkPageResume()
        if (parentActivityVisible) {
            asyncUI()
            shakeHelper.start()
            viewTouchBindHeler.onStart()
        }
    }

    override fun onLazy() {
        showLoadingView()
        Logger.e("onLazy")
        viewModel.loadData()
    }

    override fun onResume() {
        super.onResume()
        if (pageVisible) {
            checkPageResume()
        }
        shakeHelper.start()
        parentActivityVisible = true
        activityResume()
        viewTouchBindHeler.onStart()
    }

    override fun onPause() {
        super.onPause()
        viewTouchBindHeler.onStop()
        shakeHelper.stop()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (isAttach) {
                viewTouchBindHeler.onStart()
                shakeHelper.start()
            }

        } else {
            if (isAttach) {
                viewTouchBindHeler.onStop()
                shakeHelper.stop()
            }
        }
    }

    override fun onFragmentPause() {
        super.onFragmentPause()
        shakeHelper.stop()
        viewTouchBindHeler.onStop()
    }

    override fun onStop() {
        super.onStop()
        parentActivityVisible = false
    }

    override fun onDestroy() {
        super.onDestroy()
        refreshLiveDataObserver?.let {
            viewModel.refreshLiveData.removeObserver(it)
        }
        rvDataLiveDataObserver?.let {
            viewModel.rvDataLiveData.removeObserver(it)
        }
        viewTouchBindHeler.onDestroy()
    }

    private fun initViewAndEvent(rootView: View) {
        homeTouchCardView = rootView.findViewById(R.id.home_touch_card_view)
        refreshLayout = rootView.findViewById(R.id.home_fragment_second_refresh)
        fabBtn = rootView.findViewById(R.id.circle_activity_topic_detail_publish_iv)
        appBarLayout = rootView.findViewById(R.id.home_fragment_appBarLayout)
        postTabs = rootView.findViewById(R.id.home_post_tab)
        viewPager = rootView.findViewById(R.id.home_fragment_post_vp)
        betterTouchFrameLayout = rootView.findViewById(R.id.better_touch_frame_layout)
        topClickHelper = rootView.findViewById(R.id.top_click_helper)
        tabIv = rootView.findViewById(R.id.tab_iv)
        //        signView = rootView.findViewById(R.id.sign_view)
        activity?.let {
            signView = SignViewDialog(it, true)
        }
        viewPager?.isNoScroll = true
        homeTouchCardView?.apply {
            val lp = layoutParams
            lp.width = CardViewHelper.rvWidth.toInt()
            lp.height = CardViewHelper.rvHeight.toInt()
            layoutParams = lp
        }
    }

    private fun initObserver() {
        refreshLiveDataObserver = Observer {
            if (it) {
                refreshLayout?.finishRefresh()
            }
        }
        viewModel.refreshLiveData.observeForever(refreshLiveDataObserver!!)
        rvDataLiveDataObserver = Observer {

            setHomeDtata(it)
        }
        viewModel.rvDataLiveData.observeForever(rvDataLiveDataObserver!!)
        viewModel.signLiveData.observe(this, Observer {
            signView?.startSign(it)
        })
    }

    //页面从新显示
    private fun checkPageResume() {
        if ((homeIndexViewModel?.isLoginReturn == true) && LoginUtils.isLogin()) {
            if (ActivityUtils.isForeground(requireActivity())) {
                homeIndexViewModel?.isLoginReturn = false
                PopWindowManager.checkReturnHomeOrRefreshPopWindow(requireActivity(), true)
            }
        }
    }

    private fun activityResume() {
        Logger.e("PopWindowManager", "activityResume")
        homeIndexViewModel?.followCountLiveData?.observe(this, Observer {
            when (it) {
                is ApiResult.Success<*> -> {
                    val followCountEntity = it.data as FollowCountEntity
                    if (hasJumpFollow) {
                        return@Observer
                    }
                    //当页面对用户可见 && 用户关注数目为0 && 用户已经登录
                    if (followCountEntity.meCount == 0
                            && isSupportVisible
                            && AccountHelper.getInstance().user != null
                    ) {
                        val user = AccountHelper.getInstance().user
                        val key = "${AppSpConstant.RECOMMEND_SHOW}_${user.id}"
                        val hasShow: Boolean? = ConfigSPHelper.getInstance().getBoolean(key)
                        if (hasShow == true) {
                            hasJumpFollow = true
                            return@Observer
                        } else {
                            hasJumpFollow = true
                        }
                    }
                }
                is ApiResult.Error -> {
                }
            }
        })
    }

    private fun asyncScroll() {
        homeIndexViewModel?.homeScrollTop?.observe(this, Observer {
            if (!it) {
                return@Observer
            }
            tryScrollToTop()
        })
    }

    private fun tryScrollToTop() {
        val layoutParams = appBarLayout?.layoutParams ?: return
        val behavior = (layoutParams as CoordinatorLayout.LayoutParams).behavior
        if (behavior is AppBarLayout.Behavior) {
            if (behavior.topAndBottomOffset == 0) {
                return
            } else {
                behavior.topAndBottomOffset = 0
                GSYVideoManager.releaseAllVideos()
                preVideoPosition = 0L
                preVideoUrl = ""
            }
        }
    }

    private fun asyncUI() {
        Logger.e("PopWindowManager", "asyncUI")
        homeIndexViewModel?.followCountLiveData?.observe(this, Observer {
            when (it) {
                is ApiResult.Success<*> -> {
                    val followCountEntity = it.data as FollowCountEntity
                    if (hasJumpFollow) {
                        return@Observer
                    }
                    //当页面对用户可见 && 用户关注数目为0 && 用户已经登录
                    if (followCountEntity.meCount == 0
                            && isSupportVisible
                            && AccountHelper.getInstance().user != null
                    ) {
                        val user = AccountHelper.getInstance().user
                        val key = "${AppSpConstant.RECOMMEND_SHOW}_${user.id}"
                        val hasShow: Boolean? = ConfigSPHelper.getInstance().getBoolean(key)
                        if (hasShow == true) {
                            hasJumpFollow = true
                            return@Observer
                        } else {
                            hasJumpFollow = true
                        }
                    }
                }
                is ApiResult.Error -> {
                }
            }
        })
    }

    /**
     * 刷新界面
     */
    private fun refreshData() {
        viewModel.getHomeData(true)
        fetchUnRead()
    }

    fun fetchUnRead() {
        if (LoginUtils.isLogin()) {
            homeIndexViewModel?.refreshUnRedUnReceive()
        }
    }

    private fun onPublishClick() {
        val router = DcRouter(CircleConstant.Uri.CIRCLE_DYNAMIC_EDIT)
        //判断是否是选中的话题 tab。如果选中话题tab需要默认选中一个话题
        val entity = TalkTopicEntity()
        val currentItem = viewPager?.currentItem ?: 0
        when (fragmentList[currentItem]) {
            is HomeTopicPostListFragment -> {
                homeIndexViewModel?.homePageSelTopicEntity?.let {
                    entity.id = it.id
                    entity.name = it.name
                    if ((it.isDeal != true)) {
                        router.putExtra(CircleConstant.Extra.TALK, entity)
                    }
                    router.putUriParameter(
                            CircleConstant.UriParams.IS_DEAL,
                            "${(it.isDeal == true)}"
                    )
                }
            }
            is HomeDealPostListFragment -> {
                router.putUriParameter(CircleConstant.UriParams.IS_DEAL, "${true}")
            }
        }
        UTHelper.commonEvent(UTConstant.Circle.Editor_ent_HomeDynamicList)
        router.start()
    }

    fun setHomeDtata(homeData: HomeData?) {
        Logger.d("setRvData")
        if (null == homeData) {
            showEmptyView()
            return
        }
        viewBinding.homeActivityIv.hide(homeData.activity?.imgUrl.isNullOrEmpty())
        viewBinding.homeActivityIv.load(homeData.activity?.imgUrl, false)
        viewBinding.homeActivityIv.setOnShakeClickListener {
            homeData.activity?.linkUrl?.let {
                UTHelper.commonEvent(UTConstant.Home.Doubleeleven_click_HomeP)
                DcRouter(it).start()
            }
        }
        hideLoadingView()
        homeData.extraTabs?.let {
            setPostTabs(it)
        }
        tabIv?.load(homeData.creditItem?.imageUrl)
        tabIv?.setOnShakeClickListener {
            homeData.creditItem?.targetUrl?.let {
                DcRouter(it).start()
            }
        }
        updateTopItem(homeData)
        setFragments(homeData)
    }

    /**
     * 更新头部，重磅推荐
     *
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun updateTopItem(homeData: HomeData) {
        val homeItem = homeData.items
        if (null != homeItem) {

            //头部 重磅 Banner
            homeItemFragment = childFragmentManager
                    .findFragmentById(R.id.home_item_fragment) as HomeItemFragment?
            homeItemFragment?.setData(homeData)
            homeTouchCardView?.setData(homeData.items?.cardItems)
            viewTouchBindHeler.bind(fun(): View? {
                return homeItemFragment?.homeTopComonent?.getTouchCardView()
            }, fun(): View? {
                return homeTouchCardView
            })
            //缓存是否签到
            homeIndexViewModel?.isSign = homeData.isSign
        }
    }

    /**
     * 设置底部
     * 推荐、最新、关注fragment
     */
    private fun setFragments(homeData: HomeData) {
        appBarLayout?.post {
            val layoutParams = appBarLayout?.layoutParams as? CoordinatorLayout.LayoutParams
            val behavior = layoutParams?.behavior as? AppBarLayout.Behavior
            behavior?.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
                override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                    return true
                }
            })
        }
        if (null == homeData || homeData.extraTabs.isNullOrEmpty()) {
            fragmentList.clear()
            pageAdapter?.notifyDataSetChanged()
            return
        }
        var recommendPostFragment: HomeRecommendListFragment? = null
        //todo 重构优化代码，如果判断不需要移除就不所有都移除
        fragmentList.clear()
        if (childFragmentManager.fragments.isNotEmpty()) {
            val transaction = childFragmentManager.beginTransaction()
            for (fragment in childFragmentManager.fragments) {
                if (fragment !is HomeItemFragment) {
                    transaction.remove(fragment)
                }
            }
            transaction.commitNowAllowingStateLoss()
        }
        if (recommendPostFragment == null) {
            recommendPostFragment = HomeRecommendListFragment.newInstance()
            recommendPostFragment.postData = homeData.posts
        }

        fragmentList.add(recommendPostFragment)
        homeData.extraTabs?.forEach { bean ->
            val fragment = when (bean.type) {
                PostConstant.PostType.Latest,
                PostConstant.PostType.Fashion,
                PostConstant.PostType.Webpage,
                PostConstant.PostType.OFFICIAl -> {
                    HomePostListFragment.getInstance(bean.type)
                }
                PostConstant.PostType.Nearby -> {
                    HomeNearbyFragment.newInstance()
                }
                PostConstant.PostType.Talk -> {
                    HomeTopicPostListFragment.newInstance()
                }
                PostConstant.PostType.Followed -> {
                    HomeFollowedPostListFragment.getInstance()
                }
                PostConstant.PostType.Deal -> {
                    HomeDealPostListFragment.newInstance()
                }
                else -> {
                    null
                }
            }
            fragment?.let { fragmentList.add(it) }
        }
        if (homeIndexViewModel?.homeDCSelIndex ?: 0 > fragmentList.size - 1) {
            homeIndexViewModel?.homeDCSelIndex = 0
        }
        viewPager?.adapter = pageAdapter
        viewPager?.currentItem = homeIndexViewModel?.homeDCSelIndex ?: 0
        postTabs?.onPageSelected(homeIndexViewModel?.homeDCSelIndex ?: 0)
        pageAdapter?.notifyDataSetChanged()
        viewPager?.offscreenPageLimit = fragmentList.size
    }

    private fun setPostTabs(categoryList: List<HomePostExtraTabBean?>) {
        tabNameList.clear()
        tabNameList.add("推荐")
        commonNavigator = CommonNavigatorNew(requireContext())
        if (categoryList.isNullOrEmpty()) {
            return
        }
        categoryList.forEach {
            it?.title?.let { title ->
                tabNameList.add(title)
            }
        }
        tabPageAdapter = TabScaleAdapter(tabNameList)
        tabPageAdapter?.hideIndicator()
        tabPageAdapter?.apply {
            setStartAndEndTitleSize(14f, 14f)
            setNormalColor(getCompatColor(R.color.color_text_black4))
            setSelectedColor(getCompatColor(R.color.color_text_black1))
            setPadding(SizeUtils.dp2px(12f))
            setTabSelectListener { index ->
                viewPager?.setCurrentItem(index, false)
                homeIndexViewModel?.homeDCSelIndex = index
            }
        }
        commonNavigator?.setAdapter(tabPageAdapter!!)
        postTabs?.navigator = commonNavigator
        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                postTabs?.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
            ) {
                postTabs?.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                postTabs?.onPageSelected(position)
                currentTab = position
                homeIndexViewModel?.homeDCSelIndex = position
                utTabSwitch(position)
            }
        })
    }

    private fun utTabSwitch(position: Int) {
        viewModel.rvDataLiveData.value?.let {
            if (position > 0) {
                it.extraTabs?.get(position - 1)?.let { tab ->
                    when (tab.type) {
                        PostConstant.PostType.Latest -> {
                            UTHelper.commonEvent(UTConstant.Home.CLICK_LATEST_TAB)
                        }
                        PostConstant.PostType.Nearby -> {
                            UTHelper.commonEvent(UTConstant.Home.HomeP_click_Nearby_tab)
                        }
                        PostConstant.PostType.Talk -> {
                            UTHelper.commonEvent(UTConstant.Home.HomeP_click_Topic_tab)
                        }
                        PostConstant.PostType.Followed -> {
                            UTHelper.commonEvent(UTConstant.Home.CLICK_FOLLOW_TAB)
                        }
                        PostConstant.PostType.Deal -> {
                            UTHelper.commonEvent(UTConstant.Home.HomeP_click_Transaction_tab)
                        }
                    }
                }
            } else {
                UTHelper.commonEvent(UTConstant.Home.CLICK_RECOMMEND_TAB)
            }
        }
    }

    private fun checkPostTab(ev: MotionEvent) {
        tabIv?.let {
            if (checkInViewAndClick(it, ev)) {
                return
            }
        }
        kotlin.run {
            commonNavigator?.titleContainer?.let {
                for (i in 0 until it.childCount) {
                    if (checkInViewAndClick(it.getChildAt(i), ev)) {
                        return@run
                    }
                }
            }
        }

    }

    private fun checkInViewAndClick(view: View, ev: MotionEvent): Boolean {
        try {
            val location = IntArray(2)
            view.getLocationOnScreen(location)
            val rect = RectF(
                    location[0].toFloat(),
                    location[1].toFloat(),
                    location[0] + view.width.toFloat(),
                    location[1] + view.height.toFloat()
            )
            if (rect.contains(ev.rawX, ev.rawY)) {
                view.performClick()
                return true
            }
        } catch (e: Exception) {
        }
        return false
    }


    override fun switchPages(uri: Uri?) {
        super.switchPages(uri)
        val type = uri?.getQueryParameter(HomeConstant.UriParam.HOME_POST_TYPE)
        for (f in fragmentList) {
            (f as? HomePostListFragment).let {
                if (it?.postType == type) {
                    viewPager?.setCurrentItem(fragmentList.indexOf(f), false)
                }
            }
        }
    }

    interface OnAppBarScrollListener {
        /**
         *
         * 滑动的百分比
         *
         */
        fun onAppBarScroll(f: Int, appbarHeight: Int)
    }

    interface OnCardTouchChangeListener {
        fun onCardTouchChange(state: ItemTouchHelperCallback.DragStateEnum)
    }

}