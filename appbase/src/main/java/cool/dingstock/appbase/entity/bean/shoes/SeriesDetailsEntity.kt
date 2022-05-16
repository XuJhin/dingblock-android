package cool.dingstock.appbase.entity.bean.shoes

import cool.dingstock.appbase.entity.bean.base.BasePageEntity
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicDetailCommentsBean


/**
 * 类名：SeriesDetailsEntity
 * 包名：cool.dingstock.appbase.entity.bean.shoes
 * 创建时间：2022/1/6 12:22 下午
 * 创建人： WhenYoung
 * 描述：
 **/
data class SeriesDetailsEntity(
    val seriesInfo: SeriesInfoEntity? = null,
    val products: ArrayList<SeriesProductEntity>? = null,
    val rankInfo: ArrayList<SeriesRankUserEntity>?,
    val productInfo: SeriesProductInfo?,
    val scoreInfo: SeriesScoreInfoEntity?,
    val moreSeries: ArrayList<ShoesSeriesEntity>?,
    val haveMoreSeries: Boolean?,
    val haveMoreComment: Boolean?,
    val haveMoreDeal: Boolean?,
    val deals: ArrayList<DealSelectedEntity>?,
    val comments: ArrayList<CircleDynamicDetailCommentsBean>?,
    var commentCount: Int?,
    val posts: BasePageEntity<CircleDynamicBean>?,
) {
}

data class SeriesInfoEntity(
    val id: String?,
    val name: String?

) {

}

data class SeriesProductEntity(
    val id: String,
    val name: String?,
    val imageUrl: String?,
    val thumbnail: String?,
)

data class SeriesRankUserEntity(
    val id: String?,
    val avatarUrl: String?,
    val collectCount: Int?,

    )

data class SeriesProductInfo(
    val name: String? = null,
    val price: String? = null,
    val avgPrice: String? = null,
    val sku: String? = null,
    val earliestSaleDate: Long? = 0,
    val bgStory: String? = null,
    val postPhotoCount: Int = 0,
)

data class SeriesScoreInfoEntity(
    val avgScore: Float?,
    var myScore: Float?,
    val scores: ArrayList<Float>?
)

