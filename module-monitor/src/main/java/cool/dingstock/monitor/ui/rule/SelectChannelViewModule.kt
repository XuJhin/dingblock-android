package cool.dingstock.monitor.ui.rule

import cool.dingstock.appbase.entity.bean.monitor.ChannelBean
import cool.dingstock.appbase.mvvm.SingleLiveEvent
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.status.PageLoadState
import cool.dingstock.appbase.net.api.monitor.MonitorApi
import cool.dingstock.monitor.dagger.MonitorApiHelper
import javax.inject.Inject

class SelectChannelViewModule: BaseViewModel() {
    @Inject
    lateinit var monitorApi: MonitorApi

    init {
        MonitorApiHelper.apiMonitorComponent.inject(this)
    }

    val liveDataPostRefresh = SingleLiveEvent<MutableList<ChannelBean>>()
    val liveDataPostLoadMore = SingleLiveEvent<MutableList<ChannelBean>>()
    val liveDataEndLoadMore = SingleLiveEvent<Boolean>()
    val pageLoadState = SingleLiveEvent<PageLoadState>()

    private var postNextKey: Long? = null
    private var postIsRefresh = true

    fun refresh() {
        postNextKey = null
        postIsRefresh = true
        loadData()
    }
    fun loadData() {
        monitorApi.getRuleChannel(postNextKey).subscribe({ res ->
            if (!res.err) {
                res.res.let { data ->
                    if (null == data) {
                        pageLoadState.postValue(PageLoadState.Empty("数据为空", postIsRefresh))
                        return@subscribe
                    }
                    if (data.list.isNullOrEmpty()) {
                        liveDataEndLoadMore.postValue(true)
                        pageLoadState.postValue(PageLoadState.Empty("数据为空", postIsRefresh))
                        return@subscribe
                    }
                    postNextKey = data.nextKey
                    pageLoadState.postValue(PageLoadState.Success("加载成功", postIsRefresh))
                    if (postIsRefresh) {
                        liveDataPostRefresh.postValue(data.list.let { mutableListOf<ChannelBean>().apply { addAll(it) } })
                        postIsRefresh = false
                    } else {
                        liveDataPostLoadMore.postValue(data.list.let { mutableListOf<ChannelBean>().apply { addAll(it) } })
                    }
                }
            } else {
                pageLoadState.postValue(PageLoadState.Error(res.msg, postIsRefresh))
            }
        },{ e ->
            pageLoadState.postValue(PageLoadState.Error(e.message ?: "数据异常", postIsRefresh))
        })
    }
}