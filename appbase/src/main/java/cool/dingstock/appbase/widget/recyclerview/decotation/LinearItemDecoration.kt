package cool.dingstock.appbase.widget.recyclerview.decotation

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlin.math.roundToInt

class LinearItemDecoration(var divider: Drawable, var skipPosition: Int = 0) :
    androidx.recyclerview.widget.RecyclerView.ItemDecoration() {

    var drawStart = false
    var drawEnd = true
    private val bounds = Rect()

    override fun onDraw(
        canvas: Canvas,
        parent: androidx.recyclerview.widget.RecyclerView,
        state: androidx.recyclerview.widget.RecyclerView.State
    ) {
        if (parent.layoutManager == null) {
            return
        }
        if (getOrientation(parent.layoutManager!!) == androidx.recyclerview.widget.LinearLayoutManager.VERTICAL) {
            drawVertical(canvas, parent)
        } else {
            drawHorizontal(canvas, parent)
        }
    }

    private fun drawVertical(canvas: Canvas, parent: androidx.recyclerview.widget.RecyclerView) {
        canvas.save()
        val paddingRect = Rect()
        divider.getPadding(paddingRect)
        val left: Int
        val right: Int

        if (parent.clipToPadding) {
            left = parent.paddingLeft + paddingRect.left
            right = parent.width - parent.paddingRight - paddingRect.right
            canvas.clipRect(
                left, parent.paddingTop, right,
                parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }
        val childCount = parent.childCount
        for (i in skipPosition until childCount) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, bounds)
            if (drawStart && i == 0) {
                val startTop = bounds.top + child.translationY.roundToInt()
                val startBottom = startTop + divider.intrinsicHeight
                divider.setBounds(left, startTop, right, startBottom)
                divider.draw(canvas)
            }
            if (!drawEnd && i == childCount - 1) {
                break
            }
            val bottom = bounds.bottom + child.translationY.roundToInt()
            val top = bottom - divider.intrinsicHeight
            divider.setBounds(left, top, right, bottom)
            divider.draw(canvas)
        }
        canvas.restore()
    }

    private fun drawHorizontal(canvas: Canvas, parent: androidx.recyclerview.widget.RecyclerView) {
        canvas.save()
        val top: Int
        val bottom: Int
        if (parent.clipToPadding) {
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
            canvas.clipRect(
                parent.paddingLeft, top,
                parent.width - parent.paddingRight, bottom
            )
        } else {
            top = 0
            bottom = parent.height
        }
        val childCount = parent.childCount
        for (i in skipPosition until childCount) {
            val child = parent.getChildAt(i)
            parent.layoutManager?.getDecoratedBoundsWithMargins(child, bounds)
            if (drawStart && i == 0) {
                val startLeft = bounds.left + child.translationX.roundToInt()
                val startRight = startLeft + divider.intrinsicWidth
                divider.setBounds(startLeft, top, startRight, bottom)
                divider.draw(canvas)
            }
            if (!drawEnd && i == childCount - 1) {
                break
            }
            val right = bounds.right + child.translationX.roundToInt()
            val left = right - divider.intrinsicWidth
            divider.setBounds(left, top, right, bottom)
            divider.draw(canvas)
        }
        canvas.restore()
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: androidx.recyclerview.widget.RecyclerView,
        state: androidx.recyclerview.widget.RecyclerView.State
    ) {
        if (parent.layoutManager == null) {
            return
        }
        var startScale = 0
        var endScale = 1
        if (parent.getChildAdapterPosition(view) == 0) {//first item
            if (drawStart) startScale = 1
        }
        if (parent.getChildAdapterPosition(view) == state.itemCount - 1) {//last item
            endScale = if (drawEnd) 1 else 0
        }
        if (getOrientation(parent.layoutManager!!) == androidx.recyclerview.widget.LinearLayoutManager.VERTICAL) {
            outRect.set(
                0,
                divider.intrinsicHeight * startScale,
                0,
                divider.intrinsicHeight * endScale
            )
        } else {
            outRect.set(
                divider.intrinsicWidth * startScale,
                0,
                divider.intrinsicWidth * endScale,
                0
            )
        }
    }

    private fun getOrientation(layoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager): Int {
        check(layoutManager is LinearLayoutManager) { "LayoutManager must be LineLayoutManger" }
        return layoutManager.orientation
    }
}
