package cool.dingstock.appbase.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.config.AppConfigManager
import cool.dingstock.appbase.dagger.AppBaseApiHelper
import cool.dingstock.appbase.entity.bean.ApiResult
import cool.dingstock.appbase.entity.bean.circle.HomeTopicEntity
import cool.dingstock.appbase.entity.bean.score.MineScoreInfoEntity
import cool.dingstock.appbase.helper.HintPointHelper
import cool.dingstock.appbase.helper.IMHelper
import cool.dingstock.appbase.helper.SettingHelper
import cool.dingstock.appbase.mvvm.LockedLiveEvent
import cool.dingstock.appbase.mvvm.SingleLiveEvent
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.net.api.home.HomeApi
import cool.dingstock.appbase.push.DCPushManager
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.LoginUtils.loginOut
import cool.dingstock.lib_base.stroage.ConfigSPHelper
import cool.dingstock.lib_base.util.Logger
import javax.inject.Inject

/**
 * 由于主页会跟每个Model通信,不想互相依赖model,
 * 不想使用eventbus通信，
 * 暂时将viewmodel 下沉处理
 */
open class HomeIndexViewModel : BaseViewModel() {

    val homeStore: IndexStore = IndexStore()

    enum class IndexTopTab {
        Home, SneakersCalendar, Fashion, Oversea, TidePlay, H5, SHOES
    }

    enum class CurrentDrawerState {
        None, Mine, Monitor
    }

    fun updateCurrentBottomTab(tab: EIndexTab) {
//        homeStore.bottomFragmentTab = tab
    }


    @Inject
    lateinit var homeApi: HomeApi

    @Inject
    lateinit var accountApi: AccountApi

    init {
        AppBaseApiHelper.appBaseComponent.inject(this)
    }

    val monitorNoticeScroll = MutableLiveData<Int>()
    val homeScrollTop = MutableLiveData<Boolean>()
    val sneakerCalendarScrollTop = MutableLiveData<Boolean>()
    val overseaScrollTop = MutableLiveData<Boolean>()
    val fashionScrollTop = MutableLiveData<Boolean>()
    val shoesScrollTop = MutableLiveData<Boolean>()
    val tideScrollTop = MutableLiveData<Boolean>()
    val loginOut = MutableLiveData<Boolean>()
    val unReadCountLiveData = SingleLiveEvent<Int>()
    val bottomMineRedHitLiveData = SingleLiveEvent<Boolean>()
    val bottomImRedHitLiveData = SingleLiveEvent<Boolean>()
    val mineScoreInfoLiveData = SingleLiveEvent<MineScoreInfoEntity>()
    val showRocketLiveData = SingleLiveEvent<Boolean>()

    //抽屉相关
    val drawerState = LockedLiveEvent<CurrentDrawerState>()
    val drawerOpenMonitor = LockedLiveEvent<Boolean>()
    val drawerOpenMine = LockedLiveEvent<Boolean>()

    /**
     * 首页监控的下标
     * */
    var homeMonitorIndex = 2

    /**
     * 记录首页下面的显示按钮
     * */
    var homeBottomTabSelIndex = 0

    /**
     * 记录首页 潮牌清单 选中的 index
     * */
    var homeSelIndex = 0

    /**
     * 记录 关注 最新 推荐 其中的index
     * */
    var homeDCSelIndex = 0

    /**
     * 记录 当前 推荐话题 关注话题下标
     * */
    var talkIndex = 0

    /**
     * 记录 想去的推荐话题 关注话题下标
     * */
    var talkWantIndex = 0

    /**
     * 记录 当前监控 我的关注 推荐
     * */
    var monitorIndex = 0

    /**
     * 记录 想去的监控 我的关注 推荐
     * */
    var monitorWantIndex = 0

    //判断是不是从非登陆到登录回到这个页面
    var isLoginReturn = false

    /**
     * 判断是否需要检测,app启动检测一次
     * */
    var isCheckMonitorSel = false

    /**
     * 当前是否正在显示火箭
     * */
    var isHomeShowingRocket = false

    /**
     * 主页是否显示火箭
     * */
    var homeIsShowRocket = false
        set(value) {
            if (field == value) {
                return
            }
            field = value
            checkTabRocketChange()
        }

    /**
     * 日历是否显示火箭
     * */
    var calendarIsSHowRocket = false
        set(value) {
            if (field == value) {
                return
            }
            field = value
            checkTabRocketChange()
        }

    /**
     * 潮玩日历是否显示火箭
     * */
    var tideIsSHowRocket = false
        set(value) {
            if (field == value) {
                return
            }
            field = value
            checkTabRocketChange()
        }

    /**
     * 潮牌清单是否显示火箭
     * */
    var fashionIsSHowRocket = false
        set(value) {
            if (field == value) {
                return
            }
            field = value
            checkTabRocketChange()
        }

    /**
     * 海淘委托是否显示火箭
     * */
    var overseaIsSHowRocket = false
        set(value) {
            if (field == value) {
                return
            }
            field = value
            checkTabRocketChange()
        }

    /**
     * 海淘委托是否显示火箭
     * */
    var shoesIsSHowRocket = false
        set(value) {
            if (field == value) {
                return
            }
            field = value
            checkTabRocketChange()
        }

    /**
     * 是否签到
     * */
    var isSign = false


    /**
     * 首页选中的话题
     * */
    var homePageTopicSelId = ""
    var homePageSelTopicEntity: HomeTopicEntity? = null


    var scoreInfoEntity = MineScoreInfoEntity()
    var homeCurrentShowTab = IndexTopTab.Home
        set(value) {
            field = value
            checkTabRocketChange()
        }

    /**
     * 通知Fragment 滑动到顶部
     */
    fun scrollToTop() {
        when (homeCurrentShowTab) {
            IndexTopTab.Home -> {
                homeScrollTop.postValue(true)
            }
            IndexTopTab.SneakersCalendar -> {
                sneakerCalendarScrollTop.postValue(true)
            }
            IndexTopTab.Fashion -> {
                fashionScrollTop.postValue(true)
            }
            IndexTopTab.TidePlay -> {
                tideScrollTop.postValue(true)
            }
            IndexTopTab.Oversea -> {
                overseaScrollTop.postValue(true)
            }
            IndexTopTab.SHOES -> {
                shoesScrollTop.postValue(true)
            }
        }
    }

    //控制我的监控的时候是否能显示测滑
    var monitorEnableShowDrawerLiveData = MutableLiveData<Boolean>()

    val followCountLiveData = MutableLiveData<ApiResult>()

    fun refreshUnRedUnReceive() {
        fetchUnReadMessage()
        fetchScoreInfo()
    }

    fun fetchUnReadMessage() {
        if (!LoginUtils.isLogin()) {
            return
        }

        IMHelper.getTotalUnreadCount {
            HintPointHelper.updateUnReadImMessageCount(it)
        }

        homeApi.fetchUnreadMessage()
            .subscribe({
                it.res?.let { _ ->
                    HintPointHelper.updateUnReadNotificationCount(it.res?.unreadcount ?: 0)
                    unReadCountLiveData.postValue(HintPointHelper.getUnReadNotificationCount())
                    bottomImRedHitLiveData.postValue(HintPointHelper.enableShowIMBottomRedHint())
                }
            }, {
                HintPointHelper.updateUnReadNotificationCount(0)
                unReadCountLiveData.postValue(HintPointHelper.getUnReadNotificationCount())
                bottomImRedHitLiveData.postValue(HintPointHelper.enableShowIMBottomRedHint())
            })
    }

    fun clearMessage() {
        HintPointHelper.clearImTabAllPoint()
        unReadCountLiveData.postValue(0)
        bottomImRedHitLiveData.postValue(HintPointHelper.enableShowIMBottomRedHint())
    }

    fun clearScoreHint() {
        scoreInfoEntity = MineScoreInfoEntity()
        HintPointHelper.updateHaveUnReceive(false)
        mineScoreInfoLiveData.postValue(scoreInfoEntity)
        bottomMineRedHitLiveData.postValue(HintPointHelper.enableShowMineBottomRedHint())
    }

    fun fetchScoreInfo() {
        if (!LoginUtils.isLogin()) {
            return
        }
        homeApi.mineScoreInfo()
            .subscribe({
                if (!it.err) {
                    scoreInfoEntity = it.res ?: MineScoreInfoEntity()
                    HintPointHelper.updateHaveUnReceive(it.res?.unReceive ?: false)
                    mineScoreInfoLiveData.postValue(it.res!!)
                    bottomMineRedHitLiveData.postValue(HintPointHelper.enableShowMineBottomRedHint())
                } else {
                    HintPointHelper.updateHaveUnReceive(false)
                    bottomMineRedHitLiveData.postValue(HintPointHelper.enableShowMineBottomRedHint())
                }
            }, {
                HintPointHelper.updateHaveUnReceive(false)
                bottomMineRedHitLiveData.postValue(HintPointHelper.enableShowMineBottomRedHint())
            })
    }


    /**
     * 当首页切换
     * */
    private fun checkTabRocketChange() {
        when (homeCurrentShowTab) {
            IndexTopTab.Home -> {
                if (isHomeShowingRocket != homeIsShowRocket) {
                    isHomeShowingRocket = homeIsShowRocket
                    showRocketLiveData.postValue(homeIsShowRocket)
                }
            }
            IndexTopTab.SneakersCalendar -> {
                if (isHomeShowingRocket != calendarIsSHowRocket) {
                    isHomeShowingRocket = calendarIsSHowRocket
                    showRocketLiveData.postValue(calendarIsSHowRocket)
                }
            }
            IndexTopTab.Fashion -> {
                if (isHomeShowingRocket != fashionIsSHowRocket) {
                    isHomeShowingRocket = fashionIsSHowRocket
                    showRocketLiveData.postValue(fashionIsSHowRocket)
                }
            }
            IndexTopTab.TidePlay -> {
                if (isHomeShowingRocket != tideIsSHowRocket) {
                    isHomeShowingRocket = tideIsSHowRocket
                    showRocketLiveData.postValue(tideIsSHowRocket)
                }
            }
            IndexTopTab.Oversea -> {
                if (isHomeShowingRocket != overseaIsSHowRocket) {
                    isHomeShowingRocket = overseaIsSHowRocket
                    showRocketLiveData.postValue(overseaIsSHowRocket)
                }
            }
            IndexTopTab.SHOES -> {
                if (isHomeShowingRocket != shoesIsSHowRocket) {
                    isHomeShowingRocket = shoesIsSHowRocket
                    showRocketLiveData.postValue(shoesIsSHowRocket)
                }
            }
        }
    }

    fun activate(code: String?) {
    }

    fun openMineDrawer() {
        drawerOpenMine.postValue(true)
    }

    fun updateTxDeviceId() {
        val isUpdateDeviceId = ConfigSPHelper.getInstance().getBoolean("isUpdateDevice")
        val deviceId = DCPushManager.getInstance().txDeviceToken
        Logger.d("updateTxDeviceId isUpdateDeviceId:${isUpdateDeviceId} deviceId:${deviceId}")
        if (LoginUtils.isLogin() && !isUpdateDeviceId && deviceId.isNotEmpty()) {
            accountApi.setUserParameter2Net("deviceId", deviceId)
                .subscribe({
                    Logger.d("updateTxDeviceId success")
                    ConfigSPHelper.getInstance().save("isUpdateDevice", true)
                }, {
                    Logger.d("updateTxDeviceId fail ${it.message}")
                })
        }
    }


    fun signOut() {
        loginOut()
        accountApi.loginLout()
    }

    fun resetTab() {
        homeSelIndex = 0
        homeDCSelIndex = 0
        talkIndex = 0
        talkWantIndex = 0
    }
}


/**
 * 首页状态保存，用于切换Dark模式以后Activity调用recreate后恢复页面
 */
class IndexStore {

   var bottomFragmentTab= EIndexTab.HOME
}


/**
 * 底部导航tab封装，数据关联
 * @link dingblock_home_tab.json
 * @param title     标题
 * @param Id        id，用于推送和页面跳转指定tab，关联至fragment.tag
 * @param needLogin 是否需要登录
 */
enum class EIndexTab(val title: String, val Id: String, val needLogin: Boolean = false) {
    HOME("首页", "home", false),
    Market("市场", "market", false),
    PROFILE("我的", "usercenter", true),
}