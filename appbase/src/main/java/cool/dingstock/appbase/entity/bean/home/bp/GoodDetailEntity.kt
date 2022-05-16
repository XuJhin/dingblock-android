package cool.dingstock.appbase.entity.bean.home.bp

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * 商品信息
 * @param title 标题
 * @param startTime 开始时间，当小于当前时间是，无法开启定时跳转
 * @param props 规格列表，可能为空
 * @param skus  规格对应的具体商品列表，可能为空
 */
@Parcelize
data class GoodDetailEntity(
    var id: String? = "",
    var title: String = "",
    var type: String? = "",
    var date: String? = "",
    var start: String? = "",
    var startTime: Long? = 0L,
    var imageUrl: String = "",
    var linkUrl: String? = "",
    var props: MutableList<PropItemEntity>? = arrayListOf(),
    var skus: MutableList<SKUEntity>? = arrayListOf(),
    //商品原链接
    var shopUrl: String? = "",
    //是否是定金预售
    var presale: Boolean = false,
) : Parcelable

/**
 * sku具体商品
 *
 * @param propPath  规格拼接的字符串，用于匹配用户选择的规格
 * @param quantity  库存数目
 * @param price     价格
 * @param link      用于跳转的链接
 */
@Parcelize
data class SKUEntity(
    val propPath: String? = "",
    val quantity: Int? = 0,
    val price: String? = "",
    val originalPrice: String? = "",
    val link: String? = "",
    val platformUrl: String? = ""
) : Parcelable {
    fun propMap(): HashMap<String, String> {
        val hashMap: HashMap<String, String> = hashMapOf()
        if ((propPath ?: "").contains(";")) {
            val keyValueList = propPath?.split(";")
            if (keyValueList != null && keyValueList.size > 0) {
                keyValueList.forEach { kv ->
                    val propKeyAndValueList = kv.split(":")
                    if (null != propKeyAndValueList && propKeyAndValueList.size > 1) {
                        hashMap[propKeyAndValueList[0]] = propKeyAndValueList[1]
                    }
                }
            }
            return hashMap
        } else {
            val propKeyAndValueList = propPath?.split(":")
            if (null != propKeyAndValueList && propKeyAndValueList.size > 1) {
                hashMap[propKeyAndValueList[0]] = propKeyAndValueList[1]
            }
            return hashMap
        }
    }
}


/**
 * 规格信息
 *
 * @param name 名称
 * @param pid 规格id 用于匹配sku
 * @param values 属性列表
 */
@Parcelize
data class PropItemEntity(
    val name: String? = "",
    val pid: String? = "",
    val values: MutableList<PropValueEntity>? = arrayListOf()
) : Parcelable


/**
 * 具体属性
 *
 * @param name
 * @param vid 跟规格id一起匹配sku
 * @param image 产品图片
 * @param selected  自定义字段，用于标记是否选中
 */
@Parcelize
data class PropValueEntity(
    val name: String? = "",
    val vid: String? = "",
    val image: String? = "",
    var selected: Boolean = false
) : Parcelable


enum class BuyStrategyTabType(val key: String, val value: String) {
    ONLINE("线上攻略", "online"),
    OFFLINE("线下攻略", "offline"),
}

data class BuyStrategyEntity(
    val id: String? = "",
    val blockedBy: String? = "",
    val name: String? = "",
    var desc: String? = "",
    val type: String? = "",
    val timeDesc: String? = "",
    val channelType: String? = "",
    var detailImageUrl: String? = "",
    val buttonAction: String? = "",
    val buttonContent: String? = "",
    val channelIds: ArrayList<String>? = arrayListOf(),
    val buttonName: String? = "",
    val blocked: Boolean? = false,
    val imageUrl: String? = ""
)

data class BuyStrategyList(
    val list: ArrayList<BuyStrategyEntity>? = arrayListOf(),
    val nextStr: String?
)