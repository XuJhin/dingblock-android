package cool.dingstock.appbase.entity.bean.calendar

import cool.dingstock.appbase.entity.bean.circle.CircleImageBean
import cool.dingstock.appbase.entity.bean.circle.UserMap
import cool.dingstock.appbase.entity.bean.home.HomeProduct


/**
 * 类名：DealDetailsEntity
 * 包名：cool.dingstock.appbase.entity.bean.calendar
 * 创建时间：2022/2/15 11:12 上午
 * 创建人： WhenYoung
 * 描述：
 **/
data class DealDetailsEntity(
    val product: DealDetailProductEntity? = null
) {


}

data class DealDetailProductEntity(
    val product: HomeProduct? = null,
    val shoesSize: ArrayList<String>? = null,
)


data class DealPostItemEntity(
    val id: String? = null,
    val createdAt: Long? = null,
    val productId: String? = null,
    val senderAddress: String? = null,
    val price: String? = null,
    val content: String? = null,
    var images: MutableList<CircleImageBean>? = arrayListOf(),
    val userMap: UserMap? = null,
)