package cool.dingstock.appbase.entity.bean.box

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
data class BoxSearchResultEntity(
		val pageNum: Int? = 0,
		val keyWord: String? = "",
		val list: MutableList<BoxGoodsEntity>? = arrayListOf(),
		val tab: BoxUiTabEntity?
)

data class BoxUiTabEntity(
	val sort: MutableList<BoxUiSortEntity> = arrayListOf(),
	val filter: MutableList<BoxAttributesEntity> = arrayListOf()
)

data class BoxUiSortEntity(
		val id: String?,
		val name: String?,
		val type: String?
)

@Parcelize
data class BoxAttributesEntity(
		var id: String? = "",
		var name: String? = "",
		var list: ArrayList<String>? = arrayListOf()
) : Parcelable

@Parcelize
data class BoxSearchFilterEntity(
		val name: String,
		val groupId: String,
		var selected: Boolean = false,
		val forbidClick: Boolean = false
) : Parcelable

data class BoxFilterRequestEntity(
		var id: String? = "",
		var list: MutableList<String>? = arrayListOf()
)