package cool.dingstock.appbase.entity.bean.price;



import cool.dingstock.lib_base.util.CollectionUtils;





data class PriceDetailItemBean (

    var sizeIndex  : Int=0,
    var sizeList  : List<String>?,
    var bidPriceList  : List<Int>?,
    var askPriceList  : List<Int>?,
    var platforms  : List<PlatformBean>?,
    //bid 是求购价 ask是平台价（默认是平台价）
    var isBid  : Boolean?,
    var maxPrice  : Int=0,
    var minPrice  : Int=0

){
    fun getSize(): String? {
        if (CollectionUtils.isEmpty(sizeList)) {
            return ""
        }
        return if (sizeIndex ?: 0 >= sizeList?.size ?: 0) {
            ""
        } else sizeList?.get( sizeIndex?:0 )
    }


}




