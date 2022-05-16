package cool.dingstock.appbase.entity.bean.shop

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/6/1 21:09
 * @Version:         1.1.0
 * @Description:
 */
data class ShopGoodsEntity(
		val goodsName: String? = "",
		val type: String? = "",
		val skuId: String? = "",
		val imageUrl: String? = "",
		val currency: String? = "",
		val currencySymbol: String? = "",
		val discount: String? = "",
		val originPrice: String? = "",
		val promotionPrice: String? = "",
		val rmbOriginPrice: String? = "",
		val rmbPromotionPrice: String? = "",
		val spuId: String? = "",
		val stock: Int? = 0,
		val countryName: String? = "",
		val merchantName: String? = "",
		val sales: String? = "",
		val keyword: String? = "",
		val link: String? = ""
)
