package cool.dingstock.mobile

import android.content.Context
import android.text.TextUtils
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import cool.dingstock.appbase.constant.PayConstant
import cool.dingstock.appbase.dagger.AppBaseApiHelper.appBaseComponent
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.pay.WeChatOrderData
import cool.dingstock.appbase.net.api.common.CommonApi
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.mobile.alipay.AliPayActivity
import cool.dingstock.mobile.wxapi.WXEntryActivity
import io.reactivex.rxjava3.functions.Consumer
import javax.inject.Inject

class PayHelper private constructor() {
    @JvmField
    @Inject
    var commonApi: CommonApi? = null

    /**
     * 生成订单 + 三方支付
     *
     * @param callback 支付回调
     */
    fun generateOrder(
        context: Context,
        @PayConstant.PayType payType: String?,
        goodsId: String?,
        callback: PayCallback
    ) {
        if (TextUtils.isEmpty(payType)) {
            callback.onFailed("EMPTY_PAY_TYPE", "EMPTY_PAY_TYPE")
            return
        }
        if (goodsId.isNullOrEmpty()) {
            callback.onFailed("EMPTY_ID", "EMPTY_ID")
            return
        }
        when (payType) {
            PayConstant.PayType.ALI_PAY -> {
                commonApi?.payAliInfo(goodsId, payType)
                    ?.subscribe(Consumer subscribe@{ (err, payData) ->
                        if (err || payData.isNullOrEmpty()) {
                            callback.onFailed("ERROR_ORDER_INFO_EMPTY", "订单信息为空")
                            return@subscribe
                        }
                        aliPay(context, payData, callback)
                    }) { err: Throwable -> callback.onFailed(err.message, err.message) }
            }
            PayConstant.PayType.WE_CHAT_PAY -> {
                val iwxapi = WXAPIFactory.createWXAPI(
                    BaseLibrary.getInstance().context,
                    PayConstant.WX_APP_ID,
                    false
                )
                if (!iwxapi.isWXAppInstalled) {
                    callback.onFailed("EMPTY_WX_NOT_INSTALL", "未安装微信")
                    return
                }
                commonApi?.payWeChatInfo(goodsId, payType)
                    ?.subscribe(Consumer<BaseResult<WeChatOrderData>> subscribe@{ (err, payData) ->
                        if (err || payData == null) {
                            callback.onFailed("ERROR_ORDER_INFO_EMPTY", "订单信息为空")
                            return@subscribe
                        }
                        weChatPay(payData, callback)
                    }, Consumer { err: Throwable -> callback.onFailed(err.message, err.message) })
            }
            else -> callback.onFailed(
                "ERROR_PAY_TYPE",
                "ERROR_PAY_TYPE"
            )
        }
        UTHelper.payRequest(payType)
    }


    /**
     * 微信支付 api
     *
     * @param weChatOrderData 微信支付数据
     * @param callback        回调
     */
    fun weChatPay(weChatOrderData: WeChatOrderData?, callback: PayCallback?) {
        WXEntryActivity.ID = PayConstant.WX_APP_ID
        val wxapi = WXAPIFactory.createWXAPI(
            BaseLibrary.getInstance().context,
            PayConstant.WX_APP_ID,
            false
        )
        wxapi.registerApp(PayConstant.WX_APP_ID)
        val request = PayReq()
        request.appId = weChatOrderData!!.appId
        request.partnerId = weChatOrderData.partnerId
        request.prepayId = weChatOrderData.prepayId
        request.packageValue = weChatOrderData.packageValue
        request.nonceStr = weChatOrderData.nonceStr
        request.timeStamp = weChatOrderData.timeStamp
        request.sign = weChatOrderData.sign
        wxapi.sendReq(request)
        WXEntryActivity.setupPayBack(callback)
    }

    fun aliPay(context: Context?, order: String?, callback: PayCallback?) {
        AliPayActivity.startPay(context, order, callback!!)
    }

    companion object {
        @Volatile
        var instance: PayHelper? = null
            get() {
                if (null == field) {
                    synchronized(PayHelper::class.java) {
                        if (null == field) {
                            field = PayHelper()
                        }
                    }
                }
                return field
            }
            private set
    }

    init {
        appBaseComponent.inject(this)
    }
}