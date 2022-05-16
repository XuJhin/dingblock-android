package cool.dingstock.appbase.entity.bean.config

data class ChannelBean (
    /**
     * id : 1qpcRtWYtO
     * name : Converse
     * restricted : false
     * iconUrl : https://dingstock.obs.cn-east-2.myhuaweicloud.com/brands/150x150/Converse.jpg
     */
    var id  : String?,
    var name  : String?,
    var restricted  : Boolean?,
    var iconUrl  : String?,
    var subscribed  : Boolean?,
    var feedCount  : Int?,
    var isSilent  : Boolean?,
    //本地
    var selected  : Boolean? = false
)
