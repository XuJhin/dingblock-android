package cool.dingstock.launch

import android.content.Context
import cool.dingstock.launch.utils.DispatcherLog


/**
 * 类名：DcITask
 * 包名：cool.dingstock.launch
 * 创建时间：2021/6/30 11:14 上午
 * 创建人： WhenYoung
 * 描述：
 **/
abstract class DcITask {
    var mContext : Context? = TaskDispatcher.context

    protected abstract fun run()


    fun start(){
        val startTime = System.currentTimeMillis()
        DispatcherLog.d("DcITask ThreadName:${Thread.currentThread()}, Task:${javaClass.simpleName}, startTime:${startTime}")
        run()
        val endTime =  System.currentTimeMillis()
        DispatcherLog.d("DcITask ThreadName:${Thread.currentThread()}, Task:${javaClass.simpleName}, endTime:${endTime}, waitTimes:${endTime - startTime}")
    }


}