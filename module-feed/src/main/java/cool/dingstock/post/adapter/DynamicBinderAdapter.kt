package cool.dingstock.post.adapter

import androidx.lifecycle.Lifecycle
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.bean.circle.VoteOptionEntity
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.post.helper.CommentReload
import cool.dingstock.post.helper.DynamicReload
import cool.dingstock.post.item.PostItemShowWhere

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/18  9:26
 */
open class DynamicBinderAdapter(list: MutableList<Any> = ArrayList()) : DcBaseBinderAdapter(list as ArrayList<Any>) {

    companion object {
        const val MINE = "mine"
    }

    private val dynamicReload: DynamicReload = DynamicReload(this)
    private val commentReload: CommentReload = CommentReload(this)

    fun updateShowWhere(data: PostItemShowWhere) {
        dynamicReload.updatePostShowWhere(data)
    }

    fun registerDynamicReload(lifecycle: Lifecycle) {
        registerReloadDelegations(dynamicReload)
        registerReloadDelegations(commentReload)
        registerReloadLifecycle(lifecycle)
    }

    val VIEW_TYPE = 300
    val LINK_TYPE_VIDEO = "VIDEO"

}

interface PostListener {
    fun raisePost(entity: CircleDynamicBean, position: Int)
    fun commentPost(entity: CircleDynamicBean, position: Int)
    fun collectPost(entity: CircleDynamicBean, position: Int)
    fun sharePost(entity: CircleDynamicBean, position: Int)
}

interface VoteClickListener {
    /**
     * @param itemIndex        当前点击的选项在投票项中的位置
     * @param voteOptionEntity 当前点击的投票项
     * @param postId           postId
     * @param position         整个post在recycle中的位置
     */
    fun onVoteClick(itemIndex: Int, voteOptionEntity: VoteOptionEntity?, postId: String?, position: Int)
}

interface OverlayListener {
    fun clickOverlay(data: CircleDynamicBean?, position: Int)
}
