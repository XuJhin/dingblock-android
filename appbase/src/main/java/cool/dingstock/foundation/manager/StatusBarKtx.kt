package cool.dingstock.foundation.manager

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager


/**
 * Sets status bar dark font.
 * 设置状态栏字体颜色，android6.0以上
 */
public fun Activity.setStatusBarDarkFont(
    isDarkFont: Boolean,
    uiFlags: Int = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
): Int {
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    if (isDarkFont) {
        uiFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    } else {
        uiFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }
    window.decorView.systemUiVisibility = uiFlags
    return uiFlags
}

/**
 * 设置导航栏图标亮色与暗色
 * Sets dark navigation icon.
 */
private fun setNavigationIconDark(
    isDarkFont: Boolean,
    uiFlags: Int = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (isDarkFont) {
            uiFlags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            uiFlags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
    } else {
        uiFlags
    }
}


/**
 * 适配刘海屏
 * Fits notch screen.
 */
private fun Activity.fitsNotchScreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        try {
            val lp: WindowManager.LayoutParams = window.attributes
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        } catch (e: Exception) {
        }
    }
}

fun Activity.translate() {
    //防止系统栏隐藏时内容区域大小发生变化
    var uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    uiFlags = setStatusBarDarkFont(true, uiFlags)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        uiFlags = uiFlags or (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.INVISIBLE)
        window.decorView.setSystemUiVisibility(uiFlags)
    }
}

fun Activity.translateStatusBar() {
    fitsNotchScreen()
    var uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    uiFlags = uiFlags or (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.INVISIBLE)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        window.decorView.setSystemUiVisibility(uiFlags)
    }
}

fun Activity.translateNavBar() {
    var uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    uiFlags =
        uiFlags or (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        window.decorView.setSystemUiVisibility(uiFlags)
    }
}

fun Activity.showBar() {
    var uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    uiFlags =
        uiFlags or View.SYSTEM_UI_FLAG_VISIBLE
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        window.decorView.setSystemUiVisibility(uiFlags)
    }
}


enum class BarFlag {
    /**
     * 隐藏状态栏
     * Flag hide status bar bar hide.
     */
    FLAG_HIDE_STATUS_BAR,

    /**
     * 隐藏导航栏
     * Flag hide navigation bar bar hide.
     */
    FLAG_HIDE_NAVIGATION_BAR,

    /**
     * 隐藏状态栏和导航栏
     * Flag hide bar bar hide.
     */
    FLAG_HIDE_BAR,

    /**
     * 显示状态栏和导航栏
     * Flag show bar bar hide.
     */
    FLAG_SHOW_BAR,
}