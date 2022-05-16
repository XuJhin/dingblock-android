package cool.dingstock.monitor.ui.monitorCity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cool.dingstock.appbase.entity.bean.monitor.MonitorCitiesEntity
import cool.dingstock.appbase.ext.doRequest
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.monitor.MonitorApi
import cool.dingstock.monitor.dagger.MonitorApiHelper
import javax.inject.Inject


class MonitorCityViewModel : BaseViewModel() {

    @Inject
    lateinit var monitorApi: MonitorApi

    init {
        MonitorApiHelper.apiMonitorComponent.inject(this)
    }

    val monitorCitiesLiveData = MutableLiveData<ArrayList<MonitorCitiesEntity>>()

    //默认选择的TAB
    val currentSelectedTab: MutableLiveData<MonitorCitiesEntity> = MutableLiveData()


    val isUpdateMonitorCitySuccess: MutableLiveData<Boolean> = MutableLiveData()
    val monitorCityCountLiveData: MutableLiveData<Int> = MutableLiveData()
    var channelId = ""
    var monitorCityNumber = 0
    var monitorCityTotal = 0

    fun fetchQueryCities(channelId: String) {
        viewModelScope.doRequest({ monitorApi.fetchQueryCities(channelId) }, { result ->
            if (!result.err) {
                result.res?.let {
                    monitorCitiesLiveData.postValue(result.res ?: arrayListOf())
                    result.res?.forEach {
                        it.cities?.forEach {
                            if (it.isSelect) {
                                monitorCityNumber += 1
                            }
                            monitorCityTotal += 1
                        }
                    }
                    if (monitorCityNumber <= 0) {
                        monitorCityCountLiveData.postValue(monitorCityTotal)
                    } else {
                        monitorCityCountLiveData.postValue(monitorCityNumber)
                    }
                }
            } else {
                shortToast(result.msg)
            }
        }, {
            shortToast(it.message)
        })
    }

    fun upDateMonitorCity(city: String, select: Boolean) {
        viewModelScope.doRequest(
            { monitorApi.upDateMonitorCity(channelId, city, select) },
            { result ->
                if (!result.err) {
                    result.res?.let {
                        isUpdateMonitorCitySuccess.postValue(true)
                        if (select) {
                            monitorCityNumber += 1
                        } else {
                            monitorCityNumber -= 1
                        }
                        if (monitorCityNumber <= 0) {
                            monitorCityCountLiveData.postValue(monitorCityTotal)
                        } else {
                            monitorCityCountLiveData.postValue(monitorCityNumber)
                        }
                    }
                } else {
                    shortToast(result.msg)
                }
            },
            {
                shortToast(it.message)
            })
    }
}