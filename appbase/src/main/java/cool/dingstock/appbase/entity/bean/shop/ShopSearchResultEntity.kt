package cool.dingstock.appbase.entity.bean.shop

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/6/3 17:27
 * @Version:         1.1.0
 * @Description:
 */
data class ShopSearchResultEntity(
		val pageNum: Int? = 0,
		val keyWord: String? = "",
		val list: MutableList<ShopGoodsEntity>? = arrayListOf(),
		val tab: UiTabEntity?
)

data class UiTabEntity(
		val sort: MutableList<UiSortEntity> = arrayListOf(),
		val filter: MutableList<AttributesEntity> = arrayListOf()
)

data class UiSortEntity(
		val id: String?,
		val name: String?,
		val type: String?
)

@Parcelize
data class AttributesEntity(
		var id: String? = "",
		var name: String? = "",
		var list: MutableList<String>? = arrayListOf()
) : Parcelable

@Parcelize
data class SearchFilterEntity(
		val name: String,
		val groupId: String,
		var selected: Boolean = false,
		val forbidClick: Boolean = false
) : Parcelable

data class FilterRequestEntity(
		var id: String? = "",
		var list: MutableList<String>? = arrayListOf()
)