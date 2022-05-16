package cool.dingstock.appbase.entity.bean.im

data class RelationBean(
    val fansIds: List<String>?,
    val followIds: List<String>?,
    val receivedFromStrangerIds: List<String>?,
    val sentToStrangerIds: List<String>?,
    val whiteList: List<String>?
)