package cool.dingstock.appbase.uikit.widget.magicindicator.buildins

import android.content.Context

class UIUtil {
    companion object {
        fun dip2px(context: Context, dpValue: Float): Int {
            val density: Float = context.resources.displayMetrics.density
            return (dpValue * density + 0.5).toInt()
        }

        fun getScreenWidth(context: Context): Int {
            return context.resources.displayMetrics.widthPixels
        }
    }

}