package cool.dingstock.appbase.helper

import cool.dingstock.lib_base.stroage.ConfigSPHelper
import cool.dingstock.lib_base.util.Logger


/**
 * 类名：ADTShowTimeHelper
 * 包名：cool.dingstock.appbase.helper
 * 创建时间：2021/7/21 8:10 下午
 * 创建人： WhenYoung
 * 描述：
 **/
object ADTShowTimeHelper {
    private val lastADTCheckTime = "lastCheckADTTime"
    private val aDTIntervalShowTime = "aDTIntervalShowTime"
    private val firstInstall = "firstInstall"
    private val installTime = "instanllTime"

    fun updateIntervalTime(intervalTime: Long) {
        ConfigSPHelper.getInstance().save(aDTIntervalShowTime, intervalTime)
    }


    fun updateCheckTime() {
        ConfigSPHelper.getInstance().save(lastADTCheckTime, System.currentTimeMillis())
    }

    //判断是否需要进入广告页面
    fun checkNeedEnterAdtPage(): Boolean {
        //如果是在 商务广告内 就不判断联盟广告
        if (ADHelper.isInBizAd()){
            if(ADHelper.checkBizAd()){
                return true
            }
            return false
        }else{
            val lastShowTime =
                ConfigSPHelper.getInstance().getLong(lastADTCheckTime, 0)
            val intervalTime = ConfigSPHelper.getInstance().getLong(aDTIntervalShowTime, 0L)
            val currentTime = System.currentTimeMillis()
            if (intervalTime == 0L) {
                return false
            }
            //大于间隔时间就进入广告页面
            if (currentTime - lastShowTime > intervalTime) {
                return true
            }
        }
        return false
    }


}