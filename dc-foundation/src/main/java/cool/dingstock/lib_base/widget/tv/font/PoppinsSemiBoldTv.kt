package cool.dingstock.lib_base.widget.tv.font

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import cool.dingstock.lib_base.util.TypefaceUtil

class PoppinsSemiBoldTv(context: Context, attr: AttributeSet) : AppCompatTextView(context, attr) {

    init {
        typeface = TypefaceUtil.popingSemiBoldTf
    }
}