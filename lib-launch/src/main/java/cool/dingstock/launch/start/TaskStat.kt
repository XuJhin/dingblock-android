package cool.dingstock.launch.start

import cool.dingstock.launch.utils.DispatcherLog
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author: xujing
 * @Date: 2020/10/26 12:11
 * @Version: 1.1.0
 * @Description:
 */
object TaskStat {
	private val sBeans: MutableList<TaskStatBean> = ArrayList()
	private const val sOpenLaunchStat = false // 是否开启统计

	@Volatile
	private var sCurrentSituation = ""
	private var sTaskDoneCount = AtomicInteger()
	var currentSituation: String
		get() = sCurrentSituation
		set(currentSituation) {
			if (!sOpenLaunchStat) {
				return
			}
			DispatcherLog.i("situation=========$currentSituation")
			sCurrentSituation = currentSituation
			setLaunchStat()
		}

	fun setLaunchStat() {
		val bean = TaskStatBean()
		bean.situation = sCurrentSituation
		bean.count = sTaskDoneCount.get()
		sBeans.add(bean)
		sTaskDoneCount = AtomicInteger(0)
	}

	fun markTaskDone() {
		sTaskDoneCount.getAndIncrement()
	}
}