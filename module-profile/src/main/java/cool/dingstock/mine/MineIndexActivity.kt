package cool.dingstock.mine

import android.os.Bundle
import android.view.View
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.mvp.BaseActivity
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.mine.ui.index.MineFragment

class MineIndexActivity : BaseActivity() {
	override fun moduleTag(): String {
		return "index"
	}

	override fun setSystemStatusBar() {
		super.setSystemStatusBar()
		StatusBarUtil.setTranslucent(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_mine_index)
		addFragment()
		findViewById<View>(R.id.login_btn).hide(LoginUtils.isLogin())
		findViewById<View>(R.id.login_btn)
				.setOnClickListener {
					LoginUtils.isLoginAndRequestLogin(this)
				}
	}

	private fun addFragment() {
		val beginTransaction = supportFragmentManager.beginTransaction()
		val fragment = MineFragment()
		beginTransaction.add(R.id.root_view, fragment)
		beginTransaction.commit()
	}
}