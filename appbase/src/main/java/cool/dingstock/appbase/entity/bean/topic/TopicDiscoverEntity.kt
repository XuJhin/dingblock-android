package cool.dingstock.appbase.entity.bean.topic

import com.google.gson.annotations.SerializedName

data class TopicDiscoverEntity(
        val nextStr: String?,
        val talk: TalkTopicEntity?,
        @SerializedName(value = "recommend",alternate = ["list"])
        val recommend: MutableList<TalkTopicEntity>?
)