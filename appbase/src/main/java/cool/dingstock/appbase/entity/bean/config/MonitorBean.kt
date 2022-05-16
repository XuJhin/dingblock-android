package cool.dingstock.appbase.entity.bean.config;





/**
 * id : cn
 * name : 中国
 * en : China
 * iconUrl : https://dingstock.obs.cn-east-2.myhuaweicloud.com/Country/CN.png
 * channels : [("id":"1qpcRtWYtO","name":"Converse","restricted":false,"iconUrl":"https://dingstock.obs.cn-east-2.myhuaweicloud.com/brands/150x150/Converse.jpg"),("id":"ltnzw7EXPE","name":"SNKRS","restricted":false,"iconUrl":"https://dingstock.obs.cn-east-2.myhuaweicloud.com/brands/150x150/SNKRS.jpg"),("id":"Hav6b4O2Xp","name":"Nike","restricted":false,"iconUrl":"https://dingstock.obs.cn-east-2.myhuaweicloud.com/brands/150x150/NIKE.png")]
 */
data class MonitorBean(var id  : String?=null,
                       var name  : String?=null,
                       var en  : String?=null,
                       var iconUrl  : String?=null,
                       var channels  : ArrayList<ChannelBean>?=null,
                       var fixed  : Boolean?=null){
    fun javaCopy():MonitorBean{
        return copy()
    }
}
