package cool.dingstock.appbase.entity.bean.home;

import android.os.Parcelable;

import java.util.ArrayList;


import cool.dingstock.appbase.entity.bean.home.fashion.VideoEntity;
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeProduct(
    var createdAt: String?,
    var updatedAt: String?,
    var name: String?,
    var imageUrl: String?,
    var subscribeCount: Int = 0,
    var subscribed: Boolean = false,
    var liked: Boolean = false,
    var disliked: Boolean = false,
    var raffleCount: Int = 0,
    var objectId: String?,
    var avatarUrls: List<String>?,
    var brand: HomeBrandBean?,
    var dislikeCount: Int = 0,
    var likeCount: Int = 0,
    var sku: String?,
    var extraImageUrls: ArrayList<String>?,
    var price: String?,
    var commentCount: Int = 0,
    var headerName: String?,
    var talkId: String?,
    var videoMaps: ArrayList<VideoEntity>?,
    var marketPrice: String?,
//    var dealCountStr: String? = "",//warn:可能会没有该字段，没有时页面不展示该内容
    var dealCount: Int? = 0,//warn:可能会没有该字段，没有时页面不展示该内容
    //自定义字段
    var isStart: Boolean = false,
    var isEnd: Boolean = false,
    var isFooter: Boolean = false,
    var goodsType: String? = null,

    var seriesId: String? = "",
    var showSeriesEntrance: Boolean = false,//2.10.0
    var hasOnlineRaffle: Boolean = false
) : Parcelable {
    fun sketch(): HomeProductSketch {
        return HomeProductSketch(name, imageUrl, price, raffleCount)
    }

    companion object {
        fun newInstance(): HomeProduct {
            return HomeProduct(
                "",
                "",
                "",
                "",
                0,
                false,
                false,
                false,
                0,
                "",
                null,
                null,
                0,
                0,
                "",
                null,
                "",
                0,
                "",
                "",
                null,
                "",
                0,
                false,
                false,
                true,
                null,
                ""
            )
        }
    }
}
