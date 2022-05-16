package cool.dingstock.appbase.entity.bean.box

import android.os.Parcelable
import cool.dingstock.appbase.entity.bean.shop.UserAddressEntity
import kotlinx.parcelize.Parcelize
import java.util.*


/**
 * @author wj
 *  CreateAt Time 2021/7/26  16:16
 */
@Parcelize
data class BagGoodEntity(
    var id: String?,
    val type: String,
    val itemId: String?,

    val icon: String,
    val bigImage: String? = "",
    val count: Int,


    val level: String,

    var isNew: Boolean,
    val name: String,

    val price: String = "",
    val property: String? = "",

    /* 2.9.5  优惠券相关,仅优惠券有这些字段 */
    var discountInfoPrefix: String? = "",            /* 面值文案,前缀 */
    var discountInfo: String? = "",                  /* 面值文案，数字 */
    var discountInfoSuffix: String? = "",            /* 面值文案，后缀 */
    var expireInfo: String? = "",                    /* 有效期文案，道具卡也用此字段 */
    var limitInfo: String? = "",                     /* 使用门槛文案 */
    var expireDetail: String? = "",                  /* 详细的有效期文案 */
    var available: Boolean? = false,                 /* 是否可用 */
    var boxIds: ArrayList<String>? = arrayListOf(),  /* 适配盲盒ID列表 */

    var label: String? = "",                         /* 标签文字 2.9.0 */
    var labelColor: String?,                         /* 标签颜色 2.9.0 */
    var isShowBorder: Boolean = false,
    var isMain: Boolean = false                      //2.9.3


) : Parcelable

data class EmptyBoxGoodEntity(
    var id: String
)

data class BagPageEntity(
    /* 2.9.7 start */
    var showCode: Boolean = false,//是否展示背包客服
    var codeText: String? = "",
    var codeUrl: String? = "",
    /* 2.9.7 end */
    var textContent: String? = "",
    var showExchangeCode: Boolean = false, /* 是否展示兑换码 */
    var coin: Int, /* 金币数量 */
    var isEmpty: Boolean,/* 背包是否为空 */
    var list: ArrayList<BagGoodEntity>,
    var nextKey: String?
)


data class CoinRecordEntity(
    val id: String,
    val icon: String,
    val title: String,
    val date: String,
    val coinDelta: Int
)

data class CoinRecordPageEntity(
    val list: ArrayList<CoinRecordEntity>,
    val nextKey: String

)

data class ReceiveRecordEntity(
    val id: String,
    val icon: String,
    val name: String,
    val property: String,
    val count: Int,
    val state: String,
    val stateName: String,
    val title: String,
    val subTitle: String,
    val btnTitle: String,
    val address: UserAddressEntity? = null,
    val expressName: String,
    val expressNo: String,
    val date: String
)

data class ReceiveRecordPageEntity(
    val list: ArrayList<ReceiveRecordEntity>,
    val nextKey: String
)

data class ProbEntity(
    val level: String,
    val prob: String,
    val color: String? = ""
)

data class BoxDetailEntity(
    var sizeList: ArrayList<Int>?,
    val leftTryTimes: Int,
    val box: BoxEntity,
    val probList: ArrayList<ProbEntity>,
    val itemList: ArrayList<BagGoodEntity>,
)

data class BoxEntity(
    val id: String,
    val icon: String,
    val bigImage: String,
    val title: String,
    val count: Int,
    val price: String,
    val level: String,
    val hasCouponAvailable: Boolean,
    val couponInfo: String,
    val countList: ArrayList<PriceSaleEntity>? = null,
    /* 2.9.7 start */
    val sellout: Boolean? = false, /* 是否售罄 */
    /* 2.9.7 end */
)

data class ShareBoxEntity(
    val title: String,
    val name: String,
    val icon: String,
    val bigLogo: String,
    val shareTitle: String?,
    val shareImage: String?,
    var shareToMini: Boolean = false
)

@Parcelize
data class OpenBoxEntity(

    /* 2.9.7 start */
    var sizeStr: String? = "",
    var showSizeCard: Boolean?,// 是否展示尺码卡
    var canUseSizeCard: Boolean?,// 是否可以使用尺码卡
    var hasSizeCard: Boolean?,// 是否拥有尺码卡
    var sizeCard: BoxCardEntity?,
    /* 2.9.7 end */

    var showMultiCard: Boolean?,
    val hasMultiCard: Boolean?,
    val multiCard: BoxCardEntity?,

    var showReopenBtn: Boolean?,
    val hasReopenCard: Boolean?,
    val recordId: String?,
    val reopenCard: BoxCardEntity?,

    val isTry: Boolean?,
    val isNew: Boolean?,
    val newPlayerReward: Int?,
    val share: BoxOpenShareEntity?,
    val list: ArrayList<BagGoodEntity>,
    val shareTitle: String?,
    val shareImage: String?,
    var shareToMini: Boolean = false
) : Parcelable {
    var boxId: String = ""
}

@Parcelize
data class BoxOpenShareEntity(
    val wxImg: String,
    val dcImg: String
) : Parcelable

@Parcelize
data class BoxCardEntity(
    val type: String,
    val id: String,
    val name: String,
    val imgUrl: String,
    val level: String,
    val cost: Int
) : Parcelable

@Parcelize
data class AfterUseCardPrizeEntity(
    val recordId: String?,
    val type: String?,
    val id: String?,
    val name: String?,
    val icon: String?,
    val price: String?,
    val level: String?,
    val count: Int?,

    val sizeStr: String?,
    //适配选择奖励接口
    val isMain: Boolean? = false,
    //适配尺码卡选择尺寸是否被选中
    var isSelected: Boolean = false
) : Parcelable

@Parcelize
data class AfterUseCardsPrizeListEntity(
    val timeLimit: Long,
    val list: ArrayList<AfterUseCardPrizeEntity>,
) : Parcelable


@Parcelize
data class ChoosePrizeEntity(
    val sizeCard: BoxCardEntity?,
    val sizeStr: String? = "",
    val showSizeCard: Boolean? = false,// 是否展示尺码卡
    val canUseSizeCard: Boolean? = false,// 是否可以使用尺码卡
    val hasSizeCard: Boolean? = false,// 是否拥有尺码卡
    val shareTitle: String? = "",
    val list: ArrayList<AfterUseCardPrizeEntity>,
) : Parcelable


data class BoxQueryCouponEntity(
    val id: String, /* 仅作为唯一key，非优惠券ID */
    val itemId: String,  /* 物品ID=优惠券ID */
    val name: String, /* 物品名称 */
    val discountInfoPrefix: String,  /* 面值文案 前缀*/
    val discountInfo: String,  /* 面值文案 */
    val discountInfoSuffix: String,  /* 面值文案 后缀*/
    val limitInfo: String, /* 使用门槛文案 */
    val expireDetail: String,/* 详细的有效期文案 */
    val count: Int,  /* 数量 */

    var isSelected: Boolean = false,//自加字段，判断是否被点击选中
    var pos: Int = 0,
    var sameTypePos: Int = 0
)

data class BoxQueryCouponListEntity(
    val list: List<BoxQueryCouponEntity>
)

data class BoxUserInfoEntity(
    val coin: Int = 0
)