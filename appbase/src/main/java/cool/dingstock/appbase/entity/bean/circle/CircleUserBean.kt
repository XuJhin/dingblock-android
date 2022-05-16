package cool.dingstock.appbase.entity.bean.circle

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CircleUserBean(
    /**
     * avatarUrl : https://dingstock-avatar.obs.cn-east-2.myhuaweicloud.com/ujnydyFxSh/1562413192.618855.jpg
     * nickName : lishen
     * objectId : ujnydyFxSh
     */
    var avatarUrl: String? = null,
    var nickName: String? = null,
    var objectId: String? = null,
    var briefIntro: String? = null,
    //1.5.0新增属性
    var id: String? = null,
    var avatar: String? = null,
    var isVerified: Boolean? = false,
    var isBlock: Boolean = false,
    var badges: MutableList<CircleUserBadgeBean> = arrayListOf(),
    var followed: Boolean? = false,
    var intro: String?,
    //2.9.0
    var isVip: Boolean? = false,
    //2.9.5
    var achievementIconUrl: String? = "",
    var achievementId: String? ="",
    var avatarPendantId: String? = null,
    var avatarPendantUrl: String? = null
) : Parcelable

@Parcelize
data class CircleUserBadgeBean(
    var title: String? = null,
    var iconUrl: String? = null
) : Parcelable