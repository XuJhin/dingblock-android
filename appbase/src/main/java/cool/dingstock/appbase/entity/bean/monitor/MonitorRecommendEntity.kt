package cool.dingstock.appbase.entity.bean.monitor

data class MonitorRecommendEntity(
		val id: String = "",
		val createdAt: Long = 0,
		val name: String = "",
		val desc: String = "",
		val blocked: Boolean = false,
		val imageUrl: String = "",
		val channelList: MutableList<MonitorRecommendChannelEntity> = arrayListOf()
)

class MonitorDivider

data class MonitorRecommendChannelEntity(
		val desc: String = "",
		val id: String = "",
		val createdAt: Long = 0L,
		val groupId: String = "",
		val name: String = "",
		val subjectId: String = "",
		val objectId: String = "",
		val iconUrl: String = "",
		val restricted: Boolean = false,
		val feedCount: Int = 0,
		var subscribed: Boolean = false,
		val isSilent: Boolean = false,
		val isNew: Boolean = false,
		val isHot: Boolean = false,
		val blocked: Boolean = false,
		var isTail: Boolean? = false,
		var customRuleEffective: Boolean = false
)