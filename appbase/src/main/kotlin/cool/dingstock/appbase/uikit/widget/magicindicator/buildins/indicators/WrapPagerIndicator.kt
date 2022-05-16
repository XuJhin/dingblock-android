package cool.dingstock.appbase.uikit.widget.magicindicator.buildins.indicators

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import cool.dingstock.appbase.uikit.widget.magicindicator.buildins.FragmentContainerHelper
import cool.dingstock.appbase.uikit.widget.magicindicator.buildins.UIUtil
import cool.dingstock.appbase.uikit.widget.magicindicator.buildins.abs.IPagerIndicator
import cool.dingstock.appbase.uikit.widget.magicindicator.buildins.model.PositionData


class WrapPagerIndicator(context: Context) : View(context), IPagerIndicator {
    var verticalPadding = 0
    var horizontalPadding = 0
    var fillColor = 0
    private var mRoundRadius = 0f
    private var mStartInterpolator: Interpolator? = LinearInterpolator()
    private var mEndInterpolator: Interpolator? = LinearInterpolator()
    private var mPositionDataList: List<PositionData>? = null
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mRect: RectF = RectF()
    private var mRoundRadiusSet = false
    private fun init(context: Context) {
        mPaint.setStyle(Paint.Style.FILL)
        verticalPadding = UIUtil.dip2px(context, 6f)
        horizontalPadding = UIUtil.dip2px(context, 10f)
    }

    override fun onDraw(canvas: Canvas) {
        mPaint.color = fillColor
        canvas.drawRoundRect(mRect, mRoundRadius, mRoundRadius, mPaint)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mPositionDataList == null || mPositionDataList!!.isEmpty()) {
            return
        }

        // 计算锚点位置
        val current: PositionData =
            FragmentContainerHelper.getImitativePositionData(mPositionDataList!!, position)
        val next: PositionData =
            FragmentContainerHelper.getImitativePositionData(mPositionDataList!!, position + 1)
        mRect.left =
            current.mContentLeft - horizontalPadding + (next.mContentLeft - current.mContentLeft) * mEndInterpolator!!.getInterpolation(
                positionOffset
            )
        mRect.top = (current.mContentTop - verticalPadding).toFloat()
        mRect.right =
            current.mContentRight + horizontalPadding + (next.mContentRight - current.mContentRight) * mStartInterpolator!!.getInterpolation(
                positionOffset
            )
        mRect.bottom = (current.mContentBottom + verticalPadding).toFloat()
        if (!mRoundRadiusSet) {
            mRoundRadius = mRect.height() / 2
        }
        invalidate()
    }

    override fun onPageSelected(position: Int) {}
    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPositionDataProvide(dataList: List<PositionData>) {
        mPositionDataList = dataList
    }

    val paint: Paint?
        get() = mPaint
    var roundRadius: Float
        get() = mRoundRadius
        set(roundRadius) {
            mRoundRadius = roundRadius
            mRoundRadiusSet = true
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

    init {
        init(context)
    }
}