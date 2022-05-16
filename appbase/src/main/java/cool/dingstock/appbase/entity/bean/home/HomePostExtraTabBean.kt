package cool.dingstock.appbase.entity.bean.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomePostExtraTabBean (
    var title  : String?,
    var type  : String?,
    var objectId  : String?,
    var link  : String?
) : Parcelable
