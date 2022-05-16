package cool.dingstock.monitor.center

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.mvvm.activity.BaseViewModel

class MonitorCenterViewModel : BaseViewModel() {
	//记录我的监控页面是否显示深色背景（用户未登录和数据为空时 不显示深色背景）
	var minContentIsBlackTheme = false
		set(value) {
			if (field != value) {
				field = value
				mineContentShowBlackBg.postValue(value)
			}
		}
	var mineContentShowBlackBg = MutableLiveData<Boolean>()
}