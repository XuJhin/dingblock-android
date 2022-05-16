package cool.dingstock.appbase.laucher

import android.app.Application
import cool.dingstock.launch.MainTask
import cool.dingstock.lib_base.BaseLibrary

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/5/19 17:31
 * @Version:         1.1.0
 * @Description:
 */
class BaseLibraryTask : MainTask() {
	override fun priority(): Int {
		return 999
	}

	override fun needWait(): Boolean {
		return true
	}

	override fun run() {
		mContext?.let {
			BaseLibrary.initialize(it as Application?)
		}
	}
}