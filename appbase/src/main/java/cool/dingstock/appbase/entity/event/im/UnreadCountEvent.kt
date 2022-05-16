package cool.dingstock.appbase.entity.event.im

data class UnreadCountEvent(
    val unreadCount: Int
)

class UnreadNotificationEvent
class RefreshUnreadNotificationEvent
class RefreshMessageTabPointEvent

class RefreshHelperPointEvent

class ClickHelperPointEvent