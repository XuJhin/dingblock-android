package cool.dingstock.appbase.entity.bean.home

data class HomeRaffle(
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val publishedDate: Long = 0,
    val expireDate: Long = 0,
    val method: String? = null,
    val price: String? = null,
    val notifyDate: Long = 0,
    val releaseDateString: String? = null,
    val brand: HomeBrandBean? = null,
    val link: String? = null,
    val objectId: String? = null,
    val sms: HomeRaffleSmsBean? = null,
    var shareLink: String? = null,
    var joinedCount: Int = 0,
    var joined: Boolean = false,
    var message: String? = null,
    var smsStartAt: Long = 0,
    var id: String? = "",
    var sizes: List<String>?,
    var showOldPage: Boolean?, //控制新老抽签版本
    var reminded: Boolean = false,//2.10.0 是否加入提醒
    //本地扩展字段
    var headerName: String? = null,
    var bpLink: String? = "",
) {

    //扩展字段 用于处理圆角显示
    var isStart: Boolean = true
    var isEnd: Boolean = false

    //是否结束
    var hasFinished: Boolean = false
}

enum class AlarmFromWhere {
    PRODUCT_DETAIL,
    HEAVY_DETAIL,
    CARE_ADDRESS,
    SETTING_PAGE
}

data class AlarmRefreshEvent(
    val pos: Int,
    val isLightUpAlarm: Boolean,
    val fromWhere: AlarmFromWhere,
    val productId: String? = ""
)

