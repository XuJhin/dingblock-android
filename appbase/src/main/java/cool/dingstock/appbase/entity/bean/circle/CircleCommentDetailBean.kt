package cool.dingstock.appbase.entity.bean.circle;

import com.google.gson.annotations.SerializedName;


data class CircleCommentDetailBean(
        @SerializedName(value = "sections",alternate = ["list", "data"])
        var sections: ArrayList<CircleDynamicSectionBean>?,
        var nextKey: Long = 0
)