package cool.dingstock.appbase.entity.bean.home.bp

/**
 *
 * "id":"",  // 订单编号
"goodsImg":"",// 商品图片
"title":"", // 商品名
"plat":"", // 下单平台
"skuNum":1, // 购买数量
"payPrice":1, // 订单金额
"commission":1,// 奖励金额
"createdAt":1, // 订单创建时间
"rewordStatus":"" // 奖励状态 审核中：waiting 发放中：processing 已发放：processed
 *
 * */
data class RewardRecordEntity(val id: String,
                              val goodsImg: String,
                              val title: String,
                              val plat: String,
                              val skuNum: Int,
                              val payPrice: Float,
                              val commission:Float,
                              val total:Float,
                              val createdAt: Long,
                              var rewordStatus: RewordStatus? = null,
){
    enum class RewordStatus(val str:String){
        waiting("审核中"),processing("奖励发放中"),processed("奖励已发放"),closed("未通过")
    }

}


data class RewardRecordsData(
        var list: List<RewardRecordEntity>,
        var nextKey: Long?)