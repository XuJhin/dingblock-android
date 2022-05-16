package cool.dingstock.appbase.entity.bean.mine

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/29  16:53
 */
data class VipPricesBean(
        var day:String?=null,
        var price:Double=0.0,
        var discount:Double?=null,
        var discountName:String?=null,
        var goodsId:String?=null
)