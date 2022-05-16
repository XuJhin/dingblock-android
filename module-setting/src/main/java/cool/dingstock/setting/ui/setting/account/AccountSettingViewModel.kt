package cool.dingstock.setting.ui.setting.account


import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.mine.AccountSettingEntity
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.push.DCPushManager
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.bindDialog
import cool.dingstock.lib_base.util.StringUtils
import cool.dingstock.setting.dagger.SettingApiHelper
import cool.mobile.account.share.AuthorizeStart
import cool.mobile.account.share.IAuthorizeCallback
import javax.inject.Inject

class AccountSettingViewModel : BaseViewModel() {
    @Inject
    lateinit var accountApi: AccountApi

    init {
        SettingApiHelper.apiSettingComponent.inject(this)
    }

    val userAccountLiveData = MutableLiveData<AccountSettingEntity>()
    var accountData: AccountSettingEntity? = null
    val isUnBindSuccess = MutableLiveData<Boolean>()
    val isWeChatBindSuccess = MutableLiveData<Boolean>()

    fun fetchAccount() {
        accountApi.fetchAccount().subscribe({
            if (!it.err) {
                userAccountLiveData.postValue(it.res ?: AccountSettingEntity("", "", false))
                accountData = it.res ?: null
            } else {
                userAccountLiveData.postValue(AccountSettingEntity("", "", false))
                shortToast(it.msg)
            }
        }, {
            userAccountLiveData.postValue(AccountSettingEntity("", "", false))
            shortToast(it.message)
        })
    }

    //微信
    fun bindingWechat() {
        val deviceId = DCPushManager.getInstance().txDeviceToken
        if (StringUtils.isEmpty(deviceId)) {
            shortToast("登录错误请稍后再试~")
            return
        }
        postAlertLoading()
        AuthorizeStart.authorize(object : IAuthorizeCallback {
            override fun onAuthorizeSuccess(userId: String?, token: String?) {
                if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(token)) {
                    shortToast("获取授权失败")
                    UTHelper.commonEvent(UTConstant.Login.WX_LOGIN, "获取授权失败")
                    postAlertHide()
                    return
                }
                val subscribe = accountApi.bindWeChat(token!!, userId!!)
                    .bindDialog(this@AccountSettingViewModel)
                    .subscribe({ res ->
                        if (!res.err) {
                            accountData?.isWXBinding = true
                            shortToast("微信绑定成功")
                            isWeChatBindSuccess.postValue(true)
                        } else {
                            shortToast(res.msg)
                            isWeChatBindSuccess.postValue(false)
                        }
                    }, {
                        isWeChatBindSuccess.postValue(false)
                        postAlertHide()
                        shortToast(it.message)
                    })
                addDisposable(subscribe)
            }

            override fun onAuthorizeFailed(errorCode: String?, errorMsg: String?) {
                shortToast("获取授权失败")
                postAlertHide()
                UTHelper.commonEvent(UTConstant.Login.WX_LOGIN, "获取授权失败")
            }
        })
    }

    fun unBindingWeChat() {
        accountApi.unBindWeChat().subscribe({
            if (!it.err) {
                accountData?.isWXBinding = false
                isUnBindSuccess.postValue(true)
            } else {
                isUnBindSuccess.postValue(false)
            }
        }, {
            isUnBindSuccess.postValue(false)
        })
    }
}