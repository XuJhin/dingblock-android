package net.dingblock.mobile.activity

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import cool.dingstock.appbase.app.SDKInitHelper
import cool.dingstock.appbase.constant.*
import cool.dingstock.appbase.dialog.PrivacyPolicyDialog
import cool.dingstock.appbase.dialog.PrivacyPolicyDialog.PrivacyListener
import cool.dingstock.appbase.entity.bean.im.HWMessageBean
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.helper.ADHelper
import cool.dingstock.appbase.helper.IMHelper
import cool.dingstock.appbase.helper.config.ConfigHelper
import cool.dingstock.appbase.mvp.DCActivityManager
import cool.dingstock.appbase.mvvm.activity.VMActivity
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.widget.commondialog.CommonDialog
import cool.dingstock.lib_base.thread.ThreadPoolHelper
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.SizeUtils
import net.dingblock.mobile.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class BootActivity : VMActivity<BootViewModel>() {
    companion object {
        private const val BOTTOM_SPACE = 140
    }

    private var isIgnoreAd = false
    private var privacyPolicyDialog: PrivacyPolicyDialog? = null
    private lateinit var container: FrameLayout
    private lateinit var topOneLayer: FrameLayout
    private lateinit var bizAdLayer: FrameLayout
    private lateinit var bizAdIv: ImageView
    private lateinit var clickableLayer: View
    private lateinit var jumpTv: TextView
    private var isHand = AtomicBoolean(false)
    private var isResume = AtomicBoolean(false)
    private var isRouterEnd = AtomicBoolean(false)


    /**
     * 是否强制(切换至后台到达一定时间)显示的广告
     */
    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        isIgnoreAd = intent.getBooleanExtra(PushConstant.Key.KEY_IGNORE_AD, false)
        privacyPolicyDialog = PrivacyPolicyDialog(this, this)
        window.statusBarColor = getCompatColor(R.color.white)
        setSystemStatusBarMode()
        setSystemNavigationBar()

        container = findViewById(R.id.splash_ad_container)
        topOneLayer = findViewById(R.id.top_on_layer)
        bizAdLayer = findViewById(R.id.biz_ad_layer)
        bizAdIv = findViewById(R.id.ad_iv)
        jumpTv = findViewById(R.id.boot_time_txt)
        clickableLayer = findViewById(R.id.clickable_layer)
        val layoutParams = this.container.layoutParams as ViewGroup.MarginLayoutParams
        if (SizeUtils.getAllScreenHeight(this) - 1.78 * SizeUtils.getWidth()
            < SizeUtils.dp2px(BOTTOM_SPACE.toFloat())
        ) {
            layoutParams.bottomMargin = 0
            layoutParams.height = ViewGroup.MarginLayoutParams.MATCH_PARENT
            this.container.layoutParams = layoutParams
        } else {
            layoutParams.bottomMargin = SizeUtils.dp2px(BOTTOM_SPACE.toFloat())
            layoutParams.height = ViewGroup.MarginLayoutParams.MATCH_PARENT
            this.container.layoutParams = layoutParams
        }

        jumpTv.setOnShakeClickListener {
            UTHelper.commonEvent(
                UTConstant.Splash.SKIP_SPLASH,
                "splashId",
                MobileHelper.getInstance().configData.bizAD?.id ?: ""
            )
            startRoute()
        }

        viewModel.cloudUrl.observe(this) {
            hideLoadingDialog()
            it?.let {
                routeTo(it)
            }
        }
    }

    private fun checkUserAgreePolicy(): Boolean {
        return if (!ConfigHelper.isAgreePolicy()) {
            showPolicyDialog()
            true
        } else {
            false
        }
    }

    private fun startRoute() {
        if (isRouterEnd.get()) {
            return
        }
        isHand.set(true)
        isRouterEnd.set(true)
        if (checkUserAgreePolicy()) {
            return
        }
        if (!DCActivityManager.getInstance().isFistLaunch) {
            finishHide()
            return
        }
        //先跳转首页，然后在处理链接
        DcRouter(HomeConstant.Uri.TAB).startNoTransition()
        // 如果有 Push 消息，打开 push 地址
        if (null != intent
            && !TextUtils.isEmpty(intent.getStringExtra(PushConstant.Key.KEY_URL))
        ) {
            val uri = intent.getStringExtra(PushConstant.Key.KEY_URL)
            Logger.d("This intent have a uri, so start the uri. push  --- url=$uri")
            if (intent.getBooleanExtra(PushConstant.Key.KEY_ACTION_VIEW, false)) {
                try {
                    uri?.let {
                        DcRouter(uri).start()
                    }
                } catch (e: Exception) {
                }
            } else {
                uri?.let {
                    DcRouter(uri).start()
                }
            }
        } else if (intent.getStringExtra("targetId") != null) {

        } else if (intent.getStringExtra("rc") != null) {
            try {
                val hwMessageBean =
                    Gson().fromJson(intent.getStringExtra("rc"), HWMessageBean::class.java)
                IMHelper.routeToConversationActivity(
                    this,
                    hwMessageBean.conversationType.toIntOrNull() ?: 1,
                    hwMessageBean.targetId
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
            Logger.d("Come from the Launcher. so start Home Index  ---")
        }
        finish()
    }


    override fun isCheckUpdate(): Boolean {
        return false
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_boot
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onLoadData()
    }

    private fun createShortcut() {
        val timeClock = ShortcutInfoCompat.Builder(context, "timeClock")
            .setShortLabel(getString(R.string.time_clock))
            .setIcon(IconCompat.createWithResource(context, R.drawable.ic_shortcut_time_clock))
            .setIntent(Intent(Intent.ACTION_VIEW).apply {
                setClassName("net.dingblock.mobile", "cool.dingstock.bp.ui.clock.index.TimeClockActivity")
                data = Uri.parse("https://app.dingstock.net/lab/clock?needLogin=true")
            })
            .build()
        val sneakersCalendar = ShortcutInfoCompat.Builder(context, "sneakersCalendar")
            .setShortLabel(getString(R.string.sneakers_calendar))
            .setIcon(IconCompat.createWithResource(context, R.drawable.ic_shortcut_sneakers_calendar))
            .setIntent(
                Intent.makeMainActivity(
                    ComponentName(
                        "net.dingblock.mobile",
                        "net.dingblock.mobile.activity.index.HomeIndexActivity"
                    )
                ).apply {
                    data = Uri.parse("https://app.dingstock.net/home/tab?id=home&categoryId=sneakers")
                })
            .build()
        val regionRaffle = ShortcutInfoCompat.Builder(context, "regionRaffle")
            .setShortLabel(getString(R.string.region_raffle))
            .setIcon(IconCompat.createWithResource(context, R.drawable.ic_shortcut_region_raffle))
            .setIntent(Intent(Intent.ACTION_VIEW).apply {
                setClassName(
                    "net.dingblock.mobile",
                    "cool.dingstock.monitor.ui.regoin.raffle.HomeRegionRaffleActivity"
                )
                data = Uri.parse("https://app.dingstock.net/region/raffle?needLogin=true")
            })
            .build()

        ShortcutManagerCompat.pushDynamicShortcut(context, regionRaffle)
        ShortcutManagerCompat.pushDynamicShortcut(context, sneakersCalendar)
        ShortcutManagerCompat.pushDynamicShortcut(context, timeClock)
    }

    override fun initListeners() {
    }

    override fun moduleTag(): String {
        return ModuleConstant.HOME_MODULE
    }

    override fun setSystemStatusBar() {
//        StatusBarUtil.setTranslucent(this)
    }

    override fun enableEnablePendingTransition(): Boolean {
        return false
    }

    /**
     * 启动页不判断 剪切板活动
     */
    override fun enablePartyVerify(): Boolean {
        return false
    }

    private fun showNoAgreeDialog() {
        CommonDialog.Builder(
            this,
            "如果您不同意个人信息保护指引，我们将无法为您服务完整功能，您可以选择使用基础功能或直接退出应用",
            "同意并继续",
            "仅使用基本功能",
            onCancelClickListener = {
                DcRouter(HomeConstant.Uri.BASIC).startNoTransition()
                finish()
            },
            onConfirmClickListener = {
                agreePolicy()
            }
        ).builder().show()
    }

    private fun showPolicyDialog() {
        privacyPolicyDialog?.privacyListener = object : PrivacyListener {
            override fun onAgree() {
                agreePolicy()
            }

            override fun onQuit() {
                showNoAgreeDialog()
            }

            override fun readProtocol() {
                viewModel.getCouldUrl(ServerConstant.Function.ARREEMENT)
            }

            override fun readPrivacy() {
                viewModel.getCouldUrl(ServerConstant.Function.PRIVACY)
            }

            override fun readUserCollectList() {
                viewModel.getCouldUrl(ServerConstant.Function.PERSONAL_INFORMATION)
            }

            override fun thirdShareList() {
                viewModel.getCouldUrl(ServerConstant.Function.EXTERNAL_INFORMATION)
            }
        }
        if (privacyPolicyDialog?.isShowing == false) {
            privacyPolicyDialog?.show()
            UTHelper.commonEvent(UTConstant.Login.LOGIN_PRIVACY_DIALOG, "曝光")
        }
    }

    private fun onLoadData() {
        if (checkUserAgreePolicy()) {
            return
        }
        createShortcut()
        MobileHelper.getInstance().getConfigs()
        //这里是广告的逻辑
        if (isIgnoreAd) {
            startRoute()
            return
        }
        if (ADHelper.isInBizAd()) {
            //判断商务广告
            if (ADHelper.checkBizAd()) {
                showBizAd()
            } else {
                startRoute()
            }
            return
        }
        //这里判断广告
        checkLoadAd()
    }

    private fun agreePolicy() {
        ConfigHelper.setUserAgreePolicy(true)
        createShortcut()
        MobileHelper.getInstance().getConfigs()
        SDKInitHelper.beginInitSDK(application, true)
        startRoute()
    }

    @SuppressLint("SetTextI18n")
    fun showBizAd() {
        UTHelper.commonEvent(
            UTConstant.Splash.SHOW_SPLASH,
            "splashId",
            MobileHelper.getInstance().configData.bizAD?.id ?: ""
        )
        topOneLayer.hide(true)
        bizAdLayer.hide(false)
        val path = MobileHelper.getInstance().configData.bizAD?.localPath ?: return
        ADHelper.onBizAdShow()
        bizAdIv.load(path)
        clickableLayer.setOnShakeClickListener {
            MobileHelper.getInstance().configData.bizAD?.link?.let {
                UTHelper.commonEvent(
                    UTConstant.Splash.ENTER_SPLASH,
                    "splashId",
                    MobileHelper.getInstance().configData.bizAD?.id ?: ""
                )
                DcRouter(it).start()
            }
        }
        var duration = MobileHelper.getInstance().configData.bizAD?.duration ?: 5
        jumpTv.text = "跳过 $duration"
        lifecycleScope.launch(Dispatchers.IO) {
            flow {
                for (i in (1..Int.MAX_VALUE)) {
                    if (isRouterEnd.get()) {
                        break
                    }
                    delay(1000)
                    if (isResume.get()) {
                        emit(i)
                    }
                }
            }.collect {
                if (isResume.get()) {
                    duration--
                    launch(Dispatchers.Main) {
                        jumpTv.text = "跳过 $duration"
                        if (duration <= 0) {
                            jumpTv.text = "跳过"
                            UTHelper.commonEvent(
                                UTConstant.Splash.END_SPLASH,
                                "splashId",
                                MobileHelper.getInstance().configData.bizAD?.id ?: ""
                            )
                            startRoute()
                        }
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        isResume.set(true)
    }


    override fun onPause() {
        super.onPause()
        isResume.set(false)
    }


    private fun checkLoadAd() {
        isHand.set(false)
        ThreadPoolHelper.getInstance().runOnUiThread({
            if (!isHand.get()) {
                startRoute()
            }
        }, 2000)


    }

    override fun ignoreCheckAd(): Boolean {
        return true
    }
}