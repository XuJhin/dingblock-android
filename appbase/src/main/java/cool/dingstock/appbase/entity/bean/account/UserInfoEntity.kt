package cool.dingstock.appbase.entity.bean.account

data class UserInfoEntity(
    val objectId: String,
    val nickName: String,
    val avatarUrl: String,
    val desc: String
)

data class NickNameEntity(
    val msg: String? = "",
    val nickName: String? = "",
)