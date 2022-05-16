package cool.dingstock.appbase.entity.bean.circle

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CityBean(
    val city: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
): Parcelable