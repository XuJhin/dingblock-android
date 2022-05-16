package cool.dingstock.appbase.uikit.widget.magicindicator.buildins.titles

import android.content.Context
import android.view.View
import cool.dingstock.appbase.uikit.widget.magicindicator.buildins.abs.IPagerTitleView

class DummyPagerTitleView(context: Context?) : View(context), IPagerTitleView {
    override fun onSelected(index: Int, totalCount: Int) {}
    override fun onDeselected(index: Int, totalCount: Int) {}
    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {}
    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {}
}
