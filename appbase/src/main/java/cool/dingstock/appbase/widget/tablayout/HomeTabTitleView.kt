package cool.dingstock.appbase.widget.tablayout

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.load
import cool.dingstock.lib_base.util.SizeUtils
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IMeasurablePagerTitleView
import kotlin.math.round

class HomeTabTitleView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr), IMeasurablePagerTitleView {
    var maxScale: Float = 0f
    private var currentScale: Float = 0f

    var iconUrl: String? = null
    private var padding: Int = 0
    val tabTitleView = TabTitleView(context).apply {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 44.dp.toInt()).apply {
            gravity = Gravity.BOTTOM
        }
    }

    fun setupIcon(iconUrl: String?) {
        this.iconUrl = iconUrl
        tabTitleView.post {
            this.iconUrl?.let {
                addView(iconView)
                iconView.load(iconUrl)
                iconView.requestLayout()
            }
        }
    }

    private val iconView = ImageView(context).apply {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 16.dp.toInt()).apply {
            gravity = Gravity.BOTTOM
            minimumWidth = 47.dp.toInt()
        }
    }

    init {
        addView(tabTitleView)

    }

    private fun setIconBottomMargin(scale: Float) {
        if (currentScale == scale) return
        currentScale = scale
        val rect = Rect()
        tabTitleView.paint.getTextBounds(
            tabTitleView.text.toString(),
            0,
            tabTitleView.text.length,
            rect
        )
        val marginBottom =
            (measuredHeight - rect.height()) / 2 + (rect.height() / (maxScale - scale + 1) + 2.dp).toInt()
        iconView.layoutParams = (iconView.layoutParams as LayoutParams).apply {
            bottomMargin = marginBottom
        }
    }

    fun setPadding(padding: Int) {
        this.padding = padding
        tabTitleView.setPadding(
            if (padding != -1) padding else SizeUtils.dp2px(20f), 0,
            if (padding != -1) padding else SizeUtils.dp2px(20f), 0
        )
        iconView.setPadding(if (padding != -1) padding else SizeUtils.dp2px(20f), 0, 0, 0)
    }

    override fun onSelected(index: Int, totalCount: Int) {
        tabTitleView.onSelected(index, totalCount)
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        tabTitleView.onDeselected(index, totalCount)
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        tabTitleView.onLeave(index, totalCount, leavePercent, leftToRight)
        iconView.setPadding(
            padding - round(
                (tabTitleView.scaleX - 1) *
                        (tabTitleView.measuredWidth - tabTitleView.paddingLeft - tabTitleView.paddingRight) / 2
            ).toInt(), 0, 0, 0
        )
        setIconBottomMargin(tabTitleView.scaleX)
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        tabTitleView.onEnter(index, totalCount, enterPercent, leftToRight)
        iconView.setPadding(
            padding - round(
                (tabTitleView.scaleX - 1) *
                        (tabTitleView.measuredWidth - tabTitleView.paddingLeft - tabTitleView.paddingRight) / 2
            ).toInt(), 0, 0, 0
        )
        setIconBottomMargin(tabTitleView.scaleX)
    }

    override fun getContentLeft(): Int {
        return tabTitleView.contentLeft
    }

    override fun getContentTop(): Int {
        return top
    }

    override fun getContentRight(): Int {
        return tabTitleView.contentRight
    }

    override fun getContentBottom(): Int {
        return bottom
    }

}