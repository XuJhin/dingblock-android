package cool.dingstock.appbase.entity.bean.home;

data class HomeProductGroup(
    var header: String?,
    var products: List<HomeProduct>?,
    var brands: List<HomeBrandBean>?,
    var types: List<String>?
)
