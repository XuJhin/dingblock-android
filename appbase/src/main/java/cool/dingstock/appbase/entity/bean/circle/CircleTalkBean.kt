package cool.dingstock.appbase.entity.bean.circle

import android.os.Parcelable
import cool.dingstock.appbase.entity.bean.topic.party.PartyEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class CircleTalkBean(
    var createdAt: Long = 0,
    var desc: String? = null,
    var id: String? = null,
    var userId: String? = null,
    var imageUrl: String? = null,
    var name: String? = null,
    //2.9.8
    var activity: PartyEntity? = null
) : Parcelable