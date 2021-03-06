package cool.dingstock.launch

import android.os.Process
import cool.dingstock.launch.utils.DispatcherExecutor.iOExecutor
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author: xujing
 * @Date: 2020/10/26 12:07
 * @Version: 1.1.0
 * @Description:
 */
abstract class Task : ITask {
	// 当前Task依赖的Task数量（需要等待被依赖的Task执行完毕才能执行自己），默认没有依赖
	private val mDepends = CountDownLatch(if (dependsOn() == null) 0 else dependsOn()!!.size)
	protected var mTag: String = javaClass.simpleName
	protected var mContext = TaskDispatcher.context

	/**
	 * 当前进程是否是主进程
	 */
	protected var mIsMainProcess = TaskDispatcher.isMainProcess

	/**
	 * 是否正在等待
	 */
	@Volatile
	var isWaiting = false

	/**
	 * 是否正在执行
	 */
	@Volatile
	var isRunning = false

	/**
	 * Task是否执行完成
	 */
	@Volatile
	var isFinished = false

	/**
	 * Task是否已经被分发
	 */
	@Volatile
	var isSend = false

	/**
	 * 当前Task等待，让依赖的Task先执行
	 */
	fun waitToSatisfy() {
		try {
			mDepends.await()
		} catch (e: InterruptedException) {
			e.printStackTrace()
		}
	}

	/**
	 * 依赖的Task执行完一个
	 */
	fun satisfy() {
		mDepends.countDown()
	}

	/**
	 * 是否需要尽快执行，解决特殊场景的问题：一个Task耗时非常多但是优先级却一般，很有可能开始的时间较晚，
	 * 导致最后只是在等它，这种可以早开始。
	 *
	 * @return
	 */
	fun needRunAsSoon(): Boolean {
		return false
	}

	/**
	 * Task的优先级，运行在主线程则不要去改优先级
	 *
	 * @return
	 */
	override fun priority(): Int {
		return Process.THREAD_PRIORITY_BACKGROUND
	}

	/**
	 * Task执行在哪个线程池，默认在IO的线程池；
	 * CPU 密集型的一定要切换到DispatcherExecutor.getCPUExecutor();
	 *
	 * @return
	 */
	override fun runOn(): ExecutorService? {
		return iOExecutor
	}

	/**
	 * 当前Task依赖的Task集合（需要等待被依赖的Task执行完毕才能执行自己），默认没有依赖
	 *
	 * @return
	 */
	override fun dependsOn(): List<Class<out Task?>?>? {
		return null
	}

	/**
	 * 异步线程执行的Task是否需要在被调用await的时候等待，默认不需要
	 *
	 * @return
	 */
	override fun needWait(): Boolean {
		return false
	}

	override fun runOnMainThread(): Boolean {
		return false
	}

	/**
	 * 是否只在主进程，默认是
	 */
	override fun onlyInMainProcess(): Boolean {
		return true
	}

	override val tailRunnable: Runnable?
		get() = null

	override fun setTaskCallBack(callBack: TaskCallBack?) {}
	override fun needCall(): Boolean {
		return false
	}
}