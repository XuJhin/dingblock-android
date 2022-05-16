package cool.mobile.account.ui.login.fragment.mobile

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.CountDownTimer
import android.text.*
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.ViewModelProvider
import cool.dingstock.appbase.constant.AccountConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.custom.CustomerManager
import cool.dingstock.appbase.entity.event.account.EventIsAuthorized
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.mvp.lazy.VmBindingLazyFragment
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.EditTextUtils
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.widget.dialog.CaptchaDialogFactory
import cool.dingstock.lib_base.util.ScreenUtils
import cool.dingstock.lib_base.util.SizeUtils
import cool.mobile.account.R
import cool.mobile.account.databinding.FragmentAccountLoginBinding
import cool.mobile.account.ui.country.CountryPickerActivity
import cool.mobile.account.ui.login.index.LoginAcVm
import org.greenrobot.eventbus.EventBus

class AccountLoginFragment :
    VmBindingLazyFragment<AccountLoginViewModel, FragmentAccountLoginBinding>() {
    lateinit var loginAcVm: LoginAcVm
    private val isBindingMobile: Boolean
        get() = AccountConstant.AuthType.WECHAT.equals(authType, ignoreCase = true)
    private val uTEventId: String
        get() = if (isBindingMobile) {
            UTConstant.Login.BIND_MOBILE
        } else {
            UTConstant.Login.LOGIN_MOBILE
        }
    private var mZone = DEFAULT_ZONE
    private var authType: String? = ""
    private var userId: String? = ""
    private var token: String? = ""
    private var listener: ViewTreeObserver.OnGlobalLayoutListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            authType = it.getString(AccountConstant.ExtraParam.AUTH_TYPE, "") ?: ""
            userId = it.getString(AccountConstant.ExtraParam.USER_ID, "") ?: ""
            token = it.getString(AccountConstant.ExtraParam.TOKEN, "") ?: ""
        }
    }

    override fun initVariables(
        rootView: View?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        loginAcVm = ViewModelProvider(requireActivity())[LoginAcVm::class.java]
        viewModel.initData(authType, userId, token)
        viewModel.initLoginInfo(uTEventId)
        initView()
        initBottomAgreement()
        initKeyBoard()
        viewModelObserver()
    }

    override fun initListeners() {
        viewBinding.apply {
            accountLoginPhoneEdit.let { phoneEdit ->
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
                        checkEnableLogin()
                        if (phoneEdit.length() > 0) {
                            accountLoginEditDelIcon.visibility = View.VISIBLE
                            accountLoginSmsTimerBtn.isEnabled = true
                        } else {
                            accountLoginEditDelIcon.visibility = View.GONE
                            accountLoginSmsTimerBtn.isEnabled = false
                        }
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
            accountLoginEditDelIcon.setOnClickListener {
                accountLoginPhoneEdit.setText("")
            }
            accountLoginZoneTxt.setOnClickListener {
                DcRouter(AccountConstant.Uri.COUNTRYCODE)
                    .activityRequestCode(ZONE_REQUEST_CODE)
                    .start()
            }
            accountLoginSmsTimerBtn.setOnShakeClickListener {
                fetchVerificationCode()
            }
            accountLoginTxt.setOnShakeClickListener {
                login()
            }
            accountLoginSmsEdit.let { smsEdt ->
                smsEdt.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    codeLine.setBackgroundColor(
                        if (hasFocus) {
                            Color.parseColor("#ccffffff")
                        } else {
                            Color.parseColor("#33ffffff")
                        }
                    )
                }
                smsEdt.addTextChangedListener(object : TextWatcher {
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
                        checkEnableLogin()
                        if (smsEdt.length() > 6) {
                            smsEdt.setText(smsEdt.text.toString().substring(0, 6))
                            smsEdt.setSelection(smsEdt.length())
                        }
                    }

                    override fun afterTextChanged(s: Editable) {}
                })
            }
            accountLoginBackIcon.setOnClickListener {
                goBack()
            }
            accountProblemTxt.setOnClickListener {
                CustomerManager.getInstance().showCustomServiceActivity(requireActivity())
            }
        }
    }


    override fun onLazy() {
    }

    fun initView() {
        UTHelper.commonEvent(UTConstant.Login.Login_EntBindPhonenumP)
        viewBinding.apply {
            bindPhoneTv.text = if (isBindingMobile) {
                getString(R.string.title_bind_mobile)
            } else {
                getString(R.string.mobile)
            }
            updateZone(DEFAULT_ZONE)
            val lastPhoneNum = AccountHelper.getInstance().saveUserPhone
            if (!TextUtils.isEmpty(lastPhoneNum)) {
                accountLoginPhoneEdit.apply {
                    setText(EditTextUtils.formatPhoneNum(lastPhoneNum))
                    setSelection(this.text.length)
                    isEnabled = true
                    accountLoginSmsTimerBtn.isEnabled = true
                    accountLoginEditDelIcon.visibility = View.VISIBLE
                }
            } else {
                accountLoginEditDelIcon.visibility = View.GONE
            }
        }
    }

    private fun viewModelObserver() {
        viewModel.countDownLiveData.observe(this) {
            startCountDown()
        }
        viewModel.captchaDialog.observe(this) {
            showCaptchaDialog()
        }
        viewModel.webRouteLiveData.observe(this) {
            DcRouter(it).start()
        }
        viewModel.loginSuccess.observe(this) {
            loginSuccess()
        }
    }

    private fun startTimer() {
        val countDownTimer: CountDownTimer = object : CountDownTimer(500, 300) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                hideLoadingDialog()
                requireActivity().finish()
            }
        }
        countDownTimer.start()
    }

    private fun loginSuccess() {
        when (loginAcVm.loginAction) {
            AccountConstant.LOGIN_FINISH -> {
                EventBus.getDefault().post(EventIsAuthorized(true))
            }
            AccountConstant.LOGIN_2IM -> {
                EventBus.getDefault().post(EventIsAuthorized(true, "IM"))
            }
            AccountConstant.LOGIN_2MINE -> {
                EventBus.getDefault().post(EventIsAuthorized(true, "MINE"))
            }
            else -> {
                EventBus.getDefault().post(EventIsAuthorized(true))
                hideLoadingDialog()
            }
        }
        startTimer()
    }

    private fun showCaptchaDialog() {
        CaptchaDialogFactory()
            .getCaptchaDialog(
                context,
                { (appId, ticket, randStr) ->
                    viewModel.getSmsCodeWithCaptcha(
                        mZone,
                        filterPhoneNumber(),
                        appId,
                        ticket,
                        randStr
                    )
                },
                {
                    hideLoadingDialog()
                    UTHelper.commonEvent(uTEventId, "获取验证码失败")
                })
            .show()
    }

    /**
     * 底部隐私协议
     */
    private fun initBottomAgreement() {
        val navigationBarHeight = ScreenUtils.getNavigationBarHeight(requireContext())
        val layoutParams = viewBinding.bottomBar.layoutParams
        layoutParams?.height = 100.dp.toInt() + navigationBarHeight
        viewBinding.bottomBar.layoutParams = layoutParams
    }

    private fun initKeyBoard() {
        rootView?.let {
            listener = ViewTreeObserver.OnGlobalLayoutListener {
                try {
                    val rect = Rect()
                    requireActivity().window?.decorView?.getWindowVisibleDisplayFrame(rect)
                    val currentHeight = rect.bottom
                    val height = SizeUtils.getHeight()
                    viewBinding.apply {
                        val loginBottom =
                            height - accountLoginTxt.y - accountLoginTxt.height - SizeUtils.dp2px(
                                10f
                            )
                        val keyBoardHeight = height - currentHeight
                        val offset = keyBoardHeight - loginBottom
                        try {
                            if (offset > 0) {
                                ani2Top(-offset)
                            } else {
                                ani2Bottom()
                            }
                        } catch (e: Exception) {
                        }
                    }
                } catch (e: Exception) {
                }
            }
            it.viewTreeObserver.addOnGlobalLayoutListener(listener)
        }
    }

    private fun ani2Top(offSet: Float) {
        viewBinding.apply {
            val translationY = ObjectAnimator.ofFloat(inputLin, "translationY", inputLin.y, offSet)
            translationY.duration = ANIMATOR_DURATION
            translationY.start()
        }
    }

    private fun ani2Bottom() {
        viewBinding.apply {
            val translationY = ObjectAnimator.ofFloat(inputLin, "translationY", inputLin.y, 0f)
            translationY.duration = ANIMATOR_DURATION
            translationY.start()
        }
    }

    private fun updateZone(zone: String) {
        viewBinding.accountLoginZoneTxt.text = "$zone"
        mZone = zone
    }

    private fun goBack() {
        UTHelper.commonEvent(uTEventId, "点击返回")
        backToLoginIndex()
    }

    private fun fetchVerificationCode() {
        UTHelper.commonEvent(uTEventId, "点击获取验证码")
        if (!checkPhone()) {
            return
        }
        showLoadingDialog()
        viewModel.getSmsCode(mZone, filterPhoneNumber(), uTEventId)
    }

    private fun filterPhoneNumber(): String {
        return EditTextUtils.filterSpace(
            viewBinding.accountLoginPhoneEdit.text.toString().trim { it <= ' ' })
    }

    private fun login() {
        UTHelper.commonEvent(uTEventId, "点击登录")
        if (!checkPhone()) {
            return
        }
        if (!checkCode()) {
            return
        }
        showLoadingDialog()
        val phoneNum = EditTextUtils.filterSpace(
            viewBinding.accountLoginPhoneEdit.text.toString().trim { it <= ' ' })
        viewModel.login(
            mZone,
            phoneNum,
            viewBinding.accountLoginSmsEdit.text.toString().trim { it <= ' ' })
    }

    private fun checkEnableLogin() {
        val enable =
            viewBinding.accountLoginPhoneEdit.text?.length!! > 1 && viewBinding.accountLoginSmsEdit.length() == 6
        viewBinding.accountLoginTxt.isEnabled = enable
    }

    private fun checkCode(): Boolean {
        val codeNum = viewBinding.accountLoginSmsEdit.text.toString()
        if (TextUtils.isEmpty(codeNum)) {
            showToastShort(R.string.account_login_sms_hint)
            return false
        }
        val realPhoneNum = codeNum.trim { it <= ' ' }
        if (TextUtils.isEmpty(realPhoneNum)) {
            showToastShort(R.string.account_login_sms_hint)
            return false
        }
        return true
    }

    private fun checkPhone(): Boolean {
        val phoneNum = viewBinding.accountLoginPhoneEdit.text.toString()
        if (TextUtils.isEmpty(phoneNum)) {
            showToastShort(R.string.account_login_phone_hint)
            return false
        }
        val realPhoneNum = phoneNum.trim { it <= ' ' }
        if (TextUtils.isEmpty(realPhoneNum)) {
            showToastShort(R.string.account_login_phone_hint)
            return false
        }
        return true
    }

    private fun backToLoginIndex() {
        loginAcVm.phoneBack.postValue(true)
    }

    private fun startCountDown() {
        hideLoadingDialog()
        viewBinding.accountLoginSmsTimerBtn.isRObtain = true
        viewBinding.accountLoginSmsTimerBtn.start()
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

    override fun onDetach() {
        super.onDetach()
        rootView?.viewTreeObserver?.removeOnGlobalLayoutListener(listener)
    }


    companion object {
        private const val DEFAULT_ZONE = "+86"
        private const val ZONE_REQUEST_CODE = 1000
        const val ZONE_RESULT_OK = 2000
        const val ZONE_RESULT_CANCEL = 2001
        private const val ANIMATOR_DURATION = 300L

        @JvmStatic
        fun newInstance(authType: String?, userId: String?, token: String?) =
            AccountLoginFragment().apply {
                arguments = Bundle().apply {
                    putString(AccountConstant.ExtraParam.AUTH_TYPE, authType)
                    putString(AccountConstant.ExtraParam.USER_ID, userId)
                    putString(AccountConstant.ExtraParam.TOKEN, token)
                }
            }
    }
}