package cool.dingstock.monitor.ui.regoin.raffle

import android.content.pm.PackageManager
import android.os.Bundle
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.HomeConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.lazy.loadFragments
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.widget.commondialog.CommonDialog
import cool.dingstock.imagepicker.utils.PPermissionUtils
import cool.dingstock.monitor.R
import cool.dingstock.monitor.databinding.ActivityRegionRaffleBinding
import cool.dingstock.monitor.ui.regoin.tab.RegionRaffleCommonFragment

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [HomeConstant.Path.REGION_RAFFLE]
)
class HomeRegionRaffleActivity : VMBindingActivity<HomeRegionRaffleViewModel, ActivityRegionRaffleBinding>() {
    private var regionRaffleCommonFragment: RegionRaffleCommonFragment? = null

    override fun initListeners() {
    }

    override fun moduleTag(): String {
        return ModuleConstant.HOME_MODULE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //添加日历事件申请权限被拒绝后
        grantResults.forEach { permissionState->
            if (permissionState == PackageManager.PERMISSION_DENIED) {
                context?.let {
                    CommonDialog.Builder(it)
                        .content("需要获得授权访问您的日历")
                        .confirmTxt("前往授权")
                        .cancelTxt("取消")
                        .onConfirmClick {
                            val routeHelper = PPermissionUtils(context)
                            routeHelper.gotoPermissionSet()
                        }
                        .builder()
                        .show()
                }
                return
            }
        }
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        regionRaffleCommonFragment = RegionRaffleCommonFragment.getInstance("", false)
        regionRaffleCommonFragment?.let {
            loadFragments(R.id.fragment_layout, 0, regionRaffleCommonFragment!!)
        }
    }
}