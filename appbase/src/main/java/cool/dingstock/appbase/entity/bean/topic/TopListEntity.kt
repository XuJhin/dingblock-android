package cool.dingstock.appbase.entity.bean.topic

import com.google.gson.annotations.SerializedName

class TopListEntity(
        val nextKey: Long,
        @SerializedName(value = "result",alternate = ["list"])
        val result: MutableList<TalkTopicEntity> = arrayListOf(),
        val nextStr: String
)

class TopicIndexEntity(
        val nextStr: String,
        val talk: TalkTopicEntity,
        @SerializedName(value = "recommend",alternate = ["list"])
        val recommend: MutableList<TalkTopicEntity> = arrayListOf()
)