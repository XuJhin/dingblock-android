package net.dingblock.mobile.activity.launch.video

import android.os.Bundle
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.HomeConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.lib_base.stroage.ConfigSPHelper
import net.dingblock.mobile.databinding.ActivityLaunchVideoBinding

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [HomeConstant.Path.LAUNCH_VIDEO]
)
class LaunchVideoActivity : VMBindingActivity<BaseViewModel, ActivityLaunchVideoBinding>() {
    private var currentPosition = 0

    override fun setSystemStatusBar() {
        super.setSystemStatusBar()
        StatusBarUtil.transparentStatus(this)
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        ConfigSPHelper.getInstance()
            .save(HomeConstant.SP.FIRST_INSTALL_TIME, System.currentTimeMillis())
    }

    override fun initListeners() {
    }

    override fun moduleTag(): String = "appLaunch"

    override fun onPause() {
        super.onPause()
        viewBinding.videoView.pause()
        currentPosition = viewBinding.videoView.currentPosition
    }

    override fun onResume() {
        super.onResume()
        viewBinding.videoView.start()
        viewBinding.videoView.seekTo(currentPosition)
    }
}