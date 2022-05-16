package cool.dingstock.monitor.ui.manager.itemView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.binder.BaseItemBinder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.entity.bean.monitor.ChannelHeaderEntity
import cool.dingstock.monitor.R

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/12/25 11:22
 * @Version:         1.1.0
 * @Description:
 */
class ChannelHeaderItemBinder : BaseItemBinder<ChannelHeaderEntity, ChannelHeaderItemBinder.HeaderViewHolder>() {
	inner class HeaderViewHolder(itemView: View) : BaseViewHolder(itemView) {
		val channelTxt: TextView = itemView.findViewById(R.id.tv_header_name)
	}

	override fun convert(holder: HeaderViewHolder, data: ChannelHeaderEntity) {
		holder.channelTxt.apply {
			text = data.title
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
		return HeaderViewHolder(LayoutInflater.from(context)
				.inflate(R.layout.item_monitor_channel_header, parent, false)
		)
	}
}