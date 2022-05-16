package cool.dingstock.appbase.entity.bean.box

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/6/2 14:10
 * @Version:         1.1.0
 * @Description:
 */
data class BoxTabHomeInfoEntity(
	val serviceDescUrl: String? = "",
	val shopcartCount: Int? = 0,
	val searchPlaceholder: String = "",
	val tabList: MutableList<BoxHomeTabEntity>? = arrayListOf(),
	val currentTab: String? = "",
	val iconList: MutableList<BoxHomeIconGroupEntity>? = arrayListOf(),
	val list: MutableList<BoxGoodsEntity>? = arrayListOf(),
	val pageNum: Int? = 0,
)

data class BoxHomeIconGroupEntity(
		val type: String? = "",
		val list: MutableList<BoxIndexIconEntity>? = arrayListOf()
)

data class BoxHomeTabEntity(
		val title: String? = "",
		val key: String? = "",
		val type: String? = "",
		val link: String? = "",
)

data class BoxIndexGoods(
		val list: MutableList<BoxGoodsEntity>? = arrayListOf(),
		val nextKey: String?,
		val pageSize: String?
)