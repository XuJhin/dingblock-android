package cool.dingstock.monitor.ui.recommend.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseBinderAdapter
import com.chad.library.adapter.base.binder.BaseItemBinder
import cool.dingstock.appbase.constant.MonitorConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.monitor.MonitorRecommendChannelEntity
import cool.dingstock.appbase.entity.bean.monitor.MonitorRecommendEntity
import cool.dingstock.appbase.entity.event.monitor.EventChangeMonitorState
import cool.dingstock.appbase.entity.event.monitor.EventMonitorShade
import cool.dingstock.appbase.entity.event.monitor.EventRefreshMonitorPage
import cool.dingstock.appbase.entity.event.monitor.MonitorEventSource
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.imagepicker.views.roundImageView.RoundImageView
import cool.dingstock.monitor.R
import cool.dingstock.monitor.ui.recommend.entity.ScrollChannelEntity
import cool.dingstock.monitor.ui.viewmodel.BaseMonitorViewHolder
import cool.dingstock.monitor.utils.MonitorHelper
import org.greenrobot.eventbus.EventBus

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/12/24 12:19
 * @Version:         1.1.0
 * @Description:
 */
@Deprecated("")
class MonitorRecommendItemBinder(val sharePool: RecyclerView.RecycledViewPool) :
    BaseItemBinder<MonitorRecommendEntity, MonitorRecommendItemBinder.MonitorRecommendViewHolder>() {
    companion object {
        const val DEFAULT_CHUNK = 3
    }

    override fun convert(holder: MonitorRecommendViewHolder, data: MonitorRecommendEntity) {
        holder.title.text = data.name
        holder.layoutTitle.setOnClickListener {
            UTHelper.commonEvent(
                UTConstant.Monitor.RecommendP_click_ThematicEntrance,
                "TopicName",
                data.name
            )
            val url = MonitorConstant.Uri.MONITOR_TOPIC + "?id=" + data.id
            DcUriRequest(holder.itemView.context, url)
                .start()
        }
        setupChannels(data.channelList, holder)
    }

    private fun setupChannels(
        channelList: MutableList<MonitorRecommendChannelEntity>,
        holder: MonitorRecommendViewHolder
    ) {
        if (channelList.isNullOrEmpty()) {
            return
        }
        holder.channelsContent.removeAllViews()
        channelList.forEach {
            addChannelItem(holder, it)
        }
    }

    private fun addChannelItem(
        holder: MonitorRecommendItemBinder.MonitorRecommendViewHolder,
        entity: MonitorRecommendChannelEntity
    ) {
        val channelInfo = LayoutInflater.from(holder.itemView.context)
            .inflate(R.layout.item_monitor_channel, holder.channelsContent, false)
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
        MonitorHelper.updateStateUI(
            entity.subscribed,
            ivVipTag,
            channelLayout,
            tvChannelState,
            entity.restricted
        )
        root.setOnClickListener {
            UTHelper.commonEvent(
                UTConstant.Monitor.ChannelP_ent,
                "source",
                "推荐列表",
                "ChannelName",
                entity.name
            )
            holder.routeToChannelDetail(entity.objectId)
        }
        tvChannelState.setOnClickListener {
            UTHelper.commonEvent(
                UTConstant.Monitor.RecommendP_click_MonitorButton,
                "objectId",
                entity.objectId
            )
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
        holder.channelsContent.addView(channelInfo)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonitorRecommendViewHolder {
        val viewholder = MonitorRecommendViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.monitor_item_recommend_title, parent, false)
        )
        return viewholder
    }

    override fun onViewAttachedToWindow(holder: MonitorRecommendViewHolder) {
        super.onViewAttachedToWindow(holder)
        val position = holder.adapterPosition
        if (position == 0 && !MaskUtils.hasPost) {
            if (holder.channelsContent.childCount <= 0) {
                EventBus.getDefault().post(EventMonitorShade(null))
                MaskUtils.hasPost = true
                return
            }
            val childOne = holder.channelsContent.getChildAt(0)
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

    /**
     * viewHolder
     */
    inner class MonitorRecommendViewHolder(itemView: View) : BaseMonitorViewHolder(itemView) {
        val channelsContent: LinearLayout = itemView.findViewById(R.id.monitor_channels_content)
        val title: TextView = itemView.findViewById(R.id.monitor_recommend_item_title)
        val layoutTitle: RelativeLayout =
            itemView.findViewById(R.id.layout_monitor_recommend_item_header)
        val channelAdapter: BaseBinderAdapter by lazy { BaseBinderAdapter() }
        val channelIdSet: HashSet<String> = hashSetOf()

        init {
            val manager = LinearLayoutManager(itemView.context).apply {
                isAutoMeasureEnabled = true
            }
            // 需要注意：要使用RecycledViewPool的话,如果使用的LayoutManager是LinearLayoutManager或其子类（如GridLayoutManager），需要手动开启这个特性
            channelAdapter.addItemBinder(
                ScrollChannelEntity::class.java,
                RecommendChannelItemBinder()
            )
            manager.apply {
                recycleChildrenOnDetach = true
                orientation = LinearLayoutManager.HORIZONTAL
                stackFromEnd = false
            }
        }
    }
}

