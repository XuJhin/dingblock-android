package cool.dingstock.monitor.ui.regoin.tab

import android.location.Location
import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.entity.bean.mine.RegionsBean
import cool.dingstock.appbase.net.api.monitor.MonitorApi
import cool.dingstock.monitor.dagger.MonitorApiHelper
import javax.inject.Inject
import kotlin.collections.ArrayList

class RegionRaffleCommonViewModel : BaseViewModel() {

    @Inject
    lateinit var api: MonitorApi

    private var location: Location? = null
    val hideLoading = MutableLiveData<Boolean>()
    val regionsData = MutableLiveData<List<RegionsBean?>>()
    val errorView = MutableLiveData<String>()
    private var filter: String = ""


    init {
        MonitorApiHelper.apiMonitorComponent.inject(this)
    }

    fun getUserRegions() {
        val filterList: ArrayList<String> = arrayListOf()
        if (filter.isNotEmpty()) {
            filterList.add(filter)
        }
        api.getUserRegion(latitude = location?.latitude, longitude = location?.longitude, filter = filterList)
                .subscribe({ res ->
                    hideLoading.postValue(true)
                    if (!res.err && res.res != null) {
                        regionsData.postValue(res.res)
                    }else{
                        errorView.postValue(res.msg)
                    }
                }, { err ->
                    hideLoading.postValue(true)
                    errorView.postValue(err.message)
                })
    }

    fun updateFilterId(raffleStr: String?) {
        if (raffleStr != null) {
            this.filter = raffleStr
        }
    }

    fun updateLocation(location: Location?) {
        this.location = location
    }




}