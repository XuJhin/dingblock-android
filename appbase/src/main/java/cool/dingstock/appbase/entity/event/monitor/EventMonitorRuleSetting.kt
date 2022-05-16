package cool.dingstock.appbase.entity.event.monitor

data class EventMonitorRuleSetting(
    val channelId: String,
    val keywordSetting: Int,
    val sizeSetting: Int
)

class EventRefreshMonitorOfflineCities
