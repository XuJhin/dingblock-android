package cool.dingstock.appbase.entity.bean.monitor

data class MonitorCenterRegions(
        val id: String = "",
        val name: String = "",
        val raffleCount: Int = 0,
        val snapshotImageUrl: String = "",
        //本地字段
        var isAdd: Boolean = false
)
