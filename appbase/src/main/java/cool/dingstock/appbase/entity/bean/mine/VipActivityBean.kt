package cool.dingstock.appbase.entity.bean.mine

import cool.dingstock.appbase.entity.bean.home.HomeBanner

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/29  18:12
 */
data class VipActivityBean (
        var prices:List<VipPricesBean>?=null,
        var banners:List<HomeBanner>?=null,
        var protocol:String?=null
)