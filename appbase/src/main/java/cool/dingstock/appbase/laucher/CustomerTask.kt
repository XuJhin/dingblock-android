package cool.dingstock.appbase.laucher

import cool.dingstock.appbase.custom.CustomerManager
import cool.dingstock.launch.DcITask

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/5/19 17:10
 * @Version:         1.1.0
 * @Description:
 */
class CustomerTask : DcITask() {
	override fun run() {
		mContext?.let { CustomerManager.getInstance().setup(it) }
	}
}