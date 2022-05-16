package cool.dingstock.launch

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author: xujing
 * @Date: 2020/10/26 12:08
 * @Version: 1.1.0
 * @Description:
 */
abstract class MainTask : Task() {
	override fun runOnMainThread(): Boolean {
		return true
	}
}