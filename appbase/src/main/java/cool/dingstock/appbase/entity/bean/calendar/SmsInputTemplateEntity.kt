package cool.dingstock.appbase.entity.bean.calendar


/**
 * 类名：s
 * 包名：cool.dingstock.appbase.entity.bean.calendar
 * 创建时间：2021/7/6 6:08 下午
 * 创建人： WhenYoung
 * 描述：
 **/
data class SmsInputTemplateEntity(
    val sizes: ArrayList<String>?,
    val basicInfo: ArrayList<SmsPersonInputEntity>,
    val otherInfo: ArrayList<SmsPersonInputEntity>
) {
}