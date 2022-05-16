package cool.dingstock.calendar.sneaker.heavy

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.entity.bean.calendar.PriceListResultEntity
import cool.dingstock.appbase.entity.bean.home.CalenderProductEntity
import cool.dingstock.appbase.entity.bean.home.HomeProduct
import cool.dingstock.appbase.entity.bean.home.HomeProductDetail
import cool.dingstock.appbase.entity.bean.home.bp.GoodDetailEntity
import cool.dingstock.appbase.mvvm.SingleLiveEvent
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.calendar.CalendarApi
import cool.dingstock.appbase.net.api.calendar.CalendarHelper
import cool.dingstock.appbase.net.api.common.CommonApi
import cool.dingstock.appbase.net.api.home.HomeApi
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.calendar.dagger.CalendarApiHelper
import javax.inject.Inject

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/28  14:22
 */
class HeavySneakersVm : BaseViewModel() {

    @Inject
    lateinit var homeApi: HomeApi

    @Inject
    lateinit var commonApi: CommonApi

    @Inject
    lateinit var calendarApi: CalendarApi


    val raffleLiveData = MutableLiveData<List<HomeProductDetail>>()
    val productInfoLiveData = MutableLiveData<HomeProduct>()
    val likeLiveData = MutableLiveData<String>()
    val heavyListLiveData = MutableLiveData<ArrayList<CalenderProductEntity>>()
    val goodDetailLiveData = SingleLiveEvent<GoodDetailEntity>()
    val priceListLiveData = SingleLiveEvent<PriceListResultEntity>()

    var productName = ""

    init {
        CalendarApiHelper.apiHomeComponent.inject(this)
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

    fun loadHeavy() {
        postStateLoading()
        homeApi.heavyList()
            .subscribe({
                if (!it.err) {
                    postStateSuccess()
                    heavyListLiveData.postValue(it.res!!)
                } else {
                    postStateError(it.msg)
                }
            }, {
                postStateError(it.message)
            })
    }

    fun loadRaffle(entity: CalenderProductEntity) {
        homeApi.getProductDetail(entity.id ?: entity.objectId ?: "")
            .subscribe({
                if (!it.err && it.res != null) {
                    it.res?.product.let { homeProduct ->
                        productName = homeProduct?.name ?: ""
                        productInfoLiveData.postValue(homeProduct)
                    }
                    it.res?.raffles?.map { productInfo ->
                        productInfo.setupFinishState()
                    }
                    it.res?.raffles?.let { list ->
                        raffleLiveData.postValue(list)
                    }
                } else {
                }
            }, {
            })
    }

    fun productAction(action: String, productId: String) {
        if (TextUtils.isEmpty(productId)) {
            return
        }

        CalendarHelper.productAction(action, productId, object : ParseCallback<String> {
            override fun onSucceed(data: String?) {
                likeLiveData.postValue(action)
            }

            override fun onFailed(errorCode: String, errorMsg: String) {

            }
        })
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