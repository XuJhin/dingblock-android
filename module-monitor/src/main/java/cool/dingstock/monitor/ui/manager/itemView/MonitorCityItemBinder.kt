package cool.dingstock.monitor.ui.manager.itemView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.binder.BaseItemBinder
import cool.dingstock.appbase.entity.bean.monitor.MonitorCityEntity
import cool.dingstock.monitor.R
import cool.dingstock.monitor.ui.viewmodel.BaseMonitorViewHolder


class MonitorCityItemBinder : BaseItemBinder<MonitorCityEntity, MonitorCityItemBinder.HeaderViewHolder>() {
	inner class HeaderViewHolder(itemView: View) : BaseMonitorViewHolder(itemView) {
		val tvChannelName: TextView = itemView.findViewById(R.id.tv_monitor_channel_name)
	}

	override fun convert(holder: HeaderViewHolder, data: MonitorCityEntity) {
		updateStateUI(holder, data)
	}

	private fun updateStateUI(holder: HeaderViewHolder, data: MonitorCityEntity) {
		holder.tvChannelName.apply {
			text = data.name
			isSelected = data.isSelect
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
		return HeaderViewHolder(LayoutInflater.from(context)
				.inflate(R.layout.item_monitor_region_channel, parent, false)
		)
	}
}