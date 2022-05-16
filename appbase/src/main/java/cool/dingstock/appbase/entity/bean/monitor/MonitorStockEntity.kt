package cool.dingstock.appbase.entity.bean.monitor

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MonitorStockEntity(
		val imageUrl: String? = "",
		val link: String? = "",
		val downloadLink: String? = "",
		val price: Double = 0.0,
		val title: String? = "",
		val total: String? = "",
		val skus: MutableList<SkuEntity> = arrayListOf(),
		val infos: MutableList<InfoEntity> = arrayListOf()) : Parcelable

@Parcelize
data class SkuEntity(
		val count: String? = "",
		val title: String? = ""
) : Parcelable

@Parcelize
data class InfoEntity(
		val title: String? = "",
		val value: String? = ""
) : Parcelable