package cool.dingstock.appbase.laucher

import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import cool.dingstock.appbase.BuildConfig
import cool.dingstock.launch.DcITask
import cool.dingstock.lib_base.util.AppUtils
import cool.dingstock.lib_base.util.Logger
import java.util.*

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/5/18 11:02
 * @Version:         1.1.0
 * @Description:
 */
class UmengTask : DcITask() {

    override fun run() {
        mContext?.let {
            UMConfigure.init(
                it, "5ca51bde0cafb2832d000fb4",
                AppUtils.getUMChannelName(it), UMConfigure.DEVICE_TYPE_PHONE, ""
            )
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_MANUAL)
//            if (BuildConfig.DEBUG) {
//                UMConfigure.setLogEnabled(true)
//                try {
//                    val testDeviceInfo = AppUtils.getDeviceInfo(it)
//                    Logger.d("UMLog", "testDevice", testDeviceInfo)
//                    val testDeviceInfo1 = AppUtils.getTestDeviceInfo(it)
//                    Logger.d("UMLog", "testDevice1", Arrays.toString(testDeviceInfo1))
//                } catch (e: Exception) {
//                }
//            }
        }
    }
}