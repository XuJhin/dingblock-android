package cool.dingstock.appbase.laucher

import com.umeng.analytics.MobclickAgent
import cool.dingstock.launch.DcITask
import cool.dingstock.launch.Task
import cool.dingstock.lib_base.util.Logger

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/5/18 11:02
 * @Version:         1.1.0
 * @Description:
 */
class MobTask : DcITask() {
	override fun run() {
		mContext?.let {
			Logger.e("SDK_TASK:","SHARE")
			MobclickAgent.setCatchUncaughtExceptions(true)
		}
	}
}