package cool.dingstock.appbase.entity.bean.price;






data class PriceProductBean (
    /**
     * name : AJ6
     * imageUrl : https://dingstock.obs.cn-east-2.myhuaweicloud.com/extra/384664-060.png
     * priceRange : ￥100 - ￥200
     * recommendDesc : 1000人已设置提醒
     * objectId : test
     */

    var name  : String?,
    var imageUrl  : String?,
    var priceRange  : String?,
    var recommendDesc  : String?,
    var objectId  : String?,
    var priceOverview  : PriceOverviewBean?

){
    fun getLogoUrl():String?{
        if (null==priceOverview) {
            return ""
        }
        if (null==priceOverview?.platform)
            return ""
        return priceOverview?.platform?.logoUrl
    }

}
