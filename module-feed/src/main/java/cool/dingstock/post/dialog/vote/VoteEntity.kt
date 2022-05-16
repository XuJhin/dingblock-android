package cool.dingstock.post.dialog.vote

data class VoteEntity(
        var voteContent: String,
        //用于是否显示清除
        var showClear: Boolean = false,
        //以下2个参数用户控制在发布界面的显示效果
        var index: Int = -1,
        var totalSize: Int = -1
)