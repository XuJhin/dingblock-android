package cool.dingstock.appbase.entity.bean.im

data class HWMessageBean(
    val bId: String,
    val conversationType: String,
    val fromUserId: String,
    val id: String,
    val objectName: String,
    val sourceType: String,
    val tId: String,
    val targetId: String
)