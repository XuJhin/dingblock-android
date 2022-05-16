package cool.dingstock.appbase.entity.bean.box

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/6/1 20:44
 * @Version:         1.1.0
 * @Description:
 */
data class BoxStoreListEntity(
	val list: MutableList<BoxStoreEntity>? = arrayListOf(),
	val nextKey: String? = ""
)

data class BoxStoreEntity(
	val name: String? = "",
	val country: String? = "",
	val hot: Float? = 0f,
	val background: String? = "",
	val countryIcon: String? = "",
	val labels: MutableList<String>? = arrayListOf(),
	val logo: String? = "",
	val desc: String? = "",
	val link: String? = "",
	val skus: MutableList<BoxStoreSkuEntity>? = arrayListOf(),
	val tips: MutableList<BoxStoreTipEntity>? = arrayListOf(),
	var expanded: Boolean = false
)

data class BoxStoreSkuEntity(
		val skuId: String? = "",
		val image: String? = "",
		val currency: String? = "",
		val price: String? = "",
		val currencySymbol: String? = ""
)

data class BoxStoreTipEntity(
		val label: String? = "",
		val title: String? = "",
)