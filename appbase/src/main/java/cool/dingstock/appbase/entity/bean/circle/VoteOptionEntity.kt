package cool.dingstock.appbase.entity.bean.circle

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VoteOptionEntity(
        var title: String,
        var count: Int,
        var isVote: Boolean
) : Parcelable
