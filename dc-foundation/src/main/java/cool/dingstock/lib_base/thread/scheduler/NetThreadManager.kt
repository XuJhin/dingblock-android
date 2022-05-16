package cool.dingstock.lib_base.thread.scheduler

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.internal.schedulers.ExecutorScheduler
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object NetThreadManager {
    const val THREAD_COUNT = 8

    val io_ThreadPool: Scheduler by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        val executor: Executor = Executors.newFixedThreadPool(THREAD_COUNT)
        ExecutorScheduler(executor, false, false)
    }

}