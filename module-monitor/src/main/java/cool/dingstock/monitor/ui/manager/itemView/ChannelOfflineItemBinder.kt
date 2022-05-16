package cool.dingstock.monitor.ui.manager.itemView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.binder.BaseItemBinder
import cool.dingstock.appbase.entity.bean.monitor.RegionChannelBean
import cool.dingstock.appbase.entity.event.monitor.EventChangeMonitorState
import cool.dingstock.appbase.entity.event.monitor.EventRefreshMonitorPage
import cool.dingstock.appbase.entity.event.monitor.MonitorEventSource
import cool.dingstock.monitor.R
import cool.dingstock.monitor.ui.viewmodel.BaseMonitorViewHolder
import org.greenrobot.eventbus.EventBus

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author: xujing
 * @Date: 2020/12/25 11:34
 * @Version: 1.1.0
 * @Description:
 */
class ChannelOfflineItemBinder : BaseItemBinder<RegionChannelBean, ChannelOfflineItemBinder.HeaderViewHolder>() {
	inner class HeaderViewHolder(itemView: View) : BaseMonitorViewHolder(itemView) {
		val tvChannelName: TextView = itemView.findViewById(R.id.tv_monitor_channel_name)
	}

	override fun convert(holder: HeaderViewHolder, data: RegionChannelBean) {
		updateStateUI(holder, data)
		holder.tvChannelName.setOnClickListener {
			holder.switchRegionState(data.objectId,
					targetState = !data.subscribed,
					success = {
						EventBus.getDefault().post(EventChangeMonitorState(data.objectId, !data.subscribed, holder))
						EventBus.getDefault().post(EventRefreshMonitorPage(MonitorEventSource.MANAGER))
					}, failed = {})
		}
	}

	private fun updateStateUI(holder: HeaderViewHolder, data: RegionChannelBean) {
		holder.tvChannelName.apply {
			text = data.name
			isSelected = data.subscribed
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
		return HeaderViewHolder(LayoutInflater.from(context)
				.inflate(R.layout.item_monitor_region_channel, parent, false)
		)
	}
}