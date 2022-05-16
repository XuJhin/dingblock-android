package cool.dingstock.monitor.ui.regoin.list

import android.location.Location
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.entity.bean.calendar.PriceListResultEntity
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.entity.bean.home.HomeRegionRaffleBean
import cool.dingstock.appbase.entity.bean.home.bp.GoodDetailEntity
import cool.dingstock.appbase.mvvm.SingleLiveEvent
import cool.dingstock.appbase.net.api.calendar.CalendarApi
import cool.dingstock.appbase.net.api.common.CommonApi
import cool.dingstock.appbase.net.api.monitor.MonitorApi
import cool.dingstock.monitor.dagger.MonitorApiHelper
import javax.inject.Inject

class RaffleFragmentListViewModel : BaseViewModel() {

    @Inject
    lateinit var monitorApi: MonitorApi

    @Inject
    lateinit var commonApi: CommonApi

    @Inject
    lateinit var calendarApi: CalendarApi

    init {
        MonitorApiHelper.apiMonitorComponent.inject(this)
    }


    private var regionId: String = ""
    val finishRefreshData = MutableLiveData<Boolean>()
    val hideLoading = MutableLiveData<Boolean>()
    val listData = MutableLiveData<List<HomeRegionRaffleBean?>>()
    val errorLiveData = MutableLiveData<String>()
    val priceListLiveData = MutableLiveData<PriceListResultEntity>()
    val goodDetailLiveData = SingleLiveEvent<GoodDetailEntity>()
    var location: Location? = null
    fun initRegionId(params: String) {
        this.regionId = params
    }

    fun updateLocation(params: Location?) {
        this.location = params
    }

    fun parseLink(url: String) {
        if (url.isEmpty()) {
            return
        }
        postAlertLoading()
        commonApi.resolveCommon(url)
            .subscribe({ res ->
                postAlertHide()
                if (!res.err && res.res != null) {
                    goodDetailLiveData.postValue(res.res!!)
                } else {
                    shortToast("商品查询失败")
                }
            }, { err ->
                postAlertHide()
                shortToast("商品查询失败")
            })
    }

    fun getRegionShopList(isRefresh: Boolean) {
        if (regionId.isEmpty()) {
            return
        }
        monitorApi.getRegionShopList(
            regionId,
            longitude = location?.longitude,
            latitude = location?.latitude
        )
            .subscribe({ res ->
                if (!res.err && res.res != null) {
                    if (isRefresh) {
                        finishRefreshData.postValue(true)
                    } else {
                        hideLoading.postValue(true)
                    }
                    listData.postValue(res.res!!)
                } else {
                    hideLoading.postValue(true)
                    errorLiveData.postValue(res.msg)
                    finishRefreshData.postValue(true)
                }
            }, { err ->
                hideLoading.postValue(true)
                errorLiveData.postValue(err.message ?: "数据错误")
                finishRefreshData.postValue(true)
            })



//        MineHelper.getInstance().getRegionShopList(regionId, location?.longitude,
//                location?.latitude, object : ParseCallback<List<HomeRegionRaffleBean?>?> {
//            override fun onSucceed(data: List<HomeRegionRaffleBean?>?) {
//                if (isRefresh) {
//                    finishRefreshData.postValue(true)
//                } else {
//                    hideLoading.postValue(true)
//                }
//                listData.postValue(data)
//            }
//
//            override fun onFailed(errorCode: String, errorMsg: String) {
//                hideLoading.postValue(true)
//                errorLiveData.postValue(errorMsg)
//                finishRefreshData.postValue(true)
//            }
//        })
    }


    fun searchPrice(id: String?) {
        if (TextUtils.isEmpty(id)) {
            return
        }
        postAlertLoading()
        calendarApi.priceList(id ?: "")
            .subscribe({
                postAlertHide()
                if (!it.err && it.res != null) {
                    priceListLiveData.postValue(it.res!!)
                } else {
                    shortToast(it.msg)
                }
            }, {
                postAlertHide()
                shortToast(it.message)
            })

    }

}