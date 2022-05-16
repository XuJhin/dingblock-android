package cool.dingstock.mobile

import android.content.Context
import android.util.Log
import com.chinaums.pppay.unify.UnifyPayListener
import com.chinaums.pppay.unify.UnifyPayPlugin
import com.chinaums.pppay.unify.UnifyPayRequest
import cool.dingstock.appbase.constant.PayConstant
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.exception.DcException
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.mobile.wxapi.WXEntryActivity
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable


object BoxPayResultConstant {
    const val SUCCESS = "0000"
    const val CANCEL = "1000"
}

sealed class BoxWxPayStatus {
    object Success : BoxWxPayStatus()
    class Failed(val errorCode: String?, val errorMsg: String?) : BoxWxPayStatus()
    object Cancel : BoxWxPayStatus()
}

interface BoxPayCallBack {
    fun onSucceed(extraData: String)
}

object BoxPayHelper {

    fun wechatPay(
        context: Context,
        prepayParams: String
    ): Flowable<BaseResult<Boolean>> {
        return Flowable.create({
            WXEntryActivity.ID = PayConstant.WX_APP_ID
            val payPlugin = UnifyPayPlugin.getInstance(context)
            val payRequest = UnifyPayRequest()
            payRequest.payChannel = UnifyPayRequest.CHANNEL_WEIXIN
            payRequest.payData = prepayParams
            payPlugin.listener = UnifyPayListener { resultCode, resultInfo ->
                if (resultCode.equals("0000")) {

                    it.onNext(BaseResult(err = false, res = true))
                    it.onComplete()
                } else {
                    it.onError(DcException(-100, "取消支付"))
                    it.onComplete()

                }
            }
            payPlugin.sendPayRequest(payRequest)
        }, BackpressureStrategy.BUFFER)
    }

    fun aliPay(
        context: Context,
        entity: String
    ): Flowable<BaseResult<Boolean>> {
        return Flowable.create({
            val payPlugin = UnifyPayPlugin.getInstance(context)
            val payRequest = UnifyPayRequest()
            payRequest.payChannel = UnifyPayRequest.CHANNEL_ALIPAY_MINI_PROGRAM
            payRequest.payData = entity
            payPlugin.listener = UnifyPayListener { resultCode, resultInfo ->
                if (resultCode.equals("0000")) {
                    Log.d("payHelper", "支付成功 $resultInfo")
                    it.onNext(BaseResult(err = false, res = true))
                    it.onComplete()
                } else {
                    it.onError(DcException(-100, "取消支付"))
                    it.onComplete()

                }
            }
            payPlugin.sendPayRequest(payRequest)
        }, BackpressureStrategy.BUFFER)
    }

}