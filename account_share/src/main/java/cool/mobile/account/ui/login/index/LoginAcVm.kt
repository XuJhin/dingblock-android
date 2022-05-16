package cool.mobile.account.ui.login.index

import androidx.lifecycle.MutableLiveData
import com.qipeng.yp.onelogin.QPOneLogin
import com.qipeng.yp.onelogin.callback.QPResultCallback
import cool.dingstock.appbase.constant.AccountConstant
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.lib_base.util.Logger

class LoginAcVm : BaseViewModel() {
    var loginAction = AccountConstant.LOGIN_FINISH
    var enableOneLogin = false
    val enableOneLoginLiveData = MutableLiveData<Boolean>()
    val wechatBindPhoneLogin = MutableLiveData<PhoneLoginIntent>()
    val phoneLogin = MutableLiveData<Boolean>()

    val phoneBack = MutableLiveData<Boolean>()

    var hiddenLoginTime = 0 //限制隐藏弹窗登陆次数，失败次数多了就恢复正常登陆
    var isDisableHiddenLogin = false
    var autoLoginClickTime = 100L

    var isAgreeAgreement = false

    /**
     * 预先取号
     *
     * */
    fun yPOneLoginPrePhone() {
        QPOneLogin.getInstance().preGetToken(object : QPResultCallback {
            override fun onSuccess(message: String) {
                Logger.d("云片预取号成功", message)
                enableOneLogin = true
                enableOneLoginLiveData.postValue(true)
            }

            override fun onFail(message: String) {
                Logger.d("云片预取号失败", message)
                enableOneLogin = false
                enableOneLoginLiveData.postValue(false)
            }
        })
    }


    fun getCloudUrl(type: String?) {
        postAlertLoading()
        MobileHelper.getInstance().getDingUrl(type, object : ParseCallback<String?> {
            override fun onSucceed(data: String?) {
                data?.let {
                    router(it)
                }
                postAlertHide()
            }

            override fun onFailed(errorCode: String, errorMsg: String) {
                postAlertHide()
            }
        })
    }


}


data class PhoneLoginIntent(val userId: String, val token: String)