package cool.dingstock.appbase.webview.module

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.PayConstant
import cool.dingstock.appbase.constant.PushConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.dagger.AppBaseApiHelper
import cool.dingstock.appbase.entity.bean.pay.WeChatOrderData
import cool.dingstock.appbase.entity.event.account.EventActivated
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.webview.bridge.*
import cool.dingstock.appbase.widget.paydialog.PayDialog
import cool.dingstock.lib_base.json.JSONHelper
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.mobile.PayCallback
import cool.dingstock.mobile.PayHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference
import javax.inject.Inject

class PayModule(
    override var context: WeakReference<Context>?,
    override var bridge: IJsBridge?
) : IBridgeModule {

    @Inject
    lateinit var accountApi: AccountApi

    var payDialog: PayDialog? = null

    override fun moduleName(): String = "pay"

    init {
        EventBus.getDefault().register(this)
        AppBaseApiHelper.appBaseComponent.inject(this)
    }

    override fun release() {
        super.release()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @XBridgeMethod
    fun dcPay(event: BridgeEvent) {
        val goodsId = event.params?.get("goodsId") as? String?
        val payType = event.params?.get("payType") as? String?
        val activityType = (event.params?.get("activityType") as? String) ?: ""
        val isWeChatPay = payType == PayConstant.PayType.WE_CHAT_PAY
        Logger.d("dcPay is called  --- goodsId=$goodsId ,payType=$payType")
        if (TextUtils.isEmpty(payType) || TextUtils.isEmpty(goodsId)) {
            bridge?.let { event.toResponse().error("ERROR_PARAMS", "入参非法")?.send(it) }
            return
        }
        if (null == context?.get()) {
            bridge?.let {
                event.toResponse().error("ERROR_BRIDGE_INTERNAL", "native本地错误")?.send(it)
            }
            return
        }
        PayHelper.instance?.generateOrder(context!!.get()!!, payType!!, goodsId!!,
            object : PayCallback {
                override fun onFailed(errorCode: String?, errorMsg: String?) {
                    if (!isAttach()) {
                        return
                    }
                    payUt(activityType, isWeChatPay, false)
                    bridge?.let {
                        event.toResponse().error(errorCode ?: "", errorMsg ?: "")?.send(it)
                    }
                }

                override fun onCancel() {
                    if (!isAttach()) {
                        return
                    }
                    payUt(activityType, isWeChatPay, false)
                    bridge?.let { event.toResponse().error("PAY_CANCEL", "支付取消")?.send(it) }
                }

                override fun onSucceed() {
                    if (!isAttach()) {
                        return
                    }
                    payUt(activityType, isWeChatPay, true)
                    bridge?.let { event.toResponse().successDefault()?.send(it) }
                    //支付成功给js抛出event 只是为了统一IOS的代码 并没有太大作用（胶水代码~~）
                    bridge?.let {
                        BridgeEvent.eventBuild().apply {
                            module = "user"
                            eventName = PushConstant.Event.USER_PAY_SUCCESS
                        }.send(it)
                    }
                    //更新用户数据
                    val subscribe = accountApi.getUserByNet().subscribe({
                        //更新Vip页面
//                            EventBus.getDefault().post(EventUserVipChange(true))
                        updata2Js(event)
                    }, {
                        updata2Js(event)
                    })
                    //支付成功，弹窗消失,在销毁当前页面
                    ToastUtil.getInstance()._short(context?.get(), "支付成功")
                    payDialog?.dismiss()
                }
            })
    }


    @XBridgeMethod
    fun pay(event: BridgeEvent) {
        val payType = event.params?.get("payType") ?: return
        val callback = object : PayCallback {
            override fun onFailed(errorCode: String?, errorMsg: String?) {
                if (!isAttach()) {
                    return
                }
                bridge?.let { event.toResponse().error(errorCode ?: "", errorMsg ?: "")?.send(it) }
            }

            override fun onCancel() {
                if (!isAttach()) {
                    return
                }
                bridge?.let { event.toResponse().error("PAY_CANCEL", "支付取消")?.send(it) }
            }

            override fun onSucceed() {
                if (!isAttach()) {
                    return
                }
                bridge?.let {
                    event.toResponse().successDefault()?.apply {
                        result = hashMapOf("eventName" to PushConstant.Event.PAY_SUCCESS)
                    }?.send(it)
                }
                ToastUtil.getInstance()._short(context?.get(), "支付成功")
                payDialog?.dismiss()
            }
        }
        when (payType) {
            PayConstant.PayType.ALI_PAY -> {
                val payJson = event.params?.get("payJson") ?: ""
                context?.get()?.let { ctx ->
                    PayHelper.instance?.aliPay(ctx, payJson.toString(), callback)
                }
            }
            PayConstant.PayType.WE_CHAT_PAY -> {
                val payJson = event.params?.get("payJson") ?: ""
                val data = JSONHelper.fromJson<WeChatOrderData>(
                    payJson.toString(),
                    WeChatOrderData::class.java
                )
                context?.get()?.let { ctx ->
                    PayHelper.instance?.weChatPay(data, callback)
                }
            }
        }
    }


    private fun updata2Js(event: BridgeEvent) {
        //告诉h5
        //                            BridgeEvent.eventBuild().
        val actionBrideEvent = ActionBrideEvent.actionEventBuild(
            ActionBrideEvent.ActionType.USER_PAY_SUCCESS,
            this@PayModule
        )
        //                            var eventName = event.params?.get("eventName")
        actionBrideEvent.params = event.params

        bridge?.nativeAction(actionBrideEvent.toJson(), object : OnNativeActionCallback {
            override fun onCallback(actionEvent: ActionBrideEvent) {

            }
        })
    }

    @XBridgeMethod
    fun dcShowPayDialog(event: BridgeEvent) {
        (context?.get() as? Activity)?.let {
            it.runOnUiThread {
                if (null == event.params) {
                    event.params = hashMapOf()
                }
                val mobClickName = event.params?.get("mobClickName").toString()
                val activityType = event.params?.get("type").toString() ?: ""
                payDialog = PayDialog(it, activityType, mobClickName)
                payDialog?.show()
                payDialog?.setOnWehchatPayClick(object : PayDialog.OnPayClickListener {
                    override fun onPayClick(goodsId: String, mobClickName: String) {
                        event.params?.put("goodsId", goodsId)
                        event.params?.put("mobClickName", mobClickName)
                        event.params?.put("activityType", activityType)
                        event.params?.put("payType", PayConstant.PayType.WE_CHAT_PAY)
                        dcPay(event)
                    }
                })

                payDialog?.setOnAliPayClick(object : PayDialog.OnPayClickListener {
                    override fun onPayClick(goodsId: String, mobClickName: String) {
                        event.params?.put("goodsId", goodsId)
                        event.params?.put("mobClickName", mobClickName)
                        event.params?.put("activityType", activityType)
                        event.params?.put("payType", PayConstant.PayType.ALI_PAY)
                        dcPay(event)
                    }
                })

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onActiveSuccessEvent(event: EventActivated) {
        Logger.d("onActiveSuccessEvent  ---- ")
        bridge?.let {
            BridgeEvent.eventBuild().apply {
                module = moduleName()
                eventName = PushConstant.Event.UPDATE_USER_INFO
            }.send(it)
        }
    }

    private fun payUt(type: String, isWeChat: Boolean, success: Boolean) {
        val status = if (success) "开通成功" else "开通失败"
        when (type) {
            MineConstant.VipLayerType.activity1 -> {
                val event =
                    if (isWeChat) UTConstant.UserLayer.LayActivi1_click_WeChatPay else UTConstant.UserLayer.LayActivi1_click_AliPay
                UTHelper.commonEvent(event, "status", status)
            }
            MineConstant.VipLayerType.activity2 -> {
                val event =
                    if (isWeChat) UTConstant.UserLayer.LayActivi2_click_WeChatPay else UTConstant.UserLayer.LayActivi2_click_AliPay
                UTHelper.commonEvent(event, "status", status)
            }
            MineConstant.VipLayerType.activity4 -> {
                val event =
                    if (isWeChat) UTConstant.UserLayer.LayActivi4_click_WeChatPay else UTConstant.UserLayer.LayActivi4_click_AliPay
                UTHelper.commonEvent(event, "status", status)
            }
            MineConstant.VipLayerType.activity5 -> {
                val event =
                    if (isWeChat) UTConstant.UserLayer.LayActivi5_click_WeChatPay else UTConstant.UserLayer.LayActivi5_click_AliPay
                UTHelper.commonEvent(event, "status", status)
            }
        }
    }
}