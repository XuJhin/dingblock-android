package cool.dingstock.monitor.ui.recommend.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.chad.library.adapter.base.binder.BaseItemBinder
import cool.dingstock.appbase.constant.MonitorConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.monitor.MonitorRecommendEntity
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.monitor.R
import cool.dingstock.monitor.ui.viewmodel.BaseMonitorViewHolder

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/4/25 16:29
 * @Version:         1.1.0
 * @Description:
 */
class MonitorChannelTitleItemBinder : BaseItemBinder<MonitorRecommendEntity, MonitorChannelTitleItemBinder.MonitorChannelTitleViewHolder>() {
	class MonitorChannelTitleViewHolder(itemView: View) : BaseMonitorViewHolder(itemView) {
		val title: TextView = itemView.findViewById(R.id.monitor_recommend_item_title)
		val monitorRoot: RelativeLayout =
				itemView.findViewById(R.id.layout_monitor_recommend_item_header)

		companion object {
			fun create(parent: ViewGroup): MonitorChannelTitleViewHolder {
				return MonitorChannelTitleViewHolder(LayoutInflater
						.from(parent.context)
						.inflate(R.layout.monitor_item_channels_title, parent, false))
			}
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonitorChannelTitleViewHolder {
		return MonitorChannelTitleViewHolder.create(parent)
	}

	override fun convert(holder: MonitorChannelTitleViewHolder, data: MonitorRecommendEntity) {
		holder.title.text = data.name
		holder.monitorRoot.setOnClickListener {
			UTHelper.commonEvent(UTConstant.Monitor.RecommendP_click_ThematicEntrance, "TopicName", data.name)
			val url = MonitorConstant.Uri.MONITOR_TOPIC + "?id=" + data.id
			DcUriRequest(holder.itemView.context, url)
					.start()
		}
	}
}