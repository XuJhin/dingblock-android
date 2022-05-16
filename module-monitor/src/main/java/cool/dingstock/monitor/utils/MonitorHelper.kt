package cool.dingstock.monitor.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.monitor.R


/**
 * 类名：MonitorHelper
 * 包名：cool.dingstock.monitor.utils
 * 创建时间：2021/9/9 5:58 下午
 * 创建人： WhenYoung
 * 描述：
 **/
object MonitorHelper {

    fun updateStateUI(
        subscribed: Boolean,
        ivVipTag: ImageView,
        channelLayout: View,
        tvChannelState: TextView,
        restricted: Boolean
    ) {
        //设置订阅状态
        channelLayout.isSelected = subscribed
        if (subscribed) {
            if (LoginUtils.getCurrentUser()?.isVip() == true) {
                tvChannelState.text = "监控中"
            } else {
                if (restricted) {
                    tvChannelState.text = "续费"
                } else {
                    tvChannelState.text = "监控中"
                }
            }
        } else {
            tvChannelState.text = "监控"
        }
        if (!restricted) {
            ivVipTag.hide()
        } else {
            if (!LoginUtils.isLogin()) {
                ivVipTag.hide(false)
            } else {
                if (LoginUtils.getCurrentUser()?.isVip() == false) {
                    ivVipTag.hide(false)
                } else {
                    ivVipTag.hide()
                }
            }
        }
        val context = ivVipTag.context
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
}

