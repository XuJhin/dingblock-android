package cool.dingstock.appbase.entity.bean.shop

data class GoodsDetailsResultEntity(
    /* 商户信息 */
    val merchant: GoodsMerchantDetails?,
    /* 规格选项 */
    var attributes: ArrayList<GoodsAttributesEntity>,
    /* 商品属性 */
    var properties: ArrayList<PropertiesEntity>,
    /* 委托记录条数 */
    val orderTotal: Int?,
    /* 最近四条委托记录 */
    val orderList: ArrayList<DelegationRecordUserEntity>?,
    var sku: GoodsDetailsSKUEntity,
    /* 图标列表 */
    var iconList: ArrayList<GoodsDetailsActionEntity>?,
    var link: String?,
    var spuStock:Int,//库存
) {
}

data class GoodsMerchantDetails(
    val country: String,
    val countryIcon: String,
    val name: String,
    val nameCN: String,
    val url: String
)

data class GoodsAttributesEntity(
    val key: String,
    val title: String,
    val tip: String?,
    val tipLink: String?,
    val options: ArrayList<GoodsOptionsEntity>,

    )

data class GoodsOptionsEntity(
    val value: String,
    val available: Boolean = false,
    var selected: Boolean = false,
)

data class PropertiesEntity(
    val title: String,
    val value: String
)

data class GoodsDetailsSKUEntity(
    val id: String,
    val spuId: String,
//    val attributes:ArrayList<>
    val goodsName: String,
    val goodsNameCN: String,
    val desc: String,
    val descCN: String,/*中文介绍*/
    val bannerImages: ArrayList<GoodsImageEntity>,
    val images: ArrayList<GoodsImageEntity>,
    val discount: String,/* 折扣 */
    val currency: String,/* 货币代码 */
    /* 货币符号 */
    val currencySymbol: String,
    /* 原价 */
    val originPrice: String,
    /* 优惠价 */
    val promotionPrice: String,
    /* 人民币原价 */
    val rmbOriginPrice: String,
    /* 人民币优惠价  */
    val rmbPromotionPrice: String,
    /* 流通价格 */
    val marketPrice: String?,
    /* 库存 */
    val stock: Int,
    /* 原商品地址，此地址不符合要求时使用商户地址（服务端处理） */
    val url: String,

    ) {

}

data class GoodsDetailsActionEntity(
    val icon: String,
    val title: String,
    val link: String,
) {


}


data class GoodsImageEntity(val url: String) {

}

data class UpdateShopResultEntity(val shopcartCount: Int) {

}