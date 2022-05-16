package cool.dingstock.appbase.entity.bean.config;


data class RemindConfigEntity(
    var remindType: String? = "",
    var remindAt: ArrayList<Long>? = arrayListOf()
)

data class MonitorConfig(
    var version: Long?,
    var groups: List<MonitorBean>?
)
