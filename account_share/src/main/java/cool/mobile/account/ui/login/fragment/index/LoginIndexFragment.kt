package cool.mobile.account.ui.login.fragment.index

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Instrumentation
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.geetest.onelogin.config.OneLoginThemeConfig
import com.kongzue.dialogx.dialogs.TipDialog
import com.qipeng.yp.onelogin.QPOneLogin
import com.qipeng.yp.onelogin.callback.AbsQPResultCallback
import cool.dingstock.appbase.constant.AccountConstant
import cool.dingstock.appbase.constant.ServerConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.custom.CustomerManager
import cool.dingstock.appbase.entity.event.account.EventIsAuthorized
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.mvp.lazy.VmBindingLazyFragment
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.push.DCPushManager
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.AnimUtils
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.widget.commondialog.CommonDialog
import cool.dingstock.lib_base.util.*
import cool.mobile.account.R
import cool.mobile.account.databinding.FragmentLoginIndexBinding
import cool.mobile.account.ui.login.index.LoginAcVm
import cool.mobile.account.ui.login.index.LoginIndexActivity
import cool.mobile.account.ui.login.index.PhoneLoginIntent
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject


class LoginIndexFragment : VmBindingLazyFragment<LoginIndexVM, FragmentLoginIndexBinding>(){

    private lateinit var loginAcVm: LoginAcVm
    private lateinit var oneLoginThemeConfig: OneLoginThemeConfig//??????????????????
    private lateinit var autoLoginThemeConfig: OneLoginThemeConfig//??????????????????
    private lateinit var weChatLoginResultObserver: Observer<WeChatLoginResult>
    private lateinit var oneKeyLoginResultObserver: Observer<Boolean>

    override fun initVariables(
        rootView: View?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        arguments?.let {
        }
        loginAcVm = ViewModelProvider(requireActivity())[LoginAcVm::class.java]
        initView()
        initObserve()
        initYPTheme()
        initBottomAgreement(false)
    }

    override fun initListeners() {
        viewBinding.apply {
            checkboxLayer.setOnClickListener {
                if (!checkbox.isSelected) {
                    UTHelper.commonEvent(UTConstant.Login.Login_PAGE, "???????????????")
                }
                checkbox.isSelected = !checkbox.isSelected
            }
            phoneLoginTv.setOnShakeClickListener {
                if (!checkbox.isSelected) {
                    showLoginCheckDialog("phoneNumberLogin")
                    return@setOnShakeClickListener
                }
                //???????????????
                UTHelper.commonEvent(UTConstant.Login.Login_PAGE, "????????????????????????")
                loginByMobile()
            }
            llAutoLoginLayer.setOnShakeClickListener {
                if (!checkbox.isSelected) {
                    showLoginCheckDialog("autoLogin")
                    return@setOnShakeClickListener
                }
                autoLogin()
            }
            loginOnkeyHidePhoneLin.setOnShakeClickListener {
                if (!checkbox.isSelected) {
                    showLoginCheckDialog("phoneNumberLogin")
                    return@setOnShakeClickListener
                }
                //???????????????
                loginByMobile()
            }
            loginOnkeyHideWechatLin.setOnShakeClickListener {
                if (!checkbox.isSelected) {
                    showLoginCheckDialog("weChatLogin")
                    return@setOnShakeClickListener
                }
                loginByWechat()
            }
            loginOnkeyNotenableLin.setOnShakeClickListener {
                if (!checkbox.isSelected) {
                    showLoginCheckDialog("weChatLogin")
                    return@setOnShakeClickListener
                }
                //????????????
                UTHelper.commonEvent(UTConstant.Login.Login_OneClickLoginPopup)
                loginByWechat()
            }
            closeIv.setOnShakeClickListener {
                UTHelper.commonEvent(UTConstant.Login.Login_PAGE, "????????????")
                (requireActivity() as? LoginIndexActivity)?.finish()
            }
            problemTv.setOnShakeClickListener {
                UTHelper.commonEvent(UTConstant.Login.Login_PAGE, "??????????????????")
                CustomerManager.getInstance().showCustomServiceActivity(requireContext())
            }
        }
    }

    private fun autoLogin() {
        if (loginAcVm.enableOneLogin) {
            //????????????
            UTHelper.commonEvent(UTConstant.Login.Login_PAGE, "????????????????????????")
            playAutoLoading()
            loginOneKey()
        }
    }

    private fun playAutoLoading() {
        viewBinding.ivAutoLoginLoading.hide(false)
        AnimUtils.startObjectRotateAnim(viewBinding.ivAutoLoginLoading)
    }

    override fun onLazy() {

    }

    private fun initView() {
        viewBinding.apply {
            phoneLoginTv.hide(true)
            llAutoMsgLayer.hide(true)
            loginOnkeyNotenableLin.hide(true)
            llAutoLoginLayer.hide(true)
            loginOnkeyEnableLin.hide(true)
        }

    }

    private fun initObserve() {
        loginAcVm.enableOneLoginLiveData.observe(this) {
            it?.let {
                viewBinding.apply {
                    if (it) {
                        initBottomAgreement(true)
                        tvLoginPhone.text = QPOneLogin.getInstance().securityPhone ?: ""
                        tvServerMsg.text = YPHelper.getPlatServer(requireContext())
                        //????????????????????????
                        aniShowView(llAutoLoginLayer)
                        aniShowView(llAutoMsgLayer)
                        aniShowView(loginOnkeyEnableLin)
                        //?????????????????????
                        aniHideView(phoneLoginTv)
                        aniHideView(loginOnkeyNotenableLin)
                        UTHelper.commonEvent(UTConstant.Login.ONE_LOGIN, "??????")
                    } else {
                        initBottomAgreement(false)
                        aniShowView(phoneLoginTv)
                        aniShowView(loginOnkeyNotenableLin)
                    }
                }
            }
        }
        weChatLoginResultObserver = Observer { result ->
            val res = result.result
            if (!res.err && res.res != null) {
                LoginUtils.loginSuccess(res.res!!)
                UTHelper.commonEvent(UTConstant.Login.WX_LOGIN, "??????????????????")
                AccountHelper.getInstance().saveUserPhone(res.res?.mobile)
                LoginUtils.handLoginSuccess()
                loginSuccess()
            } else {
                loginAcVm.wechatBindPhoneLogin.postValue(
                    PhoneLoginIntent(
                        result.userId,
                        result.token
                    )
                )
            }
        }
        viewModel.weChatLoginLiveData.observeForever(weChatLoginResultObserver)
        oneKeyLoginResultObserver = Observer<Boolean> {
            QPOneLogin.getInstance().dismissAuthActivity()
            if (it) {
                UTHelper.commonEvent(UTConstant.Login.ONE_LOGIN_CLICK, "??????????????????")
                LoginUtils.handLoginSuccess()
                loginSuccess()
            } else {
                UTHelper.commonEvent(UTConstant.Login.ONE_LOGIN_CLICK, "??????????????????")
            }

        }
        viewModel.onKeyLoginResult.observeForever(oneKeyLoginResultObserver)
    }

    /**
     * ??????????????? ????????????UI
     */
    private fun initYPTheme() {
        //??????????????????
        oneLoginThemeConfig = OneLoginThemeConfig.Builder()
            .setDialogTheme(true, 330, 315, 0, 0, false, false)
            .setAuthBGImgPath("one_login_dialog_bg")
            .setAuthNavLayout(Color.parseColor("#00000000"), 49, true, false)
            .setAuthNavTextView("", Color.parseColor("#000000"), 15, false, "", 17, 17)
            .setAuthNavReturnImgView("login_pop_close", 24, 24, false, 295)
            .setNumberView(Color.parseColor("#000000"), 30, 0, 0, 0)
            .setNumberViewTypeface(Typeface.DEFAULT_BOLD)
            .setSloganView(Color.parseColor("#575758"), 12, 45, 0, 0)
            .setSloganViewTypeface(Typeface.DEFAULT_BOLD)
            .setLogBtnLayout("login_black_btns_bg", 260, 48, 110, 0, 0)
            .setLogBtnLoadingView("umcsdk_load_dot_white", 20, 20, 75)
            .setLogoImgView("ic_launcher", 0, 0, true, 80, 0, 0)
            .setSwitchView("??????????????????", Color.parseColor("#000000"), 13, false, 180, 0, 0)
            .setSwitchViewTypeface(Typeface.DEFAULT_BOLD)
            .setPrivacyCheckBox("login_pri_unselected", "login_pri_selected", true, 0, 0, 1)
            .setPrivacyLayout(0, 0, 0, 0, true)
            .build()

        //????????????????????????
        autoLoginThemeConfig = OneLoginThemeConfig.Builder()
            .setDialogTheme(true, 600, 600, 0, 0, false, false)
            .setAuthBGImgPath("auto_login_dialog_bg")
            .setAuthNavLayout(Color.parseColor("#00000000"), 0, true, false)
            .setAuthNavTextView("", Color.parseColor("#00000000"), 0, false, "", 17, 17)
            .setAuthNavReturnImgView("login_pop_close", 0, 0, false, 295)
            .setNumberView(Color.parseColor("#00000000"), 0, 0, 0, 0)
            .setNumberViewTypeface(Typeface.DEFAULT_BOLD)
            .setSloganView(Color.parseColor("#00000000"), 12, 45, 0, 0)
            .setSloganViewTypeface(Typeface.DEFAULT_BOLD)
            .setLogBtnLayout("auto_login_btns_bg", 600, 600, 110, 0, 0)
            .setLogBtnTextView("", 600, 600)
            .setLogBtnLoadingView("umcsdk_load_dot_white", 0, 0, 75)
            .setLogoImgView("ic_launcher", 0, 0, true, 80, 0, 0)
            .setSwitchView("", Color.parseColor("#00000000"), 13, false, 180, 0, 0)
            .setSwitchViewTypeface(Typeface.DEFAULT_BOLD)
            .setPrivacyCheckBox("login_pri_unselected", "login_pri_selected", true, 0, 0, 1)
            .setPrivacyLayout(0, 0, 0, 0, true) // ????????????
            .build()
    }

    /**
     * ??????????????????
     */
    private fun loginByWechat() {
        UTHelper.commonEvent(UTConstant.Login.Login_PAGE, "??????????????????")
        viewModel.loginWechat()
    }

    /**
     * ????????????????????????
     */
    private fun loginByMobile() {
        UTHelper.commonEvent(UTConstant.Login.Login_PAGE, "?????????????????????")
        loginAcVm.phoneLogin.postValue(true)
    }

    private fun loginOneKey() {
        val deviceId = DCPushManager.getInstance().txDeviceToken
        if (StringUtils.isEmpty(deviceId)) {
            ToastUtil.getInstance()._short(requireContext(), "???????????????????????????~")
            return
        }

        val loginThemeConfig = if (loginAcVm.isDisableHiddenLogin) {
            oneLoginThemeConfig
        } else {
            autoLoginThemeConfig
        }

        QPOneLogin.getInstance().requestToken(loginThemeConfig, object : AbsQPResultCallback() {
            override fun onSuccess(message: String?) {
                Logger.d("loginOneKey", "message = $message")
                try {
                    val result = JSONObject(message)
                    val cid = result.getString("cid")
                    if (StringUtils.isEmpty(cid)) {
                        ToastUtil.getInstance()._short(requireContext(), "???????????????????????????~")
                        QPOneLogin.getInstance().dismissAuthActivity()
                        return
                    }
                    loginAcVm.hiddenLoginTime = 0
                    loginAcVm.autoLoginClickTime = 100L
                    loginAcVm.isDisableHiddenLogin = false
                    viewBinding.ivAutoLoginLoading.hide(true)
                    UTHelper.commonEvent(UTConstant.Login.ONE_LOGIN, "??????????????????")
                    viewModel.loginOneKey(cid, deviceId)
                } catch (e: Exception) {
                    ToastUtil.getInstance()._short(requireContext(), "???????????????????????????~")
                    QPOneLogin.getInstance().dismissAuthActivity()
                }
            }

            override fun onFail(message: String?) {
                viewBinding.ivAutoLoginLoading.hide(true)
                QPOneLogin.getInstance().dismissAuthActivity()
                var messageResult = "OneLogin fail message = $message"
                Logger.d("loginOneKey", "message = $messageResult")
                try {
                    val result = JSONObject(message)
                    messageResult = "??????????????????"
                    val errorCode = result.optInt("errorCode")
                    if (errorCode == -20303) {
                        return
                    }
                    if (errorCode == -20301 || errorCode == -20302) {
                        messageResult = "????????????????????????"
                        return
                    }
                    if (errorCode == -20202 || errorCode == -20201) {
                        messageResult = "???????????????????????????????????????????????????"
                    }
                    if (errorCode == -20205) {
                        messageResult = "????????????"
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                ToastUtil.getInstance()._short(requireContext(), messageResult)
            }
        })

        if (!loginAcVm.isDisableHiddenLogin) {
            val autoClick = ThreadClass()
            autoClick.initXY(
                viewBinding.root.width / 2f,
                viewBinding.root.height / 2f,
                loginAcVm.autoLoginClickTime
            )
            autoClick.start()
            loginAcVm.hiddenLoginTime += 1
            loginAcVm.autoLoginClickTime += 200L
            if (loginAcVm.hiddenLoginTime == 3) {
                loginAcVm.isDisableHiddenLogin = true
            }
        }
    }

    private fun aniShowView(view: View) {
        view.hide(false)
        val oaAlpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        val oaScaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
        val oaScaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f)
        val oas = AnimatorSet()
        oas.duration = 700
        oas.play(oaAlpha).with(oaScaleX).with(oaScaleY)
        oas.start()
    }

    private fun aniHideView(view: View) {
        val oaAlpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
        val oaScaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f)
        val oaScaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f)
        val oaTranslateY =
            ObjectAnimator.ofFloat(view, "translationY", 0f, SizeUtils.getHeight().toFloat())
        val oas = AnimatorSet()
        oas.duration = 700
        oas.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                view.hide(true)
                view.isEnabled = false
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
        oas.play(oaTranslateY).with(oaScaleX).with(oaScaleY).with(oaAlpha)
        oas.start()
    }

    //??????????????????
    private fun initBottomAgreement(enableOnLogin: Boolean) {
        val navigationBarHeight = ScreenUtils.getNavigationBarHeight(requireContext())
        val layoutParams = viewBinding.bottomBar.layoutParams
        layoutParams?.height = (100.dp + navigationBarHeight).toInt()
        viewBinding.bottomBar.layoutParams = layoutParams

        viewBinding.apply {
            accountActivityAgreementTv.text = initSpecialText(enableOnLogin, false)
            accountActivityAgreementTv.highlightColor = Color.TRANSPARENT
            accountActivityAgreementTv.movementMethod = LinkMovementMethod.getInstance()
        }
    }


    private fun getClickableSpan(linkColor: Int, click: (() -> Unit)): ClickableSpan {
        val span = object : ClickableSpan() {
            override fun onClick(view: View) {
                click.invoke()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = linkColor
                ds.isUnderlineText = false
            }
        }
        return span
    }

    private fun initSpecialText(enableOnLogin: Boolean, isDialog: Boolean): SpannableStringBuilder {
        val content: String
        val linkColor: Int
        val textArray: ArrayList<Int>
        if (isDialog) {
            linkColor = getCompatColor(R.color.color_blue)
            if (enableOnLogin) {
                val midStr = YPHelper.getPlatAgreement(requireContext())
                content = "????????????????????????????????????????????????????????????" + midStr + "?????????????????????"
                textArray = arrayListOf(5, 13, 14, 20, 20, 20 + midStr.length)
            } else {
                content = "?????????????????????????????????????????????????????????????????????????????????"
                textArray = arrayListOf(5, 13, 14, 20)
            }
        } else {
            linkColor = Color.WHITE
            if (enableOnLogin) {
                val platAgreement = YPHelper.getPlatAgreement(requireContext())
                content = getString(R.string.account_agreement_new_txt) + platAgreement
                textArray = arrayListOf(8, 16, 17, 23, 23, 23 + platAgreement.length)
            } else {
                content = getString(R.string.account_agreement_new_txt)
                textArray = arrayListOf(8, 16, 17, 23)
            }
        }
        val spannable = SpannableStringBuilder(content)
        spannable.setSpan(getClickableSpan(linkColor) {
            loginAcVm.getCloudUrl(ServerConstant.Function.ARREEMENT, true)
        }, textArray[0], textArray[1], Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        spannable.setSpan(getClickableSpan(linkColor) {
            loginAcVm.getCloudUrl(ServerConstant.Function.PRIVACY, true)
        }, textArray[2], textArray[3], Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        if (enableOnLogin) {
            spannable.setSpan(
                getClickableSpan(linkColor) {
                    loginAcVm.getCloudUrl(YPHelper.getPlat(requireContext()), true)
                },
                textArray[4],
                textArray[5],
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
        }
        return spannable
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

    private fun showLoginCheckDialog(type: String) {
        val content = when (type) {
            "autoLogin" -> {
                initSpecialText(enableOnLogin = true, isDialog = true)
            }
            else -> {
                initSpecialText(enableOnLogin = false, isDialog = true)
            }
        }

        context?.let {
            CommonDialog.Builder(it)
                .specialContent(content)
                .confirmTxt("??????")
                .cancelTxt("?????????")
                .onCancelClick {
                    UTHelper.commonEvent(UTConstant.Login.Login_AgreementPopup, "button", "?????????")
                    viewBinding.checkbox.isSelected = false
                    TipDialog.dismiss()
                }
                .onConfirmClick {
                    UTHelper.commonEvent(UTConstant.Login.Login_AgreementPopup, "button", "??????")
                    viewBinding.checkbox.isSelected = true
                    when (type) {
                        "autoLogin" -> {
                            autoLogin()
                        }
                        "phoneNumberLogin" -> {
                            loginAcVm.isAgreeAgreement = true
                            loginByMobile()
                        }
                        "weChatLogin" -> {
                            loginByWechat()
                        }
                    }
                    TipDialog.dismiss()
                }
                .builder()
                .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.weChatLoginLiveData.removeObserver(weChatLoginResultObserver)
        viewModel.onKeyLoginResult.removeObserver(oneKeyLoginResultObserver)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            LoginIndexFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }


    class ThreadClass : Thread() {
        private var x = 0f
        private var y = 0f
        private var waitTime: Long = 100L
        override fun run() {
            try {
                sleep(waitTime)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            try {
                val inst = Instrumentation()
                inst.sendPointerSync(
                    MotionEvent.obtain(
                        SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_DOWN, x, y, 0
                    )
                )
                inst.sendPointerSync(
                    MotionEvent.obtain(
                        SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_UP, x, y, 0
                    )
                )
            } catch (e: Exception) {
            }
        }

        fun initXY(x: Float, y: Float, waitTime: Long) {
            this.x = x
            this.y = y
            this.waitTime = waitTime
        }
    }
}