package cool.dingstock.setting.ui.setting.updatePhone

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.entity.bean.mine.MobileEntity
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.setting.dagger.SettingApiHelper
import javax.inject.Inject

class SetNewPhoneViewModel : BaseViewModel() {
    @Inject
    lateinit var accountApi: AccountApi

    init {
        SettingApiHelper.apiSettingComponent.inject(this)
    }

    val countDownLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val captchaDialog: MutableLiveData<Boolean> = MutableLiveData()
    val newPhoneLiveData = MutableLiveData<MobileEntity>()
    val accidentLiveData = MutableLiveData<Boolean>()
    var zone: String = ""

    fun updateBindPhone(code: String, mobile: String, zone: String) {
        accountApi.updateBindPhone(code, mobile, zone).subscribe({
            if (!it.err) {
                if (it.code == 251) {
                    shortToast(it.msg)
                    accidentLiveData.postValue(true)
                } else {
                    //更改绑定手机后，相关数据刷新
                    it.res?.let { LoginUtils.loginSuccess(it) }
                    AccountHelper.getInstance().saveUserPhone(mobile)
                    LoginUtils.handLoginSuccess()

                    shortToast("修改手机绑定成功")
                    newPhoneLiveData.postValue(
                        MobileEntity(
                            zone = zone,
                            mobile = mobile,
                            code = code
                        )
                    )
                }
            } else {
                if (it.code == 251) {
                    accidentLiveData.postValue(true)
                }
                shortToast(it.msg)
            }
        }, {
            shortToast(it.message)
        })
    }

    private val smsCodeCallback: ParseCallback<String> = object : ParseCallback<String> {
        override fun onSucceed(data: String?) {
            postAlertHide()
            countDownLiveData.postValue(true)
        }

        override fun onFailed(errorCode: String, errorMsg: String) {
            postAlertHide()
            shortToast(errorMsg)
        }
    }


    fun getSmsCode(zone: String?, phoneNum: String?, eventId: String) {
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
                }
            })
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