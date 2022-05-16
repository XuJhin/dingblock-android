package cool.dingstock.monitor.ui.recommend

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
 * @Date:            2020/12/24 11:39
 * @Version:         1.1.0
 * @Description:
 */
class MonitorRecommendViewModel : BaseViewModel() {
	val recommendMonitorLiveData: MutableLiveData<ArrayList<MonitorRecommendEntity>> =
			MutableLiveData()
	val hotRecommendMonitorLiveData = MutableLiveData<MonitorRecommendEntity>()

	val finisRefreshLiveData: MutableLiveData<Boolean> = MutableLiveData()

	@Inject
	lateinit var monitorApi: MonitorApi

	init {
		MonitorApiHelper.apiMonitorComponent.inject(this)
	}

	fun fetchRecommendMonitor() {
		monitorApi.fetchRecommendMonitor()
				.subscribe({
					finisRefreshLiveData.postValue(true)
					if (it.err) {
						postStateError(it.msg)
						return@subscribe
					}
					if (it.res.isNullOrEmpty()) {
						postStateEmpty()
					} else {
						postStateSuccess()
						if(it.res!!.size>0){
							val list = it.res!!
							val entity = list.removeAt(0)
							hotRecommendMonitorLiveData.postValue(entity)
							recommendMonitorLiveData.postValue(list)
						}else{
							recommendMonitorLiveData.postValue(it.res)
						}
					}
				}, {
					finisRefreshLiveData.postValue(true)
					postStateError(it.localizedMessage)
				})
	}
}