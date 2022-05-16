package cool.dingstock.mobile

import androidx.annotation.ColorInt

class ColorUtil {
    /**
     * 计算状态栏颜色
     *
     * @param color color值
     * @param alpha alpha值 0-255
     * @return 最终的状态栏颜色
     */
    private fun alphaColor(@ColorInt color: Int, alpha: Int): Int {
        val a = 1 - alpha / 255f
        var red = color shr 16 and 0xff
        var green = color shr 8 and 0xff
        var blue = color and 0xff
        red = (red * a + 0.5).toInt()
        green = (green * a + 0.5).toInt()
        blue = (blue * a + 0.5).toInt()
        return 0xff shl 24 or (red shl 16) or (green shl 8) or blue
    }
}