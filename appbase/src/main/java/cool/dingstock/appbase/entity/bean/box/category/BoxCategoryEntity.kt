package cool.dingstock.appbase.entity.bean.box.category

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/6/1 16:24
 * @Version:         1.1.0
 * @Description:
 */
data class BoxCategoryListEntity(
		val list: MutableList<BoxCategoryEntity>? = null
)

data class BoxCategoryEntity(
	val id: String? = null,
	val name: String? = null,
	val children: MutableList<BoxCategorySecondEntity>? = arrayListOf(),
){
	var isSelected: Boolean? = false

}

data class BoxCategorySecondEntity(
		val id: String? = null,
		val name: String? = null,
		val appShowType: String? = null,
		val imageUrl: String? = null,
		val link: String? = null,
		val children: MutableList<BoxCategoryThirdEntity>? = arrayListOf()
)

data class BoxCategoryThirdEntity(
		val id: String? = null,
		val name: String? = null,
		val imageUrl: String? = null,
		val link: String? = null
)