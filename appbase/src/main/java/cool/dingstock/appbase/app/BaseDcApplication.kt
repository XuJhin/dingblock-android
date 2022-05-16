package cool.dingstock.appbase.app

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import androidx.multidex.MultiDex
import com.sankuai.waimai.router.Router
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import cool.dingstock.appbase.helper.DarkModeHelper
import cool.dingstock.appbase.helper.config.ConfigHelper.isAgreePolicy
import cool.dingstock.appbase.mvp.DCActivityManagerCallbacks
import cool.dingstock.appbase.refresh.DcRefreshHeader
import cool.dingstock.appbase.router.handler.DcUriHandler
import cool.dingstock.launch.TaskDispatcher
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.util.Logger
import io.reactivex.rxjava3.plugins.RxJavaPlugins


/**
 * @author WhenYoung CreateAt Time 2021/1/15  10:10
 */
abstract class BaseDcApplication : Application() {

    var isFirstLoad = true


    init {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ ->
            val refreshHeader = DcRefreshHeader(context)
            refreshHeader
        }
    }

    companion object {
        val TAG = BaseDcApplication::class.java.simpleName
        var application: BaseDcApplication? = null
            private set
    }

    override fun attachBaseContext(base: Context) {
        MultiDex.install(this)
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        application = this

        //这里处理 rxjava 报错。不然在zip的时候 error可能会把错误 抛出来导致程序崩溃
        RxJavaPlugins.setErrorHandler {
            if (it is Exception) {
                it.printStackTrace()
            }
        }
        initModuleInitor()
        TaskDispatcher.init(this)
        BaseLibrary.initialize(this)
        DarkModeHelper.setModeNow(DarkModeHelper.getDarkMode())
        registerActivityLifecycleCallbacks(DCActivityManagerCallbacks())
        SDKInitHelper.initNoDeviceId(this)
        initWMRouter()
        if (isAgreePolicy()) {
            SDKInitHelper.beginInitSDK(this, isMainProcess())
        } else {
            SDKInitHelper.preInitSDK(this)
        }
    }


    protected abstract fun initModuleInitor()

    private fun initWMRouter() {
        val defaultRootUriHandler = DcUriHandler(this)
        Router.init(defaultRootUriHandler)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Logger.w("onTrimMemory level=$level")
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    private fun isMainProcess(): Boolean {
        val pid = Process.myPid()
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (appProcess in activityManager.runningAppProcesses) {
            if (appProcess.pid == pid) {
                return applicationInfo.packageName == appProcess.processName
            }
        }
        return false
    }

    protected open fun initThirdParty() {}
}