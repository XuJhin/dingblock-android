package cool.dingstock.appbase.entity.bean.monitor

data class MonitorMenuBean(
		val header: String? = "",
		val isShowHeader: Boolean = true,
		val list: MutableList<MonitorMenuItemEntity> = arrayListOf()
) {
	/**
	 * 获取 当前menu中第一个选中的child
	 */
	fun selectedSubject(): MonitorMenuItemEntity? {
		if (list.isNullOrEmpty()) {
			return null
		}
		val filterList = list.filter { child -> child.isSelected }
		if (filterList.isNullOrEmpty()) {
			return null
		}
		return filterList[0]
	}
}

data class MonitorMenuItemEntity(
		val code: String? = "",
		val name: String? = "",
		val isEmpty: Boolean = false,
		var isSelected: Boolean = false,
		val type: String = "",
)