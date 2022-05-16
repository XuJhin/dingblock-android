package cool.dingstock.post.ui.post.deal.index

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cool.dingstock.appbase.entity.bean.calendar.DealDetailProductEntity
import cool.dingstock.appbase.ext.doRequest
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.calendar.CalendarApi
import cool.dingstock.post.dagger.PostApiHelper
import javax.inject.Inject


/**
 * 类名：DealDetailsIndexVM
 * 包名：cool.dingstock.post.ui.post.deal.index
 * 创建时间：2022/2/11 12:25 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class DealDetailsIndexVM : BaseViewModel() {

    @Inject
    lateinit var calendarApi: CalendarApi

    init {
        PostApiHelper.apiPostComponent.inject(this)
    }

    var id: String = ""
    var currentSize = ""


    val dealProductEntity = MutableLiveData<DealDetailProductEntity>()
    val tabList = arrayListOf<String>()

    fun loadData(showLoading: Boolean = true) {
        if (showLoading) {
            postStateLoading()
        }
        viewModelScope.doRequest({ calendarApi.dealDetails(id) }, {
            if (!it.err && it.res != null && it.res?.product != null) {
                if (showLoading) {
                    postStateSuccess()
                }
                dealProductEntity.postValue(it.res!!.product!!)
            } else {
                if (showLoading) {
                    postStateError(it.msg)
                }
            }
        }, {
            if (showLoading) {
                postStateError(it.message)
            }
        })

    }


}