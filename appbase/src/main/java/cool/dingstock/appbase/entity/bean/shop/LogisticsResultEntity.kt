package cool.dingstock.appbase.entity.bean.shop

data class LogisticsResultEntity(
        val positions: ArrayList<PositionsEntity>?,
        val addressee: LogisticsAddresseeEntity?,
        val state: String?,
        val sendTime: Long?,
        val receiveTime: Long?,
        val stages: ArrayList<StagesEntity>?,
        val orderInfo: LogisticsOrderInfoEntity?
) {


}

data class LogisticsAddresseeEntity(
        val id: String,
        val name: String,
        val mobile: String,
        val mobileZone: String,
        val province: String,
        val city: String,
        val district: String,
        val address: String,
) {

}

data class StagesEntity(
        var isNewestAddress: Boolean = false,//本地新加字段，用于判断是否是最新的物流站点
        val label: String,
        val expressName: String,
        val expressNo: String,
        val icon: String?,
        val items: ArrayList<StagesChildEntity>
)

data class StagesChildEntity(val info: String, val date: Long, val icon: String) {
    var isEnd = false

    //扩展参数
    var result: Any? = null
}

data class PositionsEntity(val name: String, val current: Boolean, val icon: String?) {
    var isStart = false
    var isEnd = false
    var selLeft = false
    var selRight = false
    var isComplete = false
}

data class LogisticsOrderInfoEntity(
        val orderId: String,
        val stateTxt: String,
        val expressName: String,
        val expressNo: String,
        val sku: LogisticsSkuEntity
)

data class LogisticsSkuEntity(
        val imageUrl: String,
        val goodsName: String,
        val currency: String,
        val currencySymbol: String,
        val price: String,
        val rmbPrice: String,
        val count: Int,
) {

}
