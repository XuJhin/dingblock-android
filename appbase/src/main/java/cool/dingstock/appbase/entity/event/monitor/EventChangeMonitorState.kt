package cool.dingstock.appbase.entity.event.monitor

import cool.dingstock.appbase.entity.bean.monitor.ChannelInfoEntity

class EventChangeMonitorState(
    val channelId: String, val state: Boolean,
    val item: Any?,
)

class EventRefreshMonitorPage(val eventSource: MonitorEventSource)

class EventChannelFilter(
    val channelId: String?,
    val channelName: String?,
    val customRuleEffective: Boolean = false,
    val maintaining: Boolean = false,
    val isAll: Boolean = false
) {

}

enum class MonitorEventSource {
    RECOMMEND, SUBJECT, DETAIL, MANAGER
}