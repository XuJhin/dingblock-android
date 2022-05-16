package cool.dingstock.appbase.entity.bean.home.bp


data class GoodsExtDetailsEntity(
    var stocks: ArrayList<GoodsInventoryEntity>? = null,
    var TBAuthUrl: String? = null,
    var shopUrl: String? = null,
    var date: String? = "",
    var start: String? = "",
    var startAt: Long? = 0L,
    var displayJumpAhead: Boolean? = false,//是否打开测试跳转按钮
    var couponInfo: String?,//优惠卷描述文案
    var couponUrl: String?,//有优惠卷的地址

    // 2.9.5
    var needReserve: Boolean? = false,//是否需要预约
    var reserveInfo: ArrayList<TimeStageEntity>? = null,
    //2.9.7
    var channelId: String? = ""
)

data class TimeStageEntity(
    var progress: String? = "",
    var timeStr: String? = "",
    var startAt: Long?,
    var endAt: Long?,
    var isNow: Boolean = false,
)