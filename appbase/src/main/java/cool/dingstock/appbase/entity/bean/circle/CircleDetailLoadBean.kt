package cool.dingstock.appbase.entity.bean.circle;

import com.google.gson.annotations.SerializedName


data class CircleDetailLoadBean(
        @SerializedName(value = "comments",alternate = ["list","data"])
        var comments: ArrayList<CircleDynamicDetailCommentsBean>?,
        var nextKey: Long = 0
)
