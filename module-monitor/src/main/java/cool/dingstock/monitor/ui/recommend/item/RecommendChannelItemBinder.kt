package cool.dingstock.monitor.ui.recommend.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.binder.BaseItemBinder
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.monitor.MonitorRecommendChannelEntity
import cool.dingstock.appbase.entity.event.monitor.EventChangeMonitorState
import cool.dingstock.appbase.entity.event.monitor.EventMonitorShade
import cool.dingstock.appbase.entity.event.monitor.EventRefreshMonitorPage
import cool.dingstock.appbase.entity.event.monitor.MonitorEventSource
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.imagepicker.views.roundImageView.RoundImageView
import cool.dingstock.lib_base.util.ScreenUtils
import cool.dingstock.monitor.R
import cool.dingstock.monitor.ui.recommend.entity.ScrollChannelEntity
import cool.dingstock.monitor.ui.viewmodel.BaseMonitorViewHolder
import cool.dingstock.monitor.utils.MonitorHelper
import org.greenrobot.eventbus.EventBus

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author: xujing
 * @Date: 2020/12/24 15:01
 * @Version: 1.1.0
 * @Description:
 */
@Deprecated("废弃")
class RecommendChannelItemBinder :
    BaseItemBinder<ScrollChannelEntity, RecommendChannelItemBinder.RecommendChannelViewHolder>() {
    private var itemHeight = 0f
    override fun convert(holder: RecommendChannelViewHolder, data: ScrollChannelEntity) {
        holder.contentMonitorChannel.removeAllViews()
        //遍历添加数据
        if (data.items.isNullOrEmpty()) {
            return
        }
        data.items.forEach {
            addChannelItem(holder, it)
        }
        //调整宽度(露出右边一部分)
        val layoutParams = holder.contentMonitorChannel.layoutParams as RecyclerView.LayoutParams
        layoutParams.width = (ScreenUtils.getScreenWidth(holder.itemView.context) - 45.dp).toInt()
        //如果不为第一栏 调整高度为3项的高度
        if (holder.adapterPosition != 0) {
            layoutParams.height = (itemHeight * 3).toInt()
        } else {
            layoutParams.height = (itemHeight * data.items.size).toInt()
        }
        holder.contentMonitorChannel.layoutParams = layoutParams
        holder.contentMonitorChannel.setBackgroundResource(R.drawable.monitor_shape_white_6dp)
    }

    private fun addChannelItem(
        holder: RecommendChannelViewHolder,
        entity: MonitorRecommendChannelEntity
    ) {
        val channelInfo = LayoutInflater.from(holder.itemView.context)
            .inflate(R.layout.item_monitor_channel, holder.contentMonitorChannel, false)
        val ivVipTag: ImageView = channelInfo.findViewById(R.id.iv_vip_tag)
        val root: RelativeLayout = channelInfo.findViewById(R.id.monitor_channel_root)
        val ivCover: RoundImageView = channelInfo.findViewById(R.id.round_iv_channel)
        val tvChannelTitle: TextView = channelInfo.findViewById(R.id.recommend_channel_title)
        val tvChannelState: TextView = channelInfo.findViewById(R.id.recommend_channel_status)
        val ivTag: ImageView = channelInfo.findViewById(R.id.monitor_channel_state)
        val channelLayout: LinearLayout = channelInfo.findViewById(R.id.layout_monitor_unsub)
        ivCover.load(entity.iconUrl)
        tvChannelTitle.text = entity.name
        //设置标签
        when {
            entity.isNew -> {
                ivTag.setImageResource(R.drawable.monitor_ic_recommend_new)
                ivTag.visibility = View.VISIBLE
            }
            entity.isHot -> {
                ivTag.setImageResource(R.drawable.monitor_ic_recommend_hot)
                ivTag.visibility = View.VISIBLE
            }
            else -> {
                ivTag.visibility = View.GONE
            }
        }
        MonitorHelper.updateStateUI(entity.subscribed, ivVipTag, channelLayout, tvChannelState, entity.restricted)
        root.setOnClickListener {
            UTHelper.commonEvent(UTConstant.Monitor.ChannelP_ent, "source", "推荐列表","ChannelName",entity.name)
            holder.routeToChannelDetail(entity.objectId)
        }
        tvChannelState.setOnClickListener {
            holder.switchMonitorState(entity.objectId,
                !entity.subscribed,
                restricted = entity.restricted,
                channelName = entity.name,
                success = {
                    val targetState = entity.subscribed
                    entity.subscribed = !targetState
                    MonitorHelper.updateStateUI(
                        entity.subscribed,
                        ivVipTag,
                        channelLayout,
                        tvChannelState,
                        entity.restricted
                    )
                    EventBus.getDefault()
                        .post(EventRefreshMonitorPage(MonitorEventSource.RECOMMEND))
                    EventBus.getDefault()
                        .post(EventChangeMonitorState(entity.objectId, entity.subscribed, holder))
                })
        }
        //动态获取item高度（此时获取width为0，所以采用获取padding的方式进行转换）
        if (itemHeight == 0f) {
            itemHeight = (((channelInfo.paddingTop) * 62) / 16.0).toFloat()
        }
        holder.contentMonitorChannel.addView(channelInfo)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendChannelViewHolder {
        return RecommendChannelViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.monitor_item_recommend_channel, parent, false)
        )
    }

    inner class RecommendChannelViewHolder(itemView: View) : BaseMonitorViewHolder(itemView) {
        val contentMonitorChannel =
            itemView.findViewById<LinearLayout>(R.id.layout_content_monitor_channel)
    }

    override fun onViewAttachedToWindow(holder: RecommendChannelViewHolder) {
        super.onViewAttachedToWindow(holder)
        val position = holder.adapterPosition
        if (position == 0 && !MaskUtils.hasPost) {
            if (holder.contentMonitorChannel.childCount <= 0) {
                EventBus.getDefault().post(EventMonitorShade(null))
                MaskUtils.hasPost = true
                return
            }
            val childOne = holder.contentMonitorChannel.getChildAt(0)
            if (childOne == null) {
                EventBus.getDefault().post(EventMonitorShade(null))
                MaskUtils.hasPost = true
                return
            }
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
