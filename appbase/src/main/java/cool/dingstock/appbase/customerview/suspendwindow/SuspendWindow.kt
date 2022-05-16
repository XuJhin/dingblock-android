package cool.dingstock.appbase.customerview.suspendwindow

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import cool.dingstock.appbase.R

/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/2  14:59
 */
class SuspendWindow(context: Context,attributeSet: AttributeSet) : FrameLayout(context,attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.suspend_window_layout, this, true)
    }
}