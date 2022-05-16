package cool.dingstock.appbase.laucher

import android.app.Application
import cool.dingstock.appbase.AppBaseLibrary
import cool.dingstock.launch.DcITask

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/5/19 17:12
 * @Version:         1.1.0
 * @Description:
 */
class AppBaseLibraryTask : DcITask() {

	override fun run() {
		mContext?.let {
			AppBaseLibrary.initialize(it as Application?)
		}
	}
}