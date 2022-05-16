package cool.dingstock.calendar.sneaker.product

import cool.dingstock.appbase.entity.bean.calendar.PriceListResultEntity
import cool.dingstock.appbase.entity.bean.home.HomeProductDetailData
import cool.dingstock.appbase.entity.bean.home.bp.GoodDetailEntity
import cool.dingstock.appbase.mvvm.SingleLiveEvent
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.calendar.CalendarApi
import cool.dingstock.appbase.net.api.calendar.CalendarHelper.productAction
import cool.dingstock.appbase.net.api.common.CommonApi
import cool.dingstock.appbase.net.api.home.HomeApi
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.calendar.dagger.CalendarApiHelper
import cool.dingstock.lib_base.util.Logger
import javax.inject.Inject

class HomeProductDetailViewModel : BaseViewModel() {
    @Inject
    lateinit var homeApi: HomeApi

    @Inject
    lateinit var commonApi: CommonApi

    @Inject
    lateinit var calendarApi: CalendarApi

    var productId: String? = null

    var productName: String? = ""
    val goodDetailLiveData = SingleLiveEvent<GoodDetailEntity>()

    val homeDetailData = SingleLiveEvent<HomeProductDetailData>()
    val actionSuccess = SingleLiveEvent<String>()
    val priceList = SingleLiveEvent<PriceListResultEntity?>()

    init {
        CalendarApiHelper.apiHomeComponent.inject(this)
    }

    fun parseLink(url: String?) {
        if (url.isNullOrEmpty()) {
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
            }, {
                postAlertHide()
                shortToast("商品查询失败")
            })
    }

    fun getHomeDetail() {
        if (productId.isNullOrEmpty()) {
            return
        }
        val subscribe = homeApi.getProductDetail(productId!!)
            .subscribe({ res ->
                if (!res.err && res.res != null) {
                    val data: HomeProductDetailData = res.res!!
                    if (null == data.product) {
                        postStateError("产品不存在")
                        return@subscribe
                    }
                    data.raffles?.map {
                        it.setupFinishState()
                    }
                    homeDetailData.postValue(data)
                    productName = data.product!!.name
                } else {
                    postStateError(res.msg)
                }
            }, { err ->
                postStateError(err.message)
            })
        addDisposable(subscribe)
    }


    fun productAction(action: String) {
        if (null == LoginUtils.getCurrentUser()) {
            return
        }
        if (productId.isNullOrEmpty()) {
            return
        }
        productAction(action, productId, object : ParseCallback<String> {
            override fun onSucceed(data: String?) {
                Logger.d(
                    "productAction action=" + action
                            + " productId=" + productId + " success --"
                )
                actionSuccess.postValue(action)
            }

            override fun onFailed(errorCode: String, errorMsg: String) {
                Logger.e(
                    ("productAction action=" + action
                            + " productId=" + productId + " failed --")
                )
            }
        })
    }

    fun priceList() {
        if (productId.isNullOrEmpty()) {
            return
        }
        calendarApi.priceList(productId!!)
            .subscribe({ res ->
                if (!res.err && res.res != null) {
                    priceList.postValue(res.res)
                } else {
                    shortToast(res.msg)
                }
            }, { err: Throwable ->
                priceList.postValue(null)
                shortToast(err.message)
            })
    }
}