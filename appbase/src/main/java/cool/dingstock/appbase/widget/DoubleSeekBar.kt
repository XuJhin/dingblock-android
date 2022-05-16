package cool.dingstock.appbase.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import cool.dingstock.appbase.R
import cool.dingstock.lib_base.util.SizeUtils

class DoubleSeekBar @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val thumb: Bitmap
    private val clickThumb: Bitmap

    //最大值
    private var maxValue: Int

    //最小值
    private var minValue: Int
    private var defaultWidth = 0
    private var defaultHeight = 0
    private val bgHeight: Float
    private val paddingLeft: Float
    private val paddingRight: Float
    private var positionX = 0f
    private var positionY = 0f
    private var startX = 0f
    private var startY = 0f//左按钮的坐标值 = 0f
    private var viewWidth = 0
    private var viewHeight = 0
    private var clickedType = CLICK_TYPE_NULL
    private val paintBg = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintBg2 = Paint(Paint.ANTI_ALIAS_FLAG)
    private val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var mOnChanged: OnChanged? = null
    private var maxPositionX = 0f
    private var minPositionX = 0f
    var seekValue: Long = 0
    var percent = 0f
    private fun getBitmap(drawableRes: Int): Bitmap {
        val drawable = resources.getDrawable(drawableRes)
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_4444
        )
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }

    fun setMaxValue(maxValue: Int) {
        var maxValue = maxValue
        if (maxValue < 0) {
            maxValue = 0
        }
        this.maxValue = maxValue
    }

    fun setMinValue(minValue: Int) {
        var minValue = minValue
        if (minValue < 0) {
            minValue = 0
        }
        this.minValue = minValue
    }

    var isDown = false
    var downY = 0f

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (isClickedIcon(event)) {
                downY = event.y
                postInvalidate()
                isDown = true
                return true
            }
            MotionEvent.ACTION_UP -> {
                isDown = false
                clickedType = CLICK_TYPE_NULL
                postInvalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                event.setLocation(event.x, downY)
                if (handleMoveEvent(event)) {
                    return false
                }
                return true
            }
            else -> {
            }
        }

        return super.dispatchTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (isClickedIcon(event)) {
                postInvalidate()
                isDown = true
                return true
            }
            MotionEvent.ACTION_UP -> {
                isDown = false
                clickedType = CLICK_TYPE_NULL
                postInvalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                if (handleMoveEvent(event)) {
                    return false
                }
                return true
            }
            else -> {
            }
        }
        return true
    }

    /**
     * 判断是否点击到按钮了
     *
     * @param event
     * @return
     */
    private fun isClickedIcon(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        if (x < positionX + thumb.width && x > positionX && y > startY && y < startY + thumb.height) {
            clickedType = CLICK_TYPE_THUMB
            return true
        }
        clickedType = CLICK_TYPE_NULL
        return false
    }

    /**
     * 滑动事件处理
     *
     * @param motionEvent
     */
    private fun handleMoveEvent(motionEvent: MotionEvent): Boolean {
        positionX = motionEvent.x
        if (positionX < minPositionX) {
            positionX = minPositionX
        }
        if (positionX > maxPositionX) {
            positionX = maxPositionX
        }
        val midPointX = (maxPositionX - minPositionX) / 2
        percent = (positionX - midPointX) / (maxPositionX - minPositionX)
        seekValue = (0 + percent * 2000).toLong()
        updateSeekValue(seekValue, positionX)
        return true
    }

    private fun updateSeekValue(seekValue: Long, positionX: Float) {
        this.seekValue = seekValue
        this.positionX = positionX
        invalidate()
        if (mOnChanged != null) {
            mOnChanged?.onChange(seekValue)
        }
    }

    /**
     * 注意，在LinearLayout布局中，设置weight = 1时，onlayout 会多次回调，导致indexL(/R)X值一直为默认值，导致无法滑动
     * 可以将布局改成releativeLayout。
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        viewWidth = measuredWidth
        viewHeight = measuredHeight
        maxPositionX = viewWidth - paddingRight - thumb.width / 2
        minPositionX = 0f
        startX = viewWidth / 2f - thumb.width / 2f
        startY = viewHeight / 2f - thumb.height / 2f
        positionY = startY
    }

    /**
     * 更新偏移值
     */
    fun updateValue(offset: Long) {
        seekValue = offset
        percent = (seekValue - 0f) / 2000
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBg(canvas)
        drawThumb(canvas)
    }

    private fun drawThumb(canvas: Canvas) {
        positionX = ((percent) * (maxPositionX - minPositionX) + (maxPositionX - minPositionX) / 2)
        if (clickedType == CLICK_TYPE_THUMB) {
            canvas.drawBitmap(clickThumb, positionX, positionY, iconPaint)
        } else {
            iconPaint.maskFilter = null
            canvas.drawBitmap(thumb, positionX, positionY, iconPaint)
        }
    }

    /**
     * 绘制背景
     *
     * @param canvas
     */
    private fun drawBg(canvas: Canvas) {
        positionX = ((percent) * (maxPositionX - minPositionX) + (maxPositionX - minPositionX) / 2)
        paintBg.isAntiAlias = true
        //画两端的半圆
        val circleLeftCenterX = 0 + paddingLeft
        val circleRightCenterX = viewWidth - paddingRight

        canvas.drawCircle(circleLeftCenterX, viewHeight / 2.toFloat(), bgHeight / 2, paintBg2)//蓝
        canvas.drawCircle(circleRightCenterX, viewHeight / 2.toFloat(), bgHeight / 2, paintBg)//白
        canvas.drawRect(
            RectF(
                circleLeftCenterX, viewHeight / 2 - bgHeight / 2,
                circleRightCenterX, viewHeight / 2 + bgHeight / 2
            ), paintBg
        )

        canvas.drawRect(
            RectF(
                circleLeftCenterX, viewHeight / 2 - bgHeight / 2,
                positionX + paddingRight, viewHeight / 2 + bgHeight / 2
            ), paintBg2
        )
    }

    /**
     * 回调接口，回调左右值
     */
    interface OnChanged {
        fun onChange(seekValue: Long)
    }

    /**
     * 重写onMeasure方法，设置wrap_content 时需要默认大小
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e("seekBar", "onMeasure")
        Log.e("seekBar", "onMeasure  width ${measuredWidth}")
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(defaultWidth, defaultHeight)
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(defaultWidth, heightSpecSize)
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, defaultHeight)
        }
    }

    companion object {
        private const val TAG = "DoubleSeekBar"
        private const val CLICK_TYPE_NULL = 0
        private const val CLICK_TYPE_THUMB = 1
    }

    init {
        bgHeight = SizeUtils.dp2px(3f).toFloat() //设置背景线条的高度
        thumb = getBitmap(R.drawable.icon_time_seek_thumb)
        clickThumb = getBitmap(R.drawable.icon_time_seek_thumb)
        paddingLeft = SizeUtils.dp2px(16f).toFloat()
        paddingRight = SizeUtils.dp2px(16f).toFloat()
        paintBg.color = resources.getColor(R.color.color_line)//Color.parseColor("#FFEAECF2")
        paintBg2.color = resources.getColor(R.color.color_blue)//Color.parseColor("#FF438FFF")
        maxValue = 100 //default maxvalue
        minValue = 0//default minvalue
        defaultWidth = SizeUtils.dp2px(100f)
        defaultHeight = SizeUtils.dp2px(50f)
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }
}

class DoubleSeekbarAni(
    private val seekBar: DoubleSeekBar,
    private val startValue: Long,
    private val endValue: Long
) :
    Animation() {
    init {
        duration = 200
        setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                seekBar.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animation?) {
                seekBar.isEnabled = true
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })

    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        super.applyTransformation(interpolatedTime, t)
        val targetValue = ((endValue - startValue) * interpolatedTime).toLong() + startValue
        seekBar.updateValue(targetValue)
    }


}