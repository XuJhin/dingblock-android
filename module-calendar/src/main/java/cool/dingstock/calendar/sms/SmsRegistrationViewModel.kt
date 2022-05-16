package cool.dingstock.calendar.sms

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.entity.bean.calendar.PriceListResultEntity
import cool.dingstock.appbase.entity.bean.home.SmsRegistrationBean
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.calendar.CalendarApi
import cool.dingstock.calendar.dagger.CalendarApiHelper
import javax.inject.Inject

/**
 * @author wangjiang
 *  CreateAt Time 2021/7/1  14:40
 */
class SmsRegistrationViewModel : BaseViewModel() {
    @Inject
    lateinit var calendarApi: CalendarApi

    init {
        CalendarApiHelper.apiHomeComponent.inject(this)
    }

    val smsRegistrationListData = MutableLiveData<List<SmsRegistrationBean>>()
    val smsWhiteListData = MutableLiveData<List<String>>()
    val priceListLiveData = MutableLiveData<PriceListResultEntity>()

    private var isRefresh = true

    //刷新 或者是第一次获取
    fun fetchData() {
        isRefresh = true
        fetchSmsRegistrationLists()
    }

    private fun fetchSmsRegistrationLists() {
        calendarApi.getSmsRegistrationList()
                .subscribe({
                    if (!it.err) {
                        smsRegistrationListData.postValue(it.res?.products!!)
                        smsWhiteListData.postValue(it.res?.whitelist!!)
                    } else {
                        updateEmptyData()
                    }
                }, {
                    updateEmptyData()
                })
    }

    private fun updateEmptyData() {
        smsRegistrationListData.postValue(emptyList())
        smsWhiteListData.postValue(emptyList())
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