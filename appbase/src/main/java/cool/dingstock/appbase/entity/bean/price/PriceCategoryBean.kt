package cool.dingstock.appbase.entity.bean.price;

import android.os.Parcel;
import android.os.Parcelable;
import kotlinx.parcelize.Parcelize


@Parcelize
data class PriceCategoryBean(

    var name  : String?,
    var imageUrl  : String?,
    var objectId  : String?


) : Parcelable
