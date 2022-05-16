package cool.dingstock.monitor.dialog

import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.*
import cool.dingstock.appbase.mvvm.activity.VMActivity
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.monitor.R

@RouterUri(scheme = RouterConstant.SCHEME,host = RouterConstant.HOST ,path = [MonitorConstant.Path.MONITOR_DIALOG_VIP])
class MonitorVIPDialog : VMActivity<MonitorVipViewModel>() {


	override fun setSystemStatusBar() {
		StatusBarUtil.setLightMode(this)
		StatusBarUtil.setTranslucent(this, 0)
	}

	override fun initViewAndEvent(savedInstanceState: Bundle?) {
		val source = uri.getQueryParameter(CommonConstant.UriParams.SOURCE)
		findViewById<ImageView>(R.id.icon_close_vip_dialog)
				.setOnClickListener {
					finish()
				}
		findViewById<ImageView>(R.id.dialog_cover)
				.setOnClickListener {
					UTHelper.commonEvent(UTConstant.Mine.VipP_ent,"source",source)
					MobileHelper.getInstance().getCloudUrlAndRouter(context,MineConstant.VIP_CENTER)
				}
		viewModel.routeLiveData.observe(this, Observer {
			routeTo(it)
			this.finish()
		})
	}

	override fun getLayoutId(): Int {
		return R.layout.dialog_monitor_vip
	}

	override fun initListeners() {
	}

	override fun moduleTag(): String {
		return ModuleConstant.MONITOR
	}

	override fun finish() {
		super.finish()
		overridePendingTransition(0, cool.dingstock.appbase.R.anim.dialog_window_scale_out)
	}

	override fun onBackPressed() {
		super.onBackPressed()
		overridePendingTransition(0, cool.dingstock.appbase.R.anim.dialog_window_scale_out)
	}

}