package cool.dingstock.setting.ui.verify

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.helper.SettingHelper
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.common.CommonApi
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.setting.dagger.SettingApiHelper
import javax.inject.Inject

class VerifyViewModel : BaseViewModel() {
    @Inject
    lateinit var commonApi:CommonApi
    val successLiveData = MutableLiveData<String>()
    val errorLiveData = MutableLiveData<String>()

    init {
        SettingApiHelper.apiSettingComponent.inject(this)
    }

    fun getUrl() {
        commonApi.commonInfo("applyOfficialAccount")
                .subscribe({
                    if(!it.err){
                        successLiveData.postValue(it.res?.url?:"")
                    }else{
                        errorLiveData.postValue(it.msg)
                    }
                },{
                    errorLiveData.postValue(it.message)
                })
    }
}