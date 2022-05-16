package cool.dingstock.appbase.entity.bean.calendar

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * 类名：SmsPersonInputEntity
 * 包名：cool.dingstock.appbase.entity.bean.calendar
 * 创建时间：2021/7/5 10:55 上午
 * 创建人： WhenYoung
 * 描述：
 **/
@Parcelize
data class SmsPersonInputEntity(
    val id: String,
    val bubble: String,
    val inputValue: String,
    val fieldName: String,
    val require: Boolean = false,
) :Parcelable{
    var value: String? = null
}