package cool.dingstock.appbase.entity.bean.price;






data class PriceOverviewBean (
    /**
     * platform : ("logoUrl":"https://dingstock.obs.cn-east-2.myhuaweicloud.com/brands/150x150/nike.jpg","objectId":"nice")
     * tintColor : #FF0000
     * percentage : +12%
     * price : ￥1234
     * platformInfo : $1800[45]
     */
    var platform  : PricePlatformBean?,
    var tintColor  : String?,
    var percentage  : String?,
    var price  : String?,
    var platformInfo  : String?

)
