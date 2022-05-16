package cool.dingstock.appbase.entity.bean.circle

import androidx.annotation.Nullable


data class UserDetailBean(
        @Nullable
        var userInfo: CircleDynamicDetailUserBean?,
        var posts: List<CircleDynamicBean>?,
        var nextKey: Long = 0
)


/**
 * 用户
 */
data class UserPostsEntity(
        var nextKey: Long = 0,
        var list: MutableList<CircleDynamicBean>?
)

data class AccountBriefEntity(
        var userInfo: CircleDynamicDetailUserBean?,
)