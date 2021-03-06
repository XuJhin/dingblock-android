package cool.dingstock.monitor.setting

import android.os.Bundle
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.SpanUtils
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.MonitorConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.helper.SettingHelper
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.widget.dialog.common.DcTitleDialog
import cool.dingstock.monitor.R
import cool.dingstock.monitor.databinding.ActivityMonitorSettingBinding
import cool.dingstock.uicommon.setting.dialog.DisturbDialog
import cool.dingstock.uicommon.setting.helper.EasyAuditionHelper

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [MonitorConstant.Path.MONITOR_SETTING]
)
class MonitorSettingActivity :
    VMBindingActivity<SettingViewModel, ActivityMonitorSettingBinding>() {

    private var easyAuditionHelper: EasyAuditionHelper? = null
    override fun moduleTag(): String {
        return ModuleConstant.MONITOR
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        easyAuditionHelper = EasyAuditionHelper()
        viewModel.getDisturbTime()
        initData()
        updateUI()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMsgState()
    }

    private fun updateUI() {
        viewModel.apply {
            successDialog.observe(this@MonitorSettingActivity) {
                showSuccessDialog(it)
            }
            couldLiveData.observe(
                this@MonitorSettingActivity
            ) {
                hideLoadingDialog()
                DcRouter(it).start()
            }
            hideLoadingDialog.observe(this@MonitorSettingActivity) {
                hideLoadingDialog
            }
            errorDialog.observe(this@MonitorSettingActivity) {
                showErrorView(it)
            }
            disturbTimeLiveData.observe(this@MonitorSettingActivity) {
                viewBinding.doNotDisturbSwitch.isChecked = it.isSilent
                viewBinding.tvStartTime.text = it.startHour.toString().plus(":00")
                viewBinding.tvEndTime.text = it.endHour.toString().plus(":00")
                startTime = it.startHour
                endTime = it.endHour
            }
            msgConfigLiveData.observe(this@MonitorSettingActivity) {
                viewBinding.apply {
                    cardMsgRemind.hide(it.messageConfig == null)
                    if (it.messageConfig != null) {
                        tvMsgRemindTitle.text = it.messageConfig?.title ?: ""
                        switchBtnMsgRemind.isChecked = it.messageConfig?.enable ?: false
                        SpanUtils.with(tvMsgRemindDesc).apply {
                            append("??????")
                            append(it.messageConfig?.remain.toString())
                            setForegroundColor(getCompatColor(R.color.color_blue))
                            append("???")
                            append(it.messageConfig?.rewardStr.toString())
                        }.create()
                    }
                }
            }
            msgDialogLiveData.observe(this@MonitorSettingActivity) {
                if (it != null && it.pop == true) {
                    DcTitleDialog.Builder(this@MonitorSettingActivity)
                        .title("???????????????")
                        .content(it.msg)
                        .cancelTxt("??????")
                        .confirmTxt("?????????")
                        .onConfirmClick {
                            viewModel.getCouldUrl("smsNotification", context)
                        }
                        .builder()
                        .show()
                }
            }
        }
    }

    private fun initData() {
        val colorTiny = getCompatColor(R.color.color_blue)
        SpanUtils.with(viewBinding.filterExplainTv).apply {
            append("?????????")
            append("???????????????")
            setForegroundColor(colorTiny)
            append("????????????")
            append("????????????")
            setForegroundColor(colorTiny)
            append("???")
            setForegroundColor(colorTiny)
            append("?????????????????????????????????")
            setForegroundColor(colorTiny)
            append("???????????????")
        }.create()
        viewBinding.directLinkSwitch.isChecked = SettingHelper.getInstance().isMonitorDirectLink
    }

    override fun initListeners() {
        viewBinding.directLinkSwitch.setOnCheckedChangeListener { _, isChecked ->
            UTHelper.commonEvent(
                UTConstant.Monitor.MonitorP_SetP,
                "operating",
                "????????????????????????(${if (isChecked) "???" else "???"})"
            )
            SettingHelper.getInstance().saveMonitorDirectLink(isChecked)
        }
        viewBinding.shieldFra.setOnClickListener {
            DcRouter(MonitorConstant.Uri.MONITOR_SHIELD).start()
            UTHelper.commonEvent(UTConstant.Monitor.MonitorP_SetP, "operating", "???????????????")
        }
        viewBinding.allMonitorFilterSettingBtn.setOnClickListener {
            UTHelper.commonEvent(UTConstant.Monitor.MonitorP_SetP, "operating", "?????????????????????")
            DcRouter(MonitorConstant.Uri.MONITOR_SELECT_CHANNEL).start()
        }
        viewBinding.monitorFeedbackBtn.setOnClickListener {
            UTHelper.commonEvent(UTConstant.Monitor.MonitorP_SetP, "operating", "????????????")
            viewModel.getCloudUrl("feedback")
        }
        viewBinding.doNotDisturbSwitch.setOnShakeClickListener {
            viewModel.setDisturbTime(
                viewBinding.doNotDisturbSwitch.isChecked,
                viewModel.startTime,
                viewModel.endTime
            )
        }
        viewBinding.llUnDisturbMode.setOnClickListener {
            showDisturbDialog()
        }
        viewBinding.llMsgRemind.setOnShakeClickListener {
            UTHelper.commonEvent(UTConstant.Monitor.MonitorP_SetP_Sms)
            viewModel.getCouldUrl("smsNotification", context)
        }
        viewBinding.switchBtnMsgRemind.setOnShakeClickListener {
            viewModel.setMsgState(viewBinding.switchBtnMsgRemind.isChecked)
//            if(viewBinding.switchBtnMsgRemind.isChecked){
//                LoginUtils.getCurrentUser()?.mobile?.let { it1 ->
//                    viewModel.refreshPhoneNumber(it1)
//                }
//            }
        }
        viewBinding.remingFra.setOnShakeClickListener {
            UTHelper.commonEvent(UTConstant.Monitor.MonitorP_SetP, "operating", "????????????")
            DcRouter(MonitorConstant.Uri.MONITOR_REMIND_SETTING).start()
        }
    }

    private fun showDisturbDialog() {
        val disturbDialog = DisturbDialog(this)
        val disturbData = SettingHelper.getInstance().disturbData
        disturbDialog.setStartTime(disturbData.startTime)
        disturbDialog.setEndTime(disturbData.endTime)
        disturbDialog.setMListener { _: Boolean, start: Int, end: Int ->
            viewBinding.tvStartTime.text = start.toString().plus(":00")
            viewBinding.tvEndTime.text = end.toString().plus(":00")
            viewModel.setDisturbTime(viewBinding.doNotDisturbSwitch.isChecked, start, end)
        }
        disturbDialog.show()
    }
}
