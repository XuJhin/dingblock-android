package cool.dingstock.appbase.entity.bean.circle

import com.google.gson.annotations.SerializedName

/**
 * @author WhenYoung
 *  CreateAt Time 2020/11/11  14:44
 */
data class CircleProductCommentPage(
        var nextKey: Long? = null,
        @SerializedName("data",alternate = ["list"])
        var data: ArrayList<CircleDynamicDetailCommentsBean>? = null,
        var nextStr: String? = null
)