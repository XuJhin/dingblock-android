package cool.dingstock.appbase.entity.bean.shop


/**
 * 类名：ShopPrePayResult
 * 包名：cool.dingstock.appbase.entity.bean.shop
 * 创建时间：2021/6/21 5:58 下午
 * 创建人： WhenYoung
 * 描述：
 **/
data class ShopAliPayPrePayResult(val prepayParams: String) {
}
data class ShopWeChatResultOutEntity(val prepayParams:ShopWeChatPayPrePayResult)

data class ShopWeChatPayPrePayResult(
    val appid: String,
    val nonceStr: String,
    val order_no: String,
    val packageValue: String,
    val partnerId: String,
    val prepayId: String,
    val sign: String,
    val timeStamp: String,
) {
}