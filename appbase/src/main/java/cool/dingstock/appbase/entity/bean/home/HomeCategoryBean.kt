package cool.dingstock.appbase.entity.bean.home;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class HomeCategoryBean(
    var name  : String?=null,
    var type  : String?=null,
    @SerializedName("objectId",alternate = ["id"])
    var objectId  : String?=null,
    var link  : String?=null,
    var iconUrl:String?=null,
    var tip:String? = null
) : Parcelable
