package cool.dingstock.appbase.entity.bean.monitor


data class MonitorLogEntity(
        var nextKey: Long,
        var list: List<MonitorLogBean> = arrayListOf()
)

data class MonitorLogBean(
        var content: String?,
        var avatarUrl: String?,
        var createdAt: Long
)