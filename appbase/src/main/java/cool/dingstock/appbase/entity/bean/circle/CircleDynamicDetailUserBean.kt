package cool.dingstock.appbase.entity.bean.circle

import android.graphics.Bitmap
import cool.dingstock.appbase.entity.bean.mine.MedalStatus

data class CircleDynamicDetailUserBean(
    /**
     * nickName : _wang
     * avatarUrl : https://dingstock-avatar.obs.cn-east-2.myhuaweicloud.com/dingstockayu/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20190713180509.jpg
     * objectId : qtl8aeSQya
     */
    var nickName: String = "",
    var avatarUrl: String = "",
    var bigAvatarUrl: String = "",
    var objectId: String = "",
    var desc: String = "",
    var followed: Boolean = false,
    var fansCount: Int = 0,
    var followedCount: Int = 0,
    var talkCount: Int = 0,
    var favorCount: Int = 0,
    var isVerified: Boolean = false,
    var isBeBlocked: Boolean = false,
    var isBlock: Boolean = false,
    var isVip: Boolean = false,
    //新增属性 用于控制显示VIP布局
    var showVipLayout: Boolean = true,
    var badges: List<Badges> = arrayListOf(),
    //2.9.5
    var achievement: Medal? = null,
    var achievementIconUrl: String? = "",
    var achievementId: String? = "",
    var avatarPendantId: String?,
    var avatarPendantUrl: String?,
    var avatarPendantName: String?
)

//2.9.5
data class Medal(
    var name: String? = "",
    var achievementId: String? = "",
    var iconUrl: String? = ""
)

data class Badges(
    var title: String,
    var iconUrl: String
)

data class MedalEntity(
    var id: String? = "",// //需要根据外部id铆定
    var name: String? = "",
    var imageUrl: String? = "",
    var iconUrl: String? = "",
    var imageBWUrl: String? = "",
    var validity: Long? = 0,
    var validityStr: String? = "",
    var creditProductId: String? = "",//积分商品id 用于跳转购买
    var criteriaStr: String? = "",
    var status: String? = "",//领取状态 未获得/可领取/已领取/ unfulfilled receivable completed
    var buttonStr: String? = "",
    var createdAt: Long? = 0,
    var updatedAt: Long? = 0
) {
    val madelStatus = when (status) {
        MedalStatus.UNFULFILLED.value -> MedalStatus.UNFULFILLED
        MedalStatus.RECEIVABLE.value -> MedalStatus.RECEIVABLE
        MedalStatus.COMPLETED.value -> MedalStatus.COMPLETED
        MedalStatus.WEARING.value -> MedalStatus.WEARING
        else -> MedalStatus.UNFULFILLED
    }
}

data class MedalListEntity(
    val user: MedalPreViewUserEntity? = null,
    val achievement: ArrayList<MedalEntity>? = arrayListOf()
)

data class MedalPreViewUserEntity(
    val nickName: String? = "",
    val avatarUrl: String? = "",
    val isVip: Boolean = false,
)


data class IsHaveNewMedalEntity(
    val receivable: Boolean? = false
)

enum class MedalBtnStr(val value: String) {
    ALL_MEDAL("TA的全部勋章"),
    BUY_MEDAL("用积分购买勋章"),
    NOT_FINISH("未完成"),
    GET_MEDAL("领取"),
    SUIT_MEDAL("佩戴勋章"),
    UN_SUIT_MEDAL("取消佩戴")
}

data class BitmapsEntity(
    val firstBitmap: Bitmap?,
    val secondBitmap: Bitmap?,
)


