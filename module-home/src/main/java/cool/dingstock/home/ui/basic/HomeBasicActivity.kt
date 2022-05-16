package cool.dingstock.home.ui.basic

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.adapter.VPLessViewAdapter
import cool.dingstock.appbase.app.SDKInitHelper
import cool.dingstock.appbase.constant.*
import cool.dingstock.appbase.customerview.CommonNavigatorNew
import cool.dingstock.appbase.dialog.PrivacyPolicyDialog
import cool.dingstock.appbase.entity.bean.home.HomeData
import cool.dingstock.appbase.entity.bean.home.HomePostExtraTabBean
import cool.dingstock.appbase.helper.config.ConfigHelper
import cool.dingstock.appbase.helper.config.ConfigHelper.isAgreePolicy
import cool.dingstock.appbase.mvp.BaseFragment
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.net.retrofit.manager.RxRetrofitServiceManager
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.widget.tablayout.TabScaleAdapter
import cool.dingstock.home.databinding.HomeActivityBasicBinding
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.post.list.PostConstant

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [HomeConstant.Path.BASIC]
)
class HomeBasicActivity : VMBindingActivity<HomeBasicVm, HomeActivityBasicBinding>() {
    private var currentTab: Int = 0
    private val fragmentList: MutableList<BaseFragment> = ArrayList()
    private var pageAdapter: VPLessViewAdapter? = null
    private val tabNameList: MutableList<String> = ArrayList()
    private var tabPageAdapter: TabScaleAdapter? = null
    private var commonNavigator: CommonNavigatorNew? = null

    private var privacyPolicyDialog: PrivacyPolicyDialog? = null

    override fun moduleTag(): String {
        return ModuleConstant.HOME_MODULE
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        privacyPolicyDialog = PrivacyPolicyDialog(this, this)
        pageAdapter = VPLessViewAdapter(supportFragmentManager, fragmentList)
        viewBinding.viewPager.apply {
            adapter = pageAdapter
            currentItem = 0
            offscreenPageLimit = 2
        }

        viewModel.rvDataLiveData.observeForever {
            setHomeData(it)
        }
        showLoadingView()
        viewModel.loadData()
    }

    override fun initListeners() {
        viewBinding.changeToWhole.setOnClickListener {
            if (!isAgreePolicy()) {
                showPolicyDialog()
            }
        }
    }

    private fun showPolicyDialog() {
        privacyPolicyDialog?.privacyListener = object : PrivacyPolicyDialog.PrivacyListener {
            override fun onAgree() {
                RxRetrofitServiceManager.getInstance().resetHeader()
                ConfigHelper.setUserAgreePolicy(true)
                SDKInitHelper.beginInitSDK(application, true)
                DcRouter(HomeConstant.Uri.TAB).startNoTransition()
                finish()
            }

            override fun onQuit() {

            }

            override fun readProtocol() {
                viewModel.getCloudUrl(ServerConstant.Function.ARREEMENT)
            }

            override fun readPrivacy() {
                viewModel.getCloudUrl(ServerConstant.Function.PRIVACY)
            }

            override fun readUserCollectList() {
                viewModel.getCloudUrl(ServerConstant.Function.PERSONAL_INFORMATION)
            }

            override fun thirdShareList() {
                viewModel.getCloudUrl(ServerConstant.Function.EXTERNAL_INFORMATION)
            }
        }
        if (privacyPolicyDialog?.isShowing == false) {
            privacyPolicyDialog?.show()
            UTHelper.commonEvent(UTConstant.Login.LOGIN_PRIVACY_DIALOG, "曝光")
        }
    }

    private fun setHomeData(homeData: HomeData?) {
        if (null == homeData) {
            showEmptyView()
            return
        }
        hideLoadingView()
        homeData.extraTabs?.let {
            setPostTabs(it)
        }
        setFragments(homeData)
    }

    private fun setPostTabs(categoryList: List<HomePostExtraTabBean?>) {
        tabNameList.clear()
        tabNameList.add("推荐")
        commonNavigator = CommonNavigatorNew(this)
        if (categoryList.isNullOrEmpty()) {
            return
        }
        categoryList.forEach {
            it?.title?.let { title ->
                if (title == "最新") {
                    tabNameList.add(title)
                }
            }
        }
        tabPageAdapter = TabScaleAdapter(tabNameList)
        tabPageAdapter?.hideIndicator()
        tabPageAdapter?.apply {
            setStartAndEndTitleSize(14f, 14f)
            setPadding(SizeUtils.dp2px(12f))
            setTabSelectListener { index ->
                viewBinding.viewPager.setCurrentItem(index, false)
            }
        }
        commonNavigator?.setAdapter(tabPageAdapter!!)
        viewBinding.homePostTab.navigator = commonNavigator
        viewBinding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                viewBinding.homePostTab.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                viewBinding.homePostTab.onPageScrolled(
                    position,
                    positionOffset,
                    positionOffsetPixels
                )
            }

            override fun onPageSelected(position: Int) {
                viewBinding.homePostTab.onPageSelected(position)
                currentTab = position
            }
        })
    }

    private fun setFragments(homeData: HomeData) {
        if (homeData.extraTabs.isNullOrEmpty()) {
            fragmentList.clear()
            pageAdapter?.notifyDataSetChanged()
            return
        }
        var recommendPostFragment: BasicPostListFragment? = null

        fragmentList.clear()
        if (recommendPostFragment == null) {
            recommendPostFragment =
                BasicPostListFragment.getInstance(PostConstant.PostType.Recommend)
                    .apply {
                        postData = homeData.posts
                        isBasic = true
                        basicItemClick = {
                            showPolicyDialog()
                        }
                    }
        }

        fragmentList.add(recommendPostFragment)

        homeData.extraTabs?.forEach { bean ->
            val fragment = when (bean.type) {
                PostConstant.PostType.Latest,
                PostConstant.PostType.Fashion -> {
                    BasicPostListFragment.getInstance(bean.type).apply {
                        isBasic = true
                        basicItemClick = {
                            showPolicyDialog()
                        }
                    }
                }
                else -> {
                    null
                }
            }
            fragment?.let { fragmentList.add(it) }
        }
        viewBinding.viewPager.adapter = pageAdapter
        viewBinding.viewPager.currentItem = 0
        viewBinding.homePostTab.onPageSelected(0)
        pageAdapter?.notifyDataSetChanged()
        viewBinding.viewPager.offscreenPageLimit = fragmentList.size
    }
}