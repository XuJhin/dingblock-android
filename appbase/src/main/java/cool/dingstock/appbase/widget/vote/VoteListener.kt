package cool.dingstock.appbase.widget.vote

import android.view.View

interface VoteListener {
    fun onItemClick(view: View, index: Int, status: Boolean)
    fun initAnimation()
}