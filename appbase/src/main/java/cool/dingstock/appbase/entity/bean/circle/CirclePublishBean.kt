package cool.dingstock.appbase.entity.bean.circle;

import com.google.gson.annotations.SerializedName;






data class CirclePublishBean (
    @SerializedName(value = "objectId",alternate = ["userId"])
    var objectId  : String?
)
