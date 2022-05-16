package cool.dingstock.appbase.entity.bean.monitor

data class MonitorRuleChannelBean(
    val list: List<ChannelBean>,
    val nextKey: Long
)
data class ChannelBean(
    val blocked: Boolean,
    val blockedBy: String,
    val createdAt: Long,
    val feedCount: Int,
    val groupId: String,
    val iconUrl: String,
    val id: String,
    val isFreePush: Boolean,
    val isSilent: Boolean,
    val isTrial: Boolean,
    var keywordsSetting: Int,
    val maintaining: Boolean,
    val name: String,
    val newFeedCount: Int,
    val objectId: String,
    val restricted: Boolean,
    val sizeSupport: Boolean,
    val sizeSupportNow: Boolean,
    var sizesSetting: Int,
    val updatedAt: Long,
    val weight: Int
)