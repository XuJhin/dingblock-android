package cool.dingstock.home.ui.fashion.brand

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.entity.bean.RequestState
import cool.dingstock.appbase.entity.bean.home.fashion.FashionEntity
import cool.dingstock.appbase.list.AbsListViewModel
import cool.dingstock.appbase.net.api.home.HomeApi
import cool.dingstock.home.dagger.HomeApiHelper
import javax.inject.Inject

class FashionBrandViewModel : AbsListViewModel() {

    @Inject
    lateinit var homeApi: HomeApi

    init {
        HomeApiHelper.apiHomeComponent.inject(this)
    }

    var nextStr: String? = ""
    var isRefresh: Boolean = true
    var refreshResult = MutableLiveData<MutableList<FashionEntity>>()
    val loadMoreResult = MutableLiveData<MutableList<FashionEntity>>()
    val requestState = MutableLiveData<RequestState>()
    fun refresh() {
        nextStr = ""
        isRefresh = true
        fetchFashionBrand()
    }

    private fun fetchFashionBrand() {
        homeApi.fetchAllBrand(nextStr = nextStr)
                .subscribe({res->
                    nextStr = res.res?.nextStr
                    if(!res.err&&res.res!=null){
                        if (isRefresh) {
                            refreshResult.postValue(res.res?.result)
                        } else {
                            loadMoreResult.postValue(res.res?.result)
                        }
                    }else{
                        requestState.postValue(RequestState.Error(isRefresh,res.msg,false))
                    }
                },{err->
                    requestState.postValue(RequestState.Error(isRefresh,err.message?:"数据错误",false))
                })
//        FashionHelper.fetchBrandList(nextKey = nextKey, success = {
//            if (it == null) {
//                requestState.postValue(RequestState.Error(isRefresh, "数据异常"))
//                return@fetchBrandList
//            }
//            nextKey = it.nextStr
//            if (isRefresh) {
//                refreshResult.postValue(it.result)
//            } else {
//                loadMoreResult.postValue(it.result)
//            }
//
//        }, failed = {
//            Log.e("error", it)
//        })
    }

    fun loadMore() {
        isRefresh = false
        fetchFashionBrand()
    }
}