package cool.dingstock.appbase.entity.bean.home

import android.os.Parcelable
import androidx.annotation.IntegerRes
import cool.dingstock.appbase.entity.bean.price.PriceCategoryBean
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeBrandBean(
	var imageUrl: String? = null,
	var name: String? = null,
	var id: String? = null,
	var objectId:String? = null,
	var categories: List<PriceCategoryBean>? = null,
	var type: String? = null,
		//本地
	@IntegerRes
		var imageRes: Int = 0,
	var isSelected: Boolean = true
) : Parcelable


data class HomeTypeBean(
	val name: String,
	var selected: Boolean = true
)