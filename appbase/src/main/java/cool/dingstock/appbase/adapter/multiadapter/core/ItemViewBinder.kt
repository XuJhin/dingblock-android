package cool.dingstock.appbase.adapter.multiadapter.core

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class ItemViewBinder<T, VH : RecyclerView.ViewHolder> : ItemViewDelegate<T, VH>() {

    final override fun onCreateViewHolder(context: Context, parent: ViewGroup): VH {
        return onCreateViewHolder(LayoutInflater.from(context), parent)
    }

    abstract fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): VH

}
