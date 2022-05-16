package cool.dingstock.appbase.entity.bean.pay;

import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeChatOrderData(
    @SerializedName(value = "appid")
    var appId: String?,
    @SerializedName(value = "partnerid")
    var partnerId: String?,
    @SerializedName(value = "package")
    var packageValue: String?,
    @SerializedName(value = "noncestr")
    var nonceStr: String?,
    @SerializedName(value = "timestamp")
    var timeStamp: String?,
    @SerializedName(value = "prepayid")
    var prepayId: String?,
    var sign: String?
) : Parcelable
