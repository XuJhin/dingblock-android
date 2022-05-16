package cool.dingstock.appbase.entity.bean.monitor


/**
 * 类名：SubChannelResultEntity
 * 包名：cool.dingstock.appbase.entity.bean.monitor
 * 创建时间：2021/8/31 2:56 下午
 * 创建人： WhenYoung
 * 描述：
 **/
data class SubChannelResultEntity(
    val groups: ArrayList<SubChannelGroupEntity>,
    val devLogs: DevLogs
)

data class SubChannelGroupEntity(
    val id: String,
    val name: String,
    val isExpanded:Boolean? = true,
    val channels: ArrayList<SubChannelItemEntity>
)

data class SubChannelItemEntity(
    val id: String,
    val name: String,
    val iconUrl: String,
    var newFeedCount: Int,
    val customRuleEffective: Boolean, //自定义规则是否生效
    val maintaining: Boolean //是否维护中
)

data class DevLogs (
    val id: String,
    val createdAt: Long,
    val content: String
)
