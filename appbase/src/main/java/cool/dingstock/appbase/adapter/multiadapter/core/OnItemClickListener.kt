package cool.dingstock.appbase.adapter.multiadapter.core

import android.view.View
import androidx.recyclerview.widget.RecyclerView

interface OnItemClickListener<T> {
    fun onItemClick(view: View?, holder: RecyclerView.ViewHolder?, entity: T, position: Int)
}
interface OnItemLongClickListener<T>{
    fun onItemLongClick(view: View?, holder: RecyclerView.ViewHolder?, entity: T, position: Int): Boolean
}