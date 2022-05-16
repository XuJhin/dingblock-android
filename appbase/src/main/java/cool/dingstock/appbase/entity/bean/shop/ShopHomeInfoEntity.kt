package cool.dingstock.appbase.entity.bean.shop

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/6/2 14:10
 * @Version:         1.1.0
 * @Description:
 */
data class ShopHomeInfoEntity(
		val serviceDescUrl: String? = "",
		val shopcartCount: Int? = 0,
		val searchPlaceholder: String = "",
		val tabList: MutableList<ShopHomeTabEntity>? = arrayListOf(),
		val currentTab: String? = "",
		val iconList: MutableList<ShopHomeIconGroupEntity>? = arrayListOf(),
		val skuList: MutableList<ShopGoodsEntity>? = arrayListOf(),
		val nextKey: String? = null,
)

data class ShopHomeIconGroupEntity(
		val type: String? = "",
		val list: MutableList<ShopIndexIconEntity>? = arrayListOf()
)

data class ShopHomeTabEntity(
		val title: String? = "",
		val key: String? = "",
		val type: String? = "",
		val link: String? = "",
)

data class ShopIndexGoods(
		val list: MutableList<ShopGoodsEntity>? = arrayListOf(),
		val nextKey: String?,
		val pageSize: String?
)