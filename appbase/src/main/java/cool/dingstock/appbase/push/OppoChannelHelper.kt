package cool.dingstock.appbase.push

import com.lzf.easyfloat.permission.rom.OppoUtils
import com.tencent.android.tpush.XGPushConfig
import cool.dingstock.appbase.util.PlatUtils
import cool.dingstock.lib_base.BaseLibrary


/**
 * 类名：OppoChannelHelper
 * 包名：cool.dingstock.appbase.push
 * 创建时间：2021/10/25 11:25 上午
 * 创建人： WhenYoung
 * 描述：
 **/
object OppoChannelHelper {

    private var onSuccess: ((otherToken: String) -> Unit)? = null
    private var onError: ((code: String, msg: String) -> Unit)? = null

    fun setOnPusSDkInitComplete(
        success: (otherToken: String) -> Unit,
        error: (code: String, msg: String) -> Unit
    ) {
        onSuccess = success
        onError = error
    }

    fun onSuccess() {
        if (PlatUtils.isOppo()) {
            val otherToken = XGPushConfig.getOtherPushToken(BaseLibrary.getInstance().context)
            if (otherToken.isNotEmpty()) {
                onSuccess?.invoke(otherToken)
            } else {
                onError?.invoke("", "")
            }
        }
    }

    fun onFail() {
        if (PlatUtils.isOppo()) {
            val otherToken = XGPushConfig.getOtherPushToken(BaseLibrary.getInstance().context)
            if (otherToken.isNotEmpty()) {
                onSuccess?.invoke(otherToken)
            } else {
                onError?.invoke("", "")
            }
        }
    }

}