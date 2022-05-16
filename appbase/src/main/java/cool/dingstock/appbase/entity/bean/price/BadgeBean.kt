package cool.dingstock.appbase.entity.bean.price;

import android.os.Parcel;
import android.os.Parcelable;
import kotlinx.parcelize.Parcelize


@Parcelize
data class BadgeBean (
    /**
     * link :
     * iconUrl :
     */
    var link  : String?,
    var iconUrl  : String?


) : Parcelable
