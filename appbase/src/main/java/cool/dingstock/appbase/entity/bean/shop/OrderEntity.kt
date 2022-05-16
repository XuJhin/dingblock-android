package cool.dingstock.appbase.entity.bean.shop

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author wangjiang
 *  CreateAt Time 2021/6/2  10:57
 */
data class OrderEntity(
        val id: String?,   /* 订单号 */
        val merchant: ShopEntity?, /* 商户信息 */
        val skus: ArrayList<GoodsInOrderListEntity>?, /*商品列表*/
        val currency: String,    /* 货币代码 */
        val currencySymbol: String,/*货币符号*/
        val originCurrencyOrderFee: String,/* 原货币订单金额 */
        val orderFee: String,/* 人民币订单金额（未支付订单展示第一个商家的金额） */
        val date: Long,/* 展示时间 */
        val cancelWhen: String,/* 指定时间后订单未支付就会取消订单（未付款时返回） */
        val orderState: String,/* 订单状态 */
        val orderStateTxt: String,/* 订单状态文字 */
        val skuCount: Int,
        val feeList: ArrayList<FeeEntity>?,/*计费列表*/
        val payFee: String,/* 应付小计 */
        val rmbPayFee: String,/* 人民币应付小计 */
)

data class FeeEntity(
        val title: String,/* 费用标题 */
        val value: String,  /* 费用金额 */
        val type: String,  /* 费用类型 */
        val link: String?,
        val desc: String?,/* 描述 */
        val available: Boolean = false,/* 可用状态 */
//        val currency: String,
//        val currencySymbol: String,


)

data class TimeEntity(
        val title: String,/* 标题 */
        val value: String  /* 数据 */
)

data class ShopEntity(
        val countryIcon: String,/*国旗图标*/
        val id: String,  /* 商户ID */
        val country: String,  /* 服务地区，国家代码 */
        val name: String,  /* 商户名称 */
        val nameCN: String /* 商户名称，中文 */
)

data class OrderListEntity(
        var nextKey: String,
        var pageSize: Int,
        val list: List<OrderEntity> = arrayListOf()
)

data class GoodsInOrderListEntity(
        val skuId: String,/* sku id */
        val count: String,/* 数量 */
        val brandName: String,/* 品牌名称 */
        val currencySymbol: String,/*货币符号*/
        val brandNameCN: String,/* 品牌名称，中文 */
        val goodsName: String,/* sku名称 *//* 商品名称 */
        val goodsNameCN: String,/* sku名称，中文 *//* 商品名称，中文 */
        val imageUrl: String,/* 图片地址 */
        val attrString: String,/* 规格字符串 */
        val currency: String,/* 货币代码 */
        val discount: String?,/* 折扣 */
        val originPrice: String?,/* 原价 */
        val promotionPrice: String,/* 优惠价 */
        val rmbOriginPrice: String,/* 人民币原价 */
        val rmbPromotionPrice: String,/* 人民币优惠价  */
        val spuId: String,/* spu id */
        val lastExpressInfo: lastExpressInfoEntity
)

data class lastExpressInfoEntity(
        /* 最近一条物流信息 */
        val info: String,/* "已发货，再等等就到了"  */
        val date: Long,/* 12312312312312 */
)

data class OrderTailPageEntity(
        val id: String,/* 订单id  */
        val skuGroups: List<OrderEntity>, /* 按商户分组 */
        val feeList: ArrayList<FeeEntity>?, /* 总计计费列表 */
        val remark: String,                 /* 订单备注 */

        val currency: String,/* 货币、符号 */
        val currencySymbol: String,
        val rmbOrderFee: String,/* 人民币订单金额 */


        val orderFee: String,                /* 订单金额 */
        val date: Long,             /* 展示时间 */
        val cancelWhen: Long,              /* 指定时间后订单未支付就会取消订单（未付款时返回） */
        val orderState: String,                   /* 订单状态 */
        val orderStateTxt: String,              /* 订单状态文字 */
        val tip: String,         /* 订单状态下面的提示文字 */
        val timeList: List<TimeEntity>,/* 时间信息列表*/
        val addressee: AddressEntity,/* 收货地址 */
        val orderImages: List<String>   /* 下单截图 */
)

@Parcelize
data class OrderPicEntity(
        val orderImages: List<String>
) : Parcelable


data class PreOrderResultEntity(
        val skuGroups: ArrayList<OrderEntity>,
        val feeList: ArrayList<FeeEntity>,
        val payFee: String,
        val rmbPayFee: String,
        val currency: String,
        val currencySymbol: String,
        var addressee: AddressEntity,
        val couponList: ArrayList<CouponEntity>?
)

@Parcelize
data class CouponEntity(
        val id: String,
        val type: String,
        val name: String,
        val expireStr: String,
        val value: String,
        val limitStr: String,
        val available: Boolean,
        val expireType: String,
        var selected: Boolean,
        val currency: String,
        var currencySymbol: String
) : Parcelable

data class CouPonListEntity(
        val list: List<CouponEntity>
)


data class CreateOrderEntity(
        val orderId: String?
)


