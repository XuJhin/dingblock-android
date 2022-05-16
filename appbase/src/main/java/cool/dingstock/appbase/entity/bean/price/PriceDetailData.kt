package cool.dingstock.appbase.entity.bean.price;



import cool.dingstock.appbase.entity.bean.home.HomeProduct;
import cool.dingstock.lib_base.util.CollectionUtils;





data class PriceDetailData (
    var platforms  : List<PlatformBean>?,
    var sizeOptions  : List<SizeOptionBean>?,
    var sizes  : List<PriceSizeBean>?,
    var ads  : AdsData?,
    var product  : HomeProduct?,
    var graphIntervals  : List<Int>?,
    var qrcodeLink  : String?
)
