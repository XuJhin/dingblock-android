package cool.dingstock.appbase.list

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.status.PageLoadState

open class AbsListViewModel : BaseViewModel() {
    val pageLoadStateLiveData = MutableLiveData<PageLoadState>()

    //页面状态
    fun postPageStateLoading(msg: String = "加载中…", isRefresh: Boolean = true) {
        pageLoadStateLiveData.postValue(PageLoadState.Loading(msg, isRefresh))
    }

    fun postPageStateSuccess(msg: String = "加载成功", isRefresh: Boolean = true) {
        pageLoadStateLiveData.postValue(PageLoadState.Success(msg, isRefresh))
    }

    fun postPageStateError(msg: String = "网络错误", isRefresh: Boolean = true) {
        pageLoadStateLiveData.postValue(PageLoadState.Error(msg, isRefresh))
    }

    fun postPageStateEmpty(msg: String = "数据为空", isRefresh: Boolean = true) {
        pageLoadStateLiveData.postValue(PageLoadState.Empty(msg, isRefresh))
    }
}