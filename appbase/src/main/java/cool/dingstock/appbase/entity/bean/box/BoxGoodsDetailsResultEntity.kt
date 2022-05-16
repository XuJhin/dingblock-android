package cool.dingstock.appbase.entity.bean.box

data class BoxGoodsDetailsResultEntity(
        val tip: TipEntity?,
        /* 商户信息 */
        val merchant: BoxGoodsMerchantDetails?,
        /* 规格选项 */
        var attributes: ArrayList<BoxGoodsAttributesEntity>,
        /* 商品属性 */
        var properties: ArrayList<BoxPropertiesEntity>,
        /* 委托记录条数 */
        val orderTotal: Int?,
        var sku: BoxGoodsDetailsSKUEntity,
        /* 图标列表 */
        var iconList: ArrayList<BoxGoodsDetailsActionEntity>?,
        var link: String?,
        val brandInfo: BoxBrandInfoEntity?,
        var spuStock: Int,//库存
        var myPieceCount: String,
        var myCoinCount: String,
        var myPieceCountInt: Int,
        var myCoinCountInt: Int,
        var hideInApp: Boolean? = false,

        ) {
}

data class TipEntity(val icon: String, val content: String, val link: String)

data class BoxGoodsMerchantDetails(
        val country: String,
        val countryIcon: String,
        val name: String,
        val nameCN: String,
        val url: String
)

data class BoxGoodsAttributesEntity(
        val key: String,
        val title: String,
        val tip: String?,
        val tipLink: String?,
        val options: ArrayList<BoxGoodsOptionsEntity>,

        )

data class BoxGoodsOptionsEntity(
        val value: String,
        val available: Boolean = false,
        var selected: Boolean = false,
)

data class BoxPropertiesEntity(
        val title: String,
        val value: String
)

data class BoxGoodsDetailsSKUEntity(
        val id: String,
        val spuId: String,
//    val attributes:ArrayList<>
        val goodsName: String,
        val goodsNameCN: String,
        val desc: String,
        val bannerImages: ArrayList<BoxGoodsImageEntity>,
        val images: ArrayList<BoxGoodsImageEntity>,
        val discount: String,/* 折扣 */
        val currency: String,/* 货币代码 */
        /* 库存 */
        val stock: Int,
        /* 原商品地址，此地址不符合要求时使用商户地址（服务端处理） */
        val url: String,
        val price: Float,
        val piece: Int,
        val pieceName: String,
        val coin: Int,
        val myPieceCount: String,
        val myCoinCount: String,
) {

}

data class BoxBrandInfoEntity(
        val icon: String,
        val name: String,
        val desc: String,
)

data class BoxGoodsDetailsActionEntity(
        val icon: String,
        val title: String,
        val link: String,
) {


}


data class BoxGoodsImageEntity(val url: String) {

}

data class BoxUpdatePackageResultEntity(
        val bagCount: Int,
        val coin: Int,
        val leftTryTimes: Int,
        val leftFreeSplitTimes: Int
) {

}