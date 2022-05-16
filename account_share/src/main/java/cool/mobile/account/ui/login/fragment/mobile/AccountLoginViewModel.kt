package cool.mobile.account.ui.login.fragment.mobile

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.appbase.push.DCPushManager
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.StringUtils
import cool.mobile.account.BuildConfig
import cool.mobile.account.dagger.AccountApiHelper
import javax.inject.Inject

class AccountLoginViewModel : BaseViewModel() {

    @Inject
    lateinit var accountApi: AccountApi

    private var utEventID = ""
    private var authType = ""
    private var userId = ""
    private var token = ""

    val countDownLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val captchaDialog: MutableLiveData<Boolean> = MutableLiveData()
    val webRouteLiveData: MutableLiveData<String> = MutableLiveData()
    val loginSuccess: MutableLiveData<Boolean> = MutableLiveData()

    private val smsCodeCallback: ParseCallback<String> = object : ParseCallback<String> {
        override fun onSucceed(data: String?) {
            postAlertHide()
            countDownLiveData.postValue(true)
            UTHelper.commonEvent(utEventID,"获取验证码成功")

        }

        override fun onFailed(errorCode: String, errorMsg: String) {
            postAlertHide()
            shortToast(errorMsg)
            UTHelper.commonEvent(utEventID,"获取验证码失败")

        }
    }

    init {
        AccountApiHelper.apiAccountComponent.inject(this)
    }

    fun initData(authType: String?, userId: String?, token: String?) {
        this.authType = authType ?: ""
        this.userId = userId ?: ""
        this.token = token ?: ""
    }

    fun initLoginInfo(uTEventId: String) {
        this.utEventID = uTEventId
    }

    fun getSmsCode(zone: String?, phoneNum: String?,eventId:String) {
        AccountHelper.getInstance().getCaptchaRequirement(
            object : ParseCallback<Boolean?> {
                override fun onSucceed(data: Boolean?) {
                    if (data == null || !data) {
                        AccountHelper.getInstance().getSmsCode(zone, phoneNum, smsCodeCallback)
                        return
                    }
                    captchaDialog.postValue(true)
                }

                override fun onFailed(errorCode: String, errorMsg: String) {
                    AccountHelper.getInstance().getSmsCode(zone, phoneNum, smsCodeCallback)
                    UTHelper.commonEvent(eventId,"获取验证码失败")
                }
            })
    }

    fun login(zone: String, phoneNum: String, code: String) {
        loginBySms(zone, phoneNum, code)
    }

    private fun loginBySms(zone: String, phoneNum: String, code: String) {
        var deviceId = DCPushManager.getInstance().txDeviceToken
        if(BuildConfig.isPart){
            deviceId = "ThisIsATestDeviceId"
        }
        if (StringUtils.isEmpty(deviceId)) {
            shortToast("登录出错请稍后再试")
            postAlertHide()
            return
        }
        val disposable =
            accountApi.loginMobile(zone, phoneNum, code, deviceId, authType, userId, token)
                .subscribe({ baseResult ->
                    if (baseResult.err) {
                        shortToast(baseResult.msg)
                        return@subscribe
                    }
                    baseResult.res?.let { LoginUtils.loginSuccess(it) }
                    AccountHelper.getInstance().saveUserPhone(phoneNum)
                    loginSuccess.postValue(true)
                    UTHelper.commonEvent(utEventID, "登录成功")
                    LoginUtils.handLoginSuccess()
                }) { throwable ->
                    Logger.e(
                        "loginWithSms   onFailed errorCode=" + throwable.message
                            .toString() + " errorMsg=" + throwable.message
                    )
                    postAlertHide()
                    shortToast(throwable.message)
                    UTHelper.commonEvent(utEventID, "登录失败")
                }
        addDisposable(disposable)
    }

    fun getSmsCodeWithCaptcha(
        zone: String, filterPhoneNumber: String,
        appid: String?, ticket: String?, randstr: String?
    ) {
        AccountHelper.getInstance().getSmsCodeWithCaptcha(
            zone,
            filterPhoneNumber,
            appid,
            ticket,
            randstr,
            smsCodeCallback
        )
    }
}