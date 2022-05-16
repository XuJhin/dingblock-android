package cool.dingstock.launch

import android.os.Looper
import android.os.Process
import androidx.core.os.TraceCompat
import cool.dingstock.launch.start.TaskStat.currentSituation
import cool.dingstock.launch.start.TaskStat.markTaskDone
import cool.dingstock.launch.utils.DispatcherLog.i
import cool.dingstock.launch.utils.DispatcherLog.isDebug

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author: xujing
 * @Date: 2020/10/26 12:08
 * @Version: 1.1.0
 * @Description:
 */
class DispatchRunnable : Runnable {
	private val mTask: Task
	private var mTaskDispatcher: TaskDispatcher? = null

	constructor(task: Task) {
		mTask = task
	}

	constructor(task: Task, dispatcher: TaskDispatcher?) {
		mTask = task
		mTaskDispatcher = dispatcher
	}

	override fun run() {
		TraceCompat.beginSection(mTask.javaClass.simpleName)
		i(
				mTask.javaClass.simpleName
						+ " begin run" + "  Situation  " + currentSituation
		)
		Process.setThreadPriority(mTask.priority())
		var startTime = System.currentTimeMillis()
		mTask.isWaiting = true
		mTask.waitToSatisfy()
		val waitTime = System.currentTimeMillis() - startTime
		startTime = System.currentTimeMillis()
		// 执行Task
		mTask.isRunning = true
		mTask.run()
		// 执行Task的尾部任务
		val tailRunnable = mTask.tailRunnable
		tailRunnable?.run()
		if (!mTask.needCall() || !mTask.runOnMainThread()) {
			printTaskLog(startTime, waitTime)
			markTaskDone()
			mTask.isFinished = true
			if (mTaskDispatcher != null) {
				mTaskDispatcher!!.satisfyChildren(mTask)
				mTaskDispatcher!!.markTaskDone(mTask)
			}
			i(mTask.javaClass.simpleName + " finish")
		}
		TraceCompat.endSection()
	}

	/**
	 * 打印出来Task执行的日志
	 *
	 * @param startTime
	 * @param waitTime
	 */
	private fun printTaskLog(startTime: Long, waitTime: Long) {
		val runTime = System.currentTimeMillis() - startTime
		if (isDebug) {
			i(
					mTask.javaClass.simpleName + "  wait " + waitTime + "    run "
							+ runTime + "   isMain " + (Looper.getMainLooper() == Looper.myLooper())
							+ "  needWait " + (mTask.needWait() || Looper.getMainLooper() == Looper.myLooper())
							+ "  ThreadId " + Thread.currentThread().id
							+ "  ThreadName " + Thread.currentThread().name
							+ "  Situation  " + currentSituation
			)
		}
	}
}