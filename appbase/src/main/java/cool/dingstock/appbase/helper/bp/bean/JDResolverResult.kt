package cool.dingstock.appbase.helper.bp.bean

import cool.dingstock.appbase.entity.bean.home.bp.GoodDetailEntity


/**
 * 类名：JDResolverResult
 * 包名：cool.dingstock.appbase.entity.bean.home.bp.local
 * 创建时间：2021/9/23 5:39 下午
 * 创建人： WhenYoung
 * 描述：
 **/
data class JDResolverResult(val itemId:String?,val goodsDetail:GoodDetailEntity?,val priceReq:PriceReqEntity?) {



}

data class PriceReqEntity(val url:String?,val headers:Map<String,String>?){

}