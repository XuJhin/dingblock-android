package cool.dingstock.monitor.ui.shield

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.list.AbsListViewModel
import cool.dingstock.appbase.entity.bean.monitor.MonitorProductBean
import cool.dingstock.appbase.net.api.monitor.MonitorApi
import cool.dingstock.monitor.dagger.MonitorApiHelper
import javax.inject.Inject

class ShieldViewModel : AbsListViewModel() {
	val dataList = MutableLiveData<MutableList<MonitorProductBean>>()
	val loadMoreList = MutableLiveData<MutableList<MonitorProductBean>>()
	val stateError = MutableLiveData<String>()
	val stateLoadMore = MutableLiveData<Int>()
	val stateEmpty = MutableLiveData<Boolean>()
	var nextKey: Long? = 0L
	fun refresh() {
		loadDataList(true)
	}

	@Inject
	lateinit var monitorApi: MonitorApi

	init {
		MonitorApiHelper.apiMonitorComponent.inject(this)
	}

	fun loadDataList(isRefresh: Boolean) {
		if (isRefresh) {
			//刷新时，重置nextKey
			nextKey = 0
		} else {
			if (null == nextKey || nextKey == 0L) {
				stateLoadMore.postValue(LOAD_MORE_COMPLETED)
				return
			}
		}
		monitorApi.fetchShieldList(nextKey)
				.subscribe({
					if (isRefresh) {
						if (it.res == null || it.res?.feeds.isNullOrEmpty()) {
							stateEmpty.postValue(true)
							return@subscribe
						}
						dataList.postValue(it.res?.feeds)
					} else {
						if (it.res == null || it.res?.feeds.isNullOrEmpty()) {
							stateLoadMore.postValue(LOAD_MORE_COMPLETED)
							return@subscribe
						}
						loadMoreList.postValue(it.res?.feeds)
					}
				}, {
					if (isRefresh) {
						stateError.postValue(it.message ?: it.localizedMessage)
					} else {
						stateLoadMore.postValue(LOAD_MORE_FAILED)
					}
					postPageStateError(msg = it.message ?: it.localizedMessage, isRefresh)
				})
	}

	fun bizAction(bizId: String, blocked: Boolean, success: () -> Unit, failed: (msg: String) -> Unit) {
		monitorApi.monitorBlockItem(bizId, blocked)
				.subscribe({
					if (it.err) {
						failed(it.msg)
						return@subscribe
					}
					success()
				}, {
					failed(it.message ?: it.localizedMessage)
				})
	}

	companion object {
		const val LOAD_MORE_FAILED = 1
		const val LOAD_MORE_COMPLETED = 2
	}
}