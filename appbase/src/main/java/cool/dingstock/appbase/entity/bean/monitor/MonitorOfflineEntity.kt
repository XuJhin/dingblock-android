package cool.dingstock.appbase.entity.bean.monitor

/**
 * 线下地区
 */
data class MonitorOfflineEntity(
		val title: String,
		val type: String,
		val data: MutableList<RegionChannelBean> = arrayListOf()
)

/**
 * 监控 频道
 */
data class MonitorGroupEntity(
		val title: String? = "",
		val type: String? = "",
		val data: MutableList<MonitorRecommendChannelEntity> = arrayListOf()
)

data class ChannelHeaderEntity(val title: String? = "",
							   val type: String? = "")

data class GroupChannelEntity(
		val id: String = "",
		val createdAt: Long = 0,
		val groupId: String = "",
		val name: String = "",
		val iconUrl: String = "",
		val restricted: Boolean = false,
		val feedCount: Int = 0,
		var subscribed: Boolean = false,
		val isSilent: Boolean = false,
		val blocked: Boolean = false
)

/**
 * 监控详细信息
 *
 * @param sectionName 用于分组的section,方便定位滑动位置
 */
data class RegionChannelBean(
		var sectionName: String = "",
		val name: String = "",
		val type: String = "",
		val hot: String = "",
		val district: String = "",
		val group: String = "",
		val objectId: String = "",
		val originalGroup: String = "",
		var subscribed: Boolean
)

data class UserSubscribed(
		val data: MutableList<GroupChannelEntity>
)