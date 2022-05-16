package cool.dingstock.appbase.entity.bean.common

import com.google.gson.annotations.SerializedName

data class CommonPageEntity<T>(
        @SerializedName("list",alternate = ["result"])
        val list: ArrayList<T>,
        val nextKey: Long?,
        val nexStr: String?)