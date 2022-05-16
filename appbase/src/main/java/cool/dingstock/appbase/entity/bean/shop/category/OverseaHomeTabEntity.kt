package cool.dingstock.appbase.entity.bean.shop.category

import cool.dingstock.appbase.entity.bean.shop.ShopHomeIconGroupEntity
import cool.dingstock.appbase.entity.bean.shop.ShopStoreEntity

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/6/25 15:26
 * @Version:         1.1.0
 * @Description:
 */
data class OverseaHomeTabEntity(
		val serviceDescUrl: String? = "",
		val shopcartCount: Int? = 0,
		val searchPlaceholder: String = "",
		val currentTab: String? = "",
		val searchBgUrl: String? = "",
		val iconList: MutableList<ShopHomeIconGroupEntity>? = arrayListOf(),
		val shopList: MutableList<ShopStoreEntity>? = arrayListOf(),
		val nextKey: String? = null,
		val shopTabId: String,
		val shopTabList: MutableList<ShopTabEntity>? = arrayListOf()
)

data class ShopTabEntity(
		val id: String,
		val name: String
)

