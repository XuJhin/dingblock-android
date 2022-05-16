package cool.dingstock.post.list.recommend

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cool.dingstock.appbase.entity.bean.home.HotRankItemEntity
import cool.dingstock.appbase.ext.doRequest
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.home.HomeApi
import cool.dingstock.post.dagger.PostApiHelper
import javax.inject.Inject

class HomeRecommendPostListVM : BaseViewModel() {

    @Inject
    lateinit var homeApi: HomeApi

    init {
        PostApiHelper.apiPostComponent.inject(this)
    }

    val hotRankListLiveData = MutableLiveData<ArrayList<HotRankItemEntity>>()
    val loadPostListViewData = MutableLiveData<Boolean>()

    fun loadHotRankList() {
        postStateLoading()
        viewModelScope.doRequest({ homeApi.fetchHotRankList() }, {
            if (!it.err && it.res != null) {
                postStateSuccess()
                hotRankListLiveData.postValue(it.res?: arrayListOf())
                loadPostListViewData.postValue(true)
            } else {
                postStateError(it.msg)
            }
        }, {
            postStateError(it.message)
        })
    }
}