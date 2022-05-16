package cool.dingstock.lib_base.thread;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import cool.dingstock.lib_base.util.CollectionUtils;
import cool.dingstock.lib_base.util.DeviceInfoUtils;
import cool.dingstock.lib_base.util.Logger;

public class ThreadPoolHelper {

    // 核心线程是 cpu核心 + 1；最小是 3个 核心线程
    // 最大线程数 为 cpu核心 + 1 * 10；最小是 20个 线程
    // 核心线程长期存活，非核心线程等待时间 30s
    // 等待队列 为 256
    private static final int CORE_POOL_SIZE = Math.max(2, DeviceInfoUtils.getNumberOfCPUCores()) + 1;
    private static final int MAX_POOL_SIZE = CORE_POOL_SIZE * 10;
    private static final int KEEP_ALIVE = 30;
    private static final int QUEUE_CAPACITY = 256;
    private static volatile ThreadPoolHelper mInstance;
    /**
     * 线程池
     */
    private ThreadPoolExecutor threadPool;

    private ScheduledExecutorService scheduledExecutorService;
    private Handler mHandler;
    private CopyOnWriteArrayList<Future<?>> futureList = new CopyOnWriteArrayList<>();

    private ThreadPoolHelper() {
    }

    public static ThreadPoolHelper getInstance() {
        if (null == mInstance) {
            synchronized (ThreadPoolHelper.class) {
                if (null == mInstance) {
                    mInstance = new ThreadPoolHelper();
                }
            }
        }
        return mInstance;
    }

    public void runOnUiThread(Runnable command) {
        if (null == mHandler) {
            mHandler = new Handler(Looper.getMainLooper());
        }

        mHandler.post(command);
    }

    public void runOnUiThread(Runnable command, long delayMillis) {
        if (null == mHandler) {
            mHandler = new Handler(Looper.getMainLooper());
        }

        mHandler.postDelayed(command, delayMillis);
    }

    public void threadExecute(Runnable command) {
        if (null == threadPool) {
            initThreadPool();
        }

        threadPool.execute(command);
    }

    public Future<?> threadSubmit(Runnable command) {
        if (null == threadPool) {
            initThreadPool();
        }

        return threadPool.submit(command);
    }

    public <T> Future<T> threadSubmit(Callable<T> task) {
        if (null == threadPool) {
            initThreadPool();
        }

        return threadPool.submit(task);
    }

    public <T> Future<T> threadSubmit(Runnable task, T result) {
        if (null == threadPool) {
            initThreadPool();
        }

        return threadPool.submit(task, result);
    }

    /**
     * 通过ThreadPoolExecutor 显示创建线程池
     */
    private void initThreadPool() {
        Logger.d("Start to create the thread poll.",
                " CPUCores: " + DeviceInfoUtils.getNumberOfCPUCores(),
                " ;CorePollSize: " + CORE_POOL_SIZE,
                " ;MaxPoolSize: " + MAX_POOL_SIZE);

        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(QUEUE_CAPACITY),
                new ThreadFactory() {

                    private final AtomicInteger mCount = new AtomicInteger(1);

                    @Override
                    public Thread newThread(@NonNull Runnable r) {
                        return new Thread(r, "Task:" + mCount.getAndIncrement());
                    }
                });
    }

    public void shutDownTask() {
        if (null != threadPool) {
            Logger.d("Start to shut down now.");
            threadPool.shutdownNow();
            threadPool = null;
        }
    }

    public Future startScheduleTask(@NonNull Runnable runnable, long delay, long period) {
        return startScheduleTask(runnable, delay, period, TimeUnit.MILLISECONDS);
    }

    public Future startScheduleTask(@NonNull Runnable runnable, long delay) {
        return startScheduleTask(runnable, delay, TimeUnit.MILLISECONDS);
    }

    public Future startScheduleTask(@NonNull Runnable runnable, long delay, TimeUnit unit) {
        return startScheduleTask(runnable, delay, 0, unit);
    }

    public Future startScheduleTask(@NonNull Runnable runnable, long delay, long period, TimeUnit unit) {
        Logger.d("Start to run ScheduleTask.");

        if (null == scheduledExecutorService || scheduledExecutorService.isShutdown()) {
            initScheduledExecutor();
        }

        Future future;
        if (period > 0) {
            future = scheduledExecutorService.scheduleAtFixedRate(runnable, delay, period, unit);
        } else {
            future = scheduledExecutorService.schedule(runnable, delay, unit);
        }

        futureList.add(future);
        return future;
    }

    public void shutDownScheduledTask(Future future) {
        if (null == future) {
            Logger.w("The future is");
            return;
        }

        if (null == scheduledExecutorService || scheduledExecutorService.isShutdown()) {
            Logger.d("Start to shut down task now.future=" + future.toString());
            return;
        }

        if (CollectionUtils.isEmpty(futureList)) {
            Logger.d("task pool is empty do nothing");
            return;
        }

        future.cancel(true);
        futureList.remove(future);
    }

    public void shutDownScheduledTask() {
        if (null == scheduledExecutorService || scheduledExecutorService.isShutdown()) {
            return;
        }

        if (CollectionUtils.isEmpty(futureList)) {
            Logger.d("task pool is empty do nothing");
            return;
        }

        Logger.d("Start to shut down all task now.");
        for (Future future : futureList) {
            if (future != null) {
                future.cancel(true);
            }
        }
        futureList.clear();

        scheduledExecutorService.shutdownNow();
        scheduledExecutorService = null;
    }

    private void initScheduledExecutor() {
        Logger.d("Start to init Scheduled Executor.");
        scheduledExecutorService = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE,
                new ThreadFactory() {

                    private final AtomicInteger mCount = new AtomicInteger(1);

                    @Override
                    public Thread newThread(@NonNull Runnable r) {
                        return new Thread(r, "Task:" + mCount.getAndIncrement());
                    }
                });
    }

}