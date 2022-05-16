package cool.dingstock.appbase.entity.bean.monitor

data class UserSubscribeBean(
		val data: List<UserSubscribeItemBean> = arrayListOf(),
		val title: String = "",
		val type: String = ""
)

data class Location(
		val latitude: Double = 0.0,
		val longitude: Double = 0.0
)

data class UserSubscribeItemBean(
		/**
		 * id
		 */
		val id: String = "",
		/**
		 * 名称
		 */
		val name: String = "",
		@Deprecated("使用id代替")
		val objectId: String = "",
		val blocked: Boolean = false,
		/**
		 * 发售数目
		 */
		val raffleCount: Int = 0,
		/**
		 * 是否订阅
		 */
		val subscribed: Boolean = false,
		//以下参数为地区类型独有
		val group: String = "",
		/**
		 * 片区类型 如：西南
		 */
		val district: String = "",
		//以下参数为频道类型独有
		val category: String = "",
		/**
		 *频道图标
		 */
		val iconUrl: String = "",
		val isSilent: Boolean = false,
		val location: Location = Location(0.0, 0.0),
		val restricted: Boolean = false
) {
	fun filter(): Any {
		if (group != "") {
			return RegionChannelBean(
					name = name,
					group = group,
					district = district,
					objectId = id,
					originalGroup = "",
					subscribed = subscribed,
			)
		} else {
			return MonitorRecommendChannelEntity(
					id = id,
					objectId = id,
					name = name,
					iconUrl = iconUrl,
					restricted = restricted,
					subscribed = subscribed,
					isSilent = isSilent,
					blocked = blocked
			)
		}
	}
}