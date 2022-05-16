package cool.dingstock.home.ui.fashion.brand

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cool.dingstock.lib_base.util.SizeUtils

class CustomDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val totalCount = parent.childCount
        val childPosition = parent.getChildAdapterPosition(view)
        if (childPosition == 0) {
            outRect.set(0, SizeUtils.dp2px(10f), 0, 0)
        }
    }
}