package cool.dingstock.post.helper

import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.adapter.delegation.HolderReloadDelegation
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.bean.circle.CircleUserBean
import cool.dingstock.appbase.entity.bean.circle.ShowWhere
import cool.dingstock.appbase.entity.event.account.EventIsAuthorized
import cool.dingstock.appbase.entity.event.circle.*
import cool.dingstock.appbase.entity.event.update.EventMedalWear
import cool.dingstock.appbase.entity.event.update.EventUpdateAvatar
import cool.dingstock.appbase.entity.event.update.EventUpdatePendant
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.post.adapter.holder.DynamicItemViewHolder
import cool.dingstock.post.item.PostItemShowWhere
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/16  18:15
 *  Dynamic动态重载
 */
class DynamicReload(val adapter: DcBaseBinderAdapter) : HolderReloadDelegation {
    private var postShowWhere: PostItemShowWhere = PostItemShowWhere.Default
    private var holders = HashMap<DynamicItemViewHolder, String>()

    /**
     * Key 为用户id
     * value为Set<viewHolder> 的map
     * 便于处理更用户相关的viewHolder
     */
    private var accountHolders = HashMap<DynamicItemViewHolder, String>()
    private var updataData = false

    fun updatePostShowWhere(data: PostItemShowWhere) {
        this.postShowWhere = data
    }

    private fun unRegister() {
        holders.clear()
        EventBus.getDefault().unregister(this)
    }

    private fun register() {
        EventBus.getDefault().register(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                register()
            }
            Lifecycle.Event.ON_PAUSE -> {
                when (source) {
                    is Fragment -> {
                        if (source.isRemoving) {
                            unRegister()
                            source.lifecycle.removeObserver(this)
                        }
                    }
                    is AppCompatActivity -> {
                        if (source.isFinishing) {
                            unRegister()
                            source.lifecycle.removeObserver(this)
                        }
                    }
                }
            }
            else -> {
            }
        }
    }

    fun onHolderBind(dynamicItem: CircleDynamicBean, holder: DynamicItemViewHolder) {
        dynamicItem.id?.let {
            holders[holder] = it
        }
        dynamicItem.user?.id?.let {
            accountHolders[holder] = it
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPendantReceived(event: EventUpdatePendant) {
        for (holder in holders) {
            val dataPosition = adapter.getDataPosition(holder.key)
            val circleDynamicBean = adapter.getDataList()[dataPosition] as? CircleDynamicBean
            if (event.userId == circleDynamicBean?.user?.objectId) {
                circleDynamicBean?.user?.avatarPendantUrl = event.pendantUrl
                holder.key.ivUserAvatar.setPendantUrl(event.pendantUrl)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAvatarReceived(event: EventUpdateAvatar) {
        for (holder in holders) {
            val dataPosition = adapter.getDataPosition(holder.key)
            val circleDynamicBean = adapter.getDataList()[dataPosition] as? CircleDynamicBean
            if (event.userId == circleDynamicBean?.user?.objectId) {
                circleDynamicBean?.user?.avatarUrl = event.avatarUrl
                holder.key.ivUserAvatar.setAvatarUrl(event.avatarUrl)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMedalWearReceived(event: EventMedalWear) {
        for (holder in holders) {
            val dataPosition = adapter.getDataPosition(holder.key)
            val circleDynamicBean = adapter.getDataList()[dataPosition] as? CircleDynamicBean
            if (event.userId == circleDynamicBean?.user?.objectId) {
                circleDynamicBean?.user?.achievementIconUrl = event.iconUrl
                holder.key.ivMedal.hide(event.iconUrl.isNullOrEmpty())
                holder.key.ivMedal.load(event.iconUrl)
            }
        }
    }

    /**
     * 点赞数量变化
     *
     * @param eventFavored
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFavoredReceived(eventFavored: EventFavored) {
        upDateView(eventFavored.dynamicId, eventFavored.dynamicBean, upDateFavored, reloadLike)
    }

    /**
     * 收藏变化
     *
     * @param eventFavored
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCollectReceived(eventFollowerChange: EventCollectChange) {
        upDateView(
            eventFollowerChange.dynamicId,
            eventFollowerChange.entity,
            upDateCollect,
            reloadCollect
        )
    }

    /**
     * 评论数量变化
     *
     * @param eventCommentCount
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCommentCountChange(eventCommentCount: EventCommentCount) {
        upDateView(
            eventCommentCount.dynamicId,
            eventCommentCount.entity,
            upDateComment,
            reloadComment
        )
    }

    /**
     *  动态再列表被移除
     *  @param eventCommentCount
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDynamicRemove(eventCommentCount: EventDynamicRemove) {
        when (postShowWhere) {
            PostItemShowWhere.Detail/*, PostItemShowWhere.Profile*/ -> {
                return
            }
            else -> {
                Logger.e("onDynamicRemove,", "${this}dynamicId,", eventCommentCount.dynamicId)
                upDateView(eventCommentCount.dynamicId, null, upDateRemove, reloadRemove)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRemoveDynamicAccount(eventRemoveDynamicOfAccount: EventRemoveDynamicOfAccount) {
        when (postShowWhere) {
            PostItemShowWhere.Detail,
            PostItemShowWhere.Profile -> {
                adapter.notifyDataSetChanged()
                return
            }
            else -> {
                upDateViewRemoveAccount(
                    eventRemoveDynamicOfAccount.userId,
                    null,
                    upDateRemove,
                    reloadRemove
                )
            }
        }
    }

    /**
     *  关注
     *  @param eventFollowerChange
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDynamicFollower(eventFollowerChange: EventFollowerChange) {
        for (holder in holders) {
            val dataPosition = adapter.getDataPosition(holder.key)
            val circleDynamicBean = adapter.getDataList()[dataPosition] as? CircleDynamicBean
            if (eventFollowerChange.userId == circleDynamicBean?.user?.objectId) {
                circleDynamicBean.user?.followed = eventFollowerChange.isFollowed
                if (postShowWhere == PostItemShowWhere.Detail) {
                    holder.key.followUser.visibility =
                        if (circleDynamicBean.user?.followed == true) View.GONE else View.VISIBLE
                } else {
                    holder.key.followUser.visibility = View.GONE
                }
            }
        }
        //是否更新下面的数据
        if (!updataData) {
            return
        }
        //因为只有在详情页面有关注按钮。所以不需要更新之后的数据了。因为这个列表只有这一个动态 item
    }

    /**
     *  投票
     *  @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDynamicVoter(event: EventDynamicVoter) {
        val bean = CircleDynamicBean()
        bean.id = event.dynamicId
        bean.voteOptions = event.votes
        upDateView(event.dynamicId, bean, upDateVoter, reloadVouter)
    }

    /**
     *  登录
     *  @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDynamicVoter(event: EventIsAuthorized) {
        upDataFollowView()
    }

    private fun upDateViewRemoveAccount(
        userId: String,
        fromDynamicBean: CircleDynamicBean?,
        upDateRemove: (Int, CircleDynamicBean, CircleDynamicBean?) -> Unit,
        reloadFun: (CircleDynamicBean, DynamicItemViewHolder) -> Unit
    ) {
        if (userId.isEmpty()) {
            return
        }
        removeViewHolderByUserId(userId)
    }

    private fun removeViewHolderByUserId(userId: String) {
        val dataList = adapter.getDataList()
        val listIterator = dataList.iterator()
        while (listIterator.hasNext()) {
            val entity = listIterator.next() as? CircleDynamicBean
            entity?.user?.id?.let {
                if (userId == it) {
                    val itemPosition = adapter.getItemPosition(entity)
                    listIterator.remove()
                    if (itemPosition >= 0) {
                        if (adapter.itemCount > 1) {
                            adapter.notifyItemRemoved(adapter.headerLayoutCount + itemPosition)
                        } else {
                            adapter.notifyDataSetChanged()
                        }
                    }
                } else {
                    print("error")
                }
            }
        }
    }

    private fun hideDataByUserId(
        userId: String,
        dynamicBean: CircleDynamicBean?,
        upDataFun: (Int, CircleDynamicBean, CircleDynamicBean?) -> Unit
    ) {
        val dataList = adapter.getDataList()
        for (index in dataList.indices) {
            val circleDynamicBean = dataList[index] as? CircleDynamicBean
            if (userId == circleDynamicBean?.user?.id) {
                upDataFun(index, circleDynamicBean, dynamicBean)
            }
        }
    }

    private fun upDateView(
        dynamicId: String, fromDynamicBean: CircleDynamicBean?,
        upDataFun: (position: Int, toData: CircleDynamicBean, fromData: CircleDynamicBean?) -> Unit,
        reloadFun: (dynamicItem: CircleDynamicBean, holder: DynamicItemViewHolder) -> Unit
    ) {
        if (TextUtils.isEmpty(dynamicId)) {
            return
        }
        for (holder in holders) {
            if (dynamicId == holder.value) {
                val dataPosition = adapter.getDataPosition(holder.key)
                if (dataPosition >= 0 && dataPosition < adapter.getDataList().size) {
                    val circleDynamicBean =
                        adapter.getDataList()[dataPosition] as? CircleDynamicBean
                    circleDynamicBean?.let {
                        upDataFun(dataPosition, circleDynamicBean, fromDynamicBean)
                        reloadFun(circleDynamicBean, holder.key)
                    }
                }
            }
        }
        //是否更新下面的数据
        if (!updataData) {
            return
        }
        upDateHideDataList(dynamicId, fromDynamicBean, upDataFun)
    }

    private fun upDataFollowView() {
        for (holder in holders) {
            val dataPosition = adapter.getDataPosition(holder.key)
            val circleDynamicBean = adapter.getDataList()[dataPosition] as? CircleDynamicBean
            circleDynamicBean?.let {
                if (it.user?.id == LoginUtils.getCurrentUser()?.id) {
                    holder.key.followUser.hide(true)
                }
            }
        }
    }

    private fun upDateHideDataList(
        dynamicId: String,
        dynamicBean: CircleDynamicBean?,
        upDataFun: (position: Int, toData: CircleDynamicBean, fromData: CircleDynamicBean?) -> Unit
    ) {
        val dataList = adapter.getDataList()
        for (index in dataList.indices) {
            val circleDynamicBean = dataList[index] as? CircleDynamicBean
            if (dynamicId == circleDynamicBean?.id) {
                upDataFun(index, circleDynamicBean, dynamicBean)
            }
        }
    }

    private val upDateFavored =
        fun(_: Int, toData: CircleDynamicBean, formData: CircleDynamicBean?) {
            formData?.let {
                toData.favored = formData.favored
                toData.favorCount = formData.favorCount
            }
        }
    private val upDateCollect =
        fun(_: Int, toData: CircleDynamicBean, formData: CircleDynamicBean?) {
            formData?.let {
                toData.isCollect = formData.isCollect
            }
        }

    private val upDateComment =
        fun(_: Int, toData: CircleDynamicBean, formData: CircleDynamicBean?) {
            formData?.let {
                toData.commentCount = formData.commentCount
            }
        }
    private val upDateRemove = fun(position: Int, _: CircleDynamicBean, _: CircleDynamicBean?) {
    }
    private val upDateFollower =
        fun(_: Int, toData: CircleDynamicBean, formData: CircleDynamicBean?) {
            toData.user?.followed = formData?.user?.followed
        }
    private val upDateVoter = fun(_: Int, toData: CircleDynamicBean, formData: CircleDynamicBean?) {
        formData?.voteOptions?.let {
            toData.voteOptions = it
        }
    }
    private val reloadComment = fun(item: CircleDynamicBean, holder: DynamicItemViewHolder) {
        holder.tvCommentCount.text = if (item.commentCount <= 0) {
            "评论"
        } else {
            "${item.commentCount}"
        }
        holder.detailPlayer.commentCount = item.commentCount
    }
    private val reloadCollect = fun(item: CircleDynamicBean, holder: DynamicItemViewHolder) {
        holder.imageViewCollection.isSelected = item.isCollect == true
        holder.tvCollection.isSelected = item.isCollect == true
        holder.detailPlayer.isCollected = item.isCollect ?: false
    }


    private val reloadLike = fun(dynamicItem: CircleDynamicBean, holder: DynamicItemViewHolder) {
        holder.tvRaiseCount.text = if (dynamicItem.favorCount <= 0) {
            "点赞"
        } else {
            "${dynamicItem.favorCount}"
        }
        holder.ivRaiseSelected.isSelected = dynamicItem.favored
        holder.tvRaiseCount.isSelected = dynamicItem.favored
        holder.detailPlayer.apply {
            isFavored = dynamicItem.favored
            favorCount = dynamicItem.favorCount
        }
        Logger.e("reloadLike,", "" + dynamicItem.favored + ",", dynamicItem.content)
    }
    private val reloadRemove = fun(dynamicItem: CircleDynamicBean, holder: DynamicItemViewHolder) {
        val dataPosition = adapter.getDataPosition(holder)
        Logger.e("reloadRemove,", dynamicItem.id, "," + dataPosition)
        adapter.data.removeAt(dataPosition)
        if (adapter.data.size == 0) {
            adapter.notifyDataSetChanged()
        } else {
            adapter.notifyItemRemoved(adapter.headerLayoutCount + dataPosition)
        }
    }
    private val reloadFollower =
        fun(dynamicItem: CircleDynamicBean, holder: DynamicItemViewHolder) {
            val user: CircleUserBean? = dynamicItem.user
            val currentUser = LoginUtils.getCurrentUser()
            if (dynamicItem.showWhere === ShowWhere.DETAIL) {
                if (user?.id == currentUser?.id) {
                    holder.followUser.visibility = View.GONE
                } else {
                    if (user?.followed == true) {
                        holder.followUser.visibility = View.GONE
                    } else {
                        holder.followUser.visibility = View.VISIBLE
                    }
                }
            } else {
                holder.followUser.visibility = View.GONE
            }
        }
    private val reloadVouter = fun(dynamicItem: CircleDynamicBean, holder: DynamicItemViewHolder) {
        holder.voteView.updateVote(
            dynamicItem.voteOptions,
            dynamicItem.user?.id == LoginUtils.getCurrentUser()?.id
        )
    }

    override fun onHolderConvert(entity: Any, holder: BaseViewHolder, lifecycle: Lifecycle?) {
        if (entity is CircleDynamicBean && holder is DynamicItemViewHolder) {
            onHolderBind(entity, holder)
            if (holder.ivWebLinkVideoLayout.isVisible) {
                lifecycle?.addObserver(holder.detailPlayer)
            }
        }
    }
}
