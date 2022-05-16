package cool.dingstock.launch

import android.os.Looper
import android.os.MessageQueue
import java.util.*

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/10/26 12:08
 * @Version:         1.1.0
 * @Description:
 */
class DelayInitDispatcher {
	private val mDelayTasks: Queue<Task> = LinkedList()
	private val mIdleHandler: MessageQueue.IdleHandler = MessageQueue.IdleHandler {
		if (mDelayTasks.size > 0) {
			val task: Task = mDelayTasks.poll()
			DispatchRunnable(task).run()
		}
		!mDelayTasks.isEmpty()
	}

	fun addTask(task: Task?): DelayInitDispatcher {
		mDelayTasks.add(task)
		return this
	}

	fun start() {
		Looper.myQueue().addIdleHandler(mIdleHandler)
	}
}