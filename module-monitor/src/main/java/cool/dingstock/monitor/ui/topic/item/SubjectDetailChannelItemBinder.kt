package cool.dingstock.monitor.ui.topic.item

import android.view.View
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.entity.bean.monitor.MonitorRecommendChannelEntity
import cool.dingstock.appbase.entity.event.monitor.EventChangeMonitorState
import cool.dingstock.appbase.entity.event.monitor.EventRefreshMonitorPage
import cool.dingstock.appbase.entity.event.monitor.MonitorEventSource
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.monitor.R
import cool.dingstock.monitor.ui.manager.itemView.ChannelOnLineItemBinder
import org.greenrobot.eventbus.EventBus

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author: xujing
 * @Date: 2020/12/25 18:12
 * @Version: 1.1.0
 * @Description:
 */
class SubjectDetailChannelItemBinder : ChannelOnLineItemBinder() {
    override fun convert(holder: ChannelOnLineViewHolder, data: MonitorRecommendChannelEntity) {
        super.convert(holder, data)
        if (data.desc.isEmpty()) {
            holder.channelDetail.visibility = View.GONE
        } else {
            holder.channelDetail.visibility = View.VISIBLE
            holder.channelDetail.text = data.desc
        }
        holder.channelRoot.apply {
            setBackgroundResource(R.drawable.monitor_shape_white_8dp)
            val padding = 15.dp.toInt()
            setPadding(padding, 0, padding, padding)
        }
    }

    override fun switchMonitorState(
        holder: ChannelOnLineViewHolder,
        data: MonitorRecommendChannelEntity
    ) {
        UTHelper.commonEvent(UTConstant.Monitor.RecommendedTopicP_click_MonitorButton,"ChannelName",data.name)
        holder.source = "专题页频道列表"
        holder.switchMonitorState(data.objectId, !data.subscribed,
            restricted = data.restricted, channelName = data.name, success = {
                EventBus.getDefault().post(EventRefreshMonitorPage(MonitorEventSource.SUBJECT))
                EventBus.getDefault().post(
                    EventChangeMonitorState(
                        data.objectId,
                        !data.subscribed, holder
                    )
                )
            })
    }
}