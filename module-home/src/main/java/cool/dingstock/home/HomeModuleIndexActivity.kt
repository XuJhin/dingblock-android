package cool.dingstock.home

import android.os.Bundle
import cool.dingstock.appbase.mvp.BaseActivity
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.home.ui.home.HomeIndexFragment

class HomeModuleIndexActivity : BaseActivity() {
    override fun moduleTag(): String = "defalut"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_module_index)
        addFragment()
    }

    override fun setSystemStatusBar() {
        super.setSystemStatusBar()
        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null)
        StatusBarUtil.setLightMode(this)
    }

    private fun addFragment() {
        val beginTransaction = supportFragmentManager.beginTransaction()
        val fragment = HomeIndexFragment()
        beginTransaction.add(R.id.root_view,fragment)
        beginTransaction.commit()
    }
}