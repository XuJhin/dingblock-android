package cool.dingstock.appbase.adapter.multiadapter

import androidx.recyclerview.widget.RecyclerView

interface ICoustomAdapter {
    fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder, postion: Int)
}