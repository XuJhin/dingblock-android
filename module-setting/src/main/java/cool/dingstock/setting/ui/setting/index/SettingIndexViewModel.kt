package cool.dingstock.setting.ui.setting.index

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.constant.ServerConstant
import cool.dingstock.appbase.entity.event.account.EventActivated
import cool.dingstock.appbase.entity.event.update.EventUserVipChange
import cool.dingstock.appbase.exception.DcException
import cool.dingstock.appbase.helper.SettingHelper
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.setting.dagger.SettingApiHelper
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class SettingIndexViewModel : BaseViewModel() {
	val routeLiveData: MutableLiveData<String> = MutableLiveData()
	val signOutLiveData: MutableLiveData<Boolean> = MutableLiveData()

	@Inject
	lateinit var accountApi: AccountApi

	init {
		SettingApiHelper.apiSettingComponent.inject(this)
	}

	fun getCouldUrl(type: String?) {
		MobileHelper.getInstance().getDingUrl(type, object : ParseCallback<String?> {
			override fun onSucceed(data: String?) {
				routeLiveData.postValue(data)
			}

			override fun onFailed(errorCode: String, errorMsg: String) {
				postAlertHide()
			}
		})
	}

	fun activate(code: String?) {
		SettingHelper.getInstance().activate(code, object : ParseCallback<String?> {
			override fun onSucceed(data: String?) {
				EventBus.getDefault().post(EventActivated())
				updateUser()
				postAlertHide()
				postAlertSuccess(data ?: "激活成功")

			}

			override fun onFailed(errorCode: String, errorMsg: String) {
				postAlertHide()
				postAlertError(errorMsg)
			}
		})
	}

	fun updateUser() {
		accountApi.getUserByNet()
				.subscribe({
					if (!it.err) {
						EventBus.getDefault().post(EventUserVipChange(true))
					}
				}, {})
	}

	fun signOut() {
		val disposable = accountApi.loginLout()
				.subscribe({
					LoginUtils.loginOut()
					shortToast("登出成功")
					signOutLiveData.postValue(true)
				},
						{ error ->
							if (error is DcException) {
								if (error.code == ServerConstant.ErrorCode.INVALID_SESSION || error.code == ServerConstant.ErrorCode.INVALID_SESSION1) {
									//当做登出成功处理
									LoginUtils.loginOut()
									shortToast("登出成功")
									signOutLiveData.postValue(true)
									return@subscribe
								}
							}
							shortToast("退出登录失败")
						})
		addDisposable(disposable)
	}


}