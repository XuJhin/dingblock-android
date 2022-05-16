package cool.dingstock.appbase.entity.bean.circle

import android.os.Parcelable
import android.text.TextUtils
import kotlinx.parcelize.Parcelize
@Parcelize
data class CircleLinkBean(
        var link: String? = null,
        var title: String? = null,
        var imageUrl: String? = null,
        //1.5.0新增
        var type: String? = null,
        var duration: Int? = null
) : Parcelable {
    fun autoSupplement() {
        if (TextUtils.isEmpty(title) && !TextUtils.isEmpty(link)) {
            title = link
        }
    }

    fun isLegal(): Boolean{
        return !TextUtils.isEmpty(title) && !TextUtils.isEmpty(link)
    }

}


