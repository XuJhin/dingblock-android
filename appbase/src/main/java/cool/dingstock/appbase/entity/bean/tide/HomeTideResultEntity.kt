package cool.dingstock.appbase.entity.bean.tide

import cool.dingstock.appbase.entity.bean.home.HomeProductSketch


/**
 * 类名：HomeTideResultEntity
 * 包名：cool.dingstock.appbase.entity.bean.tide
 * 创建时间：2021/7/20 6:45 下午
 * 创建人： WhenYoung
 * 描述：
 **/
data class HomeTideResultEntity(
        val companies: ArrayList<TideCompanyFilterEntity>?,
        val dealType: ArrayList<String>?,
        val sections: ArrayList<TideSectionsEntity>,
        val featured: ArrayList<GoodItem>,
        val categories:ArrayList<CategoriesEntity>?,
        val banner: ArrayList<BannerEntity>?
)

data class CategoriesEntity(
        val id:String? = null,
        val name:String? = null,
)

data class GoodItem(
        val id: String,
        val imageUrl: String?,
        val saleDate: Long?,
        val saleDateStr: String?,
        val title: String?
)

data class TideSectionsEntity(
        val header: Long,
        val select: Boolean? = false,
        val tides: ArrayList<TideItemEntity>?
)

data class TideItemEntity(
        val id: String,
        val title: String?,
        val linkUrl: String?,
        val company: String?,
        val ip: String?,
        val saleDate: Long?,
        val saleDateStr: String?,
        val companyLogo: String?,
        val price: String?,
        val imageUrl: String?,
        var isSubscribe: Boolean,
        var isPlayAnimator: Boolean = false,
        var isFooter: Boolean = false,
        //2。9。7
        val desc: String? = "",
        var dislikeCount: Int = 0,
        var raffleCount: Int = 0,
        var likeCount: Int = 0,
        var commentCount: Int = 0,
        var liked: Boolean = false,
        var disliked: Boolean = false,
        //2.10.0
        val dealDesc: String? = null,
        val label: String? = null,
        val labelColor: String? = null,
        val isSoldOut: Boolean = false,
        val type: String? = null
) {
        companion object{
                fun newInstance():TideItemEntity{
                        return TideItemEntity(
                                "",
                                "",
                                "",
                                "",
                                "",
                                0,
                                "",
                                "",
                                "",
                                "",
                                false,
                                false,
                                true)
                }
        }

        fun sketch(): HomeProductSketch {
                return HomeProductSketch(title,imageUrl,price,raffleCount)
        }

}

data class BannerEntity(
    val id: String?,
    val imageUrl: String?,
    val targetId: String?,
    val targetUrl: String?
)