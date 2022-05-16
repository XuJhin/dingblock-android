package cool.dingstock.appbase.entity.bean.home;

import android.os.Parcel;
import android.os.Parcelable;
import kotlinx.parcelize.Parcelize


@Parcelize
data class HomeCategoryGroupBean  (
    var name  : String?,
    var format  : String?,
    var objectId  : String?


) : Parcelable
