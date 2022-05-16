package cool.dingstock.appbase.ut

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/16  16:10
 */
object UTUtils {
    const val LayActivi2_TimePopup_exposure = "LayActivi2_TimePopup_exposure"
    const val LayActivi2_TimePopup_click_PanicBuy = "LayActivi2_TimePopup_click_PanicBuy"

    const val LayActivi3_FreePopup_exposure = "LayActivi3_FreePopup_exposure"
    const val LayActivi3_click_FreeCollection = "LayActivi3_click_FreeCollection"
    const val LayActivi3_click_ClosePopup = "LayActivi3_click_ClosePopup"

    const val LayActivi6_FreePopup_exposure = "LayActivi6_FreePopup_exposure"
    const val LayActivi6_click_FreeCollection = "LayActivi6_click_FreeCollection"
    const val LayActivi6_click_ClosePopup = "LayActivi6_click_ClosePopup"




    private val eventMap = mapOf(
            "alipay" to mapOf(
                    "LayActivi1" to "LayActivi1_click_AliPay",
                    "LayActivi2" to "LayActivi2_click_AliPay",
//                    "LayActivi3" to "LayActivi3_click_AliPay",//活动三和活动六 免费领取 没有支付
                    "LayActivi4" to "LayActivi4_click_AliPay",
                    "LayActivi5" to "LayActivi5_click_AliPay"
//                    "LayActivi6" to "LayActivi6_click_AliPay"//活动三和活动六 免费领取 没有支付
            ),
            "wechatPay" to mapOf(
                    "LayActivi1" to "LayActivi1_click_WeChatPay",
                    "LayActivi2" to "LayActivi2_click_WeChatPay",
//                    "LayActivi3" to "LayActivi3_click_WeChatPay",
                    "LayActivi4" to "LayActivi4_click_WeChatPay",
                    "LayActivi5" to "LayActivi5_click_WeChatPay"
//                    "LayActivi6" to "LayActivi6_click_WeChatPay"//活动三和活动六 免费领取 没有支付
            )
    )


    const val SEARCH = "HomeP_click_Search"





    fun getPayUtEventName(activityName:String,payType :String):String{
        return eventMap[payType]?.get(activityName) ?: ""
    }

}