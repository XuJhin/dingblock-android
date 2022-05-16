package net.dingblock.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dingblock.market.ui.index.MarketIndexFragment
import com.google.android.material.tabs.TabLayout
import com.gyf.immersionbar.ktx.immersionBar
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.HomeConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.mvp.BaseActivity
import cool.dingstock.appbase.util.isWhiteMode
import cool.dingstock.appbase.widget.lottie.HomeIndexAniTab
import cool.dingstock.foundation.ext.viewbinding.binding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dingblock.home.ui.index.HomeMainFragment
import net.dingblock.mobile.R
import net.dingblock.mobile.databinding.MainActivityMainBinding
import net.dingblock.mobile.provider.AppNavBarProvider
import net.dingblock.mobile.provider.AppTabEntity
import net.dingblock.mobile.provider.EAppTabType
import net.dingblock.profile.ProfileIndexFragment


@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [HomeConstant.Path.TAB]
)
class AppMainActivity : BaseActivity() {

    val viewModel: AppMainViewModel by viewModels()
    val binding: MainActivityMainBinding by binding(MainActivityMainBinding::inflate)
    private val appNavTabs: MutableList<AppTabEntity> = arrayListOf()
    lateinit var homeTabAdapter: HomeTabAdapter
    var defaultPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewBar()
        setupBottomTab()
        setupViewPager()
    }

    private fun setupViewBar() {
        immersionBar {
            statusBarDarkFont(true)
            transparentStatusBar()
            navigationBarColor(R.color.white)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntendData()
    }

    private fun handleIntendData() {


        // TODO: 2022/5/14 处理推送以及路由数据
    }

    private fun setupViewPager() {
        lifecycleScope.launch(Dispatchers.Main) {
            appNavTabs.addAll(AppNavBarProvider.providerNavAppBar())
            homeTabAdapter = HomeTabAdapter(supportFragmentManager)
            binding.appHomeVp.apply {
                adapter = homeTabAdapter
                currentItem = defaultPos
                offscreenPageLimit = 3
            }
            appNavTabs.forEachIndexed { index, navItem ->
                homeTabAdapter.fragments.add(createFragment(navItem))
                appNavTabs[index].let { homeTabBean ->
                    val tab = binding.appHomeNavTab.newTab()
                    tab.setCustomView(R.layout.home_item_tab_badge)
                    val holder = tab.customView?.let { TabViewHolder(it) }
                    holder?.tabIcon?.setImage(if (isWhiteMode()) homeTabBean.tabInfo.tabIconRes else homeTabBean.tabInfo.tabIconDarkRes)
                    holder?.tabIcon?.isSelected = false
                    holder?.tabTitle?.text = homeTabBean.tabInfo.tabText
                    binding.appHomeNavTab.addTab(tab)
                }
            }
            homeTabAdapter.notifyDataSetChanged()

        }

    }

    private fun setupBottomTab() {
        binding.appHomeNavTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val targetTab = tab
                    binding.appHomeVp.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
//        viewBinding.homeActivityBottomTabView.addOnTabSelectedListener(
//            object : TabLayout.OnTabSelectedListener {
//                override fun onTabSelected(tab: TabLayout.Tab) {
//                    Logger.e(tab.position.toString() + "")
//                    //想要选中的Tab
//                    val targetTab = tabDataList[tab.position].convertToEnum()
//                    //未登录，切换失败（前往登录页面）
//                    if (targetTab.needLogin && AccountHelper.getInstance().user == null) {
//                        with(viewBinding.homeActivityBottomTabView) {
//                            selectTab(getTabAt(currentIndex))
//                        }
////                        val params = if (targetTab == EIndexTab.MESSAGE) {
////                            AccountConstant.LOGIN_2IM
////                        } else if (targetTab == EIndexTab.PROFILE) {
////                            AccountConstant.LOGIN_2MINE
////                        } else {
////                            -1
////                        }
////                        DcRouter(AccountConstant.Uri.INDEX)
////                            .putExtra(
////                                AccountConstant.LOGIN_ACTION,
////                                params
////                            )
////                            .start()
//                        return
//                    }
//
//                    if (fragmentList.isEmpty() || currentIndex == tab.position) {
//                        return
//                    }
//                    val holder = HomeIndexActivity.TabViewHolder(tab.customView)
//                    if (currentIndex != tab.position) {
//                        UTHelper.homeTab(holder.tabTitle.text.toString())
//                    }
//                    currentIndex = tab.position
//                    val bottomFragmentTab = tabDataList[currentIndex].convertToEnum()
//                    viewModel.homeBottomTabSelIndex = tab.position
//                    addOrShowFragment(
//                        fragmentList[currentIndex],
//                        bottomFragmentTab.Id
//                    )
//                    when (bottomFragmentTab) {
//                        EIndexTab.Market -> {
//                            if (viewModel.monitorIndex == 0) {
//                                viewModel.drawerState.postValue(HomeIndexViewModel.CurrentDrawerState.Monitor)
//                            } else {
//                                viewModel.drawerState.postValue(HomeIndexViewModel.CurrentDrawerState.None)
//                            }
//                        }
//                        EIndexTab.PROFILE -> viewModel.drawerState.postValue(HomeIndexViewModel.CurrentDrawerState.Mine)
//                        else -> {
//                            viewModel.drawerState.postValue(HomeIndexViewModel.CurrentDrawerState.None)
//                        }
//                    }
//                }
//
//                override fun onTabUnselected(tab: TabLayout.Tab) {}
//                override fun onTabReselected(tab: TabLayout.Tab) {}
//            })
    }

    private fun createFragment(it: AppTabEntity): Fragment {
        return when (it.type) {
            EAppTabType.Home -> HomeMainFragment.instance(it.tabInfo.tabId)
            EAppTabType.Market -> MarketIndexFragment()
            EAppTabType.Profile -> ProfileIndexFragment.instance()
        }
    }

    override fun moduleTag(): String {
        return "main"
    }

    internal class TabViewHolder(tabView: View) {
        var tabTitle: TextView
        var tabIcon: HomeIndexAniTab

        init {
            tabTitle = tabView.findViewById(R.id.home_tab_title)
            tabIcon = tabView.findViewById(R.id.home_tab_icon)
        }
    }
}