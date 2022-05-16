package cool.dingstock.appbase.entity.bean

import com.google.gson.annotations.SerializedName


data class PageEntity<T>(
    @SerializedName(value = "data", alternate = ["list"])
    val data: MutableList<T>? = arrayListOf(),
    val nextKey: Long = 0,
    val littleHelper: LittleHelperEntity?=null
)


data class LittleHelperEntity(
    val id: String,
    val nickName: String,
    val avatarUrl: String,
)