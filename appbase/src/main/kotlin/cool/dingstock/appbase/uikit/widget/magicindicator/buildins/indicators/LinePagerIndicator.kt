package cool.dingstock.appbase.uikit.widget.magicindicator.buildins.indicators

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import net.lucode.hackware.magicindicator.FragmentContainerHelper
import net.lucode.hackware.magicindicator.buildins.ArgbEvaluatorHolder
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.model.PositionData


/**
 * 直线viewpager指示器，带颜色渐变
 * 博客: http://hackware.lucode.net
 * Created by hackware on 2016/6/26.
 */
class LinePagerIndicator(context: Context) : View(context), IPagerIndicator {
    private var mMode // 默认为MODE_MATCH_EDGE模式
            = 0

    // 控制动画
    private var mStartInterpolator: Interpolator? = LinearInterpolator()
    private var mEndInterpolator: Interpolator? = LinearInterpolator()

    var mode: Int
        get() = mMode
        set(mode) {
            mMode =
                if (mode == MODE_EXACTLY || mode == MODE_MATCH_EDGE || mode == MODE_WRAP_CONTENT) {
                    mode
                } else {
                    throw IllegalArgumentException("mode $mode not supported.")
                }
        }

    fun setColors(vararg colors: Int?) {
        this.colors = mutableListOf(*colors).filterNotNull().toMutableList()
    }

    var startInterpolator: Interpolator?
        get() = mStartInterpolator
        set(startInterpolator) {
            mStartInterpolator = startInterpolator
            if (mStartInterpolator == null) {
                mStartInterpolator = LinearInterpolator()
            }
        }
    var endInterpolator: Interpolator?
        get() = mEndInterpolator
        set(endInterpolator) {
            mEndInterpolator = endInterpolator
            if (mEndInterpolator == null) {
                mEndInterpolator = LinearInterpolator()
            }
        }

    // 相对于底部的偏移量，如果你想让直线位于title上方，设置它即可
    var yOffset = 0f
    var lineHeight = 0f
    var xOffset = 0f
    var lineWidth = 0f
    var roundRadius = 0f
    var paint: Paint? = null
        private set
    private var mPositionDataList: List<PositionData>? = null
    var colors: MutableList<Int>? = null
        private set
    private val mLineRect = RectF()
    private fun init(context: Context) {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint!!.style = Paint.Style.FILL
        lineHeight = UIUtil.dip2px(context, 3.0).toFloat()
        lineWidth = UIUtil.dip2px(context, 10.0).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(mLineRect, roundRadius, roundRadius, paint!!)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mPositionDataList == null || mPositionDataList!!.isEmpty()) {
            return
        }

        // 计算颜色
        if (colors != null && colors!!.size > 0) {
            val currentColor = colors!![Math.abs(position) % colors!!.size]
            val nextColor = colors!![Math.abs(position + 1) % colors!!.size]
            val color = ArgbEvaluatorHolder.eval(positionOffset, currentColor, nextColor)
            paint!!.color = color
        }

        // 计算锚点位置
        val current = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position)
        val next = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position + 1)
        val leftX: Float
        val nextLeftX: Float
        val rightX: Float
        val nextRightX: Float
        if (mMode == MODE_MATCH_EDGE) {
            leftX = current.mLeft + xOffset
            nextLeftX = next.mLeft + xOffset
            rightX = current.mRight - xOffset
            nextRightX = next.mRight - xOffset
        } else if (mMode == MODE_WRAP_CONTENT) {
            leftX = current.mContentLeft + xOffset
            nextLeftX = next.mContentLeft + xOffset
            rightX = current.mContentRight - xOffset
            nextRightX = next.mContentRight - xOffset
        } else {    // MODE_EXACTLY
            leftX = current.mLeft + (current.width() - lineWidth) / 2
            nextLeftX = next.mLeft + (next.width() - lineWidth) / 2
            rightX = current.mLeft + (current.width() + lineWidth) / 2
            nextRightX = next.mLeft + (next.width() + lineWidth) / 2
        }
        mLineRect.left =
            leftX + (nextLeftX - leftX) * mStartInterpolator!!.getInterpolation(positionOffset)
        mLineRect.right =
            rightX + (nextRightX - rightX) * mEndInterpolator!!.getInterpolation(positionOffset)
        mLineRect.top = height - lineHeight - yOffset
        mLineRect.bottom = height - yOffset
        invalidate()
    }

    override fun onPageSelected(position: Int) {}
    override fun onPageScrollStateChanged(state: Int) {}
    override fun onPositionDataProvide(dataList: List<PositionData>) {
        mPositionDataList = dataList
    }

    companion object {
        const val MODE_MATCH_EDGE = 0 // 直线宽度 == title宽度 - 2 * mXOffset
        const val MODE_WRAP_CONTENT = 1 // 直线宽度 == title内容宽度 - 2 * mXOffset
        const val MODE_EXACTLY = 2 // 直线宽度 == mLineWidth
    }

    init {
        init(context)
    }
}
