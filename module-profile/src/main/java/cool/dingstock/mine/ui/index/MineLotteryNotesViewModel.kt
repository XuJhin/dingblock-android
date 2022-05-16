package cool.dingstock.mine.ui.index

import androidx.lifecycle.viewModelScope
import cool.dingstock.appbase.entity.bean.mine.LotteryNoteBean
import cool.dingstock.appbase.mvvm.SingleLiveEvent
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.status.PageLoadState
import cool.dingstock.appbase.net.api.calendar.CalendarApi
import cool.dingstock.appbase.net.retrofit.api.getHttpException
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.mine.dagger.MineApiHelper
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class MineLotteryNotesViewModel: BaseViewModel() {
    @Inject
    lateinit var calendarApi: CalendarApi

    var selectedDate: Long = 0L
    var state: String? = null

    val liveDataPostRefresh = SingleLiveEvent<MutableList<LotteryNoteBean>>()
    val liveDataPostLoadMore = SingleLiveEvent<MutableList<LotteryNoteBean>>()
    val liveDataEndLoadMore = SingleLiveEvent<Boolean>()
    val pageLoadState = SingleLiveEvent<PageLoadState>()

    private var postIsRefresh = true
    private var postNextKey: Long? = null
    var postType = ""

    fun refresh() {
        postIsRefresh = true
        postNextKey = null
        loadData()
    }

    fun loadData() = viewModelScope.launch {
        calendarApi.getRecordList(postNextKey, selectedDate, state)
            .catch {
                pageLoadState.postValue(PageLoadState.Error(getHttpException(it).msg, postIsRefresh))
            }.collect { res ->
                if (!res.err) {
                    res.res.let { data ->
                        if (null == data) {
                            pageLoadState.postValue(PageLoadState.Empty("数据为空", postIsRefresh))
                            return@collect
                        }
                        if (data.list.isNullOrEmpty()) {
                            liveDataEndLoadMore.postValue(true)
                            pageLoadState.postValue(PageLoadState.Empty("数据为空", postIsRefresh))
                            return@collect
                        }
                        postNextKey = data.nextKey
                        pageLoadState.postValue(PageLoadState.Success("加载成功", postIsRefresh))
                        if (postIsRefresh) {
                            liveDataPostRefresh.postValue(data.list.let { mutableListOf<LotteryNoteBean>().apply { addAll(it) } })
                            postIsRefresh = false
                        } else {
                            liveDataPostLoadMore.postValue(data.list.let { mutableListOf<LotteryNoteBean>().apply { addAll(it) } })
                        }
                    }
                } else {
                    pageLoadState.postValue(PageLoadState.Error(res.msg, postIsRefresh))
                }
            }
    }

    suspend fun deleteRecord(id: String) = calendarApi.deleteRecord(id)
        .catch {
            ToastUtil.getInstance()._short(BaseLibrary.getInstance().context, it.message ?: "网络异常")
        }.stateIn(viewModelScope)

    suspend fun updateRecord(id: String, state: String?) = calendarApi.updateRecord(id, state)
        .catch {
            ToastUtil.getInstance()._short(BaseLibrary.getInstance().context, it.message ?: "网络异常")
        }.stateIn(viewModelScope)

    init {
        MineApiHelper.apiMineComponent.inject(this)
    }
}