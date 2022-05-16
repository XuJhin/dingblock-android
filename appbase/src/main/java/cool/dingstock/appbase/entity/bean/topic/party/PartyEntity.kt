package cool.dingstock.appbase.entity.bean.topic.party

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * 类名：PartyEntity
 * 包名：cool.dingstock.appbase.entity.bean.topic.party
 * 创建时间：2022/3/1 10:41 上午
 * 创建人： WhenYoung
 * 描述：
 **/
@Parcelize
data class PartyEntity(
    val id: String? = "",
    val createdAt: Long? = 0,
    val title: String? = "",
    val desc: String? = "",
    val icon: String? = "",
    val bgImgUrl: String? = "",
    val isExpired: Boolean? = false,
    val blocked: Boolean? = false,
) : Parcelable {
}