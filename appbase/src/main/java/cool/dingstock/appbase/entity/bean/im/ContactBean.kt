package cool.dingstock.appbase.entity.bean.im

data class ContactBean(
    val operation: String,
    val sourceUserId: String,
    val targetUserId: String
)