package cool.dingstock.appbase.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cool.dingstock.appbase.ext.dp

class CommonDecoration(
    private val padding: Int = 10.dp.toInt(),
    private val firstPadding: Int = 0,
    private val needBottom: Boolean = true
): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        parent.adapter?.itemCount?.let {
            val position = parent.getChildAdapterPosition(view)
            if (position == 0 && firstPadding != 0) {
                outRect.top = firstPadding
            } else {
                outRect.top = padding
            }
            if (position == it - 1 && needBottom) {
                outRect.bottom = padding
            } else {
                outRect.bottom = 0
            }
        }
    }
}