package cool.dingstock.monitor.ui.remindSetting

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.entity.bean.config.RemindConfigEntity
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.common.CommonApi
import cool.dingstock.appbase.net.api.monitor.MonitorApi
import cool.dingstock.monitor.dagger.MonitorApiHelper
import javax.inject.Inject

class RemindSettingViewModel : BaseViewModel() {

    @Inject
    lateinit var commonApi: CommonApi

    @Inject
    lateinit var monitorApi: MonitorApi

    init {
        MonitorApiHelper.apiMonitorComponent.inject(this)
    }

    var isUpDataSuccess: MutableLiveData<Boolean> = MutableLiveData()
    var isFetchDataSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val remindConfigLiveData: MutableLiveData<RemindConfigEntity> = MutableLiveData()

    fun updateRemindSetting(remindType: String, remindAt: ArrayList<Long>) {
        commonApi.updateRemindSetting(remindType, remindAt)
            .subscribe({
                if (!it.err) {
                    isUpDataSuccess.postValue(true)
                } else {
                    shortToast(it.msg)
                }
            }, {
                shortToast(it.message)
            })
    }

    fun fetchRemindSetting() {
        commonApi.fetchRemindSetting()
            .subscribe({
                if (!it.err) {
                    remindConfigLiveData.postValue(it.res)
                    isFetchDataSuccess.postValue(true)
                } else {
                    isFetchDataSuccess.postValue(false)
                    shortToast(it.msg)
                }
            }, {
                isFetchDataSuccess.postValue(false)
                shortToast(it.message)
            })
    }
}