package cool.dingstock.appbase.uikit.widget.magicindicator.buildins.titles

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import cool.dingstock.appbase.uikit.widget.magicindicator.buildins.UIUtil
import cool.dingstock.appbase.uikit.widget.magicindicator.buildins.abs.IMeasurablePagerTitleView


class ClipPagerTitleView(context: Context) : View(context),
    IMeasurablePagerTitleView {
    private var mText: String = ""
    private var mTextColor = 0
    private var mClipColor = 0
    private var mLeftToRight = false
    private var mClipPercent = 0f
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mTextBounds: Rect = Rect()
    private fun init(context: Context) {
        val textSize: Int = UIUtil.dip2px(context, 16f)
        mPaint.textSize = textSize.toFloat()
        val padding: Int = UIUtil.dip2px(context, 10f)
        setPadding(padding, 0, padding.toFloat().toInt(), 0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureTextBounds()
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))
    }

    private fun measureWidth(widthMeasureSpec: Int): Int {
        val mode: Int = MeasureSpec.getMode(widthMeasureSpec)
        val size: Int = MeasureSpec.getSize(widthMeasureSpec)
        var result = size
        when (mode) {
            MeasureSpec.AT_MOST -> {
                val width: Int = mTextBounds.width() + getPaddingLeft() + getPaddingRight()
                result = Math.min(width, size)
            }
            MeasureSpec.UNSPECIFIED -> result =
                mTextBounds.width() + getPaddingLeft() + getPaddingRight()
            else -> {}
        }
        return result
    }

    private fun measureHeight(heightMeasureSpec: Int): Int {
        val mode: Int = MeasureSpec.getMode(heightMeasureSpec)
        val size: Int = MeasureSpec.getSize(heightMeasureSpec)
        var result = size
        when (mode) {
            MeasureSpec.AT_MOST -> {
                val height: Int = mTextBounds.height() + getPaddingTop() + getPaddingBottom()
                result = Math.min(height, size)
            }
            MeasureSpec.UNSPECIFIED -> result =
                mTextBounds.height() + getPaddingTop() + getPaddingBottom()
            else -> {}
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        val x: Int = (getWidth() - mTextBounds.width()) / 2
        val fontMetrics: Paint.FontMetrics = mPaint!!.getFontMetrics()
        val y = ((getHeight() - fontMetrics.bottom - fontMetrics.top) / 2) as Int

        // 画底层
        mPaint.setColor(mTextColor)
        canvas.drawText(mText, x.toFloat(), y.toFloat(), mPaint)

        // 画clip层
        canvas.save()
        if (mLeftToRight) {
            canvas.clipRect(0f, 0f, width * mClipPercent, height.toFloat())
        } else {
            canvas.clipRect(
                getWidth() * (1 - mClipPercent),
                0f,
                getWidth().toFloat(),
                getHeight().toFloat()
            )
        }
        mPaint.setColor(mClipColor)
        canvas.drawText(mText, x.toFloat(), y.toFloat(), mPaint)
        canvas.restore()
    }

    override fun onSelected(index: Int, totalCount: Int) {}
    override fun onDeselected(index: Int, totalCount: Int) {}
    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        mLeftToRight = !leftToRight
        mClipPercent = 1.0f - leavePercent
        invalidate()
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        mLeftToRight = leftToRight
        mClipPercent = enterPercent
        invalidate()
    }

    private fun measureTextBounds() {
        mPaint.getTextBounds(mText, 0, if (mText.isEmpty()) 0 else mText.length, mTextBounds)
    }

    var text: String?
        get() = mText
        set(text) {
            if (text != null) {
                mText = text
            }
            requestLayout()
        }
    var textSize: Float
        get() = mPaint.getTextSize()
        set(textSize) {
            mPaint.setTextSize(textSize)
            requestLayout()
        }
    var textColor: Int
        get() = mTextColor
        set(textColor) {
            mTextColor = textColor
            invalidate()
        }
    var clipColor: Int
        get() = mClipColor
        set(clipColor) {
            mClipColor = clipColor
            invalidate()
        }
    override val contentLeft: Int
        get() {
            val contentWidth: Int = mTextBounds.width()
            return getLeft() + getWidth() / 2 - contentWidth / 2
        }
    override val contentTop: Int
        get() {
            val metrics: Paint.FontMetrics = mPaint.getFontMetrics()
            val contentHeight: Float = metrics.bottom - metrics.top
            return (getHeight() / 2 - contentHeight / 2).toInt()
        }
    override val contentRight: Int
        get() {
            val contentWidth: Int = mTextBounds.width()
            return getLeft() + getWidth() / 2 + contentWidth / 2
        }
    override val contentBottom: Int
        get() {
            val metrics: Paint.FontMetrics = mPaint.getFontMetrics()
            val contentHeight: Float = metrics.bottom - metrics.top
            return ((getHeight() / 2 + contentHeight / 2).toInt())
        }

    init {
        init(context)
    }
}