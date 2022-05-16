package cool.dingstock.appbase.entity.bean.home.bp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class MoutaiListBean(
    val list: List<MoutaiBean>,
    val nextKey: Long?
)

@Parcelize
data class MoutaiBean(
    val bizId: String?,
    val blocked: Boolean?,
    val channelId: String?,
    val channelMap: ChannelMap?,
    val createdAt: Long?,
    val detail: List<String>?,
    val id: String?,
    val imageUrl: String?,
    var isSubscribed: Boolean?,
    val link: String?,
    val linkUrl: String?,
    val shopUrl: String?,
    val title: String?,
    val buttonStr: String?,
    val buttonAction: String?
): Parcelable

@Parcelize
data class ChannelMap(
    val category: String?,
    val groupIds: List<String>?,
    val id: String?,
    val isSilent: Boolean?,
    val name: String?,
    val objectId: String?,
    val restricted: Boolean?,
    val sizeSupport: Boolean?,
    val iconUrl: String?
): Parcelable