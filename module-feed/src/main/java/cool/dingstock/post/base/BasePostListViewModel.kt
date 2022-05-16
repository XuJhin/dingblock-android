package cool.dingstock.post.base

import cool.dingstock.appbase.mvvm.SingleLiveEvent
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.status.PageLoadState
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.bean.home.HomePostData
import cool.dingstock.appbase.net.parse.ParseCallback

abstract class BasePostListViewModel : BaseViewModel() {
    open val liveDataPostRefresh = SingleLiveEvent<MutableList<CircleDynamicBean>>()
    open val liveDataPostLoadMore = SingleLiveEvent<MutableList<CircleDynamicBean>>()
    open val liveDataEndLoadMore = SingleLiveEvent<Boolean>()
    open val pageLoadState = SingleLiveEvent<PageLoadState>()

    var postNextKey: Long = 0
    var postIsRefresh = true
    open var postListCallBack: ParseCallback<HomePostData> = object : ParseCallback<HomePostData> {
        override fun onSucceed(data: HomePostData?) {
            if (null == data) {
                pageLoadState.postValue(PageLoadState.Empty("数据为空", postIsRefresh))
                return
            }
            if (data.posts.isNullOrEmpty()) {
                liveDataEndLoadMore.postValue(true)
                pageLoadState.postValue(PageLoadState.Empty("数据为空", postIsRefresh))
                return
            }
            postNextKey = data.nextKey
            pageLoadState.postValue(PageLoadState.Success("加载成功", postIsRefresh))
            if (postIsRefresh) {
                liveDataPostRefresh.postValue(data.posts as MutableList<CircleDynamicBean>?)
                postIsRefresh = false
            } else {
                liveDataPostLoadMore.postValue(data.posts)
            }
        }

        override fun onFailed(errorCode: String, errorMsg: String) {
            pageLoadState.postValue(PageLoadState.Error(errorMsg, postIsRefresh))
        }
    }




    /**
     * 获取列表数据
     */
    open fun fetchPost() {
        postNextKey = 0
        postIsRefresh = true
    }

    /**
     * 加载更多
     */
    open fun fetchMorePost() {
        postIsRefresh = false
    }
}