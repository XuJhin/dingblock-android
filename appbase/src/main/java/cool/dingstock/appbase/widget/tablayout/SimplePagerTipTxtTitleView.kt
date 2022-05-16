package cool.dingstock.appbase.widget.tablayout

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import cool.dingstock.appbase.R
import cool.dingstock.appbase.ext.azDp
import cool.dingstock.appbase.ext.hide
import net.lucode.hackware.magicindicator.buildins.ArgbEvaluatorHolder
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IMeasurablePagerTitleView

/**
 * @author WhenYoung
 *  CreateAt Time 2020/11/27  10:15
 */
class SimplePagerTipTxtTitleView(context: Context) : FrameLayout(context),
        IMeasurablePagerTitleView {

    private var startTitleSize = 0f
    private var endTitleSize = 0f
    var selectedColor = 0
    var normalColor = 0

    val tv: TextView
    private val tipTV: TextView
    private val tipPointView: View

    init {
        LayoutInflater.from(context).inflate(R.layout.simple_pager_tip_txt_title_layout, this, true)
        tv = findViewById(R.id.tv)
        tipTV = findViewById(R.id.tip_txt)
        tipPointView = findViewById(R.id.view_hint_point)
        val padding = UIUtil.dip2px(context, 10.0)
        setPadding(padding, 0, padding, 0)
        tv.ellipsize = TextUtils.TruncateAt.END
    }


    override fun getContentLeft(): Int {
        val bound = Rect()
        var longestString = ""
        if (tv.text.toString().contains("\n")) {
            val brokenStrings: Array<String> = tv.text.toString().split("\\n").toTypedArray()
            for (each in brokenStrings) {
                if (each.length > longestString.length) longestString = each
            }
        } else {
            longestString = tv.text.toString()
        }
        tv.paint.getTextBounds(longestString, 0, longestString.length, bound)
        val contentWidth = bound.width() + 26 * 2.azDp.toInt()
        return left + width / 2 - contentWidth / 2
    }

    override fun getContentTop(): Int {
        val metrics: Paint.FontMetrics = tv.getPaint().getFontMetrics()
        val contentHeight = metrics.bottom - metrics.top + 14.azDp
        return (height / 2 - contentHeight / 2).toInt()
    }

    override fun getContentRight(): Int {
        val bound = Rect()
        var longestString = ""
        if (tv.text.toString().contains("\n")) {
            val brokenStrings: Array<String> = tv.text.toString().split("\\n").toTypedArray()
            for (each in brokenStrings) {
                if (each.length > longestString.length) longestString = each
            }
        } else {
            longestString = tv.text.toString()
        }
        tv.paint.getTextBounds(longestString, 0, longestString.length, bound)
        val contentWidth = bound.width() + 26 * 2.azDp.toInt()
        return left + width / 2 + contentWidth / 2
    }

    override fun getContentBottom(): Int {
        val metrics: Paint.FontMetrics = tv.paint.fontMetrics
        val contentHeight = metrics.bottom - metrics.top + +14.azDp
        return (height / 2 + contentHeight / 2).toInt()
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        val color = ArgbEvaluatorHolder.eval(leavePercent, selectedColor, normalColor)
        tv.setTextColor(color)
        val scaleX =
                endTitleSize / startTitleSize - leavePercent * (endTitleSize / startTitleSize - 1)
        val scaleY =
                endTitleSize / startTitleSize - leavePercent * (endTitleSize / startTitleSize - 1)
        setScaleX(scaleX)
        setScaleY(scaleY)
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        val color = ArgbEvaluatorHolder.eval(enterPercent, normalColor, selectedColor)
        tv.setTextColor(color)
        val scaleX = enterPercent * (endTitleSize / startTitleSize - 1) + 1
        val scaleY = enterPercent * (endTitleSize / startTitleSize - 1) + 1
        setScaleX(scaleX)
        setScaleY(scaleY)
    }


    override fun onSelected(index: Int, totalCount: Int) {
        tv.setTypeface(Typeface.DEFAULT_BOLD)
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        tv.setTypeface(Typeface.DEFAULT)
    }

    fun setStartAndEndTitleSize(startTitleSize: Float, endTitleSize: Float) {
        this.startTitleSize = startTitleSize
        this.endTitleSize = endTitleSize
    }

    fun setText(text: String) {
        tv.text = text
    }


    fun setTextSize(startTitleSize: Float) {
        tv.textSize = startTitleSize
    }

    fun setTipBgType(bgType: String) {
        when (bgType) {
            "RED" -> {
                tipTV.background = context.getDrawable(R.drawable.common_red_c_bg)
                tipPointView.background = context.getDrawable(R.drawable.common_red_c_bg)
            }
            "BLUE" -> {
                tipTV.background = context.getDrawable(R.drawable.common_blue_c_bg)
                tipPointView.background = context.getDrawable(R.drawable.common_blue_c_bg)
            }
            else -> {
            }
        }
    }

    fun setTipText(text: String) {
        tipTV.text = text
        tipTV.hide(TextUtils.isEmpty(text))
    }

    fun isShowHintPoint(isShow: Boolean) {
        tipPointView.hide(!isShow)
    }
}