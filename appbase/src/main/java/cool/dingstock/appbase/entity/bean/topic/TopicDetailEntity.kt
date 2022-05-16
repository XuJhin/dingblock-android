package cool.dingstock.appbase.entity.bean.topic

import com.google.gson.annotations.SerializedName
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean

data class TopicDetailEntity(
        val talk: TalkTopicEntity,
        val nextKey: Long = 0,
        @SerializedName(value = "posts",alternate = ["list"])
        val posts: MutableList<CircleDynamicBean>
)