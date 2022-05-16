package cool.dingstock.appbase.entity.bean.home

import java.util.ArrayList


data class HomeRegionRaffleBean(
    var header: HomeProduct?,
    var data: List<HomeRaffle>?
) {
    var isExpose = false
}


data class SmsBrandBean(
    var id: String,
    var createdAt: Long?,
    var name: String?,
    var imageUrl: String?,
    var objectId: String?,
    var blocked: Boolean
)

data class SmsRaffleBean(
    var id: String,
    var createdAt: Long?,
    var brandId: String?,
    var regionId: String?,
    var productId: String?,
    var link: String?,
    var price: String?,
    var method: String?,
    var publishedDate: Long?,
    var expireDate: Long?,
    var releaseDateString: String?,
    val sms: HomeRaffleSmsBean? = null,
    var smsStartAt: Long,
    var smsStatus: String,
    var brand: SmsBrandBean?,
    var objectId: String?,
    var blocked: Boolean?,
    var sizes: List<String>?,
    var showOldPage: Boolean?, //控制新老抽签版本
    var reminded: Boolean = false,//2.10.0 是否加入提醒

//可有可无字段
    var notifyDate: Long,//
    var shareLink: String? = "",//
    var message: String? = "",//
    //自加字段
    var isLast: Boolean = false,
    var isStart: Boolean = false
)

data class SmsRegistrationEntity(
    var products: List<SmsRegistrationBean>?,
    var whitelist: List<String>?
)

data class SmsRegistrationBean(
    var id: String,
    var createdAt: Long?,
    var brandId: String,
    var name: String?,
    var sku: String?,
    var price: String?,
    var featured: Boolean,
    var imageUrl: String?,
    var likeCount: Int = 0,
    var dislikeCount: Int = 0,
    var commentCount: Int = 0,
    var raffles: ArrayList<SmsRaffleBean>,
    var raffleCount: Int = 0,
    var liked: Boolean = false,
    var disliked: Boolean = false,
    var objectId: String?,
    var blocked: Boolean,
    var marketPrice: String?,
    var dealCount: Int?,
) {
    var isExpose = false
    fun sketch(): HomeProductSketch {
        return HomeProductSketch(name, imageUrl, price, raffleCount)
    }
}