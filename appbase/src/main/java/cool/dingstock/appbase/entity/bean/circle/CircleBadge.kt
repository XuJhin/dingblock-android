package cool.dingstock.appbase.entity.bean.circle

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CircleBadge(
        /**
         * text : 出售
         */
        var color: String? = null,
        var text: String? = null)
    : Parcelable