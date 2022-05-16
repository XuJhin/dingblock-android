package cool.dingstock.appbase.entity.bean.calendar

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * 类名：SmsPersonEntity
 * 包名：cool.dingstock.appbase.entity.bean.calendar
 * 创建时间：2021/7/2 11:48 上午
 * 创建人： WhenYoung
 * 描述：
 **/
@Parcelize
data class SmsPersonEntity(val id: String, var name: String, val desc: HashMap<String, String?>?) :
    Parcelable {
    @Transient
    var  selected: Boolean  = false
}