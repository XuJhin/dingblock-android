package cool.dingstock.setting.ui.setting.updatePhone

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.constant.SettingConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.EditTextUtils
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.widget.commondialog.CommonDialog
import cool.dingstock.appbase.widget.dialog.CaptchaDialogFactory
import cool.dingstock.uicommon.setting.databinding.ActivityCheckPhoneBinding
import cool.mobile.account.R

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [SettingConstant.Path.CHECK_PHONE]
)
class CheckPhoneActivity : VMBindingActivity<CheckPhoneViewModel, ActivityCheckPhoneBinding>() {

    override fun moduleTag(): String {
        return ModuleConstant.SETTING
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        viewModel.phoneNumber = uri.getQueryParameter(SettingConstant.PARAM_KEY.PHONE_NUMBER) ?: ""
        viewModel.zoneNumber = uri.getQueryParameter(SettingConstant.PARAM_KEY.PHONE_ZONE) ?: ""

        viewBinding.apply {
            tvNumber.text = EditTextUtils.formatPhoneNum(viewModel.phoneNumber) //按照电话格式加空格
            tvAddressNumber.text = viewModel.zoneNumber
            tvConfirm.isEnabled = false //确认按钮开局不可点
            tvCheckWord.isEnabled = true //验证码按钮开局可以点
        }
    }

    override fun initBaseViewModelObserver() {
        viewModel.apply {
            oldPhoneLiveData.observe(this@CheckPhoneActivity) {
                DcRouter(SettingConstant.Uri.SET_PHONE)
                    .putExtra(SettingConstant.PARAM_KEY.OLD_PHONE, it)
                    .start()
            }
            countDownLiveData.observe(this@CheckPhoneActivity) {//获取验证码成功后
                viewBinding.tvCheckWord.isRObtain = true
                viewBinding.tvCheckWord.start()
                Handler().postDelayed({ showSoftInputFromWindow(viewBinding.edtCheckWord) }, 300)
            }
            captchaDialog.observe(this@CheckPhoneActivity) {
                showCaptchaDialog()
            }
        }
        super.initBaseViewModelObserver()
    }

    override fun initListeners() {
        viewBinding.apply {
            tvCheckWord.setOnShakeClickListener {
                fetchVerificationCode()
            }
            tvConfirm.setOnShakeClickListener {
                UTHelper.commonEvent(UTConstant.Setting.SettingP_click_NextStep)
                viewBinding.apply {
                    viewModel.checkCurrentPhone(
                        edtCheckWord.text.toString(),
                        tvNumber.text.toString().replace(" ", ""),
                        tvAddressNumber.text.toString()
                    )
                }
            }
            tvServer.setOnShakeClickListener {
                UTHelper.commonEvent(UTConstant.Setting.SettingP_click_Unavailable)
                showServerDialog()
            }
            ivClear.setOnShakeClickListener {
                edtCheckWord.setText("")
            }
            edtCheckWord.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    edtCheckWord.text.toString().replace(" ", "").replace(".", "")
                    tvConfirm.isEnabled = edtCheckWord.text.length >= 6
                    ivClear.hide(edtCheckWord.text.isEmpty())
                }

                override fun afterTextChanged(s: Editable) {
                }
            })
        }
    }

    private fun showServerDialog() {
        CommonDialog.Builder(context)
            .content("人工申诉，请联系在线客服")
            .cancelTxt("取消")
            .confirmTxt("联系客服")
            .onConfirmClick {
                MobileHelper.getInstance()
                    .getCloudUrlAndPutValueRouter(context, "help", "questionItemsId", "6")
            }
            .builder()
            .show()
    }

    private fun showSoftInputFromWindow(editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        val inputManager =
            editText.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(editText, 0)
    }

    private fun fetchVerificationCode() {
        if (!checkPhone()) {
            return
        }
        showLoadingDialog()
        viewModel.apply {
            getSmsCode(zoneNumber, phoneNumber, "")
        }
    }

    private fun checkPhone(): Boolean {
        val phoneNum = viewBinding.tvNumber.text.toString()
        if (TextUtils.isEmpty(phoneNum)) {
            showToastShort(R.string.account_login_phone_hint)
            return false
        }
        val realPhoneNum = phoneNum.replace(" ", "")
        if (TextUtils.isEmpty(realPhoneNum)) {
            showToastShort(R.string.account_login_phone_hint)
            return false
        }
        return true
    }


    private fun showCaptchaDialog() {
        CaptchaDialogFactory()
            .getCaptchaDialog(
                context,
                { (appId, ticket, randStr) ->
                    viewModel.apply {
                        getSmsCodeWithCaptcha(
                            zoneNumber,
                            phoneNumber,
                            appId,
                            ticket,
                            randStr
                        )
                    }
                },
                {
                    hideLoadingDialog()
                })
            .show()
    }
}