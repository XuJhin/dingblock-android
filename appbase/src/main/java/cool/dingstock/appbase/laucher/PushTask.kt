package cool.dingstock.appbase.laucher

import android.app.Application
import cool.dingstock.appbase.push.DCPushManager
import cool.dingstock.launch.DcITask
import cool.dingstock.launch.Task

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/5/18 11:07
 * @Version:         1.1.0
 * @Description:
 */
class PushTask : DcITask() {

	override fun run() {
		mContext?.let {
			DCPushManager.getInstance().setup(it as Application?)
		}
	}
}