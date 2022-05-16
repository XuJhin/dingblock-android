package cool.dingstock.home.utils

import cool.dingstock.appbase.ext.azDp
import cool.dingstock.home.ui.dingchao.HomeTopComponentView
import cool.dingstock.lib_base.util.SizeUtils


/**
 * 类名：CardViewHelper
 * 包名：cool.dingstock.home.utils
 * 创建时间：2021/8/5 4:48 下午
 * 创建人： WhenYoung
 * 描述：
 **/
object CardViewHelper {

    var cardWidth = 0f
    var cardHeight = 0f
    var rvWidth = 0f
    var rvHeight = 0f

    fun initSize() {
        val screenWidth = SizeUtils.getWidth() - 30.azDp
        val totalWidth = (375 - 30).azDp
        val scale = screenWidth / totalWidth
        cardWidth = (228).azDp * scale
        val btnWidth = 105.azDp * scale
        cardHeight = btnWidth

        rvWidth = (228 + 8).azDp * scale
        rvHeight = (110).azDp * scale
    }

    fun getCardSize(): IntArray {
        val screenWidth = SizeUtils.getWidth() - 30.azDp
        val totalWidth = (375 - 30).azDp
        val scale = screenWidth / totalWidth
        val vpWidth = 228.azDp * scale
        val btnWidth = 105.azDp * scale
        val vpHeight = btnWidth
        return intArrayOf(vpWidth.toInt() - 100, vpHeight.toInt() - 100)
    }

}