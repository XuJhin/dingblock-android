package cool.dingstock.home.helper

import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.adapter.delegation.HolderReloadDelegation
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.event.circle.EventCommentCount
import cool.dingstock.appbase.entity.event.circle.EventFavored
import cool.dingstock.appbase.entity.event.circle.EventViewCount
import cool.dingstock.appbase.entity.event.update.EventMedalWear
import cool.dingstock.appbase.entity.event.update.EventUpdateAvatar
import cool.dingstock.appbase.entity.event.update.EventUpdatePendant
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.home.R
import cool.dingstock.imagepicker.views.AvatarView
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.post.view.DcVideoPlayer
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NewSaleReload(val adapter: DcBaseBinderAdapter) : HolderReloadDelegation {
    private var holders = HashMap<BaseViewHolder, String>()

    private fun unRegister() {
        holders.clear()
        EventBus.getDefault().unregister(this)
    }

    private fun register() {
        EventBus.getDefault().register(this)
    }

    private val upDateFavored =
        fun(_: Int, toData: CircleDynamicBean, formData: CircleDynamicBean?) {
            formData?.let {
                toData.favored = formData.favored
                toData.favorCount = formData.favorCount
            }
        }
    private val upDateComment =
        fun(_: Int, toData: CircleDynamicBean, formData: CircleDynamicBean?) {
            formData?.let {
                toData.commentCount = formData.commentCount
            }
        }

    private val updateViewCount = fun(_: Int, toData: CircleDynamicBean, formData: CircleDynamicBean?) {
        formData?.let {
            toData.viewCount = it.viewCount
        }
    }

    private val reloadComment = fun(item: CircleDynamicBean, holder: BaseViewHolder) {
        val textView = holder.itemView.findViewById<TextView>(R.id.tv_post_comment)
        val detailPlayer = holder.itemView.findViewById<DcVideoPlayer>(R.id.detail_player)
        textView.text = if (item.commentCount <= 0) {
            "评论"
        } else {
            "${item.commentCount}"
        }
        detailPlayer.commentCount = item.commentCount
    }
    private val reloadLike = fun(dynamicItem: CircleDynamicBean, holder: BaseViewHolder) {
        val textView = holder.itemView.findViewById<TextView>(R.id.tv_post_raise)
        val imageView = holder.itemView.findViewById<ImageView>(R.id.iv_post_raise)
        val detailPlayer = holder.itemView.findViewById<DcVideoPlayer>(R.id.detail_player)
        textView.text = if (dynamicItem.favorCount <= 0) {
            "点赞"
        } else {
            "${dynamicItem.favorCount}"
        }
        detailPlayer.apply {
            isFavored = dynamicItem.favored
            favorCount = dynamicItem.favorCount
        }
        imageView.isSelected = dynamicItem.favored
        textView.isSelected = dynamicItem.favored
        Logger.e("reloadLike,", "" + dynamicItem.favored + ",", dynamicItem.content)
    }

    private val reloadViewCount = fun(dynamicItem: CircleDynamicBean, holder: BaseViewHolder) {
        val textView = holder.itemView.findViewById<TextView>(R.id.tv_view_count)
        textView.text = "浏览${dynamicItem.viewCount}"
    }

    private fun upDateView(dynamicId: String, fromDynamicBean: CircleDynamicBean?,
                           upDataFun: (position: Int, toData: CircleDynamicBean, fromData: CircleDynamicBean?) -> Unit,
                           reloadFun: (dynamicItem: CircleDynamicBean, holder: BaseViewHolder) -> Unit) {
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
     * 评论数量变化
     *
     * @param eventCommentCount
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCommentCountChange(eventCommentCount: EventCommentCount) {
        upDateView(eventCommentCount.dynamicId, eventCommentCount.entity, upDateComment, reloadComment)
    }

    /**
     * 浏览数量变化
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onViewCountChange(viewCount: EventViewCount) {
        upDateView(viewCount.dynamicId, viewCount.dynamicBean, updateViewCount, reloadViewCount)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPendantReceived(event: EventUpdatePendant) {
        for (holder in holders) {
            val dataPosition = adapter.getDataPosition(holder.key)
            val circleDynamicBean = adapter.getDataList()[dataPosition] as? CircleDynamicBean
            if (event.userId == circleDynamicBean?.user?.objectId) {
                circleDynamicBean?.user?.avatarPendantUrl = event.pendantUrl
                holder.key.itemView.findViewById<AvatarView>(R.id.iv_brand_logo).setPendantUrl(event.pendantUrl)
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
                holder.key.itemView.findViewById<AvatarView>(R.id.iv_brand_logo).setAvatarUrl(event.avatarUrl)
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
                holder.key.itemView.findViewById<ImageView>(R.id.iv_medal).hide(event.iconUrl.isNullOrEmpty())
                holder.key.itemView.findViewById<ImageView>(R.id.iv_medal).load(event.iconUrl)
            }
        }
    }

    private fun onHolderBind(dynamicItem: CircleDynamicBean, holder: BaseViewHolder) {
        dynamicItem.id?.let {
            holders[holder] = it
        }
    }

    override fun onHolderConvert(entity: Any, holder: BaseViewHolder, lifecycle: Lifecycle?) {
        if (entity is CircleDynamicBean) {
            onHolderBind(entity, holder)
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
}