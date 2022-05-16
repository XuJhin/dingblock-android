package cool.dingstock.appbase.entity.bean.home;

import android.os.Parcel;
import android.os.Parcelable;
import cool.dingstock.appbase.entity.bean.calendar.SmsFiledEntity
import kotlinx.parcelize.Parcelize


@Parcelize
data class HomeField(
    var name: String?,
    var tip: String?,
    var suffix: String?,
    var type: String?,
    var key: String?,
    var needSpace: Boolean = false,
    var placeholder: String?

) : Parcelable{
    var value: SmsFiledEntity? = null
}
