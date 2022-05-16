package cool.dingstock.appbase.entity.bean.monitor

data class ChannelDetailBean(
    val channel: ChannelInfoEntity? = ChannelInfoEntity(),
    val nextKey: Long,
    val feeds: MutableList<MonitorProductBean> = arrayListOf()
)

data class LoadChannelDetail(
    val nextKey: Long,
    val feeds: MutableList<MonitorProductBean> = arrayListOf()
)

data class CateEntity(
    var name: String = "",
    var isSelect: Boolean = false,
    var isAll: Boolean = false
)

data class ChannelInfoEntity(
    var id: String = "",
    var createdAt: Long = 0,
    var groupId: String = "",
    var name: String = "",
    var iconUrl: String = "",
    var restricted: Boolean = false,
    var feedCount: Int = 0,
    var subscribed: Boolean = false,
    var subscribedAt: Long = 0,
    var isSilent: Boolean = false,
    var blocked: Boolean = false,
    var desc: String? = null,

    var customRuleEffective: Boolean = false, /* 自定义规则是否生效中 2.8.5 */
    var maintaining: Boolean = false,  /* 是否维护中 2.8.5 */
    var customRing: String? = "",  /* 自定义铃声 2.8.5 */

    var isOffline: Boolean? = false,//2.10.0
    var customCitiesCountStr: String? = "",  /* 2.10.0 */
    var customCateCountStr: String? = "",  /* 2.10.0 */
    var customCate: ArrayList<CateEntity> = arrayListOf(),  /* 2.10.0 */
)