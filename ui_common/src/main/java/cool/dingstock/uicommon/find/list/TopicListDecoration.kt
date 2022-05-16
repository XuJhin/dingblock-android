package cool.dingstock.uicommon.find.list

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cool.dingstock.lib_base.util.SizeUtils

class TopicListDecoration : RecyclerView.ItemDecoration() {
    val margin = SizeUtils.dp2px(6f)
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.layoutManager == null) {
            return
        }
        val childPosition = parent.getChildLayoutPosition(view)
        if (childPosition == state.itemCount - 1) {
            outRect.set(0, 0, 0, margin * 2)
        } else {
            outRect.set(0, 0, 0, margin)
        }
    }
}