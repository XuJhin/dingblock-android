package cool.dingstock.calendar.ui.pager

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.calendar.PriceListResultEntity
import cool.dingstock.appbase.entity.bean.home.HomeProductDetailData
import cool.dingstock.appbase.entity.bean.home.bp.GoodDetailEntity
import cool.dingstock.appbase.mvvm.SingleLiveEvent
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.status.LoadingDialogStatus
import cool.dingstock.appbase.net.api.calendar.CalendarApi
import cool.dingstock.appbase.net.api.calendar.CalendarHelper
import cool.dingstock.appbase.net.api.calendar.CalendarHelper.productAction
import cool.dingstock.appbase.net.api.common.CommonApi
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.calendar.dagger.CalendarApiHelper
import cool.dingstock.lib_base.util.Logger
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class CalendarFragmentHeavyGoodVM : BaseViewModel() {
    var goodId: String? = null
    val priceLivedata: SingleLiveEvent<PriceListResultEntity> = SingleLiveEvent()
    val likeLiveData = MutableLiveData<String>()
    val goodDetailLiveData = SingleLiveEvent<GoodDetailEntity>()
    var scrollToPosition = 0

    @Inject
    lateinit var calendarApi: CalendarApi

    @Inject
    lateinit var commonApi: CommonApi

    init {
        CalendarApiHelper.apiHomeComponent.inject(this)
    }

    val dataResult: SingleLiveEvent<HomeProductDetailData> = SingleLiveEvent()
    fun fetchGoodInfo() {
        if (goodId == null) {
            return
        }
        viewModelScope.launch {
            calendarApi.getProductDetail(goodId!!)
                .catch { }
                .onStart { }
                .collectLatest {
                    it.res?.raffles?.map { detail->
                        detail.setupFinishState()

                    }
                    dataResult.postValue(it.res)
                }
        }
    }

    fun priceList(goodId: String) {
        calendarApi.priceList(goodId)
            .subscribe({ (err, res1, _, msg): BaseResult<PriceListResultEntity> ->
                loadingDialogLiveData.postValue(LoadingDialogStatus.Loading())
                if (!err && res1 != null) {
                    loadingDialogLiveData.postValue(LoadingDialogStatus.Hide)
                    priceLivedata.postValue(res1)
                } else {
                    toastLiveData.postValue(msg)
                }
            }, { err: Throwable ->
                loadingDialogLiveData.postValue(LoadingDialogStatus.Hide)
                toastLiveData.postValue(err.message)
            })
    }

    fun productAction(action: String) {
        val productId = dataResult.value?.product?.objectId
        if (TextUtils.isEmpty(productId)) {
            return
        }
        likeLiveData.postValue(action)
        productAction(action, productId, object : ParseCallback<String> {
            override fun onSucceed(data: String?) {

            }

            override fun onFailed(errorCode: String, errorMsg: String) {

            }
        })
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
}