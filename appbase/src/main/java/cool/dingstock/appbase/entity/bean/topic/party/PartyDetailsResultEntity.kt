package cool.dingstock.appbase.entity.bean.topic.party

import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.bean.circle.CircleTalkBean
import cool.dingstock.appbase.entity.bean.topic.TalkTopicEntity
import cool.dingstock.appbase.entity.bean.topic.TopicDetailEntity


/**
 * 类名：PartyEntity
 * 包名：cool.dingstock.appbase.entity.bean.topic.party
 * 创建时间：2022/3/1 10:41 上午
 * 创建人： WhenYoung
 * 描述：
 **/
data class PartyDetailsResultEntity(
    val talk: TalkTopicEntity? = null,
//    val activity: PartyEntity? = null,
    val nextKey: Long? = null,
    val posts: ArrayList<CircleDynamicBean>? = null,
)

data class HotRankDetailEntity(
    val discuss: TalkTopicEntity? = null,
    val nextKey: Long? = null,
    val posts: ArrayList<CircleDynamicBean>? = null,
)
