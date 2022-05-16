package cool.dingstock.appbase.laucher

import com.fm.openinstall.OpenInstall
import cool.dingstock.launch.DcITask
import cool.dingstock.launch.MainTask

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/5/19 17:08
 * @Version:         1.1.0
 * @Description:
 */
class OpenInstallTask : DcITask() {

	override fun run() {
		mContext?.let {
			OpenInstall.init(it)
		}
	}
}