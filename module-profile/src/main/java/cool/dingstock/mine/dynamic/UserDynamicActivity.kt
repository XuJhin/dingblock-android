package cool.dingstock.mine.dynamic

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.mvvm.StateActivity
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.mine.R
import cool.dingstock.mine.ui.index.MineFragment
import cool.dingstock.mine.ui.index.MineFragmentViewModel

/**
 * 用户主页
 * @author      xujing
 * @since       1.5.3
 */
@RouterUri(
        scheme = RouterConstant.SCHEME,
        host = RouterConstant.HOST,
        path = [MineConstant.Path.DYNAMIC]
)
class UserDynamicActivity : StateActivity() {

    private var objectId: String? = ""
    private var pagePos: String? = ""

    private lateinit var viewModel: MineFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        initBundleData()
        super.onCreate(savedInstanceState)
    }

    override fun initVariables(savedInstanceState: Bundle?) {
        StatusBarUtil.setTranslucentForImageView(this, 0, null)
        StatusBarUtil.setLightMode(this)
        val mineFragment = MineFragment.getInstance()

        objectId?.let { mineFragment.setMine(false).setUserId(it) }
        mineFragment.setVpPage(pagePos)

        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_layout, mineFragment, "mineFragment")
                .commitNowAllowingStateLoss()
        viewModel = ViewModelProvider(mineFragment)[MineFragmentViewModel::class.java]
    }

    override fun initListeners() {
    }

    private fun initBundleData() {
        objectId = uri.getQueryParameter(MineConstant.PARAM_KEY.ID)
        pagePos = uri.getQueryParameter(MineConstant.PARAM_KEY.USER_DYNAMIC_PAGE) ?: MineConstant.DYNAMIC_PAGE
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_user_dynamic
    }

    override fun moduleTag(): String {
        return ModuleConstant.MINE_MODULE
    }
}