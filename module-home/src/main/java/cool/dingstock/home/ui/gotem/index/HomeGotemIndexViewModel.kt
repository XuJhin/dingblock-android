package cool.dingstock.home.ui.gotem.index

import cool.dingstock.appbase.entity.bean.home.HomeGotemBean
import cool.dingstock.appbase.mvvm.SingleLiveEvent
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.status.PageLoadState
import cool.dingstock.appbase.net.api.home.HomeHelper
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.lib_base.util.Logger

class HomeGotemIndexViewModel: BaseViewModel() {
    val itemList = SingleLiveEvent<Pair<List<HomeGotemBean>, Boolean>>()
    val pageLoadState = SingleLiveEvent<PageLoadState>()
    private var page = 0

    fun getGotemData(isRefresh: Boolean) {
        if (isRefresh) {
            page = 0
        } else {
            page++
        }
        HomeHelper.getInstance().getGotemData(page, object : ParseCallback<List<HomeGotemBean>> {
            override fun onSucceed(data: List<HomeGotemBean>) {
                Logger.d(
                    "getGotemData success="
                            + " isRefresh=" + isRefresh
                            + " data=" + data.size
                )
                if (data.isEmpty()) {
                    pageLoadState.postValue(PageLoadState.Empty("数据为空", isRefresh))
                    return
                }
                pageLoadState.postValue(PageLoadState.Success("加载成功", isRefresh))
                itemList.postValue(data to isRefresh)
            }

            override fun onFailed(errorCode: String, errorMsg: String) {
                Logger.e(
                    "getGotemData failed errorCode="
                            + errorCode + " errorMsg=" + errorMsg
                )
                pageLoadState.postValue(PageLoadState.Error(errorMsg, isRefresh))
            }
        })
    }
}