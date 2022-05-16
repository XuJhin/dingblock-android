package cool.dingstock.appbase.entity.bean.home.bp

import cool.dingstock.appbase.entity.bean.base.BasePageEntity
import cool.dingstock.appbase.entity.bean.home.HomeBanner
import cool.dingstock.appbase.entity.bean.monitor.MonitorChannelBean


data class BpLabIndexEntity(
    val headerInfo: BpHeaderInfoEntity?,
    val banners: ArrayList<HomeBanner>? = arrayListOf(),
    val iconList: ArrayList<BpIconEntity>,
    val remindListCount: Int? = 0,
    val categories: ArrayList<BpCategoryEntity>,
    val products: BasePageEntity<GoodsItemEntity>
)

data class BpHeaderInfoEntity(
    val headerImgUrl: String,
    val headerDesc: String,
    val searchPlaceholder: String,
)


data class BpCategoryEntity(
    val timeType: String,
    val desc: String? = "",
    val startAt: Long,
    val isSelected: Boolean,
    val platTypes: ArrayList<String>
)


data class BpIconEntity(
    val icon: String,
    val title: String,
    val desc: String,
    val showTip: Boolean,
    val tip: String,
    val link: String
)


data class BpChannelEntity(
    val id: String,
    val type: String,
    val iconUrl: String,
    val name: String,
)


data class ClueProductBean(
    val id: String? = null,
    val createdAt: Long? = null,
    val channelId: String? = null,
    val detail: ArrayList<String> = arrayListOf(),
    val title: String? = null,
    val link: String? = null,
    val imageUrl: String? = null,
    val bizId: String? = null,
    var stock: String? = "",
    var blocked: Boolean = false,
    var couponPrice: String? = "",
    var type: String? = "",
    var couponConditions: String? = "",
    val channel: MonitorChannelBean? = null,
)


data class ClueDataBean(
    var list: ArrayList<ClueProductBean>,
    var channels: ArrayList<BpChannelEntity>,
    var nextKey: Long
)

data class MonitorRemindMsgEntity(
    var pos: Int = 0,
    val msg: String? = null,
    val icon: Int? = null,
    var isSelected: Boolean = false,
)