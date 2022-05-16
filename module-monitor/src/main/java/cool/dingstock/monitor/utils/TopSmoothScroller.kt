package cool.dingstock.monitor.utils

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller

class TopSmoothScroller(context: Context?) : LinearSmoothScroller(context) {
    override fun getHorizontalSnapPreference(): Int {
        return SNAP_TO_START //具体见源码注释
    }

    override fun getVerticalSnapPreference(): Int {
        return SNAP_TO_START //具体见源码注释
    }
}