package cool.dingstock.appbase.app

import android.app.Application
import android.content.Context
import com.mob.MobSDK
import com.mob.OperationCallback
import com.umeng.commonsdk.UMConfigure
import cool.dingstock.appbase.laucher.*
import cool.dingstock.launch.DcITask
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.util.Logger
import java.util.*
import java.util.concurrent.Executors

object AppBaseSDKInitor : BaseSDKInitor {
    val TAG = "SDKInitor"
    var isInit = false

    private val service = Executors.newFixedThreadPool(8)

    /**
     * 不涉及隐私协议，但是必须提前初始化
     * */
    override fun initNoDeviceId(app: Application) {
        service.run { RetrofitTask().start() }
        service.run { UITask().start() }
    }

    override fun preInitSDK(app: Application) {
        preInit(app)
    }

    override fun beginInitSDK(app: Application, isMainProcess: Boolean) {
        startInitSDK(isMainProcess)
    }

    private fun preInit(application: Application) {
        UMConfigure.preInit(application, "", "")
    }


    private fun startInitSDK(isMainProgress: Boolean) {
        if (isInit) {
            return
        }
        MobSDK.submitPolicyGrantResult(true, object : OperationCallback<Void>() {
            override fun onFailure(p0: Throwable?) {
                Logger.d(TAG, "隐私协议授权结果提交：成功")
            }

            override fun onComplete(p0: Void?) {
                Logger.d(TAG, "隐私协议授权结果提交：成功")
            }
        })
        isInit = true
        if (isMainProgress) {
            delayInit()
            initSdk()
        }
    }

    private fun delayInit() {
        //可以延迟加载
        val mDelayTasks: Queue<DcITask> = LinkedList()
//        mDelayTasks.add(QbSdKTask())
        mDelayTasks.add(MobTask())
        mDelayTasks.add(MediaPlayerTask())
        mDelayTasks.add(UmengTask())
        mDelayTasks.add(PushTask())
        mDelayTasks.add(CustomerTask())
        mDelayTasks.add(AppBaseLibraryTask())
        while (!mDelayTasks.isEmpty()) {
            val task = mDelayTasks.poll()
            service.run {
                task?.start()
            }
        }
    }

    private fun initSdk() {
        //必须加载完毕才能启动
        BpTask().start()
        OpenInstallTask().start()
    }
}