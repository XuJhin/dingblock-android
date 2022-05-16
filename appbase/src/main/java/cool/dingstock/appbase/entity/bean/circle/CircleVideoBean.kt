package cool.dingstock.appbase.entity.bean.circle

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CircleVideoBean(
        var url: String? = null,
        var imageUrl: String? = null,
        var title: String? = null,
        var duration: Int? = null,
        var width: Float = 0f,
        var height: Float = 0f
) : Parcelable
