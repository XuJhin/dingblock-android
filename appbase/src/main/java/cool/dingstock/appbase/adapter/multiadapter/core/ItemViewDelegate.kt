package cool.dingstock.appbase.adapter.multiadapter.core

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class ItemViewDelegate<T, VH : RecyclerView.ViewHolder> {
    internal var _adapter: MultiTypeAdapter? = null
    val adapter: MultiTypeAdapter
        get() {
            if (_adapter == null) {
                throw  IllegalStateException(
                        "This $this has not been attached to MultiTypeAdapter yet. " +
                                "You should not call the method before registering the delegate."
                )
            }
            return _adapter!!
        }

    var adapterItems: List<Any>
        get() = adapter.items
        set(value) {
            adapter.items = value
        }

    abstract fun onCreateViewHolder(context: Context, parent: ViewGroup): VH

    abstract fun onBindViewHolder(holder: VH, item: T)

    open fun onBindViewHolder(holder: VH, item: T, payloads: List<Any>) {
        onBindViewHolder(holder, item)
    }

    fun getPosition(holder: RecyclerView.ViewHolder): Int {
        return holder.adapterPosition
    }

    open fun getItemId(item: T): Long = RecyclerView.NO_ID

    open fun onViewRecycled(holder: VH) {}

    open fun onFailedToRecycleView(holder: VH): Boolean {
        return false
    }

    open fun onViewAttachedToWindow(holder: VH) {}
    open fun onViewDetachedFromWindow(holder: VH) {}
}