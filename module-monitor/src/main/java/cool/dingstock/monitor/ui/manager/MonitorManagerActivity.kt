package cool.dingstock.monitor.ui.manager

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.FragmentContainerView
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.MonitorConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.helper.PushCheckHelper
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.activity.VMActivity
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.widget.TitleBar
import cool.dingstock.monitor.R


/**
 * 频道管理页面
 */
@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [MonitorConstant.Path.MONITOR_MANAGE, MonitorConstant.Path.MONITOR_MANAGE1]
)
class MonitorManagerActivity : VMActivity<BaseViewModel>() {
    private lateinit var containerLayer: FragmentContainerView
    var monitorManagerFragment: MonitorManagerFragment? = null
    lateinit var titleBar: TitleBar
    var source: String? = "default"
    var typeCode: String? = null
    override fun getLayoutId(): Int {
        return R.layout.monitor_activity_edit
    }

    override fun initBundleData() {
        super.initBundleData()
        source = intent.data?.getQueryParameter("source")
        typeCode = uri.getQueryParameter("typeCode")
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        titleBar = findViewById(R.id.monitor_edit_titleBar)
        containerLayer = findViewById(R.id.monitor_activity_edit_layer)
        initTitle()
        addFragment()
    }

    override fun setSystemStatusBar() {
        super.setSystemStatusBar()
        StatusBarUtil.setColor(this, resources.getColor(R.color.color_gray), 0)
    }

    private fun initTitle() {
        titleBar.apply {
            setRightVisibility(false)
            setLeftOnClickListener { finishPage() }
        }
    }

    private fun addFragment() {
        monitorManagerFragment =
            supportFragmentManager.findFragmentByTag("monitorManager") as MonitorManagerFragment?
        if (monitorManagerFragment == null) {
            monitorManagerFragment = MonitorManagerFragment.instance(source, typeCode)
        }
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        if (monitorManagerFragment?.isAdded == true) {
            fragmentTransaction.show(monitorManagerFragment!!)
        } else {
            fragmentTransaction.add(
                R.id.monitor_activity_edit_layer,
                monitorManagerFragment!!,
                "monitorManager"
            )
        }
        fragmentTransaction.commitNowAllowingStateLoss()
    }

    override fun initListeners() {}
    override fun moduleTag(): String {
        return ModuleConstant.MONITOR
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishPage()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 结束页面
     */
    private fun finishPage() {
        val pushCheckHelper = PushCheckHelper()
        pushCheckHelper.with(this)
            .start()
    }

}