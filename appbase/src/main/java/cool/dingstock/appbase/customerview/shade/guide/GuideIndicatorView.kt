package cool.dingstock.appbase.customerview.shade.guide

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cool.dingstock.appbase.R
import cool.dingstock.appbase.ext.hide

/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/24  16:34
 */
class GuideIndicatorView(context: Context) : FrameLayout(context) {
    private var tv: TextView
    private var topView: ImageView
    private var bottomView: ImageView

    init {
        LayoutInflater.from(context).inflate(R.layout.guide_indicator_layout, this, true)
        tv = findViewById(R.id.tv)
        topView = findViewById(R.id.top_iv)
        bottomView = findViewById(R.id.bottom_iv)
    }

    fun setGuideIndicator(guideIndicator: GuideIndicator) {
        tv.setBackgroundResource(guideIndicator.indicatorBg)
        tv.setTextColor(guideIndicator.indicatorTextColor)
        tv.textSize = guideIndicator.indicatorTextSize
        tv.text = guideIndicator.indicatorText
        val layoutParams = tv.layoutParams
        layoutParams.width = guideIndicator.indicatorTvWidth.toInt()
        layoutParams.height = guideIndicator.indicatorTvHeight.toInt()
        tv.layoutParams = layoutParams

        val layoutParamsTopView:LinearLayout.LayoutParams = topView.layoutParams as LinearLayout.LayoutParams
        layoutParamsTopView.marginStart = guideIndicator.indicatorTriangleMarenStart.toInt()
        topView.layoutParams = layoutParamsTopView
        val layoutParamsBottomView = bottomView.layoutParams as  LinearLayout.LayoutParams
        layoutParamsBottomView.marginStart = guideIndicator.indicatorTriangleMarenStart.toInt()
        bottomView.layoutParams = layoutParamsBottomView

        topView.setImageResource(guideIndicator.indicatorTriangle)
        bottomView.setImageResource(guideIndicator.indicatorTriangle)
        topView.hide(guideIndicator.indicatorMargeY<0)
        bottomView.hide(guideIndicator.indicatorMargeY>=0)

    }

}