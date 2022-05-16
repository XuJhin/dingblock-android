package cool.dingstock.post.helper

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.adapter.delegation.HolderReloadDelegation
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicDetailCommentsBean
import cool.dingstock.appbase.entity.event.update.EventMedalWear
import cool.dingstock.appbase.entity.event.update.EventUpdateAvatar
import cool.dingstock.appbase.entity.event.update.EventUpdatePendant
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.post.adapter.holder.DynamicCommentViewHolder
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CommentReload(val adapter: DcBaseBinderAdapter) : HolderReloadDelegation {
    private var holders = HashMap<DynamicCommentViewHolder, String>()

    private fun unRegister() {
        holders.clear()
        EventBus.getDefault().unregister(this)
    }

    private fun register() {
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPendantReceived(event: EventUpdatePendant) {
        for (holder in holders) {
            val dataPosition = adapter.getDataPosition(holder.key)
            val circleCommentBean = adapter.getDataList()[dataPosition] as? CircleDynamicDetailCommentsBean
            if (event.userId == circleCommentBean?.user?.objectId) {
                circleCommentBean?.user?.avatarPendantUrl = event.pendantUrl
                holder.key.userIv.setPendantUrl(event.pendantUrl)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAvatarReceived(event: EventUpdateAvatar) {
        for (holder in holders) {
            val dataPosition = adapter.getDataPosition(holder.key)
            val circleCommentBean = adapter.getDataList()[dataPosition] as? CircleDynamicDetailCommentsBean
            if (event.userId == circleCommentBean?.user?.objectId) {
                circleCommentBean?.user?.avatarUrl = event.avatarUrl ?: ""
                holder.key.userIv.setAvatarUrl(event.avatarUrl)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMedalWearReceived(event: EventMedalWear) {
        for (holder in holders) {
            val dataPosition = adapter.getDataPosition(holder.key)
            val circleCommentBean = adapter.getDataList()[dataPosition] as? CircleDynamicDetailCommentsBean
            if (event.userId == circleCommentBean?.user?.objectId) {
                circleCommentBean?.user?.achievementIconUrl = event.iconUrl
                holder.key.imgMedal.hide(event.iconUrl.isNullOrEmpty())
                holder.key.imgMedal.load(event.iconUrl)
            }
        }
    }

    override fun onHolderConvert(entity: Any, holder: BaseViewHolder, lifecycle: Lifecycle?) {
        if (entity is CircleDynamicDetailCommentsBean && holder is DynamicCommentViewHolder) {
            entity.objectId?.let {
                holders[holder] = it
            }
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                register()
            }
            Lifecycle.Event.ON_PAUSE -> {
                when (source) {
                    is Fragment -> {
                        if (source.activity?.isFinishing == true) {
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
}