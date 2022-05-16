package cool.dingstock.appbase.entity.bean.price;








data class PriceSizeBean (

    /**
     * bid : [100,200,300,0]
     * ask : [100,200,300,0]
     */

    var bid  : List<Int>?,
    var ask  : List<Int>?
)
