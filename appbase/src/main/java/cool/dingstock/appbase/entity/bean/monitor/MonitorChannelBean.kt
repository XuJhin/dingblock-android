package cool.dingstock.appbase.entity.bean.monitor

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.ArrayList


@Parcelize
data class MonitorChannelBean(
    /**)
     * createdAt : 2019-03-19T18:57:21.452Z
     * hide : false
     * name : SNKRS2.0英区
     * sizeIndex : -1
     * fullName : SNKRS2.0英区
     * iconUrl : https://lc-gmid1k1s.cn-e1.lcfile.com/8fa7cf7949622a158fc5.jpg
     * channelId : snkrs_gb_001
     * objectId : w91zv6B07j
     * updatedAt : 2019-03-24T17:38:09.128Z
     */
    val restricted: Boolean = false,
    val groupIds: ArrayList<String> = arrayListOf(),
    val name: String? = null,
    val id: String? = null,
    val isSilent: Boolean = false,
    val category: String? = null,
    val objectId: String? = null,

    val createdAt: String? = null,
    val hide: Boolean = false,
    val index: Int = 0,
    val fullName: String? = null,
    val iconUrl: String? = null,
    val channelId: String? = null,
    val updatedAt: String? = null,
    var isOffline: Boolean? = false,//2.10.0
) : Parcelable