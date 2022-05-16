package cool.dingstock.appbase.customerview.shade.guide

import android.graphics.Color
import cool.dingstock.appbase.R
import cool.dingstock.appbase.ext.dp
import cool.dingstock.lib_base.util.SizeUtils


/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/24  11:47
 *
 *  指示器
 *
 *  @param indicatorMargeY 指示器距离 目标View   的margeY 可以为 负数 ，负数是在上面 相对于top，正数是在下面相对于bottom
 *  @param indicatorMargeStart 指示器距离 目标View  的margeStart
 *
 */
data class GuideIndicator(var indicatorMargeY: Float = 0f
                          , var indicatorMargeStart: Float = 0f
                          , var indicatorTvWidth: Float = 0f
                          , var indicatorTvHeight: Float = 0f
                          , var indicatorText: String = ""
                          , var indicatorTextColor: Int = Color.WHITE
                          , var indicatorTextSize: Float = 15f
                          , var indicatorBg: Int = R.drawable.guide_red_bg_radius_6
                          , var indicatorTriangle: Int = R.drawable.guide_indicator_triangle_top
                          , var indicatorTriangleWidth: Float = 16.dp
                          , var indicatorTriangleHeight: Float = 7.dp
                          , var indicatorTriangleMarenStart: Float = 25.dp
)