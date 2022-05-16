package cool.dingstock.launch

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.annotation.UiThread
import cool.dingstock.launch.sort.TaskSortUtil
import cool.dingstock.launch.start.TaskStat.markTaskDone
import cool.dingstock.launch.utils.DispatcherLog.d
import cool.dingstock.launch.utils.DispatcherLog.i
import cool.dingstock.launch.utils.DispatcherLog.isDebug
import cool.dingstock.launch.utils.Utils.isMainProcess
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author: xujing
 * @Date: 2020/10/26 12:06
 * @Version: 1.1.0
 * @Description: 启动器调用类
 */
class TaskDispatcher private constructor() {
	private val mFutures: MutableList<Future<*>> = ArrayList()
	private val mClsAllTasks: MutableList<Class<out Task>> = ArrayList()
	private val mMainThreadTasks: MutableList<Task> = ArrayList()

	/**
	 * 需要等待的任务数
	 */
	private val mNeedWaitCount = AtomicInteger() //

	/**
	 * 调用了 await 还没结束且需要等待的任务列表
	 */
	private val mNeedWaitTasks: MutableList<Task> = ArrayList()

	/**
	 * 已经结束的Task
	 */
	private val mFinishedTasks: MutableList<Class<out Task?>?> = ArrayList(100) //
	private val mDependedHashMap = HashMap<Class<out Task?>?, ArrayList<Task>?>()

	/**
	 * 启动器分析的次数，统计下分析的耗时；
	 */
	private val mAnalyseCount = AtomicInteger()
	private var mStartTime: Long = 0
	private var mAllTasks: MutableList<Task> = ArrayList()
	private var mCountDownLatch: CountDownLatch? = null
	fun addTask(task: Task?): TaskDispatcher {
		if (task != null) {
			collectDepends(task)
			mAllTasks.add(task)
			mClsAllTasks.add(task.javaClass)
			// 非主线程且需要wait的，主线程不需要CountDownLatch也是同步的
			if (ifNeedWait(task)) {
				mNeedWaitTasks.add(task)
				mNeedWaitCount.getAndIncrement()
			}
		}
		return this
	}

	private fun collectDepends(task: Task) {
		if (task.dependsOn() != null && task.dependsOn()!!.size > 0) {
			for (cls in task.dependsOn()!!) {
				if (mDependedHashMap[cls] == null) {
					mDependedHashMap[cls] = ArrayList()
				}
				mDependedHashMap[cls]!!.add(task)
				if (mFinishedTasks.contains(cls)) {
					task.satisfy()
				}
			}
		}
	}

	private fun ifNeedWait(task: Task): Boolean {
		return !task.runOnMainThread() && task.needWait()
	}

	@UiThread
	fun start() {
		mStartTime = System.currentTimeMillis()
		if (Looper.getMainLooper() != Looper.myLooper()) {
			throw RuntimeException("must be called from UiThread")
		}
		if (mAllTasks.size > 0) {
			mAnalyseCount.getAndIncrement()
			printDependedMsg()
			mAllTasks = TaskSortUtil.getSortResult(mAllTasks, mClsAllTasks)
			mCountDownLatch = CountDownLatch(mNeedWaitCount.get())
			sendAndExecuteAsyncTasks()
			i("task analyse cost " + (System.currentTimeMillis() - mStartTime) + "  begin main ")
			executeTaskMain()
		}
		i("task analyse cost startTime cost " + (System.currentTimeMillis() - mStartTime))
	}

	private fun executeTaskMain() {
		mStartTime = System.currentTimeMillis()
		for (task in mMainThreadTasks) {
			val time = System.currentTimeMillis()
			DispatchRunnable(task, this).run()
			d("real main " + task.javaClass.simpleName
					+ " cost   " + (System.currentTimeMillis() - time))
		}
		d("maintask cost " + (System.currentTimeMillis() - mStartTime))
	}

	/**
	 * 发送去并且执行异步任务
	 */
	private fun sendAndExecuteAsyncTasks() {
		for (task in mAllTasks) {
			if (task.onlyInMainProcess() && !isMainProcess) {
				markTaskDone(task)
			} else {
				sendTaskReal(task)
			}
			task.isSend = true
		}
	}

	fun markTaskDone(task: Task) {
		if (ifNeedWait(task)) {
			mFinishedTasks.add(task.javaClass)
			mNeedWaitTasks.remove(task)
			mCountDownLatch!!.countDown()
			mNeedWaitCount.getAndDecrement()
		}
	}

	/**
	 * 发送任务
	 */
	private fun sendTaskReal(task: Task) {
		if (task.runOnMainThread()) {
			mMainThreadTasks.add(task)
			if (task.needCall()) {
				task.setTaskCallBack(object : TaskCallBack {
					override fun call() {
						markTaskDone()
						task.isFinished = true
						satisfyChildren(task)
						markTaskDone(task)
						i(task.javaClass.simpleName + " finish")
						Log.i("testLog", "call")
					}
				})
			}
		} else {
			// 直接发，是否执行取决于具体线程池
			val future = task.runOn()!!
					.submit(DispatchRunnable(task, this))
			mFutures.add(future)
		}
	}

	/**
	 * 通知Children一个前置任务已完成
	 */
	fun satisfyChildren(launchTask: Task) {
		val arrayList = mDependedHashMap[launchTask.javaClass]
		if (arrayList != null && arrayList.size > 0) {
			for (task in arrayList) {
				task.satisfy()
			}
		}
	}

	/**
	 * 查看被依赖的信息
	 */
	private fun printDependedMsg() {
		i("needWait size : " + mNeedWaitCount.get())
		if (false) {
			for (cls in mDependedHashMap.keys) {
				i("cls " + cls!!.simpleName + "   " + mDependedHashMap[cls]!!.size)
				for (task in mDependedHashMap[cls]!!) {
					i("cls       " + task.javaClass.simpleName)
				}
			}
		}
	}

	fun cancel() {
		for (future in mFutures) {
			future.cancel(true)
		}
	}

	fun executeTask(task: Task) {
		if (ifNeedWait(task)) {
			mNeedWaitCount.getAndIncrement()
		}
		task.runOn()!!.execute(DispatchRunnable(task, this))
	}

	@UiThread
	fun await() {
		try {
			if (isDebug) {
				i("still has " + mNeedWaitCount.get())
				for (task in mNeedWaitTasks) {
					i("needWait: " + task.javaClass.simpleName)
				}
			}
			if (mNeedWaitCount.get() > 0) {
				if (mCountDownLatch == null) {
					throw RuntimeException("You have to call start() before call await()")
				}
				mCountDownLatch!!.await(WAIT_TIME.toLong(), TimeUnit.MILLISECONDS)
			}
		} catch (e: InterruptedException) {
		}
	}

	companion object {
		private const val WAIT_TIME = 10000

		@SuppressLint("StaticFieldLeak")
		var context: Context? = null
			private set
		var isMainProcess = false
			private set

		@Volatile
		private var sHasInit = false
		fun init(context: Context?) {
			if (context != null) {
				Companion.context = context
				sHasInit = true
				isMainProcess = isMainProcess(
						Companion.context!!
				)
			}
		}

		/**
		 * 注意：每次获取的都是新对象
		 *
		 * @return
		 */
		fun createInstance(): TaskDispatcher {
			if (!sHasInit) {
				throw RuntimeException("must call TaskDispatcher.init first")
			}
			return TaskDispatcher()
		}
	}
}