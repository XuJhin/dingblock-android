package cool.dingstock.monitor.ui.manager.itemView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.monitor.MonitorRecommendChannelEntity
import cool.dingstock.appbase.entity.event.monitor.EventChangeMonitorState
import cool.dingstock.appbase.entity.event.monitor.EventRefreshMonitorPage
import cool.dingstock.appbase.entity.event.monitor.MonitorEventSource
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.imagepicker.views.roundImageView.RoundImageView
import cool.dingstock.monitor.R
import cool.dingstock.monitor.ui.viewmodel.BaseMonitorViewHolder
import cool.dingstock.monitor.utils.MonitorHelper
import org.greenrobot.eventbus.EventBus

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/12/25 11:22
 * @Version:         1.1.0
 * @Description:
 */
open class ChannelOnLineItemBinder : DcBaseItemBinder<MonitorRecommendChannelEntity, ChannelOnLineItemBinder.ChannelOnLineViewHolder>() {
	var source = ""


	inner class ChannelOnLineViewHolder(itemView: View) : BaseMonitorViewHolder(itemView) {
		val rivChannel: RoundImageView =
				itemView.findViewById(R.id.round_iv_channel)
		val channelName: TextView = itemView.findViewById(R.id.recommend_channel_title)
		val channelMonitorState: TextView =
				itemView.findViewById(R.id.recommend_channel_status)
		val channelLayout: LinearLayout = itemView.findViewById(R.id.layout_monitor_unsub)
		val channelDetail: TextView = itemView.findViewById(R.id.tv_monitor_channel_desc)
		val channelRoot: ConstraintLayout = itemView.findViewById(R.id.monitor_channel_root)
		val channelTag: ImageView = itemView.findViewById(R.id.monitor_channel_state)
		val vipTag: ImageView = itemView.findViewById(R.id.iv_vip_tag)
		val ruleTv: TextView = itemView.findViewById(R.id.rule_effect_tv)
	}

	override fun onConvert(holder: ChannelOnLineViewHolder, data: MonitorRecommendChannelEntity) {
		holder.rivChannel.load(data.iconUrl)
		holder.channelName.text = data.name
		when {
			data.isHot -> {
				holder.channelTag.visibility = View.VISIBLE
				holder.channelTag.setImageResource(R.drawable.monitor_ic_recommend_hot)
			}
			data.isNew -> {
				holder.channelTag.visibility = View.VISIBLE
				holder.channelTag.setImageResource(R.drawable.monitor_ic_recommend_new)
			}
			else -> {
				holder.channelTag.visibility = View.GONE
			}
		}
		MonitorHelper.updateStateUI(data.subscribed,holder.vipTag,holder.channelLayout,holder.channelMonitorState,data.restricted)
		holder.channelLayout.setOnClickListener {
			switchMonitorState(holder, data)
		}
		holder.ruleTv.hide(!data.customRuleEffective || !data.subscribed)
	}

	open fun switchMonitorState(holder: ChannelOnLineViewHolder, data: MonitorRecommendChannelEntity) {
		UTHelper.commonEvent(UTConstant.Monitor.AllChannelP_click_MonitorButton, "ChannelName", data.name)
		// TODO: 2021/7/8
		holder.switchMonitorState(data.objectId, !data.subscribed,
				restricted = data.restricted ,channelName = data.name,success = {
			EventBus.getDefault().post(EventRefreshMonitorPage(MonitorEventSource.MANAGER))
			EventBus.getDefault().post(EventChangeMonitorState(data.objectId,
					!data.subscribed, holder))
		})
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelOnLineViewHolder {
		val vh = ChannelOnLineViewHolder(LayoutInflater.from(context)
			.inflate(R.layout.item_monitor_group_channel, parent, false))
		vh.source = source
		return vh

	}

}