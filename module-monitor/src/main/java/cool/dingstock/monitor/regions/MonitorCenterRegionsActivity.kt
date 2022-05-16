package cool.dingstock.monitor.regions

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.MonitorConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.lazy.loadFragments
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.util.setSvgColorRes
import cool.dingstock.monitor.R
import cool.dingstock.monitor.databinding.ActivityMonitorCenterRegionsBinding
import cool.dingstock.monitor.ui.regoin.tab.RegionRaffleCommonFragment

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [MonitorConstant.Path.MONITOR_REGIONS]
)
class MonitorCenterRegionsActivity :
    VMBindingActivity<BaseViewModel, ActivityMonitorCenterRegionsBinding>() {
    var raffleStr: String? = ""
    private var regionRaffleCommonFragment: RegionRaffleCommonFragment? = null
    override fun moduleTag(): String {
        return ModuleConstant.HOME_MODULE
    }

    override fun setSystemStatusBar() {
        super.setSystemStatusBar()
        StatusBarUtil.transparentStatus(this)
    }

    private fun setWindowParams() {
        val window = window ?: return
        window.setGravity(Gravity.BOTTOM)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //注意此处
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        raffleStr = uri.getQueryParameter(MonitorConstant.UriParam.FILTER_ID)
        regionRaffleCommonFragment?.viewModel?.updateFilterId(raffleStr)
        viewBinding.titleBar.title = getString(R.string.follow_regions)
        val imageView = AppCompatImageView(this)
        imageView.setSvgColorRes(R.drawable.svg_close, R.color.color_text_black1, 1f)
        val lp = RelativeLayout.LayoutParams(26.dp.toInt(), 26.dp.toInt())
        lp.marginStart = 12.dp.toInt()
        lp.addRule(RelativeLayout.CENTER_VERTICAL)
        viewBinding.titleBar.setLeftView(imageView, lp)
        imageView.setOnClickListener {
            finish()
        }
        regionRaffleCommonFragment = RegionRaffleCommonFragment.getInstance(raffleStr, true)
        regionRaffleCommonFragment?.let {
            loadFragments(R.id.frame_fragment, 0, it)
        }
    }

    override fun initListeners() {
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.dialog_out_bottom)
    }

    override fun onResume() {
        super.onResume()
        setWindowParams()
    }

    override fun isBottomPop(): Boolean {
        return true
    }


}
