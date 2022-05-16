package cool.dingstock.appbase.adapter.multiadapter.wrapper

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

object WrapperUtils {
    fun onAttachedToRecyclerView(
            innerAdapter: RecyclerView.Adapter<*>,
            recyclerView: RecyclerView,
            callback: SpanSizeCallback
    ) {
        innerAdapter.onAttachedToRecyclerView(recyclerView)
        val layoutManager =
                recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            val spanSizeLookup =
                    layoutManager.spanSizeLookup
            layoutManager.spanSizeLookup = object :
                    GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return callback.getSpanSize(layoutManager, spanSizeLookup, position)
                }
            }
            layoutManager.spanCount = layoutManager.spanCount
        }
    }

    fun setFullSpan(holder: RecyclerView.ViewHolder) {
        val lp = holder.itemView.layoutParams
        if (lp != null
                && lp is StaggeredGridLayoutManager.LayoutParams
        ) {
            lp.isFullSpan = true
        }
    }

    interface SpanSizeCallback {
        fun getSpanSize(
                layoutManager: GridLayoutManager?,
                oldLookup: GridLayoutManager.SpanSizeLookup?,
                position: Int
        ): Int
    }
}