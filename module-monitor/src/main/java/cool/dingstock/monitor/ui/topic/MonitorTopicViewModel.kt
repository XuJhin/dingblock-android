package cool.dingstock.monitor.ui.topic

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.entity.bean.monitor.MonitorRecommendEntity
import cool.dingstock.appbase.net.api.monitor.MonitorApi
import cool.dingstock.monitor.dagger.MonitorApiHelper
import javax.inject.Inject

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/12/24 18:03
 * @Version:         1.1.0
 * @Description:
 */
class MonitorTopicViewModel : BaseViewModel() {
	val subjectDetailLiveData: MutableLiveData<MonitorRecommendEntity> = MutableLiveData()

	@Inject
	lateinit var monitorApi: MonitorApi

	init {
		MonitorApiHelper.apiMonitorComponent.inject(this)
	}

	fun fetchSubjectDetail(id: String) {
		monitorApi.fetchRecommendSubject(id)
				.subscribe({
					if (it.err) {
						postStateError(it.msg)
						return@subscribe
					}
					postStateSuccess()
					subjectDetailLiveData.postValue(it.res)
				}, {
					postStateError(it.localizedMessage)
				})
	}
}