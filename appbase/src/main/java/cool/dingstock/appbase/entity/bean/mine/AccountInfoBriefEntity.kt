package cool.dingstock.appbase.entity.bean.mine

import com.google.gson.annotations.SerializedName

data class MineFollowEntity(
    @SerializedName(value = "users", alternate = ["list"])
    val users: MutableList<AccountInfoBriefEntity> = arrayListOf(),
    val nextKey: Long = 0

)


/**
 * 用户的简略信息
 */
data class AccountInfoBriefEntity(
    val nickName: String? = "",
    val id: String,
    val avatarUrl: String? = null,
    //是否认证
    val isVerified: Boolean = false,
    //是否关注
    var followed: Boolean = false,
    //粉丝数目
    var fansCount: Int = 0,
    var notFollow: Boolean = false,

    ///
    val briefIntro: String? = null,
    val state: String? = null,
    val blocks: List<String>? = null,
    val objectId: String? = null,

    //自定义字段
    var isStart: Boolean = false,
    var isEnd: Boolean = false,
    var isVip: Boolean = false,
    var avatarPendantId: String?,
    var avatarPendantUrl: String?
)


/**
 * im设置页推送是否开启
 */
data class IMNotificationSettingEntity(
    var receiveFavor: Boolean = false,
    var receiveFollow: Boolean = false,
    var receiveComment: Boolean = false,
    var receiveMessage: Boolean = false,
    var receiveStrangerMessage: Boolean = false
)