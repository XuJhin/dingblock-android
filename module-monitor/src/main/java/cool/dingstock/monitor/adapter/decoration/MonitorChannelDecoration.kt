package cool.dingstock.monitor.adapter.decoration

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import cool.dingstock.lib_base.util.SizeUtils

class MonitorChannelDecoration : ItemDecoration() {

    private val spacing = SizeUtils.dp2px(8f)
    private val columnSpacing = SizeUtils.dp2px(5f)
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        var noDrawCount = 0
        val layoutManager = parent.layoutManager as GridLayoutManager
        val position = parent.getChildAdapterPosition(view)
        when (layoutManager.spanSizeLookup.getSpanSize(position)) {
            3 -> {
                noDrawCount += 1
                outRect.set(0, 0, 0, 0)
                Log.e("decoration", "noDrawCount" + noDrawCount)
            }
            1 -> {
                outRect.set(columnSpacing, spacing, columnSpacing, spacing)
            }
        }
    }

}