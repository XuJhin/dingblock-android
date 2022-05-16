package cool.dingstock.appbase.customerview.shade.guide

/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/24  11:47
 *
 *  透明孔加 指示器
 *
 *  @param targetStart 被暴露的目标View 透明孔 的start
 *  @param targetTop 被暴露的目标View 透明孔 的top
 *  @param targetEnd 被暴露的目标View 透明孔 的end
 *  @param targetBottom 被暴露的目标View 透明孔 的Bottom
 *  @param targetRadius 被暴露的目标View 透明孔 的圆角
 *  @param indicators 被暴露的目标View 透明孔 的指示器，可以有多个指示器
 *
 */
data class GuideHoleIndicator(var targetStart: Float = 0f
                              , var targetTop: Float = 0f
                              , var targetEnd: Float = 0f
                              , var targetBottom: Float = 0f
                              , var targetRadius: Float = 0f
                              , var indicators: ArrayList<GuideIndicator> = arrayListOf()){

    constructor(targetStart: Float = 0f
                ,targetTop: Float = 0f
                ,  targetEnd: Float = 0f
                ,  targetBottom: Float = 0f
                ,  targetRadius: Float = 0f
                ,  indicator: GuideIndicator):this(targetStart,targetTop,targetEnd,targetBottom,targetRadius, arrayListOf(indicator))
}
