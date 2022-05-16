package cool.dingstock.monitor.ui.recommend.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.chad.library.adapter.base.binder.BaseItemBinder
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.monitor.MonitorRecommendChannelEntity
import cool.dingstock.appbase.entity.event.monitor.EventChangeMonitorState
import cool.dingstock.appbase.entity.event.monitor.EventMonitorShade
import cool.dingstock.appbase.entity.event.monitor.EventRefreshMonitorPage
import cool.dingstock.appbase.entity.event.monitor.MonitorEventSource
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.imagepicker.views.roundImageView.RoundImageView
import cool.dingstock.monitor.R
import cool.dingstock.monitor.ui.viewmodel.BaseMonitorViewHolder
import cool.dingstock.monitor.utils.MonitorHelper
import org.greenrobot.eventbus.EventBus

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/4/25 16:41
 * @Version:         1.1.0
 * @Description:
 */
class MonitorChannelItemBinder :
    BaseItemBinder<MonitorRecommendChannelEntity, MonitorChannelItemBinder.MonitorChannelViewHolder>() {
    var source = ""

    class MonitorChannelViewHolder(itemView: View) : BaseMonitorViewHolder(itemView) {
        val ivVipTag: ImageView = itemView.findViewById(R.id.iv_vip_tag)
        val root: RelativeLayout = itemView.findViewById(R.id.monitor_channel_root)
        val ivCover: RoundImageView = itemView.findViewById(R.id.round_iv_channel)
        val tvChannelTitle: TextView = itemView.findViewById(R.id.recommend_channel_title)
        val tvChannelState: TextView = itemView.findViewById(R.id.recommend_channel_status)
        val ivTag: ImageView = itemView.findViewById(R.id.monitor_channel_state)
        val channelLayout: LinearLayout = itemView.findViewById(R.id.layout_monitor_unsub)

        companion object {
            fun create(parent: ViewGroup, source: String): MonitorChannelViewHolder {
                val vh = MonitorChannelViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_monitor_channel, parent, false)
                )
                vh.source = source
                return vh
            }
        }
    }

    override fun convert(holder: MonitorChannelViewHolder, data: MonitorRecommendChannelEntity) {
        if (data.isTail == true) {
            holder.root.setBackgroundResource(R.drawable.monitor_shape_white_8dp_bottom)
        } else {
            holder.root.setBackgroundResource(R.color.white)
        }
        holder.ivCover.load(data.iconUrl)
        holder.tvChannelTitle.text = data.name
        //设置标签
        when {
            data.isNew -> {
                holder.ivTag.setImageResource(R.drawable.monitor_ic_recommend_new)
                holder.ivTag.visibility = View.VISIBLE
            }
            data.isHot -> {
                holder.ivTag.setImageResource(R.drawable.monitor_ic_recommend_hot)
                holder.ivTag.visibility = View.VISIBLE
            }
            else -> {
                holder.ivTag.visibility = View.GONE
            }
        }
        MonitorHelper.updateStateUI(
            data.subscribed,
            holder.ivVipTag,
            holder.channelLayout,
            holder.tvChannelState,
            data.restricted
        )
        holder.root.setOnClickListener {
            UTHelper.commonEvent(
                UTConstant.Monitor.ChannelP_ent,
                "source",
                "推荐列表",
                "ChannelName",
                data.name
            )
            UTHelper.commonEvent(
                UTConstant.Monitor.RecommendP_click_RecommendedChannel,
                "ChannelName",
                data.name
            )
            holder.routeToChannelDetail(data.objectId)
        }
        holder.channelLayout.setOnClickListener {
            UTHelper.commonEvent(
                UTConstant.Monitor.RecommendP_click_MonitorButton,
                "objectId",
                data.objectId,
                "ChannelName",
                data.name
            )
            holder.switchMonitorState(data.objectId,
                !data.subscribed,
                restricted = data.restricted,
                channelName = data.name,
                success = {
                    val targetState = data.subscribed
                    data.subscribed = !targetState
                    MonitorHelper.updateStateUI(
                        data.subscribed,
                        holder.ivVipTag,
                        holder.channelLayout,
                        holder.tvChannelState,
                        data.restricted
                    )
                    EventBus.getDefault()
                        .post(EventRefreshMonitorPage(MonitorEventSource.RECOMMEND))
                    EventBus.getDefault()
                        .post(EventChangeMonitorState(data.objectId, data.subscribed, holder))
                })
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonitorChannelViewHolder {
        return MonitorChannelViewHolder.create(parent, source)
    }

    override fun onViewAttachedToWindow(holder: MonitorChannelViewHolder) {
        super.onViewAttachedToWindow(holder)
        val position = holder.adapterPosition
        if (position == 1 && !MaskUtils.hasPost) {
            val childOne = holder.itemView
            val subjectView = childOne.findViewById<View>(R.id.layout_monitor_unsub)
            var listener: ViewTreeObserver.OnGlobalLayoutListener? = null
            listener = ViewTreeObserver.OnGlobalLayoutListener {
                subjectView.viewTreeObserver.removeOnGlobalLayoutListener(listener)
                EventBus.getDefault().post(EventMonitorShade(subjectView))
            }
            subjectView.viewTreeObserver.addOnGlobalLayoutListener(listener)
            MaskUtils.hasPost = true
        }
    }
}

object MaskUtils {
    var hasPost = false
}