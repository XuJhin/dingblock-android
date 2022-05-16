package cool.dingstock.appbase.uikit.widget.magicindicator.buildins.titles

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextUtils
import android.view.Gravity
import cool.dingstock.appbase.uikit.widget.magicindicator.buildins.UIUtil
import cool.dingstock.appbase.uikit.widget.magicindicator.buildins.abs.IMeasurablePagerTitleView


open class SimplePagerTitleView(context: Context) :
    androidx.appcompat.widget.AppCompatTextView(context, null),
    IMeasurablePagerTitleView {
    open var selectedColor = 0
    open var normalColor = 0

    private fun init(context: Context) {
        gravity = Gravity.CENTER
        val padding: Int = UIUtil.dip2px(context, 10f)
        setPadding(padding, 0, padding, 0)
        setSingleLine()
        ellipsize = TextUtils.TruncateAt.END
    }

    override fun onSelected(index: Int, totalCount: Int) {
        setTextColor(selectedColor)
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        setTextColor(normalColor)
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {}
    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {}
    override val contentLeft: Int
        get() {
            val bound = Rect()
            var longestString = ""
            if (getText().toString().contains("\n")) {
                val brokenStrings: List<String> = getText().toString().split("\\n")
                for (each in brokenStrings) {
                    if (each.length > longestString.length) longestString = each
                }
            } else {
                longestString = getText().toString()
            }
            getPaint().getTextBounds(longestString, 0, longestString.length, bound)
            val contentWidth: Int = bound.width()
            return (getLeft() + getWidth() / 2f - contentWidth / 2f).toInt()
        }
    override val contentTop: Int
        get() {
            val metrics: Paint.FontMetrics = getPaint().getFontMetrics()
            val contentHeight: Float = metrics.bottom - metrics.top
            return (getHeight() / 2f - contentHeight / 2f).toInt()
        }
    override val contentRight: Int
        get() {
            val bound = Rect()
            var longestString = ""
            if (text.toString().contains("\n")) {
                val brokenStrings: List<String> = text.toString().split("\\n")
                for (each in brokenStrings) {
                    if (each.length > longestString.length) longestString = each
                }
            } else {
                longestString = text.toString()
            }
            paint.getTextBounds(longestString, 0, longestString.length, bound)
            val contentWidth: Int = bound.width()
            return (left + width / 2.0f + contentWidth / 2f).toInt()
        }
    override val contentBottom: Int
        get() {
            val metrics: Paint.FontMetrics = getPaint().getFontMetrics()
            val contentHeight: Float = metrics.bottom - metrics.top
            return (getHeight() / 2f + contentHeight / 2f).toInt()
        }

    init {
        init(context)
    }
}