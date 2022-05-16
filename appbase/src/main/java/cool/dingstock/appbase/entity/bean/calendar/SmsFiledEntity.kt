package cool.dingstock.appbase.entity.bean.calendar

import cool.dingstock.appbase.entity.bean.home.HomeField


/**
 * 类名：SmsFiledEntity
 * 包名：cool.dingstock.appbase.entity.bean.calendar
 * 创建时间：2021/7/5 4:43 下午
 * 创建人： WhenYoung
 * 描述：
 **/
data class SmsFiledEntity(val key: String, val filedName:String,var value: String? = null,val homeField: HomeField,var needShow:Boolean = true) {
    val needNameSpace = false
}