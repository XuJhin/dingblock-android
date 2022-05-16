package cool.dingstock.appbase.uikit.widget.magicindicator.buildins.titles

import android.content.Context
import net.lucode.hackware.magicindicator.buildins.ArgbEvaluatorHolder


class ColorTransitionPagerTitleView(context: Context) : SimplePagerTitleView(context) {
    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        val color = ArgbEvaluatorHolder.eval(leavePercent, selectedColor, normalColor)
        setTextColor(color)
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        val color = ArgbEvaluatorHolder.eval(enterPercent, normalColor, selectedColor)
        setTextColor(color)
    }

    override fun onSelected(index: Int, totalCount: Int) {}
    override fun onDeselected(index: Int, totalCount: Int) {}
}
