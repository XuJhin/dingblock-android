package cool.dingstock.appbase.entity.bean.price;








data class PricesBean (
    /**
     * prefix : ￥
     * bid : [100,200,300]
     * ask : [100,200,300]
     */
    var prefix  : String?,
    var bid  : List<Int>?,
    var ask  : List<Int>?

)
