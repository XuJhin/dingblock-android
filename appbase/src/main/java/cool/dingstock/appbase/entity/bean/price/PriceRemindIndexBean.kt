package cool.dingstock.appbase.entity.bean.price;








data class PriceRemindIndexBean (
    var product  : PriceProductBean?,
    var size  : String?,
    var price  : String?,
    var tintColor  : String?,
    var type  : String?,
    var objectId  : String?,
    var platforms  : List<PlatformBean>?
)
