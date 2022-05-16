package cool.dingstock.lib_base.util

import android.graphics.Typeface
import cool.dingstock.lib_base.BaseLibrary

object TypefaceUtil {
    val popingBoldTf by lazy {
        Typeface.createFromAsset(BaseLibrary.getInstance().context.assets, "font/PoppinsBold.ttf")
    }
    val popingSemiBoldTf by lazy {
        Typeface.createFromAsset(
            BaseLibrary.getInstance().context.assets,
            "font/PoppinsSemiBold.ttf"
        )
    }
}