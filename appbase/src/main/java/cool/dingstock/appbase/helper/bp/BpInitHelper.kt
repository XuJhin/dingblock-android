package cool.dingstock.appbase.helper.bp

import android.os.Handler
import android.os.Message
import androidx.lifecycle.MutableLiveData
import cool.dingstock.lib_base.BaseLibrary

object BpInitHelper {

    private val livedata = MutableLiveData<Boolean>()
    private var bpLocalHelper: BpLocalHelper? = null

    private val duration = 5000L
    private var mHandler: Handler = object : Handler(BaseLibrary.getInstance().context.mainLooper) {
        override fun handleMessage(msg: Message) {
            if (livedata.value != true) {
                bpLocalHelper = BpLocalHelper(BaseLibrary.getInstance().context, livedata)
            }
            super.handleMessage(msg)
        }
    }

    init {
    }

    fun initBpHelper() {
        if (bpLocalHelper == null) {
            bpLocalHelper = BpLocalHelper(BaseLibrary.getInstance().context, livedata)
        }
        mHandler.sendEmptyMessageDelayed(0, duration)
    }

    fun getBpHelper(): BpLocalHelper {
        return bpLocalHelper!!
    }

    fun getBpResolveState(): Boolean {
        return livedata.value == true
    }
}