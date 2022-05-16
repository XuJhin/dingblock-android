package cool.dingstock.appbase.entity.bean.config;







data class ConfigVersion (
    /**
     * iOSVersion : 1.0
     * androidVersion :
     * iOSForceUpdate : false
     * androidForceUpdate : false
     * iOSUpdateLink :
     * androidUpdateLink :
     */
    var iOSVersion  : String? =null,
    var iOSForceUpdate  : Boolean?=null,
    var iOSUpdateLink  : String?=null,

    var androidVersion  : String?=null,
    var androidForceUpdate  : Boolean?=null,
    var androidUpdateLink  : String?=null,
    var androidUpdateTip  : String?=null,
    var monitorVersion  : Long = 0
){
}
