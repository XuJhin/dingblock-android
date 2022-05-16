package cool.dingstock.launch.utils

import android.util.Log

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author: xujing
 * @Date: 2020/10/26 12:19
 * @Version: 1.1.0
 * @Description:
 */
object DispatcherLog {
	var isDebug = true
	fun i(msg: String?) {
		if (!isDebug) {
			return
		}
		Log.i("TaskDispatcher", msg?:"")
	}

	fun d(msg: String?) {
		if (!isDebug) {
			return
		}
		Log.d("TaskDispatcher", msg?:"")
	}
}