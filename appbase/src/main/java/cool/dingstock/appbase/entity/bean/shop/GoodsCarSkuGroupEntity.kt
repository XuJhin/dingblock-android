package cool.dingstock.appbase.entity.bean.shop

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class GoodsCarResultEntity(val skuGroups: ArrayList<GoodsCarSkuGroupEntity>) {

}

data class GoodsCarSkuGroupEntity(
    val skus: ArrayList<GoodsCarSkuItemEntity>,
    val expressFeeInfo: String?,
    val merchant: GoodsCarMerchantEntity,
) {
    //价格信息
    var priceEntity: GoodsCarPriceEntity? = null
    var selected: Boolean = false


}

data class GoodsCarMerchantEntity(
    val id: String,
    val country: String,
    val name: String,
    val nameCN: String,
    val countryIcon: String
)

data class GoodsCarSkuItemEntity(
    val skuId: String,
    var count: Int,
    val goodsName: String,
    val goodsNameCN: String,
    val attrString: String,
    val imageUrl: String,
    val currency: String,
    val currencySymbol: String,
    val discount: String,
    val originPrice: String,
    val promotionPrice: String,
    val rmbOriginPrice: String,
    val rmbPromotionPrice: String,
    val spuId: String,
    val stock: Int? = 0,
) {
    var selected: Boolean = false

}

data class GoodsCarPriceResultEntity(
    val skuGroups: ArrayList<GoodsCarPriceEntity>,
    val payFee: String,

    val rmbPayFee: String,
    val currency: String,
    val currencySymbol: String

)

data class GoodsCarPriceEntity(
    val merchantId: String,
    val currency: String,
    val currencySymbol: String,
    val payFee: String,
    val rmbPayFee: String,
    val feeList: ArrayList<GoodsCarFeeEntity>,
)

data class GoodsCarFeeEntity(
    val title: String,
    val value: String,
    val type: String,
    val link: String? = null
) {

}

@Parcelize
data class GoodsCarSelSkuEntity(val skuId: String, var count: Int) : Parcelable {

}






