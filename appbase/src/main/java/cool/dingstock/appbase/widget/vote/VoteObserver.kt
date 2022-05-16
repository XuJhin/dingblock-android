package cool.dingstock.appbase.widget.vote

import android.view.View

interface VoteObserver {
    fun update(view: View, status: Boolean)
    fun setTotalNumber(totalNumber: Int)
}