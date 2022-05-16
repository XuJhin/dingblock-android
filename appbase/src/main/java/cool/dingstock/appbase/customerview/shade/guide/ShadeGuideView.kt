package cool.dingstock.appbase.customerview.shade.guide

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import cool.dingstock.appbase.R
import cool.dingstock.appbase.ext.dp

/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/24  11:42
 *
 *  遮罩 引导页面
 *
 */
class ShadeGuideView(context: Context, attributeSet: AttributeSet?) : FrameLayout(context, attributeSet) {
    private val stv: ShadeTransparencyView = ShadeTransparencyView(context)
    private val nextBtn: TextView = TextView(context)

    //本次引导页有多少屏
    private val guideWindowFrames = arrayListOf<GuideWindowFrame>()
    //-1 代表还没有开始
    private var currentShowFrame = -1

    //本屏幕 指示器 的集合
    private val indicators = arrayListOf<GuideIndicatorView>()

    constructor(context: Context) : this(context, null)

    init {
        initView()
        initListener()
    }

    private fun initView() {
        val stvLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(stv, stvLayoutParams)
        nextBtn.text = "我知道了"
        nextBtn.gravity = Gravity.CENTER
        nextBtn.setTextColor(Color.WHITE)
        nextBtn.setBackgroundResource(R.drawable.common_dotted_bg_radius_20)
        val nextBtnLayoutParams = LayoutParams(140.dp.toInt(), 41.dp.toInt())
        nextBtnLayoutParams.gravity = Gravity.CENTER
        addView(nextBtn,nextBtnLayoutParams)
    }

    private fun initListener() {
        setOnClickListener {

        }
        nextBtn.setOnClickListener {
            //next
            showNextFrame()
        }
    }

    fun setGuideWindowFrames(guideWindowFrames:ArrayList<GuideWindowFrame>){
        this.guideWindowFrames.clear()
        this.guideWindowFrames.addAll(guideWindowFrames)
        currentShowFrame = -1
        showNextFrame()
    }

    private fun showNextFrame(){
        //最后一个了
        if(currentShowFrame == guideWindowFrames.size-1){
            (parent as? ViewGroup)?.apply {
                removeView(this@ShadeGuideView)
            }
            return
        }
        //取下一个
        currentShowFrame++
        if(currentShowFrame<0||currentShowFrame>guideWindowFrames.size-1){
            (parent as? ViewGroup)?.apply {
                removeView(this@ShadeGuideView)
            }
            return
        }
        val guideWindowFrame = guideWindowFrames[currentShowFrame]
        //重新绘制孔洞
        stv.setHoleList(guideWindowFrame.guideHoleIndicators)

        //绘制指示器 start
        //先移除之前的
        for(v in indicators){
            removeView(v)
        }
        indicators.clear()
        //绘制新的
        for (hole in guideWindowFrame.guideHoleIndicators){
            for(indicator in hole.indicators){
                val guideIndicatorView = GuideIndicatorView(context)
                //更新指示器的显示样式
                guideIndicatorView.setGuideIndicator(indicator)
                val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                addView(guideIndicatorView,layoutParams)
                indicators.add(guideIndicatorView)
                //设置 指示器的显示位置
                if(indicator.indicatorMargeY>=0){
                    guideIndicatorView.y = hole.targetBottom+indicator.indicatorMargeY
                }else{
                    guideIndicatorView.y = hole.targetTop+indicator.indicatorMargeY-indicator.indicatorTvHeight-indicator.indicatorTriangleHeight
                }
                guideIndicatorView.x = hole.targetStart+indicator.indicatorMargeStart
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        guideWindowFrames.clear()
        currentShowFrame=-1
    }



}