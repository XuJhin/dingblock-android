package cool.dingstock.monitor

import android.os.Bundle
import android.view.View
import cool.dingstock.appbase.base.BaseDcActivity
import cool.dingstock.monitor.center.MonitorContentFragment

class MonitorIndexActivity : BaseDcActivity() {
	override fun moduleTag(): String {
		return "test"
	}

	override fun getLayoutId(): Int {
		return R.layout.activity_monitor_index
	}

	override fun initVariables(savedInstanceState: Bundle?) {
		addFragment()
	}

	override fun initListeners() {
	}

	private fun addFragment() {
		val beginTransaction = supportFragmentManager.beginTransaction()
		val fragment = MonitorContentFragment()
		beginTransaction.add(R.id.root_view, fragment)
		beginTransaction.commit()
	}
}