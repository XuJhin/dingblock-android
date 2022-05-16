package cool.dingstock.appbase.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import cool.dingstock.appbase.R
import cool.dingstock.appbase.ext.hide

class DynamicEmptyView(context: Context) : FrameLayout(context) {

    val mRootView: View

    init {
        mRootView = LayoutInflater.from(context).inflate(R.layout.mine_fragment_empty, this, true)
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        mRootView.layoutParams = params
    }

    constructor(context: Context, noBg: Boolean, fullParent: Boolean) : this(context) {
        if (noBg) {
            mRootView.findViewById<View>(R.id.m_root_view).background = null
        }
        if (fullParent) {
            val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            mRootView.layoutParams = params
        }
    }

    constructor(context: Context, noBg: Boolean, fullParent: Boolean, isShow: Boolean) : this(context, noBg, fullParent) {
        val mEmptyView = findViewById<View>(R.id.view_space)
        mEmptyView.hide(!isShow)
    }
}