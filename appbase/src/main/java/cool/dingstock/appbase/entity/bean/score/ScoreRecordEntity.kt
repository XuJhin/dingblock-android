package cool.dingstock.appbase.entity.bean.score

data class ScoreRecordEntity(
        val id: String,
        val eventId: String,
        val createdAt: Long,
        val eventName: String,
        val amount: Int,
        val buttonStr: String,
        val code: String,
        val orderSource: String,// 订单来源
        val consignee: String,// 收货人
        val consignPhone: String, // 收货电话
        val address: String,// 收货地址
        val expressCo: String,// 快递公司
        val expressId: String,// 快递单号
        val orderStatus: String? = null,// 订单状态 sent/prepare 已发出/待发出
        val title: String,
        val subTitle: String,
        val detailButtonStr: String,
)