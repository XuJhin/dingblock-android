package cool.dingstock.appbase.entity.bean.box


/**
 * 类名：BoxHomeIndexItemEntity
 * 包名：cool.dingstock.appbase.entity.bean.box
 * 创建时间：2021/8/3 11:20 上午
 * 创建人： WhenYoung
 * 描述：
 **/
data class BoxHomeIndexItemEntity(
    val id: String,
    val title: String,
    val icon: String,
    val bigImage: String,
    val count: Int,
    val price: Float,
    val level: String,
    val countList: ArrayList<PriceSaleEntity>,
    val hasCouponAvailable: Boolean? = null,
    val couponInfo: String? = null,
    val sellout: Boolean? = false, /*  2.9.7是否售罄 */
) {
    var isSelected = false

}


data class PriceSaleEntity(
    var count: Int,
    val label: String = "",
    val price: String,
    val discountInfo: String,
    val hasCouponAvailable: Boolean
) {
    var isSelected = false
}

/**
 * "id": "1234", /* 盲盒ID */
"icon": "http://icon.com/1.jpg", /* 盲盒图标 */
"bigImage": "http://icon.com/1.jpg", /* 盲盒大图 */
"title": "AJ潮盒", /* 名称 */
"count": 10, /* 剩余数量 */
"price": "100" /* 盲盒单价 */,
"level": "S" /* 级别 */
 *
 * */