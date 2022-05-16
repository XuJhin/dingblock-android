package cool.dingstock.setting.ui.setting.account

import android.content.Intent
import android.os.Bundle
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.constant.SettingConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.ext.getColorRes
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.widget.commondialog.CommonDialog
import cool.dingstock.uicommon.setting.R
import cool.dingstock.uicommon.setting.databinding.ActivityAccountSettingBinding

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [SettingConstant.Path.ACCOUNT_SETTING]
)
class AccountSettingActivity :
    VMBindingActivity<AccountSettingViewModel, ActivityAccountSettingBinding>() {

    override fun moduleTag(): String {
        return ModuleConstant.SETTING
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        showLoadingView()
        viewModel.fetchAccount()
    }

    override fun onNewIntent(intent: Intent?) {
        viewModel.fetchAccount()
        super.onNewIntent(intent)
    }

    override fun initBaseViewModelObserver() {
        viewModel.apply {
            userAccountLiveData.observe(this@AccountSettingActivity) {
                hideLoadingView()
                viewBinding.apply {
                    tvPhone.text = it.zone.plus(" ").plus(it.mobile)
                    tvWechat.text = if (it.isWXBinding == true) "已绑定" else "未绑定"
                }
            }
            isUnBindSuccess.observe(this@AccountSettingActivity) {
                shortToast(if (it) "微信解绑成功" else "微信解绑失败,请重试")
                viewBinding.apply {
                    tvWechat.text = if (it == true) "未绑定" else "已绑定"
                }
            }
            isWeChatBindSuccess.observe(this@AccountSettingActivity) {
                viewBinding.apply {
                    tvWechat.text = if (it == true) "已绑定" else "未绑定"
                }
            }
        }
        super.initBaseViewModelObserver()
    }

    override fun initListeners() {
        viewBinding.apply {
            bindingPhone.setOnShakeClickListener {
                UTHelper.commonEvent(UTConstant.Setting.SettingP_click_Phone)
                showUpdateBindingPhoneDialog()
            }
            bindingWechat.setOnShakeClickListener {
                if (viewModel.accountData?.isWXBinding == true) {
                    UTHelper.commonEvent(UTConstant.Setting.SettingP_click_Wechat, "name", "解绑")
                    showCancelWeChatBindingDialog()
                } else { //binding Wechat
                    UTHelper.commonEvent(UTConstant.Setting.SettingP_click_Wechat, "name", "绑定")
                    viewModel.bindingWechat()
                }
            }
            accountGiveUp.setOnShakeClickListener {
                UTHelper.commonEvent(UTConstant.Setting.SettingP_click_Cancellation)
                showGiveUpAccountDialog()
            }
        }
    }

    private fun showGiveUpAccountDialog() {
        if (null == AccountHelper.getInstance().user) {
            return
        }
        DcRouter(SettingConstant.Uri.REGISTER_OUT).start()
    }


    private fun showUpdateBindingPhoneDialog() {
        if (null == AccountHelper.getInstance().user) {
            return
        }
        CommonDialog.Builder(context)
            .content("更换已绑定手机？")
            .cancelTxt("取消")
            .confirmTxt("更换")
            .onConfirmClick {
                DcRouter(SettingConstant.Uri.CHECK_PHONE)
                    .putUriParameter(
                        SettingConstant.PARAM_KEY.PHONE_NUMBER,
                        viewModel.accountData?.mobile
                    )
                    .putUriParameter(
                        SettingConstant.PARAM_KEY.PHONE_ZONE,
                        viewModel.accountData?.zone
                    )
                    .start()
            }
            .builder()
            .show()
    }

    private fun showCancelWeChatBindingDialog() {
        if (null == AccountHelper.getInstance().user) {
            return
        }
        CommonDialog.Builder(context)
            .content("确定解除微信绑定？")
            .cancelTxt("取消")
            .confirmTxt("确定")
            .onConfirmClick {
                viewModel.unBindingWeChat()
            }
            .builder()
            .show()
    }
}