package cool.dingstock.appbase.entity.bean.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalenderProductEntity(
    var name: String? = null,
    var imageUrl: String? = null,
    var sku: String? = null,
    var objectId: String? = null,
    var brand: HomeBrandBean? = null,
    var extraImageUrls: MutableList<String> = arrayListOf(),
    var price: String? = null,
    var id: String? = null,
    val raffleCount: Int? = null,
    val likeCount: Int? = null,
    val dislikeCount: Int? = null,
    val subscribeCount: Int? = null,
    val commentCount: Int? = null,
    val liked: Boolean? = null,
    val disliked: Boolean? = null,
    val subscribed: Boolean? = null,
    val color: String? = "#000000",
    val topBgUrl: String? = null,
    val marketPrice: String? = null,//市场价
    var dealCount: Int? = 0, /* 2.9.2新增 */
) : Parcelable {
    //  2.12.0新增，用于判断在列表中是否被选中
    var hasSelected=false

}