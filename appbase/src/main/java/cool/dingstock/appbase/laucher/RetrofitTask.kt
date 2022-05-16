package cool.dingstock.appbase.laucher

import cool.dingstock.appbase.net.retrofit.manager.RxRetrofitServiceManager
import cool.dingstock.launch.DcITask
import cool.dingstock.launch.Task

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/5/19 17:32
 * @Version:         1.1.0
 * @Description:
 */
class RetrofitTask : DcITask() {

	override fun run() {
		mContext?.let {
			RxRetrofitServiceManager.init(it)
		}
	}
}