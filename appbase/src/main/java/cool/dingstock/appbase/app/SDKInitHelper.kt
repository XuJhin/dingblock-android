package cool.dingstock.appbase.app

import android.app.Application


/**
 * 类名：SDKInitHelper
 * 包名：cool.dingstock.appbase.app
 * 创建时间：2021/9/16 12:18 下午
 * 创建人： WhenYoung
 * 描述：
 **/
object SDKInitHelper {

    val initors = linkedMapOf<BaseSDKInitor,String>()

    fun initNoDeviceId(app:Application){
        for (key in initors.keys) {
            key.initNoDeviceId(app)
        }
    }


    /**
     * 预加载
     * */
    fun preInitSDK(app: Application) {
        for (key in initors.keys) {
            key.preInitSDK(app)
        }
    }



    /**
     * 同意协议之后正式初始化
     * */
    fun beginInitSDK(app: Application, isMainProcess: Boolean) {
        for (key in initors.keys) {
            key.beginInitSDK(app,isMainProcess)
        }
    }

    fun registerInitor(initor:BaseSDKInitor){
        initors.put(initor,initor.javaClass.name)
    }


}