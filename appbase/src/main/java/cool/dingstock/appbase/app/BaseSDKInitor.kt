package cool.dingstock.appbase.app

import android.app.Application


/**
 * 类名：BaseSDKInitor
 * 包名：cool.dingstock.appbase.app
 * 创建时间：2021/9/16 12:16 下午
 * 创建人： WhenYoung
 * 描述：
 **/
interface BaseSDKInitor {

    /**
     * 不涉及隐私协议，但是必须提前初始化
     * */
    fun initNoDeviceId(app:Application)

    fun preInitSDK(app: Application)

    fun beginInitSDK(app: Application, isMainProcess: Boolean)
}