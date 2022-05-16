package cool.dingstock.monitor.ui.recommend.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.binder.BaseItemBinder
import cool.dingstock.appbase.entity.bean.monitor.MonitorDivider
import cool.dingstock.monitor.R
import cool.dingstock.monitor.ui.viewmodel.BaseMonitorViewHolder

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/4/25 16:49
 * @Version:         1.1.0
 * @Description:
 */
class MonitorChannelDivider : BaseItemBinder<MonitorDivider, MonitorChannelDivider.DividerViewHolder>() {
	class DividerViewHolder(itemView: View) : BaseMonitorViewHolder(itemView) {
		companion object {
			fun create(parent: ViewGroup): DividerViewHolder {
				return DividerViewHolder(LayoutInflater
						.from(parent.context)
						.inflate(R.layout.monitor_rv_channels_divider, parent, false))
			}
		}
	}

	override fun convert(holder: DividerViewHolder, data: MonitorDivider) {
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DividerViewHolder {
		return DividerViewHolder.create(parent)
	}
}