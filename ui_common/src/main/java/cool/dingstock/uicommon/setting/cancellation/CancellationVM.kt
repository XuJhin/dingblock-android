package cool.dingstock.uicommon.setting.cancellation

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.uicommon.dagger.UICommonApiHelper
import javax.inject.Inject


/**
 * 类名：CancellationVM
 * 包名：cool.dingstock.uicommon.setting.cancellation
 * 创建时间：2022/1/19 7:37 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class CancellationVM:BaseViewModel() {

    val registerOutLivaData: MutableLiveData<Boolean> = MutableLiveData()

    @Inject
    lateinit var accountApi: AccountApi

    init {
        UICommonApiHelper.apiPostComponent.inject(this)
    }

    fun registerOut(){
        accountApi.registerOut().subscribe({
            shortToast("账号注销成功注销成功后返回app首页")
            LoginUtils.loginOut()
            registerOutLivaData.postValue(true)
        },{
            shortToast("注销失败")
        })
    }
}