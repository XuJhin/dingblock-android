//package net.dingblock.mobile.activity.index
//
//
//import android.content.DialogInterface
//import android.content.Intent
//import android.content.res.Configuration
//import android.graphics.BitmapFactory
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.provider.Settings
//import android.text.TextUtils
//import android.util.Log
//import android.view.KeyEvent
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.EditText
//import android.widget.LinearLayout
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatDelegate
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.core.view.GravityCompat
//import androidx.core.view.isVisible
//import androidx.drawerlayout.widget.DrawerLayout
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Lifecycle
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import androidx.viewpager.widget.ViewPager
//import com.chad.library.adapter.base.BaseBinderAdapter
//import com.fm.openinstall.OpenInstall
//import com.fm.openinstall.listener.AppInstallAdapter
//import com.fm.openinstall.model.AppData
//import com.google.android.material.tabs.TabLayout
//import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
//import com.shuyu.gsyvideoplayer.GSYVideoManager
//import cool.dingstock.appbase.constant.*
//import cool.dingstock.appbase.delegate.DrawerActivityDelegate
//import cool.dingstock.appbase.entity.bean.drawer.MineDrawerEntity
//import cool.dingstock.appbase.entity.bean.drawer.MineDrawerItemEntity
//import cool.dingstock.appbase.entity.bean.home.HomeTabBean
//import cool.dingstock.appbase.entity.bean.mine.MineVipChargeEntity
//import cool.dingstock.appbase.entity.bean.push.PushMessage.NoticeBean
//import cool.dingstock.appbase.entity.event.account.EventActivated
//import cool.dingstock.appbase.entity.event.account.EventIsAuthorized
//import cool.dingstock.appbase.entity.event.account.EventSessionInvalid
//import cool.dingstock.appbase.entity.event.account.EventUserLoginOut
//import cool.dingstock.appbase.entity.event.im.EventSystemNotice
//import cool.dingstock.appbase.entity.event.update.EventCommunityChange
//import cool.dingstock.appbase.ext.hide
//import cool.dingstock.appbase.helper.DarkMode
//import cool.dingstock.appbase.helper.DarkModeHelper
//import cool.dingstock.appbase.helper.HomeSuggestHelper
//import cool.dingstock.appbase.helper.PopWindowManager.checkAppLaunchedPopWindow
//import cool.dingstock.appbase.helper.SettingHelper
//import cool.dingstock.appbase.mvp.BaseFragment
//import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
//import cool.dingstock.appbase.net.api.account.AccountHelper
//import cool.dingstock.appbase.net.api.home.HomeHelper
//import cool.dingstock.appbase.net.mobile.MobileHelper
//import cool.dingstock.appbase.net.parse.ParseCallback
//import cool.dingstock.appbase.share.ShareParams
//import cool.dingstock.appbase.share.SharePlatform
//import cool.dingstock.appbase.share.ShareType
//import cool.dingstock.appbase.ut.UTHelper
//import cool.dingstock.appbase.util.LoginUtils
//import cool.dingstock.appbase.util.LoginUtils.isLogin
//import cool.dingstock.appbase.util.StatusBarUtil
//import cool.dingstock.appbase.util.isWhiteMode
//import cool.dingstock.appbase.viewmodel.EIndexTab
//import cool.dingstock.appbase.viewmodel.HomeIndexViewModel
//import cool.dingstock.appbase.widget.common_edit_dialog.CommonEditDialog
//import cool.dingstock.appbase.widget.common_edit_dialog.CommonEditDialog.OnConfirmClickListener
//import cool.dingstock.appbase.widget.lottie.HomeIndexAniTab
//import cool.dingstock.home.ui.home.HomeIndexFragment
//import cool.dingstock.lib_base.json.JSONHelper
//import cool.dingstock.lib_base.stroage.ConfigFileHelper
//import cool.dingstock.lib_base.stroage.ConfigSPHelper
//import cool.dingstock.lib_base.util.*
//import cool.dingstock.lib_base.util.AppUtils.getVersionName
//import cool.dingstock.mine.ui.index.MineFragment
//import cool.mobile.account.share.ShareDialog
//import net.dingblock.mobile.R
//import net.dingblock.mobile.activity.index.drawer.mine.item.MineDrawerGroupItemBinder
//import net.dingblock.mobile.activity.index.drawer.mine.item.MineDrawerItemBinder
//import net.dingblock.mobile.databinding.HomeActivityIndexBinding
//import org.greenrobot.eventbus.EventBus
//import org.greenrobot.eventbus.Subscribe
//import org.greenrobot.eventbus.ThreadMode
//import java.util.*
//
////@RouterUri(
////    scheme = RouterConstant.SCHEME,
////    host = RouterConstant.HOST,
////    path = [HomeConstant.Path.TAB]
////)
//class HomeIndexActivity : VMBindingActivity<HomeIndexViewModel, HomeActivityIndexBinding>(),
//    DrawerActivityDelegate {
//    private var tabDataList: List<HomeTabBean> = arrayListOf()
//    private var fragmentList: MutableList<BaseFragment> = arrayListOf()
//    private var currentFragment: Fragment? = null
//    private var monitorDrawer: MonitorDrawerView? = null
//    private var mineDrawer: View? = null
//
//    private val mineDrawerAdapter: BaseBinderAdapter by lazy {
//        BaseBinderAdapter()
//    }
//
//    private var monitorFragment: MonitorContentFragment? = null
//
//    private var currentIndex = 0
//    private var mExitTime: Long = 0
//
//    //缓存首页的tab
//    private var homeTab: TabLayout.Tab? = null
//
//    //判断这个页面是不是第一次启动的标志位
//    private var isLaunch = true
//
//    private var mineDrawerItem: MineDrawerItemEntity? = null
//
//    override fun moduleTag(): String {
//        return ModuleConstant.HOME_MODULE
//    }
//
//    override fun setSystemStatusBar() {
//        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null)
//        setSystemStatusBarMode()
//    }
//
//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        if (isLaunch && hasFocus) {
//            isLaunch = false
//            checkAppLaunchedPopWindow(this)
//        } else {
//            super.onWindowFocusChanged(hasFocus)
//        }
//    }
//
//
//    private fun initDrawer() {
//        with(viewBinding.homeIndexDrawLayout) {
//            setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//            addDrawerListener(object : DrawerLayout.DrawerListener {
//                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
//
//                }
//
//                override fun onDrawerOpened(drawerView: View) {
//                    if (monitorDrawer?.visibility == View.VISIBLE) {
//                        monitorDrawer?.loadData()
//                    }
//                }
//
//                override fun onDrawerClosed(drawerView: View) {
//                    monitorDrawer?.onClose()
//                }
//
//                override fun onDrawerStateChanged(newState: Int) {
//
//                }
//
//            })
//        }
//        mineDrawerAdapter.apply {
//            val mineDrawerItemBinder = MineDrawerItemBinder()
//            addItemBinder(MineDrawerEntity::class.java, MineDrawerGroupItemBinder())
//            addItemBinder(MineDrawerItemEntity::class.java, mineDrawerItemBinder)
//            viewBinding.homeIndexDrawLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
//                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
//
//                override fun onDrawerOpened(drawerView: View) {}
//
//                override fun onDrawerClosed(drawerView: View) {
//                    if (mineDrawer?.isVisible == true) {
//                        mineDrawerItem?.let {
//                            //native的使用 本地路由跳转
//                            when (it.actionType) {
//                                "native" -> {
//                                    if (it.link.isNotEmpty()) {
//                                        when (it.name) {
//                                            "我的订单" -> {
//                                                UTHelper.commonEvent(UTConstant.Oversea.Oversea_Myorder)
//                                            }
//                                            "我的优惠券" -> {
//                                                UTHelper.commonEvent(UTConstant.Oversea.Oversea_Mycoupon)
//                                            }
//                                            "编辑资料" -> {
//                                                UTHelper.commonEvent(UTConstant.Setting.SettingP_click_EditInformation)
//                                            }
//                                            "已监控地区/频道" -> {
//                                                UTHelper.commonEvent(UTConstant.Setting.SettingP_click_FocusAreaChannel)
//                                            }
//                                        }
//                                        DcRouter(it.link).start()
//                                    }
//                                }
//                                "h5" -> {
//                                    if (!TextUtils.isEmpty(it.value)) {
//                                        MobileHelper.getInstance()
//                                            .getCloudUrl(
//                                                it.value,
//                                                object : ParseCallback<MineVipChargeEntity?> {
//                                                    override fun onFailed(
//                                                        errorCode: String,
//                                                        errorMsg: String
//                                                    ) {
//                                                    }
//
//                                                    override fun onSucceed(data: MineVipChargeEntity?) {
//                                                        if (data?.url.isNullOrEmpty()) {
//                                                            return
//                                                        }
//                                                        data?.url?.let {
//                                                            DcRouter(data.url)
//                                                                .start()
//                                                        }
//                                                    }
//                                                })
//                                    }
//                                }
//                                "vip_code" -> {
//                                    UTHelper.commonEvent(UTConstant.Setting.SettingP_click_LnvitationCode)
//                                    showActivateDialog()
//                                }
//                                "setting_personal_rule" -> {
//                                    getCouldUrl(ServerConstant.Function.PRIVACY)
//                                }
//                                "setting_app_permission" -> {
//                                    getCouldUrl(ServerConstant.Function.appPermissionsUrl)
//                                }
//                                "setting_personal_list" -> {
//                                    getCouldUrl(ServerConstant.Function.personalInformationUrl)
//                                }
//                                "setting_third_party" -> {
//                                    getCouldUrl(ServerConstant.Function.externalInformationUrl)
//                                }
//                            }
//                            mineDrawerItem = null
//                        }
//                    }
//                }
//
//                override fun onDrawerStateChanged(newState: Int) {}
//
//            })
//            setOnItemClickListener { adapter, _, position ->
//                val entity = adapter.data[position]!!
//                if (entity is MineDrawerItemEntity) {
//                    mineDrawerItem = entity
//                    viewBinding.homeIndexDrawLayout.closeDrawer(GravityCompat.START)
//                    if (entity.value == "kolInvitations") {
//                        UTHelper.commonEvent(UTConstant.Setting.MyP_click_Profit)
//                    }
//                }
//            }
//        }
//        setupMonitorDrawer()
//        setupProfileDrawer()
//    }
//
//    private fun setupProfileDrawer() {
//        if (mineDrawer == null) {
//            mineDrawer = LayoutInflater.from(context).inflate(R.layout.layout_drawer_mine, null)
//            val layoutSetting: ConstraintLayout? =
//                mineDrawer?.findViewById(R.id.drawer_layout_setting)
//            val layoutShare: ConstraintLayout? = mineDrawer?.findViewById(R.id.drawer_layout_share)
//            val layoutCustomer: ConstraintLayout? =
//                mineDrawer?.findViewById(R.id.drawer_layout_customer)
//            viewBinding.homeIndexLeftLayer.isClickable = true
//            val params: ViewGroup.LayoutParams = ViewPager.LayoutParams()
//            params.width = LinearLayout.LayoutParams.MATCH_PARENT
//            params.height = LinearLayout.LayoutParams.MATCH_PARENT
//            viewBinding.homeIndexLeftLayer.addView(mineDrawer, params)
//            val rvMineDrawer: RecyclerView? = mineDrawer?.findViewById(R.id.mine_drawer_rv)
//            rvMineDrawer?.adapter = mineDrawerAdapter
//            rvMineDrawer?.layoutManager = LinearLayoutManager(this)
//            mineDrawerAdapter.setList(getMineDrawerList())
//            layoutSetting?.setOnClickListener {
//                UTHelper.commonEvent(UTConstant.Mine.MyP_click_SetUp)
//                DcRouter(SettingConstant.Uri.INDEX)
//                    .start()
//                closeDrawer()
//            }
//            layoutCustomer?.setOnClickListener {
//                UTHelper.commonEvent(UTConstant.Setting.SettingP_click_HelpFeedback, "客服中心")
//                MobileHelper.getInstance().getCloudUrlAndRouter(context, "help")
//                closeDrawer()
//            }
//            layoutShare?.setOnClickListener {
//                shareApp()
//                closeDrawer()
//            }
//        }
//    }
//
//    private fun setupMonitorDrawer() {
//        if (monitorDrawer == null) {
//            monitorDrawer = MonitorDrawerView(this)
//            monitorDrawer?.setOnFilterClick {
//                closeDrawer()
//            }
//            monitorDrawer?.isClickable = true
//            val params: ViewGroup.LayoutParams = ViewPager.LayoutParams()
//            params.width = LinearLayout.LayoutParams.MATCH_PARENT
//            params.height = LinearLayout.LayoutParams.MATCH_PARENT
//            viewBinding.homeIndexLeftLayer.removeAllViews()
//            viewBinding.homeIndexLeftLayer.addView(monitorDrawer, params)
//        }
//    }
//
//    override fun initListeners() {
//        viewBinding.homeActivityBottomTabView.addOnTabSelectedListener(
//            object : OnTabSelectedListener {
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
//                    val holder = TabViewHolder(tab.customView)
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
//    }
//
//    override fun openOrCloseDrawer() {
//        with(viewBinding.homeIndexDrawLayout) {
//            if (this.isDrawerOpen(GravityCompat.START)) {
//                closeDrawer(GravityCompat.START, true)
//            } else {
//                openDrawer(GravityCompat.START, true)
//            }
//        }
//    }
//
//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//        switchPage()
//    }
//
//    override fun onDestroy() {
//        EventBus.getDefault().unregister(this)
//        super.onDestroy()
//    }
//
//    private fun switchPage() {
//        var tabId: String? = null
//        if (uri != null) {
//            tabId = uri.getQueryParameter(HomeConstant.UriParam.KEY_ID)
//        }
//        var tab: TabLayout.Tab? = null
//        if (!TextUtils.isEmpty(tabId)) {
//            fragmentList[indexOfTab(tabId)].switchPages(uri)
//            tab = viewBinding.homeActivityBottomTabView.getTabAt(indexOfTab(tabId))
//        }
//        tab?.select()
//    }
//
//    private fun indexOfTab(tabId: String?): Int {
//        if (TextUtils.isEmpty(tabId) || CollectionUtils.isEmpty(tabDataList)) {
//            return 0
//        }
//        for (homeTabBean in tabDataList) {
//            if (tabId == homeTabBean.tabId) {
//                return tabDataList.indexOf(homeTabBean)
//            }
//        }
//        return 0
//    }
//
//    /**
//     * 判断是add fragment 还是直接 show fragment
//     */
//    private fun addOrShowFragment(targetFragment: Fragment?, tag: String) {
//        homeTabUT(targetFragment)
//        val transaction = supportFragmentManager.beginTransaction()
//        if (currentFragment === targetFragment) {
//            return
//        }
//        if (targetFragment == null || currentFragment == null) {
//            return
//        }
//        //将名字作为 tag
//        val fragmentByTag = supportFragmentManager.findFragmentByTag(tag)
//        // 如果当前fragment未被添加，则添加到Fragment管理器中
//        if (supportFragmentManager.fragments.size > fragmentList.size) {
//            kotlin.run {
//                for (fragment in supportFragmentManager.fragments) {
//                    if (fragment.tag == targetFragment.tag && fragment != targetFragment) {
//                        transaction.remove(fragment)
//                        return@run
//                    }
//                }
//            }
//        }
//        if (fragmentByTag != null && targetFragment != fragmentByTag) {
//            transaction.remove(fragmentByTag)
//        }
//        if (!targetFragment.isAdded) {
//            transaction.hide(currentFragment!!)
//                .add(R.id.home_activity_container, targetFragment, tag).commitAllowingStateLoss()
//        } else {
//            transaction.hide(currentFragment!!).show(targetFragment).commitAllowingStateLoss()
//        }
//        transaction.setMaxLifecycle(targetFragment, Lifecycle.State.RESUMED)
//        transaction.setMaxLifecycle(currentFragment!!, Lifecycle.State.STARTED)
//        currentFragment = targetFragment
//        when (targetFragment) {
//            is HomeIndexFragment -> {
//                viewModel.updateCurrentBottomTab(EIndexTab.HOME)
//            }
//            is MonitorContentFragment -> {
//                viewModel.updateCurrentBottomTab(EIndexTab.Market)
//            }
//            is MineFragment -> {
//                viewModel.updateCurrentBottomTab(EIndexTab.PROFILE)
//            }
//            else -> {
//                return
//            }
//        }
//    }
//
//    private fun homeTabUT(targetFragment: Fragment?) {
//        when (targetFragment) {
//            is HomeIndexFragment -> {
//                UTHelper.commonEvent(UTConstant.Tab.HOME)
//            }
//            is MonitorContentFragment -> {
//                UTHelper.commonEvent(UTConstant.Tab.MONITOR)
//            }
//            is MineFragment -> {
//                UTHelper.commonEvent(UTConstant.Tab.ME)
//            }
//            else -> {
//                return
//            }
//        }
//    }
//
//    private fun fetchUnread() {
//        if (isLogin()) {
//            viewModel.refreshUnRedUnReceive()
//        }
//    }
//
//    private fun initObserver() {
//        viewModel.bottomImRedHitLiveData.observe(
//            this
//        ) { aBoolean -> }
//        viewModel.bottomMineRedHitLiveData.observe(
//            this
//        ) { aBoolean -> updateUnreadTag(aBoolean, EIndexTab.PROFILE) }
//        viewModel.unReadCountLiveData.observe(this) { integer ->
//        }
//        viewModel.showRocketLiveData.observe(this) { aBoolean -> showRocket(aBoolean) }
//        viewModel.drawerOpenMine.observe(this) {
//            if (it) {
//                openMineDrawer()
//            }
//        }
//        viewModel.drawerOpenMonitor.observe(this) {
//            if (it) {
//                openMonitorDrawer()
//            }
//        }
//        viewModel.drawerState.observe(this) {
//            when (it) {
//                HomeIndexViewModel.CurrentDrawerState.None -> {
//                    viewBinding.homeIndexDrawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//                }
//                HomeIndexViewModel.CurrentDrawerState.Monitor -> {
//                    if (viewModel.monitorEnableShowDrawerLiveData.value == true) {
//                        viewBinding.homeIndexDrawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
//                    } else {
//                        viewBinding.homeIndexDrawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//                    }
//                    viewBinding.homeIndexLeftLayer.apply {
//                        layoutParams.width =
//                            (ScreenUtils.getScreenWidth(this@HomeIndexActivity) * 0.8).toInt()
//                    }
//                    mineDrawer?.hide(true)
//                    monitorDrawer?.hide(false)
//                }
//                HomeIndexViewModel.CurrentDrawerState.Mine -> {
//                    viewBinding.homeIndexDrawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
//                    viewBinding.homeIndexLeftLayer.apply {
//                        layoutParams.width = SizeUtils.dp2px(240f)
//                    }
//                    mineDrawer?.hide(false)
//                    monitorDrawer?.hide(true)
//                }
//            }
//        }
//        viewModel.monitorEnableShowDrawerLiveData.observe(this) {
//            when (viewModel.drawerState.value) {
//                HomeIndexViewModel.CurrentDrawerState.Monitor -> {
//                    if (it == true) {
//                        viewBinding.homeIndexDrawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
//                    } else {
//                        viewBinding.homeIndexDrawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//                    }
//                }
//                else -> {
//                }
//            }
//        }
//    }
//
//
//    /**
//     * 打开监控页的抽屉布局
//     */
//    private fun openMonitorDrawer() {
//        mineDrawer?.hide(true)
//        monitorDrawer?.hide(false)
//        viewBinding.homeIndexDrawLayout.openDrawer(GravityCompat.START, true)
//    }
//
//    private fun openMineDrawer() {
//        mineDrawer?.hide(false)
//        monitorDrawer?.hide(true)
//        viewBinding.homeIndexDrawLayout.openDrawer(GravityCompat.START, true)
//    }
//
//    private fun shareApp() {
//        val type = ShareType.Link
//        val params = ShareParams()
//        params.title = getString(cool.dingstock.uicommon.setting.R.string.common_share_title)
//        params.content = getString(cool.dingstock.uicommon.setting.R.string.common_share_content)
//        params.imageBitmap =
//            BitmapFactory.decodeResource(
//                resources,
//                cool.dingstock.uicommon.setting.R.mipmap.ic_launcher
//            )
//        params.link = getString(cool.dingstock.uicommon.setting.R.string.dc_yyb_link)
//
//        params.platforms =
//            arrayListOf(
//                SharePlatform.WeChat,
//                SharePlatform.WeChatMoments,
//                SharePlatform.QQ,
//                SharePlatform.Copy
//            )
//
//        type.params = params
//        val shareDialog = ShareDialog(context)
//        shareDialog.shareType = type
//        shareDialog.utEventId = UTConstant.Setting.SettingP_click_ShareFriends
//        shareDialog.show()
//    }
//
//    private fun closeDrawer() {
//        viewBinding.homeIndexDrawLayout.closeDrawer(GravityCompat.START)
//    }
//
//    private fun showActivateDialog() {
//        CommonEditDialog.Builder(this)
//            .hint("请输入激活码")
//            .title("激活码")
//            .confirmTxt("提交")
//            .cancelTxt("取消")
//            .confirmDismiss(false)
//            .onConfirmClick(object : OnConfirmClickListener {
//                override fun onConfirmClick(edit: EditText, dialog: CommonEditDialog) {
//                    val editTxt = dialog.getEditTxt()
//                    if (TextUtils.isEmpty(editTxt)) {
//                        showFailedDialog(R.string.setting_invite_edit_hint)
//                        return
//                    }
//                    showLoadingDialog()
//                    SettingHelper.getInstance().activate(editTxt, object : ParseCallback<String?> {
//                        override fun onSucceed(data: String?) {
//                            hideLoadingDialog()
//                            EventBus.getDefault().post(EventActivated())
//                            showSuccessDialog(if (TextUtils.isEmpty(data)) "激活成功" else data)
//                        }
//
//                        override fun onFailed(errorCode: String, errorMsg: String) {
//                            hideLoadingDialog()
//                            showFailedDialog(if (TextUtils.isEmpty(errorMsg)) "激活失败" else errorMsg)
//                        }
//                    })
//                    dialog.dismiss()
//                }
//            })
//            .builder()
//            .show()
//    }
//
//    private fun updateUnreadTag(isShow: Boolean, EIndexTab: EIndexTab) {
//        val position = findTabIndex(EIndexTab)
//        val mineTab = viewBinding.homeActivityBottomTabView.getTabAt(position) ?: return
//        val tabView = mineTab.customView
//        val tagView = tabView!!.findViewById<View>(R.id.tag_unread)
//        if (isShow) {
//            tagView.visibility = View.VISIBLE
//        } else {
//            tagView.visibility = View.INVISIBLE
//        }
//    }
//
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        Log.d("changeTheme", "DarkMode saveInstanceState")
//    }
//
//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        Log.d("changeTheme", "DarkMode onConfigurationChanged")
//        val currentSetting = DarkModeHelper.getDarkMode()
//        val mode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
//        when (currentSetting) {
//            DarkMode.FOLLOW_SYSTEM -> {
//                when (mode) {
//                    Configuration.UI_MODE_NIGHT_YES -> {
//                        delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
//                    }
//                    Configuration.UI_MODE_NIGHT_NO -> {
//                        delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
//                    }
//                }
//                recreate()
//            }
//            else -> {
//                return
//            }
//        }
//    }
//
//    override fun onNightModeChanged(mode: Int) {
//        super.onNightModeChanged(mode)
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        Log.d("changeTheme", "DarkMode onRestoreInstanceState")
//    }
//
//
//    /**
//     * 获取底步TAB的position
//     */
//    private fun findTabIndex(type: String): Int {
//        if (tabDataList.isEmpty()) {
//            return -1
//        }
//        tabDataList.forEachIndexed { index, tabData ->
//            if (tabData.tabId.equals(type)) {
//                return index
//            }
//        }
//        return fragmentList.size - 1
//    }
//
//    private fun showRocket(isShow: Boolean) {
//        if (isShow) {
//            TabViewHolder(homeTab!!.customView).tabIcon.showOtherAni()
//            homeTab!!.customView!!.setOnClickListener { v: View? ->
//                if (!viewBinding.homeActivityBottomTabView.getTabAt(findHomeIndex())!!.isSelected) {
//                    selectedTab(EIndexTab.HOME)
//                } else {
//                    //判断当前在哪里。然后判断显示火箭
//                    if (viewModel.homeIsShowRocket) {
//                        viewModel.homeIsShowRocket = false
//                        TabViewHolder(homeTab!!.customView).tabIcon.dismissOtherAni()
//                    }
//                    viewModel.scrollToTop()
//                }
//            }
//        } else {
//            TabViewHolder(homeTab!!.customView).tabIcon.dismissOtherAni()
//        }
//    }
//
//    private fun findHomeIndex(): Int {
//        var homePosition = 0
//        for (i in fragmentList.indices) {
//            if (fragmentList[i] is HomeIndexFragment) {
//                homePosition = i
//            }
//        }
//        return homePosition
//    }
//
//    private fun checkRouteAndNotice() {
//        val routeUrl = ConfigSPHelper.getInstance().getString(PushConstant.Key.KEY_ROUTE)
//        if (!TextUtils.isEmpty(routeUrl)) {
//            ConfigSPHelper.getInstance().save(PushConstant.Key.KEY_ROUTE, "")
//            DcRouter(routeUrl).start()
//        }
//        val noticeStr = ConfigSPHelper.getInstance().getString(PushConstant.Key.KYE_NOTICE)
//        if (TextUtils.isEmpty(noticeStr)) {
//            return
//        }
//        val (content, link) = JSONHelper.fromJson(noticeStr, NoticeBean::class.java) ?: return
//        ConfigSPHelper.getInstance().save(PushConstant.Key.KYE_NOTICE, "")
//        val builder = makeAlertDialog()
//            .setMessage(content)
//            .setNegativeButton("好的") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
//        if (TextUtils.isEmpty(link)) {
//            builder.setPositiveButton(
//                "前往"
//            ) { _: DialogInterface?, _: Int ->
//                link?.let {
//                    DcRouter(it).start()
//                }
//            }
//        }
//        builder.show()
//    }
//
//    private fun fingerprintCheck() {
//        val application = application
//        val displayMetrics = application.resources.displayMetrics
//        val height = displayMetrics.heightPixels
//        val width = displayMetrics.widthPixels
//        val language = Locale.getDefault().language
//        val release = Build.VERSION.RELEASE
//        val model = Build.MODEL
//        val bootloader = Build.BOOTLOADER
//        val hardware = Build.HARDWARE
//        var uuid: String? = ""
//        uuid = if (Build.VERSION.SDK_INT >= 26) {
//            Settings.Secure.getString(application.contentResolver, "android_id")
//        } else {
//            UUID.randomUUID().toString()
//        }
//        val codename = Build.VERSION.CODENAME
//        val incremental = Build.VERSION.INCREMENTAL
//        val sdk = Build.VERSION.SDK_INT
//        val manufacturer = Build.MANUFACTURER
//        val product = Build.PRODUCT
//        val tags = Build.TAGS
//        val type = Build.TYPE
//        val user = Build.USER
//        val display = Build.DISPLAY
//        val board = Build.BOARD
//        val brand = Build.BRAND
//        val device = Build.DEVICE
//        val fingerprint = Build.FINGERPRINT
//        val host = Build.HOST
//        val id = Build.ID
//        val builder = StringBuilder()
//        val props: MutableList<Any>? =
//            Arrays.asList(
//                height,
//                width,
//                language,
//                release,
//                model,
//                bootloader,
//                hardware,
//                uuid,
//                codename,
//                incremental,
//                sdk,
//                manufacturer,
//                product,
//                tags,
//                type,
//                user,
//                display,
//                board,
//                brand,
//                device,
//                fingerprint,
//                host,
//                id
//            )
//        if (props != null) {
//            for (prop in props) {
//                builder.append(prop)
//                builder.append(",")
//            }
//        }
//        UTHelper.deviceFinger(builder.toString())
//    }
//
//
//    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (GSYVideoManager.backFromWindowFull(this)) {
//                return true
//            }
////            if (viewBinding.homeIndexDrawLayout.isOpen == true) {
////                closeDrawer()
////                return true
////            }
//            if (System.currentTimeMillis() - mExitTime > 2000) {
//                ToastUtil.getInstance().makeTextAndShow(this, "再按一返回键退出盯链", Toast.LENGTH_SHORT)
//                mExitTime = System.currentTimeMillis()
//            } else {
//                // System.exit() 不使用退出 直接将其后台
//                val intent = Intent(Intent.ACTION_MAIN)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                intent.addCategory(Intent.CATEGORY_HOME)
//                startActivity(intent)
//            }
//            return true
//        }
//        return super.onKeyDown(keyCode, event)
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onLoginEvent(eventLogin: EventIsAuthorized) {
//        with(viewBinding.homeActivityBottomTabView) {
//            if (eventLogin.isWhere2Login == "MINE") {
//                selectTab(getTabAt(findTabIndex(EIndexTab.PROFILE)))
//                //从首页 我的 登录成功
//            } else if (eventLogin.isWhere2Login == "IM") {
//            }
//        }
//
//        HomeSuggestHelper.reset()
//        if (eventLogin.login) {
//            viewModel.isLoginReturn = true
//            viewModel.talkIndex = viewModel.talkWantIndex
//            viewModel.monitorIndex = viewModel.monitorWantIndex
//            //刷新
//            setupTabAndFragments()
//        }
//        if (eventLogin.login) {
//            OpenInstall.getInstall(object : AppInstallAdapter() {
//                override fun onInstall(appData: AppData) {
//                    if (null == appData) {
//                        return
//                    }
//                    OpenInstall.reportRegister()
//                    Logger.d("wakeupData = $appData")
//                    MobileHelper.getInstance().installation(appData)
//                }
//            })
//        }
//        //更新我的抽屉
//        mineDrawerAdapter.setList(getMineDrawerList())
//
//    }
//
//    private fun setupTabAndFragments() {
//        if (fragmentList.size > 0) {
//            for (fragment in fragmentList) {
//                fragment.reload()
//            }
//            return
//        }
//        //获取Tab数据
//        tabDataList = HomeHelper.getInstance().tabDataList
//        viewBinding.homeActivityBottomTabView.removeAllTabs()
//        removeAllFragments()
//        fragmentList = ArrayList()
//        for (position in tabDataList.indices) {
//            val homeTabBean = tabDataList[position]
//            val tab = viewBinding.homeActivityBottomTabView.newTab()
//            tab.setCustomView(R.layout.home_item_tab_badge)
//            val holder = TabViewHolder(tab.customView)
//            holder.tabIcon.setImage(if (isWhiteMode()) homeTabBean.tabIconRes else homeTabBean.tabIconDarkRes)
//            holder.tabIcon.isSelected = false
//            holder.tabTitle.text = homeTabBean.tabText
//            viewBinding.homeActivityBottomTabView.addTab(tab)
//            var fragment: BaseFragment? = null
//            when (Objects.requireNonNull(homeTabBean.tabId)) {
//                EIndexTab.HOME.Id -> {
//                    homeTab = tab
//                    holder.tabIcon.setOtherAni(if (isWhiteMode()) ROCKET_ANI else ROCKET_DARK_ANI)
//                    fragment = HomeIndexFragment()
//                }
//                EIndexTab.Market.Id -> fragment = getMonitorFragment()
//                EIndexTab.PROFILE.Id -> fragment = MineFragment.getInstance().setMine(true)
//                else -> {
//                }
//            }
//            if (null != fragment) {
//                fragmentList.add(fragment)
//            }
//        }
//        //获取需要显示的Tab以及对应的Fragment
//        val targetTab = viewModel.homeStore.bottomFragmentTab
//        currentIndex = findTabIndex(targetTab)
//        currentFragment = fragmentList[currentIndex]
//
//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.add(
//            R.id.home_activity_container,
//            currentFragment as BaseFragment,
//            targetTab.Id
//        )
//        fragmentTransaction.commitAllowingStateLoss()
//
//        //这里发生了 自动调用的问题
//        //        switchPage();
//        //记录监控下标的位置
//        viewModel.homeMonitorIndex = findMonitoringIndex()
//        val mineTab = viewBinding.homeActivityBottomTabView.getTabAt(tabDataList.size - 1)
//        mineTab?.view?.setOnLongClickListener {
//            showVersionDialog()
//            true
//        }
//    }
//
//    /**
//     * 显示当前版本对话框
//     */
//    private fun showVersionDialog() {
//        AlertDialog.Builder(this)
//            .setMessage("您当前使用的版本是" + getVersionName(this))
//            .setPositiveButton("知道了") { dialog, _ -> dialog.dismiss() }
//            .show()
//    }
//
//    private fun getMineDrawerList(): MutableList<Any> {
//        val configJson = ConfigFileHelper.getConfigJson("mine_drawer_240.json")
//        if (TextUtils.isEmpty(configJson)) {
//            return arrayListOf()
//        }
//        val drawerEntityList = JSONHelper.fromJsonList(configJson, MineDrawerEntity::class.java)
//        if (drawerEntityList == null || drawerEntityList.isEmpty()) {
//            return arrayListOf()
//        }
//        val dataList: MutableList<Any> = ArrayList()
//        val count = drawerEntityList.size
//        for (i in 0 until count) {
//            val item = drawerEntityList[i]
//            dataList.add(item)
//            item.items.forEach {
//                if (it.value == "kolInvitations") {
//                    if (!TextUtils.isEmpty(LoginUtils.getCurrentUser()?.inviteCode)) {
//                        dataList.add(it)
//                    }
//                } else {
//                    dataList.add(it)
//                }
//            }
//        }
//        return dataList
//    }
//
//    private fun removeAllFragments() {
//        if (CollectionUtils.isEmpty(fragmentList)) {
//            return
//        }
//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        for (fragment in fragmentList) {
//            fragmentTransaction.remove(fragment)
//        }
//        fragmentTransaction.commitAllowingStateLoss()
//    }
//
//    /**
//     * 监控fragment 1、当用户未登录或者没有订阅时,显示 MonitorEditFragment 2、返回MonitorContentFragment
//     *
//     * @return
//     */
//    private fun getMonitorFragment(): BaseFragment {
//        monitorFragment = MonitorContentFragment()
//        val args = Bundle()
//        monitorFragment!!.arguments = args
//        return monitorFragment as MonitorContentFragment
//    }
//
//    private fun findMonitoringIndex(): Int {
//        for (i in fragmentList.indices) {
//            if (fragmentList[i] is MonitorContentFragment) {
//                return i
//            }
//        }
//        return if (SettingHelper.getInstance().isMonitorTabFirst) {
//            0
//        } else {
//            1
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onUserLoginOut(userLoginOut: EventUserLoginOut?) {
//        //这里一定要重置intent里面的data
//        intent.data = Uri.parse(HomeConstant.Uri.TAB)
//
//        resetTabSelectedHome()
//        //清空上次登录的状态浏览的index
//        viewModel.resetTab()
//        viewModel.loginOut.postValue(true)
//
//        setupTabAndFragments()
//        viewModel.clearMessage()
//        viewModel.clearScoreHint()
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    fun onSessionInvalidEvent(sessionInvalid: EventSessionInvalid?) {
//        //token 过期 直接退出登录清空本地缓存，然后清空deviceId
//        viewModel.signOut()
//        //选中到首页Tab去
//        //清空上次登录的状态浏览的index
//        viewModel.homeSelIndex = 0
//        viewModel.homeDCSelIndex = 0
//        resetTabSelectedHome()
//        updateUnreadTag(false, EIndexTab.PROFILE)
//        setupTabAndFragments()
//        ToastUtil.getInstance()._short(this, "登录已过期，请重新登录")
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun isShowIMHint(event: EventSystemNotice) {
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun reFreshUnreadNotification(event: EventCommunityChange) {
//        viewModel.fetchUnReadMessage()
//    }
//
//
//    /**
//     * 退出登录时选中homeTab
//     */
//    private fun resetTabSelectedHome() {
//        currentIndex = if (SettingHelper.getInstance().isMonitorTabFirst) {
//            viewBinding.homeActivityBottomTabView.selectTab(
//                viewBinding.homeActivityBottomTabView.getTabAt(
//                    1
//                )
//            )
//            1
//        } else {
//            viewBinding.homeActivityBottomTabView.selectTab(
//                viewBinding.homeActivityBottomTabView.getTabAt(
//                    0
//                )
//            )
//            0
//        }
//        viewModel.homeStore.bottomFragmentTab = EIndexTab.HOME
//        viewModel.homeBottomTabSelIndex = currentIndex
//        addOrShowFragment(fragmentList[currentIndex], viewModel.homeStore.bottomFragmentTab.Id)
//    }
//
//    private fun getCouldUrl(type: String?) {
//        MobileHelper.getInstance().getDingUrl(type, object : ParseCallback<String?> {
//            override fun onSucceed(data: String?) {
//                data?.let { DcRouter(it).start() }
//            }
//
//            override fun onFailed(errorCode: String, errorMsg: String) {
//            }
//        })
//    }
//
//    private fun setupDefaultTab() {
//        val bottomFragmentTab = viewModel.homeStore.bottomFragmentTab
//        val tabLayout = viewBinding.homeActivityBottomTabView
//        tabLayout.selectTab(tabLayout.getTabAt(findTabIndex(bottomFragmentTab)))
//    }
//
//    private fun findTabIndex(type: EIndexTab): Int {
//        return findTabIndex(type.Id)
//    }
//
//
//    internal class TabViewHolder(tabView: View?) {
//        var tabTitle: TextView
//        var tabIcon: HomeIndexAniTab
//
//        init {
//            tabTitle = tabView!!.findViewById(R.id.home_tab_title)
//            tabIcon = tabView.findViewById(R.id.home_tab_icon)
//        }
//    }
//
//
//    override fun initViewAndEvent(savedInstanceState: Bundle?) {
//        if (savedInstanceState != null) {
//            val transaction = supportFragmentManager.beginTransaction()
//            supportFragmentManager.fragments.forEach {
//                transaction.remove(it)
//            }
//            transaction.commit()
//        }
//        handleBundleData()
//        //TODO 判断是否置换token
//        viewModel.updateTxDeviceId()
//        initDrawer()
//        fingerprintCheck()
//        EventBus.getDefault().register(this)
//        checkRouteAndNotice()
//        initObserver()
//        fetchUnread()
//        setupTabAndFragments()
//        setupDefaultTab()
//    }
//
//
//    /**
//     * 处理Scheme传递过来的路由
//     */
//    private fun handleBundleData() {
//        val extra = intent.extras
//        extra?.let {
//            val targetRoute = it.getString("SchemeRoute")
//            targetRoute?.let { routePath ->
//                routeTo(routePath)
//            }
//        }
//    }
//
//
//    companion object {
//        const val HOME_TAB_ID = "home"
//        const val MONITOR_TAB_ID = "monitor"
//        const val COMMUNITY_TAB_ID = "community"
//        const val USER_TAB_ID = "usercenter"
//        const val MINE_LOGIN = 0x0001
//        private const val OVERSEA_TAB_ID = "oversea"
//        private const val ROCKET_ANI = "rocket.json"
//        private const val ROCKET_DARK_ANI = "rocket_dark.json"
//    }
//
//    fun selectedTabByID(tabId: String) {
//        with(viewBinding.homeActivityBottomTabView) {
//            selectTab(getTabAt(findTabIndex(tabId)))
//        }
//    }
//
//    fun selectedTab(EIndexTab: EIndexTab) {
//        selectedTabByID(EIndexTab.Id)
//    }
//
//}
//
//fun HomeTabBean.convertToEnum(): EIndexTab {
//    return EIndexTab.values().firstOrNull { it.Id == this.tabId }
//        ?: if (SettingHelper.getInstance().isMonitorTabFirst) {
//            EIndexTab.Market
//        } else {
//            EIndexTab.HOME
//        }
//}