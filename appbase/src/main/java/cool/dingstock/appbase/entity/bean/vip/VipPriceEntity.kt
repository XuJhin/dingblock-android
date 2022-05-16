package cool.dingstock.appbase.entity.bean.vip

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/21  10:59
 *
 *  "name": "12个月",
"price": 248,
"autoPrice": 348,
"discount":6.8
"month": 12,
"priceAverage": "20.75",
"iosId": "normalAnnual",
"androidId": "annualAndroid",
"iosAutoId": "annualAuto"
 */
data class VipPriceEntity(
    var name: String? = null,
    var price: Double? = null,
    var autoPrice: Double? = null,
    var discount: Double? = null,
    var originalPrice: Double? = null,
    var month: Int? = null,
    var priceAverage: Double? = null,
    var iosId: String? = null,
    var androidId: String? = null,
    var iosAutoId: String? = null,
    var presentsImage: String? = null,/* 2.9.8 新增会员赠送 */
    var presentsDetail: VipPrizeEntity? = null,/* 2.9.8 新增会员赠送 */
)

/* 2.9.8 新增会员赠送 */
data class VipPrizeEntity(
    var title: String? = null,
    var decs: String? = null,
    var qqImgUrl: String? = null,
    var wyImgUrl: String? = null,
)
