package cool.dingstock.appbase.helper

import androidx.appcompat.app.AppCompatDelegate
import cool.dingstock.lib_base.stroage.ConfigSPHelper

/**
 * @author wangjiang
 *  CreateAt Time 2021/7/20  11:45
 */
object DarkModeHelper {
    private const val darkModeKey = "dark_mode_key"

    fun setDarkMode(darkMode: DarkMode) {
        setModeNow(darkMode)
        ConfigSPHelper.getInstance().save(darkModeKey, darkMode.name)
    }

    fun setModeNow(darkMode: DarkMode) {
        when (darkMode) {
            DarkMode.DARK_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            DarkMode.WHITE_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            DarkMode.FOLLOW_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    fun getDarkMode(): DarkMode {
        val defaultMode = when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_NO -> DarkMode.WHITE_MODE.name
            AppCompatDelegate.MODE_NIGHT_YES -> DarkMode.DARK_MODE.name
            else -> DarkMode.FOLLOW_SYSTEM.name
        }
        val modeStr = ConfigSPHelper.getInstance().getString(darkModeKey, defaultMode)
        return DarkMode.valueOf(modeStr)
    }
}

enum class DarkMode {
    DARK_MODE,
    WHITE_MODE,
    FOLLOW_SYSTEM
}