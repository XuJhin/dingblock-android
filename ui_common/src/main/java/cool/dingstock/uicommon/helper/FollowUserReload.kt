package cool.dingstock.uicommon.helper

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.adapter.delegation.HolderReloadDelegation
import cool.dingstock.appbase.entity.bean.mine.AccountInfoBriefEntity
import cool.dingstock.appbase.entity.event.circle.EventFollowerChange
import cool.dingstock.uicommon.mine.item.FollowItemViewHolder
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/28  10:05
 */
class FollowUserReload(val adapter: DcBaseBinderAdapter) : LifecycleEventObserver, HolderReloadDelegation {
	var holders = HashMap<FollowItemViewHolder, String>()
	var updataData = true

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
			Lifecycle.Event.ON_DESTROY -> {
				unRegister()
			}
		}
	}

	fun onHolderBind(dynamicItem: AccountInfoBriefEntity, holder: FollowItemViewHolder) {
		dynamicItem.id.let {
			holders[holder] = it
		}
	}

	/**
	 *  关注
	 *  @param eventFollowerChange
	 */
	@SuppressLint("SetTextI18n")
	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onDynamicFollower(eventFollowerChange: EventFollowerChange) {
		for (holder in holders) {
			if (holder.value == eventFollowerChange.userId) {
				val dataPosition = adapter.getDataPosition(holder.key)
				val followUser = adapter.getDataList()[dataPosition] as? AccountInfoBriefEntity
				if (eventFollowerChange.userId == followUser?.id) {
					followUser.followed = eventFollowerChange.isFollowed
					followUser.fansCount.let {
						if (eventFollowerChange.isFollowed) {
							followUser.fansCount = it + 1
						} else {
							followUser.fansCount = it - 1
						}
					}
					val holder = holder.key
					if (followUser.followed) {
						holder.tvFollowState.text = "已关注"
					} else {
						holder.tvFollowState.text = "关注"
					}
					holder.tvUserFollowedNumber.apply {
						text = "${followUser.fansCount}粉丝"
					}
					holder.tvFollowState.isSelected = followUser.followed
				}
			}
		}
	}

	override fun onHolderConvert(entity: Any, holder: BaseViewHolder, lifecycle: Lifecycle?) {
		if (entity is AccountInfoBriefEntity && holder is FollowItemViewHolder) {
			onHolderBind(entity, holder)
		}
	}
}
