package cool.dingstock.appbase.entity.bean.shop


/**
 * 类名：CheckStockResultEntity
 * 包名：cool.dingstock.appbase.entity.bean.shop
 * 创建时间：2021/6/21 11:34 上午
 * 创建人： WhenYoung
 * 描述：
 **/
data class CheckStockResultEntity(val ok: Boolean, val skusNotAvailable: ArrayList<NoStockEntity>) {
}

data class NoStockEntity(val skuId: String, val imageUrl: String) {

}