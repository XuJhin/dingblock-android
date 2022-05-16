package cool.dingstock.appbase.entity.bean.setting;






data class SettingItemBean (

    /**
     * name : 关于盯链
     * type : aboutUs
     * actionType : link
     * value :
     */

    var name  : String?,
    var type  : String?,
    var actionType  : String?,
    var value  : String?,
    var desc  : String?,
    var resId  : String?,


    var switchOpen  : Boolean =false
)
