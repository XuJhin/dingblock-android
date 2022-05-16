package cool.dingstock.setting.ui.setting.updatePhone

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.*
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.EditTextUtils
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.widget.dialog.CaptchaDialogFactory
import cool.dingstock.uicommon.setting.databinding.ActivitySetNewPhoneBinding
import cool.mobile.account.R
import cool.mobile.account.ui.country.CountryPickerActivity

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [SettingConstant.Path.SET_PHONE]
)
class SetNewPhoneActivity : VMBindingActivity<SetNewPhoneViewModel, ActivitySetNewPhoneBinding>() {

    override fun moduleTag(): String {
        return ModuleConstant.SETTING
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        viewBinding.apply {
            tvConfirm.isEnabled = false //确认按钮开局不可点
            tvCheckWord.isEnabled = false //验证码按钮开局不可以点
        }
        Handler().postDelayed({ showSoftInputFromWindow(viewBinding.edtNumber) }, 300)
    }

    override fun initBaseViewModelObserver() {
        viewModel.apply {
            newPhoneLiveData.observe(this@SetNewPhoneActivity) {
                DcRouter(SettingConstant.Uri.ACCOUNT_SETTING).start()
                finish()
            }
            countDownLiveData.observe(this@SetNewPhoneActivity) {//获取验证码成功后
                viewBinding.tvCheckWord.isRObtain = true
                viewBinding.tvCheckWord.start()
                Handler().postDelayed({ showSoftInputFromWindow(viewBinding.edtCheckWord) }, 300)
            }
            captchaDialog.observe(this@SetNewPhoneActivity) {
                showCaptchaDialog()
            }
            accidentLiveData.observe(this@SetNewPhoneActivity) {//异常情况返回第一次校验页面
                DcRouter(SettingConstant.Uri.CHECK_PHONE)
                    .putUriParameter(
                        SettingConstant.PARAM_KEY.PHONE_NUMBER,
                        LoginUtils.getCurrentUser()?.mobile
                    )
                    .putUriParameter(
                        SettingConstant.PARAM_KEY.PHONE_ZONE,
                        LoginUtils.getCurrentUser()?.zone
                    )
                    .start()
                finish()
            }
        }
        super.initBaseViewModelObserver()
    }

    override fun initListeners() {
        viewBinding.apply {
            tvAddressNumber.setOnShakeClickListener {
                DcRouter(AccountConstant.Uri.COUNTRYCODE)
                    .activityRequestCode(ZONE_REQUEST_CODE)
                    .start()
            }
            ivMoreAddress.setOnShakeClickListener {
                DcRouter(AccountConstant.Uri.COUNTRYCODE)
                    .activityRequestCode(ZONE_REQUEST_CODE)
                    .start()
            }
            tvCheckWord.setOnShakeClickListener {
                fetchVerificationCode()
            }
            tvConfirm.setOnShakeClickListener {
                UTHelper.commonEvent(UTConstant.Setting.SettingP_click_Complete)
                viewBinding.apply {
                    viewModel.updateBindPhone(
                        edtCheckWord.text.toString(),
                        edtNumber.text.toString().replace(" ", ""),
                        tvAddressNumber.text.toString()
                    )
                }
            }
            ivClear.setOnShakeClickListener {
                edtNumber.setText("")
            }
            ivCheckWordClear.setOnShakeClickListener {
                edtCheckWord.setText("")
            }

            edtNumber.let { phoneEdit ->
                phoneEdit.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        tvCheckWord.isEnabled = edtNumber.text.isNotEmpty()
                        ivClear.hide(edtNumber.text.isEmpty())

                        EditTextUtils.phoneNumTextChange(s, start, before, phoneEdit)
                        if (phoneEdit.length() > 13) {
                            val substring = phoneEdit.text.toString().substring(0, 13)
                            phoneEdit.setText(substring)
                            phoneEdit.setSelection(phoneEdit.length())
                        }
                    }

                    override fun afterTextChanged(s: Editable) {}
                })
            }
            edtCheckWord.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    edtCheckWord.text.toString().replace(" ", "")
                    edtCheckWord.text.toString().replace(".", "")
                    tvConfirm.isEnabled = edtCheckWord.text.length >= 6
                    ivCheckWordClear.hide(edtCheckWord.text.isEmpty())
                }

                override fun afterTextChanged(s: Editable) {
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ZONE_REQUEST_CODE && resultCode == ZONE_RESULT_OK) {
            if (null == data) {
                return
            }
            val zone = data.getStringExtra(CountryPickerActivity.COUNTRY_CODE)
            if (TextUtils.isEmpty(zone)) {
                return
            }
            updateZone("+$zone")
        }
    }

    private fun updateZone(zone: String) {
        viewBinding.tvAddressNumber.text = zone
        viewModel.zone = zone
    }

    private fun fetchVerificationCode() {
        if (!checkPhone()) {
            return
        }
        showLoadingDialog()
        viewModel.apply {
            getSmsCode(
                viewBinding.tvAddressNumber.text.toString(),
                viewBinding.edtNumber.text.toString().replace(" ", ""), ""
            )
        }
    }

    private fun showSoftInputFromWindow(editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        val inputManager =
            editText.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(editText, 0)
    }

    private fun checkPhone(): Boolean {
        val phoneNum = viewBinding.edtNumber.text.toString()
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
                            viewBinding.tvAddressNumber.text.toString(),
                            viewBinding.edtNumber.text.toString().replace(" ", ""),
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

    companion object {
        private const val DEFAULT_ZONE = "+86"
        private const val ZONE_REQUEST_CODE = 1000
        const val ZONE_RESULT_OK = 2000
    }
}