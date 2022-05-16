package cool.dingstock.appbase.entity.bean.circle;

import com.google.gson.annotations.SerializedName;



data class CircleDynamicSectionBean (
    var header  : String?,
    @SerializedName(value = "comments", alternate = ["data","list","subComments"])
    var comments  : List<CircleDynamicDetailCommentsBean?>?,
    var nextKey  : Long = 0
)
