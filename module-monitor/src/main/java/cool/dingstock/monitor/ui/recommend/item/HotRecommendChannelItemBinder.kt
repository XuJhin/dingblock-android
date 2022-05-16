package cool.dingstock.monitor.ui.recommend.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.binder.BaseItemBinder
import cool.dingstock.appbase.entity.bean.monitor.MonitorRecommendChannelEntity
import cool.dingstock.appbase.entity.event.monitor.EventChangeMonitorState
import cool.dingstock.appbase.entity.event.monitor.EventRefreshMonitorPage
import cool.dingstock.appbase.entity.event.monitor.MonitorEventSource
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.net.api.monitor.MonitorApi
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.monitor.R
import cool.dingstock.monitor.dagger.MonitorApiHelper
import cool.dingstock.monitor.databinding.MonitorHotRecommendItemLayoutBinding
import cool.dingstock.monitor.ui.viewmodel.BaseMonitorViewHolder
import cool.dingstock.monitor.utils.MonitorHelper
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


/**
 * 类名：HotRecommendChannelItemBinder
 * 包名：cool.dingstock.monitor.ui.recommend.item
 * 创建时间：2021/8/30 12:25 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class HotRecommendChannelItemBinder :
    BaseItemBinder<MonitorRecommendChannelEntity, HotRecommendVH>() {

    @Inject
    lateinit var monitorApi: MonitorApi

    var source = ""


    init {
        MonitorApiHelper.apiMonitorComponent.inject(this)
    }

    override fun convert(holder: HotRecommendVH, data: MonitorRecommendChannelEntity) {
        updateStateUI(
            data.subscribed,
            holder.vb.recommendVipTag,
            holder.vb.recommendSubjectLayer,
            holder.vb.recommendChannelStatusTv,
            data.restricted
        )
        holder.vb.recommendIv.load(data.iconUrl)
        holder.vb.recommendTitleTv.text = data.name
        holder.vb.recommendSubjectLayer.setOnShakeClickListener {
            holder.switchMonitorState(data.objectId,
                !data.subscribed,
                restricted = data.restricted,
                channelName = data.name,
                success = {
                    val targetState = data.subscribed
                    data.subscribed = !targetState
                    updateStateUI(
                        data.subscribed,
                        holder.vb.recommendVipTag,
                        holder.vb.recommendSubjectLayer,
                        holder.vb.recommendChannelStatusTv,
                        data.restricted
                    )
                    EventBus.getDefault()
                        .post(EventRefreshMonitorPage(MonitorEventSource.RECOMMEND))
                    EventBus.getDefault()
                        .post(EventChangeMonitorState(data.objectId, data.subscribed, holder))
                })
        }
        holder.vb.root.setOnShakeClickListener {
            holder.routeToChannelDetail(data.objectId ?: data.id)
        }
    }

    private fun updateStateUI(
        subscribed: Boolean,
        ivVipTag: ImageView,
        subLayer: View,
        tvChannelState: TextView,
        restricted: Boolean
    ) {
        MonitorHelper.updateStateUI(subscribed, ivVipTag, subLayer, tvChannelState, restricted)
        //vip标签
        if (!restricted || subscribed) {
            tvChannelState.setTextColor(
                ContextCompat.getColorStateList(
                    context,
                    R.color.monitor_channel_selected
                )
            )
        } else if (!LoginUtils.isLogin()) {
            tvChannelState.setTextColor(
                ContextCompat.getColorStateList(
                    context,
                    R.color.monitor_channel_vip_selected
                )
            )
        } else if (LoginUtils.getCurrentUser()?.isVip() == false) {
            tvChannelState.setTextColor(
                ContextCompat.getColorStateList(
                    context,
                    R.color.monitor_channel_vip_selected
                )
            )
        } else {
            tvChannelState.setTextColor(
                ContextCompat.getColorStateList(
                    context,
                    R.color.monitor_channel_selected
                )
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotRecommendVH {
        return HotRecommendVH(
            MonitorHotRecommendItemLayoutBinding.inflate(
                LayoutInflater.from(
                    context
                ), parent, false
            )
        )
    }

}

class HotRecommendVH(val vb: MonitorHotRecommendItemLayoutBinding) :
    BaseMonitorViewHolder(vb.root) {

}