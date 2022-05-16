package cool.dingstock.appbase.entity.bean.config

import android.os.Parcelable
import cool.dingstock.appbase.entity.bean.home.UpdateVerEntity
import kotlinx.parcelize.Parcelize

data class ConfigData(
    /**
     * //商务广告信息
     */
    var updateConfig: UpdateVerEntity? = null,
    var monitorConfig: AppMonitorConfig? = null,
    var bizAD: BizAdEntity? = null,
    var fpSupportPayMethod: FpSupportPayMethodEntity?,
    var imConfig: IMConfig?,
    var dealConfig: DealConfig?,
    var hotPostIcon: String?,
    var dealTalkId: String?,
    var seriesTalkName:String?,
    var expressEntryList: ExpressEntryList?
)

data class BizAdEntity(
    var showInterval: Int? = 0,
    var vipInterval: Int?,
    var duration: Int?,
    var startTime: Long?,
    var endTime: Long?,
    var id: String?,
    var link: String?,
    var mediaUrl: String?,
    var mediaType: String?,
    //本地缓存的文件地址
    var localPath: String?,
)

data class FpSupportPayMethodEntity(
    val aliPay: PayMethodEntity,
    val wechatPay: PayMethodEntity,
)

data class PayMethodEntity(val default: Boolean, val available: Boolean) {}


data class TopOnConfigEntity(val pacing: Long)

data class IMConfig(
    val remindContent: String,
    val forbidWord: List<String>,
    var fastReply: List<FastReplay>?
)

data class DealConfig(
    val typeList: List<Type>,
    val typeName: String
)

data class Type(
    val name: String,
    var hideSizeSegment: Boolean?,
    val qualityList: List<String>,
    val qualityName: String,
    val sizeList: List<String>,
    val sizeName: String,
    var selected: Boolean = false
)

data class ExpressEntryList(
    val dealDetailEntry: DealDetailEntry?,
    val calendarDealEntry: CalendarDealEntry?,
    val messageEntry: MessageEntry?
)

data class DealDetailEntry(
    val enabled: Boolean?,
    val vipTip: String?,
    val tip: String?
)

data class MessageEntry(
    val enabled: Boolean?,
    val vipTip: String?,
    val tip: String?
)

data class CalendarDealEntry(
    val enabled: Boolean?,
    val vipTip: String?,
    val tip: String?
)

data class EmojiConfig(
    val icon: String?,
    val name: String?,
    val type: String?,
    val urlList: List<EmojiUrl>?
)

@Parcelize
data class EmojiUrl(
    val id: String?,
    val imageUrl: String?
): Parcelable

data class ShareConfig(
    val icon: String?,
    val plat: String?,
    val platName: String?,
    val shareType: String?
)

data class FastReplay(
    val id: String?,
    val value: String?,
    var enable: Boolean = false
)