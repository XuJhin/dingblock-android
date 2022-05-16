package cool.dingstock.appbase.entity.bean.shop

/**
 * @author wangjiang
 *  CreateAt Time 2021/6/2  11:32
 */

data class OrderDetailEntity(
        val id: String,   /* 订单号 */
        val merchant: ShopEntity, /* 商户信息 */
        val skus: List<GoodsInOrderListEntity>,/* 商户下的sku列表 */
        val currency: String,  /* 货币代码 */
        val feeList: List<GoodCostEntity>, /* 计费列表 */
        val addressee: OrderGetGoodAddressEntity, /* 收货地址 */

        val remark: String, /* 订单备注 */
        val orderFee: String,/* 人民币订单金额 */
        val date: String,/* 展示时间 */
        val cancelWhen: String,/* 指定时间后订单未支付就会取消订单（未付款时返回） */
        val orderState: String,/* 订单状态 */
        val orderStateTxt: String,/* 订单状态文字 */
        val tip: String,/* 订单状态下面的提示文字 */
        val orderTime: String,/* 下单时间 */
        val payTime: String,/* 支付时间 */
        val agentOrderTime: String,/* 代下单时间 */

        val sendTime: String,/* 发货时间 */
        val cancelTime: String,/* 取消时间 */
        val completeTime: String,/* 完成时间 */

//val feeList:List<GoodCostEntity>/* 总计计费列表 */
)


data class GoodCostEntity(
        val title: String, /* 费用标题 */
        val value: String,/* 费用金额 */
        val type: String/* 费用类型 */
)

data class OrderGetGoodAddressEntity(
        var id: String,/* 地址ID */
        var name: String,/* 名字 */
        var mobile: String,/* 手机号 */
        var mobileZone: String,/* 手机国家代码 */
        var province: String,/* 省 */
        var city: String,/* 市 */
        var district: String,/* 区 */
        var address: String/* 详细地址 */
)
