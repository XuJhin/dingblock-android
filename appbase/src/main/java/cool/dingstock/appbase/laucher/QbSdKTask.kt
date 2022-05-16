package cool.dingstock.appbase.laucher

import cool.dingstock.appbase.helper.config.ConfigHelper
import cool.dingstock.launch.DcITask
import cool.dingstock.lib_base.util.Logger

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/5/18 14:39
 * @Version:         1.1.0
 * @Description:
 */
class QbSdKTask : DcITask() {
    override fun run() {
        //H5内核
        mContext?.let {
//            val cb: PreInitCallback = object : PreInitCallback {
//                override fun onViewInitFinished(isX5: Boolean) {
//                    Logger.d(" onViewInitFinished isX5=$isX5")
//                }
//
//                override fun onCoreInitFinished() {
//                    Logger.d(" onCoreInitFinished ---")
//                }
//            }
//            if (ConfigHelper.isAgreePolicy()) {
//                QbSdk.initX5Environment(it, cb)
//                val tbsVersion = QbSdk.getTbsVersion(it)
//            }
//            Logger.d("QbSdkVersion:", QbSdk.getTbsVersion(it).toString())
        }
    }
}