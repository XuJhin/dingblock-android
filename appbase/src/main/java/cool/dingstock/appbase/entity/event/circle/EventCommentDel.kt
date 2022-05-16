package cool.dingstock.appbase.entity.event.circle

import cool.dingstock.appbase.entity.bean.circle.CircleDynamicDetailCommentsBean


/**
 * 类名：EventCommentDel
 * 包名：cool.dingstock.appbase.entity.event.circle
 * 创建时间：2021/12/6 4:43 下午
 * 创建人： WhenYoung
 * 描述：
 **/
data class EventCommentDel(
    val style: String,
    val mainId: String,
    val commentId: String,
    val onePosition: Int = 0,
    val secondPosition: Int,
    val mainSecondPosition: Int
)

data class EventCommentSuccess(val style: String, val bean: CircleDynamicDetailCommentsBean)

class EventCommentFailed()
