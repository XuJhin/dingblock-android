package cool.dingstock.setting.ui.setting.darkMode

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.constant.SettingConstant
import cool.dingstock.appbase.helper.DarkMode
import cool.dingstock.appbase.helper.DarkModeHelper
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.util.isDarkMode
import cool.dingstock.appbase.util.isWhiteMode
import cool.dingstock.uicommon.setting.R
import cool.dingstock.uicommon.setting.databinding.ActivityDarkSettingBinding

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [SettingConstant.Path.SET_DARK_MODEL]
)
class DarkModeSettingActivity: VMBindingActivity<DarkModeSettingViewModel, ActivityDarkSettingBinding>() {
    private var needTransition = true

    override fun moduleTag(): String {
        return ModuleConstant.SETTING
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        with(viewBinding) {
            darkModeBtn.animationDuration = 0L
            followSystemBtn.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            darkModeBtn.isChecked = isDarkMode()
            setDarkModeEnable()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.i("dingStock", "onRestoreInstanceState")
    }

    override fun enableEnablePendingTransition(): Boolean {
        return needTransition
    }

    private fun setDarkModeEnable() {
        viewBinding.darkModeTv.isEnabled =
            AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        viewBinding.darkModeBtn.isEnabled =
            AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    override fun initListeners() {
        with(viewBinding) {
            followSystemBtn.setOnCheckedChangeListener { _, isChecked ->
                DarkModeHelper.setDarkMode(if (isChecked) DarkMode.FOLLOW_SYSTEM else if (isWhiteMode()) DarkMode.WHITE_MODE else DarkMode.DARK_MODE)
                setDarkModeEnable()
            }
            darkModeBtn.setOnCheckedChangeListener { _, isChecked ->
                DarkModeHelper.setDarkMode(if (isChecked) DarkMode.DARK_MODE else DarkMode.WHITE_MODE)
                recreateActivity()
            }
        }
    }

    private fun recreateActivity() {
        needTransition = false
        DcRouter(SettingConstant.Uri.SET_DARK_MODEL).overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        ).start()
        finish()
    }
}