package cool.dingstock.appbase.entity.bean.box

data class BoxCouponSearchBean(
    val count: Int?,
    val discountInfo: String?,
    val discountInfoPrefix: String?,
    val discountInfoSuffix: String?,
    val expireDetail: String?,
    val limitInfo: String?,
    val name: String?,
    val shouldPopup: Boolean?,
    val type: String?
)