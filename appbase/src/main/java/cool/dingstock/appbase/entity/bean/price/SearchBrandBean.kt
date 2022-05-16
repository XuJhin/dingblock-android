package cool.dingstock.appbase.entity.bean.price;



import cool.dingstock.appbase.entity.bean.home.HomeProduct;





data class SearchBrandBean (
    var name  : String?,
    var objectId  : String?,
    var categories  : List<HomeProduct>?
)
