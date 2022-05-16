package cool.dingstock.monitor.ui.log

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cool.dingstock.appbase.entity.bean.monitor.MonitorLogBean
import cool.dingstock.appbase.ext.doRequest
import cool.dingstock.appbase.list.AbsListViewModel
import cool.dingstock.appbase.net.api.monitor.MonitorApi
import cool.dingstock.monitor.dagger.MonitorApiHelper
import javax.inject.Inject

class MonitorLogViewModel : AbsListViewModel() {

    val listLiveData = MutableLiveData<List<MonitorLogBean>>()
    val listLoadLiveData = MutableLiveData<List<MonitorLogBean>>()
    private var nextKey: Long? = null

    @Inject
    lateinit var api: MonitorApi

    init {
        MonitorApiHelper.apiMonitorComponent.inject(this)
    }

    fun refresh(isRefresh: Boolean) {
        if (isRefresh) {
            nextKey = null
        }
        viewModelScope.doRequest({ api.getMonitorLogs(nextKey) }, { result ->
            if (!result.err) {
                nextKey = result.res?.nextKey
                if (isRefresh) {
                    listLiveData.postValue(result.res?.list)
                } else {
                    listLoadLiveData.postValue(result.res?.list)
                }
            } else {
                shortToast(result.msg)
            }
        }, {
            shortToast(it.message)
        })
    }
}