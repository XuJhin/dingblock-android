package cool.dingstock.appbase.entity.bean.circle

import android.os.Parcelable
import cool.dingstock.appbase.entity.bean.box.PriceSaleEntity
import cool.dingstock.appbase.entity.bean.calendar.PriceInfoEntity
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.IgnoredOnParcel

/**
 * @param
 */
@Parcelize
data class CircleDynamicBean(
        val locationName: String? = null,
        val distance: String? = null,
        var createdAt: Long = 0,
        @Deprecated("已废弃，使用id代替")
        var objectId: String? = null,
        var id: String? = null,
        var viewCount: Int = 0,
        var talkId: String = "",
        var favorCount: Int = 0,
        var commentCount: Int = 0,
        var favored: Boolean = false,
        //文本内容
        var content: String? = null,
        //发布者
        var user: CircleUserBean? = null,
        //图片内容
        var images: MutableList<CircleImageBean>? = arrayListOf(),
        var webpageLink: CircleLinkBean? = null,
        // 视频
        var video: CircleVideoBean? = null,
        // 求购、发售 角标
        var badge: CircleBadge? = null,
        @Deprecated("已没使用")
        var overlay: CircleOverlayBean? = null,
        // 话题,
        var talkMap: CircleTalkBean? = null,
        //是否显示话题
        var isShowTalk: Boolean? = true,
        var shareLink: String? = null,
        //====================
        //以下内容1.5.0以后新增
        //====================
        //是否置顶
        var isSticky: Boolean = false,
        var type: String? = null,
        var blocked: Boolean = false,
        var voteOptions: MutableList<VoteOptionEntity> = arrayListOf(),
        var isFashion: Boolean = false,
        //===========自定义属性用于控制按钮的显示和隐藏=============
        var showWhere: ShowWhere,
        var hideOverLay: Boolean = false,
        //本地标记是否是 我的圈子的item
        var inMineItem: Boolean = false,
        //localData
        // 是否在详情
        var detailItem: Boolean = false,
        // 是否在推荐
        var recommendItem: Boolean = false,
        var showTalk: Boolean = true,
        var showFollow: Boolean = false,
        //167版本新增 神评
        var hotComment: HotComment? = null,
        //173新增判断是否上传 曝光
        var isUpDataExposure: Boolean = false,
        //是否是收藏
        var isCollect: Boolean? = false,
        // 2.9.0 交易


        var goodsSizeList: ArrayList<TradingSizePriceEntity>? = null,
        var countStr: String? = null,
        var label: String? = null,
        var labelColor: String? = null,
        var labelBgColor: String? = null,
        var senderAddress: String? = null,
        var otherProductsStr: String? = null,
        //2.9.2 交易发布的商品
        var product: TradingProductEntity? = null,
        //2.9.3 是否显示尺码
        var hideSizeSegment: Boolean? = null,
        var score: Int? = null,
        //2.9.5 系列认证
        private var postType: String? = null,//common,deal,series
        var seriesId: String? = null,
        var blockedReason: String? = null,
        var seriesMap: SeriesMap? = null,
        // 2.9.5 抽奖
        var lotteryMap: LotteryEntity? = null,
        var contentLink: ArrayList<String>? = null,

        //2.12.0热议
        var discussMap: DiscussEntity? = null


) : Parcelable {
    constructor() : this(
            null, null, 0,
            null, null,
            0, "",
            0, 0,
            false, null,
            null, null,
            null, null,
            null, null,
            null, true,
            null,
            false, null,
            false, arrayListOf(),
            false, ShowWhere.COMMENT,
            false, false,
            false, false,
            false, true
    )

    @IgnoredOnParcel
    val isDeal: Boolean
        get() {
            return postType == "deal"
        }

    //通过对数据流的处理，来判断是否显示在鞋库认证里面，不处理就不是鞋库
    @IgnoredOnParcel
    var isShowInShoes: Boolean = false

    @IgnoredOnParcel
    val isSeries: Boolean
        get() {
            if (!isShowInShoes) {
                return false
            }
            return postType == "series"
        }

}

@Parcelize
data class TradingSizePriceEntity(val size: String? = null, val price: String? = null) :
        Parcelable {

}

@Parcelize
data class TradingProductEntity(
        val id: String? = null,
        val imageUrl: String? = null,
        val name: String? = null,
        val sku: String? = null,
        val dealCount: String? = null,
) :
        Parcelable {

}

@Parcelize
data class SeriesMap(
        var id: String? = null,
        var brandId: String? = null,
        var name: String? = null,
        var imageUrl: String? = null,
        var isHot: String? = null,
) : Parcelable

@Parcelize
data class LotteryEntity(
        val id: String?,
        private var status: String?,// drawing 抽奖中/waiting 等待开奖/end 已开奖
        val endAt: Long?,
        val awards: ArrayList<AwardEntity>?,
) : Parcelable {

    fun getStatus(): Status {
        return when (status) {
            Status.drawing.name -> Status.drawing
            Status.waiting.name -> Status.waiting
            Status.end.name -> Status.end
            else -> Status.drawing
        }
    }

    fun updateStatus(status: Status) {
        this.status = status.name
    }

    enum class Status(val title: String) {
        drawing("抽奖中"), waiting("等待开奖"), end("已开奖")
    }
}

@Parcelize
data class AwardEntity(
        val id: String?,
        val name: String?,
        val contents: String?,
        val users: String?,
        val limit: String?,
) : Parcelable


/**
 * 用来feed流的显示位置
 * 部分位置需要特殊处理
 */
enum class ShowWhere {
    RECOMMEND, //显示在首页推荐
    DETAIL,
    MINE,
    COMMENT,
    FASHION,
    FOLLOW,
    TALK_DETAILS,
    FIND
}

@Parcelize
data class DiscussEntity(
        val id: String?,
        val icon: String?,
        val title: String?
) : Parcelable