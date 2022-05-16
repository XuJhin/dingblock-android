package cool.dingstock.appbase.entity.bean.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * 类名：RecommendKolUserEntity
 * 包名：cool.dingstock.appbase.entity.bean.home
 * 创建时间：2021/10/11 9:54 上午
 * 创建人： WhenYoung
 * 描述：
 **/
data class RecommendKolUserEntity(
    val id: String,
    val createdAt: Long,
    val vipValidity: Long,
    val briefIntro: String,
    val nickName: String,
    val fansCount: Int,
    val avatarUrl: String,
    val isVerified: Boolean,
    val isVip: Boolean,
    val blocked: Boolean,
) {
    var followed = false
}

@Parcelize
data class HotRankItemEntity(
    val id: String? = "",
    val title: String? = "",
    val icon: String? = "",
): Parcelable
