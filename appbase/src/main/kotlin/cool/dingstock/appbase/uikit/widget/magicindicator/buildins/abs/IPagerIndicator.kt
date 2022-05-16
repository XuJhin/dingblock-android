package cool.dingstock.appbase.uikit.widget.magicindicator.buildins.abs

import cool.dingstock.appbase.uikit.widget.magicindicator.buildins.model.PositionData

interface IPagerIndicator {
    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)
    fun onPageSelected(position: Int)
    fun onPageScrollStateChanged(state: Int)
    fun onPositionDataProvide(dataList: List<PositionData>)
}