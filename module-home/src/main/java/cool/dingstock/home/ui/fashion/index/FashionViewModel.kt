package cool.dingstock.home.ui.fashion.index

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.entity.bean.RequestState
import cool.dingstock.appbase.entity.bean.home.fashion.FashionEntity
import cool.dingstock.appbase.net.api.home.HomeApi
import cool.dingstock.home.dagger.HomeApiHelper
import cool.dingstock.post.base.BasePostListViewModel
import javax.inject.Inject

class FashionViewModel : BasePostListViewModel() {

    @Inject
    lateinit var homeApi: HomeApi

    var nextKey: Long = 0
    var isRefresh: Boolean = true
    var refreshResult = MutableLiveData<MutableList<FashionEntity>>()
    val requestState = MutableLiveData<RequestState>()
    val takeId = MutableLiveData<String>()

    init {
        HomeApiHelper.apiHomeComponent.inject(this)
    }

    fun refresh() {
        nextKey = 0
        isRefresh = true
        fetchPost()
    }

    fun fetchAllFashionBrand() {
        homeApi.fetchAllBrandNew()
            .subscribe({res->
                if (res.res?.result.isNullOrEmpty()) {
                    requestState.postValue(RequestState.Empty(isRefresh = isRefresh))
                    return@subscribe
                }
                if (isRefresh) {
                    res.res?.result?.run {
                        var hasSelected = false
                        for (item in this) {
                            if (item.selected) {
                                hasSelected = true
                                takeId.postValue(item.talkId)
                                break
                            }
                        }
                        if (!hasSelected && size > 0) {
                            get(0).selected = true
                            takeId.postValue(get(0).talkId)
                        }
                        refreshResult.postValue(this)
                    }
                }
            },{err->
                requestState.postValue(RequestState.Error(isRefresh,err.message?:"数据错误",false))
            })
    }

    override fun fetchPost() {
        super.fetchPost()
        isRefresh = true
        nextKey = 0
        fetchPostCore(nextKey = postNextKey)
    }

    private fun fetchPostCore(nextKey: Long) {
        takeId.value?.let {
            homeApi.fashionPosts(nextKey, it).subscribe({ res ->
                this.nextKey = res.res?.nextKey ?: 0
                if (!res.err && res.res != null) {
                    postListCallBack.onSucceed(res.res)
                } else {
                    postListCallBack.onFailed(res.code.toString(), res.msg)
                }
            }, { err ->
                postListCallBack.onFailed("0", err.message ?: "数据异常")
            })
        }
    }

    override fun fetchMorePost() {
        super.fetchMorePost()
        fetchPostCore(nextKey = postNextKey)
    }
}