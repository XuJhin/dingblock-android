package cool.dingstock.appbase.entity.bean.shop.category

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/6/1 16:24
 * @Version:         1.1.0
 * @Description:
 */
data class ShopCategoryListEntity(
		val list: MutableList<ShopCategoryEntity>? = null
)

data class ShopCategoryEntity(
		val id: String? = null,
		val name: String? = null,
		val children: MutableList<ShopCategorySecondEntity>? = arrayListOf(),
		var isSelected: Boolean? = false,
)

data class ShopCategorySecondEntity(
		val id: String? = null,
		val name: String? = null,
		val appShowType: String? = null,
		val imageUrl: String? = null,
		val link: String? = null,
		val children: MutableList<ShopCategoryThirdEntity>? = arrayListOf()
)

data class ShopCategoryThirdEntity(
		val id: String? = null,
		val name: String? = null,
		val imageUrl: String? = null,
		val link: String? = null
)