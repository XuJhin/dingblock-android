package cool.dingstock.appbase.entity.bean.box

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/6/1 21:09
 * @Version:         1.1.0
 * @Description:
 */
data class BoxGoodsEntity(
		val goodsName: String? = "",
		val skuId: String? = "",
		val imageUrl: String? = "",
		val type:String? = "",
		val currency: String? = "",
		val currencySymbol: String? = "",
		val discount: String? = "",
		val coin:Int? = 0,
		val price:String? = "",
		var piece:String? = "",
		val labelType:String? = "",
		val spuId: String? = "",
		val stock: Int? = 0,
		val countryName: String? = "",
		val merchantName: String? = "",
		val sales: String? = "",
		val keyword: String? = "",
		val link: String? = ""
)
