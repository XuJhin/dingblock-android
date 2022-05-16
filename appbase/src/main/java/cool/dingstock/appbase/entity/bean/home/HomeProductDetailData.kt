package cool.dingstock.appbase.entity.bean.home;

data class HomeProductDetailData(

    var product: HomeProduct?,

    var raffles: List<HomeProductDetail>?,

    var showPriceFab: Boolean = false

)
