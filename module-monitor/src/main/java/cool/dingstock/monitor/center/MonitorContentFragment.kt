package cool.dingstock.monitor.center

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import cool.dingstock.appbase.adapter.VPLessViewAdapter
import cool.dingstock.appbase.constant.MonitorConstant
import cool.dingstock.appbase.constant.SpConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.customerview.CommonNavigatorNew
import cool.dingstock.appbase.entity.event.account.EventUserLoginOut
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.visual
import cool.dingstock.appbase.helper.ViewPagerScrollHelper
import cool.dingstock.appbase.mvp.BaseFragment
import cool.dingstock.appbase.mvp.lazy.VmBindingLazyFragment
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.ColorUtils
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.isDarkMode
import cool.dingstock.appbase.util.isWhiteMode
import cool.dingstock.appbase.viewmodel.HomeIndexViewModel
import cool.dingstock.appbase.widget.tablayout.SimplePagerTipTitleView
import cool.dingstock.appbase.widget.tablayout.TabScaleRedTipAdapter
import cool.dingstock.foundation.ext.tintColorRes
import cool.dingstock.lib_base.stroage.ConfigSPHelper
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.monitor.R
import cool.dingstock.monitor.databinding.MonitorFragmentLayoutBinding
import cool.dingstock.monitor.mine.monitor.MonitorMineFragment
import cool.dingstock.monitor.ui.recommend.MonitorRecommendFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author WhenYoung
 * CreateAt Time 2020/12/25  11:38
 * 监控中心主页
 */
class MonitorContentFragment :
    VmBindingLazyFragment<MonitorCenterViewModel, MonitorFragmentLayoutBinding>() {
    private lateinit var tabPageAdapter: TabScaleRedTipAdapter
    private lateinit var commonNavigator: CommonNavigatorNew

    var mainVm: HomeIndexViewModel? = null
    val vpsl = ViewPagerScrollHelper()

    /**
     * 当前选中的Tab
     */
    private var currentTab: Int = 0
    private val fragmentList: MutableList<BaseFragment> = ArrayList()
    private var pageAdapter: VPLessViewAdapter? = null
    private val tabNameList: MutableList<String> = ArrayList()
    private lateinit var recommendFragment: MonitorRecommendFragment
    private lateinit var mineFragment: MonitorMineFragment
    private var hideIconMenu = false
    private var managerIvAlpha = 0f
    var isHideHintPoint = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onLazy() {}

    override fun reload() {
        super.reload()
        for (f in fragmentList) {
            f.reload()
        }
    }

    override fun initVariables(
        rootView: View?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        mainVm = ViewModelProvider(requireActivity())[HomeIndexViewModel::class.java]
        resetStatusBarHeight()
        rootView?.findViewById<View>(cool.dingstock.appbase.R.id.fake_status_bar)?.background = null
        initView()
        initObserver()
        initViewPager()
        initTab()
        checkSel()
    }

    private fun initView() {
        isHideHintPoint =
            ConfigSPHelper.getInstance().getBoolean(MonitorConstant.DataParam.MONITOR_HINT_POINT)
        viewBinding.toolbarLayout.viewHintPoint.hide(isHideHintPoint)
        if (requireContext().isDarkMode()) {
            viewBinding.toolbarLayout.iconMenu.tintColorRes(R.color.color_text_absolutely_white)
            viewBinding.toolbarLayout.monitorSettingIv.tintColorRes(R.color.color_text_absolutely_white)
            viewBinding.monitorLayoutGradient.visibility = View.INVISIBLE
        } else {
            viewBinding.monitorLayoutGradient.visibility = View.VISIBLE
        }
    }

    private fun initObserver() {
        viewModel.mineContentShowBlackBg.observe(this) { isDark ->
            if (requireActivity().isDarkMode()) {
                return@observe
            }
            changeTabAndBgVColor(viewBinding.viewpager.currentItem, tabPageAdapter)
            if (isDark) {
                if (currentTab == 0) {
                    setupIconColor(R.color.white)
                    viewBinding.layoutMonitorBg.alpha = 1f
                    if (managerIvAlpha != 0f) {
                        viewBinding.toolbarLayout.myMonitorManagerIv.alpha = 1f
                    }
                } else {
                    setupIconColor(R.color.white)
                    viewBinding.layoutMonitorBg.alpha = 0f
                    viewBinding.toolbarLayout.myMonitorManagerIv.alpha = 0f
                    viewBinding.toolbarLayout.myMonitorManagerIv.hide()
                }
            } else {
                setupIconColor(R.color.black)
                viewBinding.layoutMonitorBg.alpha = 0f
                viewBinding.toolbarLayout.myMonitorManagerIv.alpha = 0f
                viewBinding.toolbarLayout.myMonitorManagerIv.hide()
            }
        }
    }

    private fun initViewPager() {
        recommendFragment = MonitorRecommendFragment()
        mineFragment = MonitorMineFragment.getInstance().apply {
            setTopManagerIvAlpha {
                managerIvAlpha = it
                setManagerIvAlpha(it)
            }
        }
        fragmentList.add(mineFragment)
        fragmentList.add(recommendFragment)
        pageAdapter = VPLessViewAdapter(childFragmentManager, fragmentList)
        viewBinding.viewpager.adapter = pageAdapter
        vpsl.setViewPagerChangeListener(object : ViewPagerScrollHelper.OnViewPagerChangeListener {
            override fun onEnter(
                index: Int,
                totalCount: Int,
                enterPercent: Float,
                leftToRight: Boolean
            ) {
                if (index == 0) {
                    if (!viewModel.minContentIsBlackTheme) {
                        viewBinding.layoutMonitorBg.alpha = 0f
                        viewBinding.toolbarLayout.myMonitorManagerIv.alpha = 0f
                        viewBinding.toolbarLayout.myMonitorManagerIv.hide()
                        initIconColor(1f)
                        return
                    }
                    viewBinding.layoutMonitorBg.alpha = enterPercent
                    setManagerIvAlpha(enterPercent * managerIvAlpha)
                    initIconColor(1 - enterPercent)
                } else {
                    if (!viewModel.minContentIsBlackTheme) {
                        viewBinding.layoutMonitorBg.alpha = 0f
                        viewBinding.toolbarLayout.myMonitorManagerIv.alpha = 0f
                        viewBinding.toolbarLayout.myMonitorManagerIv.hide()
                        initIconColor(1f)
                        return
                    }
                    viewBinding.layoutMonitorBg.alpha = 1 - enterPercent
                    setManagerIvAlpha((1 - enterPercent) * managerIvAlpha)
                    initIconColor(enterPercent)
                }
            }

            override fun onLeave(
                index: Int,
                totalCount: Int,
                leavePercent: Float,
                leftToRight: Boolean
            ) {
            }

            override fun onSelected(index: Int, totalCount: Int) {
                changeTabAndBgVColor(index, tabPageAdapter)
            }

            override fun onDeselected(index: Int, totalCount: Int) {
            }
        })
    }

    private fun initTab() {
        tabNameList.add("我的监控")
        tabNameList.add("推荐")
        commonNavigator = CommonNavigatorNew(requireContext())
        tabPageAdapter = TabScaleRedTipAdapter(tabNameList)
        commonNavigator.post {
            setShowRecommendTip()
        }
        if (requireContext().isDarkMode()) {
            tabPageAdapter.setNormalColor(resources.getColor(R.color.color_text_black4))
            tabPageAdapter.setSelectedColor(resources.getColor(R.color.color_text_black1))
        }
        //显示红点
        tabPageAdapter.apply {
            setStartAndEndTitleSize(16f, 16f)
            setPadding(SizeUtils.dp2px(11f))
            setTabSelectListener { index ->
                mainVm?.monitorWantIndex = index
                if (index == 0) {
                    if (!LoginUtils.isLoginAndRequestLogin(requireContext())) {
                        return@setTabSelectListener
                    }
                }
                viewBinding.viewpager.currentItem = index
                mainVm?.monitorIndex = index
                viewBinding.viewpager.setCurrentItem(index, false)
            }
        }
        changeTabAndBgVColor(viewBinding.viewpager.currentItem, tabPageAdapter)
        tabPageAdapter.hideIndicator()
        tabPageAdapter.notifyDataSetChanged()
        commonNavigator.setAdapter(tabPageAdapter)
        viewBinding.toolbarLayout.monitorTitleTab.navigator = commonNavigator
        viewBinding.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                viewBinding.toolbarLayout.monitorTitleTab.onPageScrollStateChanged(state)
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    mineFragment.pauseEmptyAnim()
                } else if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if (viewBinding.viewpager.currentItem == 0) {
                        mineFragment.resumeEmptyAnim()
                    }
                }
                vpsl.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                viewBinding.toolbarLayout.monitorTitleTab.onPageScrolled(
                    position,
                    positionOffset,
                    positionOffsetPixels
                )
                vpsl.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                if (!hideIconMenu) {
                    viewBinding.toolbarLayout.iconMenu.hide(position == 1)
                }
                vpsl.onPageSelected(position)
                viewBinding.toolbarLayout.monitorTitleTab.onPageSelected(position)
                currentTab = position
                mainVm?.monitorWantIndex = position
                mainVm?.monitorIndex = position
                mainVm?.drawerState?.postValue(if (position == 0) HomeIndexViewModel.CurrentDrawerState.Monitor else HomeIndexViewModel.CurrentDrawerState.None)
                hideRecommendTip(tabPageAdapter)
                UTHelper.commonEvent(if (position == 0) UTConstant.Monitor.MonitorP_sel_MyMonitoring_tab else UTConstant.Monitor.MonitorP_sel_Recommend_tab)
            }
        })
    }

    /**
     * 更新设置按钮颜色
     * positionOffset  白色到黑色的偏移量 0代表白色1,1
     *
     */
    private fun initIconColor(expectedColor: Float) {
        if (requireContext().isDarkMode()) {
            setupIconColor(R.color.color_text_absolutely_white)
            return
        }
        if (!LoginUtils.isLogin()) {
            setupIconColor(R.color.black)
            return
        }

        val svgColor = ColorUtils.getColorChanges(0xffffff, 0x000000, expectedColor)
        viewBinding.toolbarLayout.iconMenu.imageTintList = ColorStateList.valueOf(svgColor)
        viewBinding.toolbarLayout.monitorSettingIv.imageTintList =
            ColorStateList.valueOf(svgColor)
    }

    private fun setManagerIvAlpha(alpha: Float) {
        if (managerIvAlpha > 0f) {
            viewBinding.toolbarLayout.myMonitorManagerIv.visual(alpha > 0f)
            viewBinding.toolbarLayout.myMonitorManagerIv.alpha = alpha
        } else {
            viewBinding.toolbarLayout.myMonitorManagerIv.hide()
        }
    }

    override fun initListeners() {
        viewBinding.toolbarLayout.monitorSettingIv.setOnClickListener {
            if (!LoginUtils.isLoginAndRequestLogin(requireContext())) {
                return@setOnClickListener
            }
            //2.9.7 监控设置添加小红点提示
            if (!isHideHintPoint) {
                viewBinding.toolbarLayout.viewHintPoint.hide(true)
                ConfigSPHelper.getInstance()
                    .save(MonitorConstant.DataParam.MONITOR_HINT_POINT, true)
            }
            DcRouter(MonitorConstant.Uri.MONITOR_SETTING).start()
            UTHelper.commonEvent(UTConstant.Monitor.MonitorP_click_MonitorSet_icon)
        }
        viewBinding.toolbarLayout.iconMenu.setOnClickListener {
            UTHelper.commonEvent(UTConstant.Monitor.MonitorP_click_ClassicMode_icon)
            if (LoginUtils.isLoginAndRequestLogin(requireContext())) {
                mainVm?.drawerOpenMonitor?.postValue(true)
            }
        }
        viewBinding.toolbarLayout.myMonitorManagerIv.setOnClickListener {
            mineFragment.routeToOnLineSubscribe()
            UTHelper.commonEvent(UTConstant.Monitor.MyMonitorP_click_AddMonitor)
        }
    }

    private fun checkSel() {
        if (!LoginUtils.isLogin()) {
            mainVm?.isCheckMonitorSel = true
            viewBinding.viewpager.currentItem = 1
            return
        }
        //有监控获 默认选中 我的监控，在app启动之中第一次启动才判断
        if (mainVm?.isCheckMonitorSel == false) {
            mainVm?.isCheckMonitorSel = true
            if (LoginUtils.getCurrentUser()?.channels == null || LoginUtils.getCurrentUser()?.channels?.size == 0) {
                viewBinding.viewpager.currentItem = 1
                mainVm?.drawerState?.postValue(HomeIndexViewModel.CurrentDrawerState.None)
            } else {
                viewBinding.viewpager.currentItem = 0
                mainVm?.drawerState?.postValue(HomeIndexViewModel.CurrentDrawerState.Monitor)
            }
            return
        }
        viewBinding.viewpager.currentItem = mainVm?.monitorIndex ?: 0
    }

    private fun setupIconColor(@ColorRes colorRes: Int) {
        viewBinding.toolbarLayout.iconMenu.imageTintList =
            ContextCompat.getColorStateList(requireContext(), colorRes)
        viewBinding.toolbarLayout.monitorSettingIv.imageTintList =
            ContextCompat.getColorStateList(requireContext(), colorRes)
    }

    private fun changeTabAndBgVColor(position: Int, tabPageAdapter: TabScaleRedTipAdapter) {
        if (requireActivity().isWhiteMode()) {
            if (position == 0) {
                if (viewModel.minContentIsBlackTheme) {
                    tabPageAdapter.setSelectedColor(getCompatColor(R.color.white))
                    tabPageAdapter.setNormalColor(getCompatColor(R.color.color_text_white_a40))
                    tabPageAdapter.notifyDataSetChanged()
                } else {
                    tabPageAdapter.setSelectedColor(getCompatColor(R.color.color_text_black1))
                    tabPageAdapter.setNormalColor(getCompatColor(R.color.color_text_black4))
                    tabPageAdapter.notifyDataSetChanged()
                }
            } else {
                tabPageAdapter.setSelectedColor(getCompatColor(R.color.color_text_black1))
                tabPageAdapter.setNormalColor(getCompatColor(R.color.color_text_black4))
                tabPageAdapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * 判断显示红点
     * */
    private fun setShowRecommendTip() {
        try {
            //判断
            MobileHelper.getInstance().configData?.monitorConfig?.lastUpdateTime?.let { lastUpdateTime ->
                val lastShowTime =
                    ConfigSPHelper.getInstance().getLong(SpConstant.MONITOR_RECOMMEND_TIP, 0L)
                //如果最新version大于上次的time
                if (lastUpdateTime > lastShowTime) {
                    findRecommendTab()?.showTip = true
                }
            }
        } catch (e: Exception) {
        }
    }

    /**
     * 判断隐藏红点
     * */
    private fun hideRecommendTip(tabPageAdapter: TabScaleRedTipAdapter) {
        if (currentTab == 1) { //选中推荐移除红点
            findRecommendTab()?.showTip = false
            MobileHelper.getInstance().configData?.monitorConfig?.lastUpdateTime?.let {
                ConfigSPHelper.getInstance().save(SpConstant.MONITOR_RECOMMEND_TIP, it)
            }
        }
    }

    private fun findRecommendTab(): SimplePagerTipTitleView? {
        (commonNavigator.titleContainer?.getChildAt(1) as? SimplePagerTipTitleView)?.let { view ->
            return view
        }
        return null
    }

    private fun finMineTab(): SimplePagerTipTitleView? {
        (commonNavigator.titleContainer?.getChildAt(0) as? SimplePagerTipTitleView)?.let { view ->
            return view
        }
        return null
    }

    fun hideTopRightIcon(hide: Boolean) {
        hideIconMenu = hide
        if (viewBinding.viewpager.currentItem == 0) {
            viewBinding.toolbarLayout.iconMenu.hide(hideIconMenu)
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun loginOut(loginOut: EventUserLoginOut) {
        setupIconColor(R.color.black)
    }

    companion object {
        const val MONITOR_SHADE_GUID = "monitor_shade_guid"
    }
}