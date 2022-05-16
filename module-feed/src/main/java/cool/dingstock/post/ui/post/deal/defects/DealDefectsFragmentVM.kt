package cool.dingstock.post.ui.post.deal.defects

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cool.dingstock.appbase.entity.bean.calendar.DealPostItemEntity
import cool.dingstock.appbase.ext.doRequest
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.calendar.CalendarApi
import cool.dingstock.post.dagger.PostApiHelper
import javax.inject.Inject


/**
 * 类名：DealDefectsVM
 * 包名：cool.dingstock.post.ui.post.deal.defects
 * 创建时间：2022/2/11 6:21 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class DealDefectsFragmentVM:BaseViewModel() {

    @Inject
    lateinit var calendarApi: CalendarApi

    init {
        PostApiHelper.apiPostComponent.inject(this)
    }

    var type:String =""
    var id = ""
    var currentSize = ""
    var nextKey: Long? = null
    var loadDataLiveData = MutableLiveData<ArrayList<DealPostItemEntity>>()
    var loadMoreLiveData = MutableLiveData<ArrayList<DealPostItemEntity>>()
    var isFirstLoadData = true

    fun loadData() {
        if(isFirstLoadData){
            postStateLoading()
            isFirstLoadData = false
        }
        nextKey = null
        viewModelScope.doRequest({
            calendarApi.dealDetailsPostPage(
                id,
                type,
                currentSize,
                nextKey
            )
        }, {
            if (!it.err && it.res != null) {
                postStateSuccess()
                nextKey = it.res?.nextKey
                loadDataLiveData.postValue(it.res?.list ?: arrayListOf())
            } else {
                postStateError(it.msg)
            }
        }, {
            postStateError(it.message)
        })
    }

    fun loadMoreData() {
        viewModelScope.doRequest({
            calendarApi.dealDetailsPostPage(
                id,
                type,
                currentSize,
                nextKey
            )
        }, {
            if (!it.err && it.res != null) {
                postStateSuccess()
                nextKey = it.res?.nextKey
                loadMoreLiveData.postValue(it.res?.list ?: arrayListOf())
            } else {
                loadMoreLiveData.postValue(arrayListOf())
            }
        }, {
            loadMoreLiveData.postValue(arrayListOf())
        })
    }

}