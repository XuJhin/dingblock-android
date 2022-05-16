package cool.dingstock.uicommon.setting.cancellation

import android.os.Bundle
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.HomeConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.constant.SettingConstant
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.widget.commondialog.CommonDialog
import cool.dingstock.uicommon.databinding.ActivityCancellationBinding

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [SettingConstant.Path.REGISTER_OUT]
)
class CancellationActivity : VMBindingActivity<CancellationVM, ActivityCancellationBinding>() {

    override fun moduleTag(): String = "SETTING"

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        viewModel.registerOutLivaData.observe(this) {
            DcRouter(HomeConstant.Uri.TAB).start()
        }
    }

    override fun initListeners() {
        viewBinding.cancellationConfirmBtn.setOnShakeClickListener {
            CommonDialog.Builder(context)
                .content("注销帐号不可恢复，一旦注销，您将无法再登录使用帐号，确认注销吗？")
                .cancelTxt("再想想")
                .confirmTxt("注销")
                .onConfirmClick {
                    viewModel.registerOut()
                }
                .builder()
                .show()
        }
    }
}