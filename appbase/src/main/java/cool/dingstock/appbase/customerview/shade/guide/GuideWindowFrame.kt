package cool.dingstock.appbase.customerview.shade.guide

/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/24  11:47
 *
 *  引导页的一屏
 *
 * @param guideHoleIndicators 这一屏的指示器
 *
 */
data class GuideWindowFrame(var guideHoleIndicators:ArrayList<GuideHoleIndicator> = arrayListOf()){

    constructor(guideHoleIndicator:GuideHoleIndicator):this(arrayListOf(guideHoleIndicator))
}
