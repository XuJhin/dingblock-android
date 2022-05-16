package cool.dingstock.appbase.entity.bean.monitor

data class MonitorSaveRuleBean(
    val channelId: String,
    val keywords: List<String>,
    val sizes: List<String>,
)
