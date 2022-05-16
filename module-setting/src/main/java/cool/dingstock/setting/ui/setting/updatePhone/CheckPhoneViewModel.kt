package cool.dingstock.setting.ui.setting.updatePhone

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.entity.bean.mine.MobileEntity
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.setting.dagger.SettingApiHelper
import javax.inject.Inject

class CheckPhoneViewModel : BaseViewModel() {
    @Inject
    lateinit var accountApi: AccountApi

    init {
        SettingApiHelper.apiSettingComponent.inject(this)
    }

    var phoneNumber = ""
    var zoneNumber = ""

    val oldPhoneLiveData = MutableLiveData<MobileEntity>()
    val countDownLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val captchaDialog: MutableLiveData<Boolean> = MutableLiveData()

    fun checkCurrentPhone(code: String, mobile: String, zone: String) {
        accountApi.checkCurrentPhone(code, mobile, zone).subscribe({
            if (!it.err) {
                oldPhoneLiveData.postValue(
                    MobileEntity(
                        zone = zone,
                        mobile = mobile,
                        code = code
                    )
                )
            } else {
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