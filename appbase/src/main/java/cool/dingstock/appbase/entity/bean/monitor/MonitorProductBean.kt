package cool.dingstock.appbase.entity.bean.monitor

import android.os.Parcelable
import com.chad.library.adapter.base.entity.MultiItemEntity
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class MonitorProductBean(
    val id: String? = null,
    val createdAt: Long? = null,
    val channelId: String? = null,
    val detail: ArrayList<String> = arrayListOf(),
    val title: String? = null,
    val link: String? = null,
    val imageUrl: String? = null,
    val couponPrice: String? = null,
    val type: String? = null,
    val couponConditions: String? = null,
    var blocked: Boolean = false,

    val channel: MonitorChannelBean? = null,

    val bizId: String? = null,
    val raw: String? = null,
    val pushed: Boolean = false,
    val objectId: String? = null,
    val updatedAt: String? = null,
    var stock: String? = "",
    //2.9.0
    var productId: String? = null,


    //==============
    //以下字段用于控制界面显示
    //==============
    //是否展开
    var isExpand: Boolean = false,
    var isShowRightArrow: Boolean = true,
    override val itemType: Int = MonitorProductBean::class.java.hashCode()
) : MultiItemEntity, Parcelable