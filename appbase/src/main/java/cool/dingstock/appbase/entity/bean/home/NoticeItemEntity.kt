package cool.dingstock.appbase.entity.bean.home

import cool.dingstock.appbase.entity.bean.circle.CircleUserBean

data class NoticeItemEntity(
    val createdAt: Long = 0,
    //消息类型（reply:回复；favor:点赞,follow:关注）
    val type: String,
    val favorCount: Int = 0,
    val blocked: Boolean = false,
    val user: CircleUserBean,
    val commentId: String,
    val original: OriginalEntity?,
    val favor: FavorInfo?,
    val content: String = "",
    val title: String = "",
    val postId: String? = null,
    val productId: String? = null,
)

data class OriginalEntity(
    val objectId: String = "",
    val content: String = "",
    val imageUrl: String = ""
)

/**
 * 点赞信息
 */
data class FavorInfo(
    val count: Int,
    val users: MutableList<CircleUserBean>
)