//package cool.dingstock.setting.ui.theme
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatDelegate
//import com.sankuai.waimai.router.annotation.RouterUri
//import cool.dingstock.appbase.constant.ModuleConstant
//import cool.dingstock.appbase.constant.RouterConstant
//import cool.dingstock.appbase.constant.SettingConstant
//import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
//import cool.dingstock.foundation.manager.ThemeManager
//import cool.dingstock.uicommon.setting.databinding.SettingActivityThemeBinding
//
//@RouterUri(
//    scheme = RouterConstant.SCHEME,
//    host = RouterConstant.HOST,
//    path = [SettingConstant.Path.SET_THEME]
//)
//class AppThemeActivity : VMBindingActivity<AppThemeVM, SettingActivityThemeBinding>() {
//    override fun moduleTag(): String {
//        return ModuleConstant.SETTING
//    }
//
//    override fun initViewAndEvent(savedInstanceState: Bundle?) {
//        val currentThemeConfig = ThemeManager.getCurrentThemeConfig()
//        when (currentThemeConfig) {
//            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
//                viewBinding.rbFollowSystem.isChecked = true
//            }
//            AppCompatDelegate.MODE_NIGHT_YES -> {
//                viewBinding.rbDark.isChecked = true
//            }
//            AppCompatDelegate.MODE_NIGHT_NO -> {
//                viewBinding.rbLight.isChecked = true
//            }
//            else -> {}
//        }
//    }
//
//    override fun initListeners() {
//        with(viewBinding) {
//            rbFollowSystem.setOnClickListener {
//                ThemeManager.followSystem() {
//
//                }
//            }
//            rbLight.setOnClickListener {
//                ThemeManager.applyLightTheme {
//                }
//            }
//            rbDark.setOnClickListener {
//                ThemeManager.applyDarkTheme {
//                }
//            }
//        }
//    }
//
//
//}
//
//
