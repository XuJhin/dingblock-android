package cool.dingstock.appbase.entity.bean.calendar


/**
 * 类名：PriceListEntity
 * 包名：cool.dingstock.appbase.entity.bean.calendar
 * 创建时间：2021/7/16 10:03 上午
 * 创建人： WhenYoung
 * 描述：
 **/
data class PriceListResultEntity(val product:ProductPriceEntity) {
}

data class ProductPriceEntity(
    val id: String,
    val updatedAt: Long,
    val name: String,
    val sku: String,
    val price: String,
    val imageUrl: String,
    val channelInfo: ArrayList<PriceChannelInfo>,
    val listTitle: String,
    val priceInfoList: ArrayList<PriceInfoListEntity>,
)

data class PriceChannelInfo(val key: String, val icon: String)

data class PriceInfoListEntity(val size: String, val priceList: ArrayList<PriceInfoEntity>)

data class PriceInfoEntity(val key: String, val price: String)