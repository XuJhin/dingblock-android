package cool.dingstock.appbase.entity.bean.score

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScoreExchangeItemEntity(
		val id: String? = "",
		val imageUrl: String? = "",
		val name: String? = "",
		val cost: Int? = 0,
		val desc: String? = "",
		val soldOut: Boolean? = false,
) : Parcelable
