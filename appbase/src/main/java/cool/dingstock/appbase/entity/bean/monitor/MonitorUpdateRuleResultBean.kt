package cool.dingstock.appbase.entity.bean.monitor

data class MonitorUpdateRuleResultBean(
        val blocked: Boolean,
        val blockedBy: String,
        val channelId: String,
        val createdAt: Long,
        val deviceId: String,
        val id: String,
        val keywords: List<String>,
        val ringId: String,
        val sizes: List<String>,
        val tag: Int,
        val updatedAt: Long,
        val userId: String
)


data class MonitorDisturbTimeSetBean(
        val isSilent: Boolean,
        val startHour: Int,
        val endHour: Int,
        val blocked: Boolean)