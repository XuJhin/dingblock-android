package cool.dingstock.monitor.ui.regoin.tab

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.permissionx.guolindev.PermissionX
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.MonitorConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.customerview.CommonNavigatorNew
import cool.dingstock.appbase.entity.bean.mine.RegionsBean
import cool.dingstock.appbase.entity.event.monitor.EventChangeMonitorState
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.lazy.FragmentLazyStatePageAdapter
import cool.dingstock.appbase.mvp.lazy.VmBindingLazyFragment
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LocationHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.OnLocationResultListener
import cool.dingstock.appbase.widget.tablayout.TabScaleAdapter
import cool.dingstock.lib_base.thread.ThreadPoolHelper
import cool.dingstock.lib_base.util.CollectionUtils
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.monitor.R
import cool.dingstock.monitor.databinding.FragmentRegionRaffleCommonBinding
import cool.dingstock.monitor.ui.regoin.list.HomeRegionRaffleFragment
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 地区顶部Tab
 */
class RegionRaffleCommonFragment :
    VmBindingLazyFragment<RegionRaffleCommonViewModel, FragmentRegionRaffleCommonBinding>() {
    companion object {
        const val RAFFLE_ID = "raffleID"
        const val IS_CHANGE_UI = "changeUI"
        fun getInstance(raffleStr: String?, changeUI: Boolean): RegionRaffleCommonFragment {
            return RegionRaffleCommonFragment().apply {
                arguments = Bundle().apply {
                    raffleStr?.let {
                        putString(RAFFLE_ID, it)
                    }
                    putBoolean(IS_CHANGE_UI, changeUI)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[RegionRaffleCommonViewModel::class.java]
        arguments?.let {
            raffleId = it.getString(RAFFLE_ID, "")
            isResetUi = it.getBoolean(IS_CHANGE_UI)
        }
        viewModel.updateFilterId(raffleId)
        EventBus.getDefault().register(this)
    }

    private var tabLayout: MagicIndicator? = null
    private var viewPager: ViewPager? = null
    private var settingBtn: View? = null
    private var location: Location? = null
    private var raffleId: String = ""
    private var isResetUi = false

    private var commonNavigator : CommonNavigatorNew? = null
    //fragment 设置
    private var fragmentPageAdapter :FragmentLazyStatePageAdapter? = null
    private val fragmentList: MutableList<Fragment> = ArrayList()
    private val titleList: MutableList<String> = ArrayList()


    private fun changeUIInMonitorRegion() {
    }

    /**
     * 同步数据
     */
    private fun syncData() {
        viewModel.apply {
            hideLoading.observe(this@RegionRaffleCommonFragment) {
                this@RegionRaffleCommonFragment.hideLoadingView()
            }
            errorView.observe(this@RegionRaffleCommonFragment) {
                this@RegionRaffleCommonFragment.showErrorView(it)
            }
            regionsData.observe(viewLifecycleOwner) {
                setTabData(it)
            }
        }
    }


    override fun onLazy() {
        if (uri.getQueryParameter("needLogin") == "true") {
            activity?.let {
                if (!LoginUtils.isLoginAndRequestLogin(it)) {
                    it.finish()
                }
            }
        }
        requestData()
    }

    override fun initListeners() {
        settingBtn?.setOnClickListener {
            if (LoginUtils.isLoginAndRequestLogin(requireContext())) {
                DcRouter(MonitorConstant.Uri.MONITOR_MANAGE).start()
                UTHelper.commonEvent(UTConstant.Home.CLICK_REGION_SETTING)
                UTHelper.commonEvent(
                    UTConstant.Monitor.EnterSource_AllChannelP,
                    "Source",
                    "首页_关注地区功能位"
                )
            }
        }
        setOnErrorViewClick { refreshData() }
    }

    private fun refreshData() {
        viewModel.updateLocation(location)
        viewModel.getUserRegions()
    }

    fun moduleTag(): String {
        return ModuleConstant.HOME_MODULE
    }


    private fun setTabData(regionList: List<RegionsBean?>) {
        fragmentList.clear()
        titleList.clear()
        if (regionList.isEmpty()) {
            //            showEmptyView("你还没关注任何地区哦~")
            viewBinding.emptyLayer.visibility = View.VISIBLE
            fragmentPageAdapter?.notifyDataSetChanged()
            commonNavigator?.notifyDataSetChanged()
            return
        }
        viewBinding.emptyLayer.visibility = View.GONE
        var defaultSelected = 0
        if (CollectionUtils.isNotEmpty(regionList)) {
            for (region in regionList) {
                region?.name?.let {
                    titleList.add(it)
                    fragmentList.add(HomeRegionRaffleFragment.newInstance(region.objectId))
                }
            }
        }
        if (regionList.isNotEmpty()) {
            regionList.forEachIndexed { index, regionsBean ->
                if (raffleId == regionsBean!!.objectId) {
                    defaultSelected = index
                }
            }
        }
        fragmentPageAdapter?.notifyDataSetChanged()
        commonNavigator?.notifyDataSetChanged()
        ThreadPoolHelper.getInstance().runOnUiThread({
            commonNavigator?.onPageSelected(defaultSelected)
            tabLayout?.onPageSelected(defaultSelected)
            viewPager?.currentItem = defaultSelected
        }, 100)
        //关联
    }

    private fun initVP(){
        commonNavigator = CommonNavigatorNew(requireContext())
        //fragment 设置
        fragmentPageAdapter =
            FragmentLazyStatePageAdapter(childFragmentManager, fragmentList, titleList)
        viewPager?.adapter = fragmentPageAdapter
        //tab设置
        val tabPageAdapter = TabScaleAdapter(titleList)
        context?.getColor(R.color.color_text_black1)?.let {
            tabPageAdapter.setIndicatorColor(it, 28.dp.toInt(), 0)
        }
        tabPageAdapter.setStartAndEndTitleSize(14f, 16f)
        tabPageAdapter.setPadding(SizeUtils.dp2px(16f))
        if (isResetUi) {
            settingBtn?.visibility = View.INVISIBLE
        } else {
            tabPageAdapter.hideIndicator()
            settingBtn?.visibility = View.VISIBLE
        }

        tabPageAdapter.setTabSelectListener { index: Int ->
            viewPager?.currentItem = index
        }
        commonNavigator?.setAdapter(tabPageAdapter)
        tabLayout?.navigator = commonNavigator
        //关联
        ViewPagerHelper.bind(tabLayout, viewPager)
    }


    override fun initVariables(
        rootView: View?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        initView()
        syncData()
        initVP()
        if (isResetUi) {
            changeUIInMonitorRegion()
        }
    }

    private fun requestData() {
        showLoadingView()
        PermissionX.init(this)
            .permissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
//				.permissions(Manifest.permission.ACCESS_COARSE_LOCATION)
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
                            viewModel.updateLocation(location)
                            viewModel.getUserRegions()
                        }

                        override fun onLocationChange(location: Location?) {
                        }

                        override fun onLocationFail() {
                            viewModel.getUserRegions()
                        }
                    }).bind(this).startLocation()
                } else {
                    viewModel.getUserRegions()
                }
            }
    }

    private fun initView() {
        settingBtn = rootView?.findViewById(R.id.home_activity_region_raffle_tab_setting)
        tabLayout = rootView?.findViewById(R.id.home_activity_region_raffle_tab)
        viewPager = rootView?.findViewById(R.id.home_activity_region_raffle_page)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSubscribeChange(event: EventChangeMonitorState){
        refreshData()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}
