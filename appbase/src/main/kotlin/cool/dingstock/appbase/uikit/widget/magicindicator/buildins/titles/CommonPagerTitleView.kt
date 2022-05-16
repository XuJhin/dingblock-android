package cool.dingstock.appbase.uikit.widget.magicindicator.buildins.titles

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import cool.dingstock.appbase.uikit.widget.magicindicator.buildins.abs.IMeasurablePagerTitleView

class CommonPagerTitleView(context: Context) : FrameLayout(context),
    IMeasurablePagerTitleView {
    var onPagerTitleChangeListener: OnPagerTitleChangeListener? = null
    var contentPositionDataProvider: ContentPositionDataProvider? = null

    override fun onSelected(index: Int, totalCount: Int) {
        if (onPagerTitleChangeListener != null) {
            onPagerTitleChangeListener!!.onSelected(index, totalCount)
        }
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        if (onPagerTitleChangeListener != null) {
            onPagerTitleChangeListener!!.onDeselected(index, totalCount)
        }
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        if (onPagerTitleChangeListener != null) {
            onPagerTitleChangeListener!!.onLeave(index, totalCount, leavePercent, leftToRight)
        }
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        if (onPagerTitleChangeListener != null) {
            onPagerTitleChangeListener!!.onEnter(index, totalCount, enterPercent, leftToRight)
        }
    }

    override val contentLeft: Int
        get() = if (contentPositionDataProvider != null) {
            contentPositionDataProvider!!.contentLeft
        } else getLeft()
    override val contentTop: Int
        get() {
            return if (contentPositionDataProvider != null) {
                contentPositionDataProvider!!.contentTop
            } else getTop()
        }
    override val contentRight: Int
        get() {
            return if (contentPositionDataProvider != null) {
                contentPositionDataProvider!!.contentRight
            } else getRight()
        }
    override val contentBottom: Int
        get() {
            return if (contentPositionDataProvider != null) {
                contentPositionDataProvider!!.contentBottom
            } else getBottom()
        }

    /**
     * 外部直接将布局设置进来
     *
     * @param contentView
     */
    fun setContentView(contentView: View?) {
        setContentView(contentView, null)
    }

    fun setContentView(contentView: View?, lp: FrameLayout.LayoutParams?) {
        var lp: FrameLayout.LayoutParams? = lp
        removeAllViews()
        if (contentView != null) {
            if (lp == null) {
                lp = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            addView(contentView, lp)
        }
    }

    fun setContentView(layoutId: Int) {
        val child: View = LayoutInflater.from(getContext()).inflate(layoutId, null)
        setContentView(child, null)
    }

    interface OnPagerTitleChangeListener {
        fun onSelected(index: Int, totalCount: Int)
        fun onDeselected(index: Int, totalCount: Int)
        fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean)
        fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean)
    }

    interface ContentPositionDataProvider {
        val contentLeft: Int
        val contentTop: Int
        val contentRight: Int
        val contentBottom: Int
    }
}