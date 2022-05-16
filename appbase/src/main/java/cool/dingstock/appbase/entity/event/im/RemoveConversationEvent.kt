package cool.dingstock.appbase.entity.event.im

data class RemoveConversationEvent(
    val conversationType: Int,
    val targetId: String,
    val tagId: String,
)
