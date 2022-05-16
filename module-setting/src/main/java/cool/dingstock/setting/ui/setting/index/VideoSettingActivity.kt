package cool.dingstock.setting.ui.setting.index

import android.os.Bundle
import com.sankuai.waimai.router.annotation.RouterUri
import com.shuyu.gsyvideoplayer.GSYVideoManager
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.constant.SettingConstant
import cool.dingstock.appbase.helper.SettingHelper
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.uicommon.setting.databinding.ActivityVideoSettingBinding

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [SettingConstant.Path.SET_VIDEO]
)
class VideoSettingActivity :
    VMBindingActivity<VideoSettingViewModel, ActivityVideoSettingBinding>() {
    override fun moduleTag(): String {
        return ModuleConstant.SETTING
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        with(viewBinding) {
            wifiAutoPlay.isChecked = SettingHelper.getInstance().isWifiAutoPlay
            cellularAutoPlay.isChecked = SettingHelper.getInstance().isCellularAutoPlay
            defaultOpenSound.isChecked = SettingHelper.getInstance().isDefaultOpenSound
            wifiAutoGif.isChecked = SettingHelper.getInstance().isWifiAutoGif
            cellularAutoGif.isChecked = SettingHelper.getInstance().isCellularAutoGif
        }
    }

    override fun initListeners() {
        with(viewBinding) {
            wifiAutoPlay.setOnCheckedChangeListener { _, isChecked ->
                SettingHelper.getInstance().saveWifiAutoPlay(isChecked)
            }
            cellularAutoPlay.setOnCheckedChangeListener { _, isChecked ->
                SettingHelper.getInstance().saveCellularAutoPlay(isChecked)
            }
            defaultOpenSound.setOnCheckedChangeListener { _, isChecked ->
                SettingHelper.getInstance().saveDefaultOpenSound(isChecked)
                GSYVideoManager.instance().isNeedMute = !isChecked
            }
            wifiAutoGif.setOnCheckedChangeListener { _, isChecked ->
                SettingHelper.getInstance().saveWifiAutoGif(isChecked)
            }
            cellularAutoGif.setOnCheckedChangeListener { _, isChecked ->
                SettingHelper.getInstance().saveCellularAutoGif(isChecked)
            }
        }
    }
}