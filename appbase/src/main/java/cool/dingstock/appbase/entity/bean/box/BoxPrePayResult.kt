package cool.dingstock.appbase.entity.bean.box

import com.google.gson.annotations.SerializedName


/**
 * 类名：ShopPrePayResult
 * 包名：cool.dingstock.appbase.entity.bean.shop
 * 创建时间：2021/6/21 5:58 下午
 * 创建人： WhenYoung
 * 描述：
 **/
data class BoxAliPayPrePayResult(val prepayParams: AliPayParams)

data class AliPayParams(
    val msgType: String,
    @SerializedName(value = "package")
    val packageName: String,
    val sign: String,
    val prepayid: String,
    val noncestr: String,
    val timestamp: String,
    val miniuser: String,
    val minipath: String,
    val appScheme: String
)

data class BoxWeChatPayPrePayResult(val prepayParams: WxPayParams)

data class WxPayParams(
    val miniuser: String,
    @SerializedName(value = "package")
    val packageValue: String,
    val minipath: String,
    val appid: String,
    val sign: String,
    val partnerid: String,
    val prepayid: String,
    val timeStamp: String,
    val nonceStr: String,
)