package cool.dingstock.monitor.dialog

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.entity.bean.mine.MineVipChargeEntity
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.net.api.monitor.MonitorApi
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.monitor.dagger.MonitorApiHelper
import javax.inject.Inject

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/12/28 10:40
 * @Version:         1.1.0
 * @Description:
 */
class MonitorVipViewModel : BaseViewModel() {
	val routeLiveData: MutableLiveData<String> = MutableLiveData()

	@Inject
	lateinit var monitorApi: MonitorApi

	init {
		MonitorApiHelper.apiMonitorComponent.inject(this)
	}

	fun fetchVipUrl() {
	}
}