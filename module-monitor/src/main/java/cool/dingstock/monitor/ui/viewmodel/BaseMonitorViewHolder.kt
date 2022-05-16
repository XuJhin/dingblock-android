package cool.dingstock.monitor.ui.viewmodel

import android.view.View
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.constant.CommonConstant
import cool.dingstock.appbase.constant.MonitorConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.net.api.monitor.MonitorApi
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.monitor.dagger.MonitorApiHelper
import javax.inject.Inject

open class BaseMonitorViewHolder(itemView: View) : BaseViewHolder(itemView) {
    @Inject
    lateinit var monitorApi: MonitorApi

    var source = ""


    init {
        MonitorApiHelper.apiMonitorComponent.inject(this)
    }

    /**
     * 切换监控状态
     */
    fun switchMonitorState(
        id: String, targetState: Boolean,
        restricted: Boolean = false,
        needCheckVip: Boolean = true,
        channelName: String,
        success: () -> Unit
    ) {
        if (!LoginUtils.isLoginAndRequestLogin(itemView.context)) {
            return
        }
        if (needCheckVip) {
            if (restricted && (LoginUtils.getCurrentUser()?.isVip() == false)) {
                DcUriRequest(itemView.context, MonitorConstant.Uri.MONITOR_DIALOG_VIP)
                    .dialogCenterAni()
                    .putUriParameter(CommonConstant.UriParams.SOURCE, source)
                    .start()
                return
            }
        }
        UTHelper.commonEvent(
            if (targetState) UTConstant.Monitor.MonitorP_click_MonitorButton else UTConstant.Monitor.MonitorP_click_cancel_MonitorButton,
            "objectId", id,
            "ChannelName", channelName
        )
        monitorApi.switchMonitorState(id, targetState)
            .subscribe({
                success()
            }, {})
    }

    fun switchRegionState(
        id: String,
        targetState: Boolean,
        success: () -> Unit,
        failed: () -> Unit
    ) {
        if (!LoginUtils.isLoginAndRequestLogin(itemView.context)) {
            return
        }
        monitorApi.switchRegionMonitor(id, targetState)
            .subscribe({
                success()
            }, {
                failed()
            })
    }

    /**
     * 进入详情
     */
    fun routeToChannelDetail(channelId: String) {
        if (LoginUtils.isLoginAndRequestLogin(itemView.context)) {
            DcUriRequest(itemView.context, MonitorConstant.Uri.MONITOR_DETAIL)
                .putUriParameter(MonitorConstant.UriParam.CHANNEL_ID, channelId)
                .start()
        }
    }
}