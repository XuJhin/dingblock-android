package cool.dingstock.appbase.entity.bean.home.bp

import com.google.gson.annotations.SerializedName

data class GoodsItemEntity(
        var id: String,
        val cratedAt: Long,
        val updatedAt: Long,
        val type: String,
        val name: String,
        val date: String,
        val linkUrl: String,
        val start: String,
        val imgUrl: String,
        val startAt: Long,
        val price: String,
        val originalPrice: String,
        var commission: String? = "",
        var vipCommission: String? = "",
        var commissionStr: String? = "",
        var vipCommissionStr: String? = "",
        val weight: Int,
        val shopName: String,
        val blocked: Boolean,
        val shopUrl: String? = "",
        var isReminded: Boolean = false,
        var hasCoupon: Boolean = false,
        var couponInfo: String? = null,
        //是否可以使用此功能 true 时直接使用
        val memberuse: Boolean = true,
        var label: String? = "",
        var labelColor: String? = "",
)

data class RemindListDatas(
        var GoodsItemEntitys: List<GoodsItemEntity> = arrayListOf(),
        var ExpireGoodsItemEntitys: List<GoodsItemEntity> = arrayListOf()
)

data class PageBpGoods(
        val headerInfo: HeaderInfo?,
        @SerializedName(value = "products", alternate = ["list"])
        val products: MutableList<GoodsItemEntity> = arrayListOf(),
        val nextKey: Long
)

data class HeaderInfo(
    val desc: String?,
    val imageUrl: String?,
    val title: String?
)

data class PageRemindGoods(
        @SerializedName(value = "remindList")
        val remindList: MutableList<GoodsItemEntity> = arrayListOf(),
        @SerializedName(value = "expireList")
        val expireList: MutableList<GoodsItemEntity> = arrayListOf(),
        val nextKey: Long?
)


data class SignGoodsPageEntity(
        var nextKey: Long,
        var countForUnderway: String?,
        var countForWaiting: String?,
        var list: ArrayList<SignGoodEntity>,
        var plats:ArrayList<SignResultSearchEntity>?,
)

data class SignResultSearchEntity(
        val plat:String?,
        val platImgUrl:String?,
        val linkUrl:String?,
)


data class SignGoodEntity(
        var id: String,//
        var createdAt: Long,
        var updatedAt: Long,
        var blockedBy: String,
        var name: String,//
        var type: String,//
        var plat: String,//
        var platImgUrl: String,//
        var linkUrl: String,//
        var imgUrl: String,//
        var announcedAt: Long,//
        var price: String,//
        var weight: Int,
        var joined: Boolean,//
        var blocked: Boolean,
        var buttonStr: String,//
        var joinCountStr: String,//
        var joinedCount: Int,//
        var date: String,//
        var start: String,//
        var shopUrl: String
)

data class TestTimeEntity(
        var counts: String,
        var statusValue: String
)

data class TestBigData(
        var data: TestTimeEntity,
        var pos: Int,
        var err: Boolean = false
)