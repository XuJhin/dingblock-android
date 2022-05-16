package cool.dingstock.foundation.manager

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import cool.dingstock.lib_base.stroage.ConfigSPHelper


/**
 * 颜色主题
 */
object ThemeManager {
    private const val SP_K_THEME = "sp_k_theme"

    fun initTheme() {
        AppCompatDelegate.setDefaultNightMode(getCurrentThemeConfig())
    }

    fun isDarkMode(context: Context): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }
    }

    fun getCurrentThemeConfig(): Int {
        return ConfigSPHelper.getInstance()?.getInt(
            SP_K_THEME,
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        ) ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    private fun saveCurrentTheme(mode: Int) {
        ConfigSPHelper.getInstance()?.save(SP_K_THEME, mode)
    }

    fun applyDarkTheme(then: () -> Unit) {
        setNewTheme(AppCompatDelegate.MODE_NIGHT_YES)
        then()
    }

    fun applyLightTheme(then: () -> Unit) {
        setNewTheme(AppCompatDelegate.MODE_NIGHT_NO)
        then()
    }

    fun followSystem(then: () -> Unit) {
        setNewTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        then()
    }

    private fun setNewTheme(mode: Int) {
        if (AppCompatDelegate.getDefaultNightMode() == mode) return
        AppCompatDelegate.setDefaultNightMode(mode)
        saveCurrentTheme(mode)
    }
}