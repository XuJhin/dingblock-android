package cool.dingstock.appbase.widget.dividerview

import android.R.attr.orientation
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.SlidingDrawer.ORIENTATION_HORIZONTAL
import cool.dingstock.appbase.R


class DividerView : View {
    private var mPaint : Paint
    private var orientation:Int

    constructor(context: Context?,attrs:AttributeSet?):super(context,attrs){
        val dashGap: Int
        val dashLength: Int
        val dashThickness: Int
        val color: Int

        val a: TypedArray = context!!.theme.obtainStyledAttributes(attrs, R.styleable.DividerView, 0, 0)

        try {
            dashGap = a.getDimensionPixelSize(R.styleable.DividerView_dashGap, 5)
            dashLength = a.getDimensionPixelSize(R.styleable.DividerView_dashLength, 5)
            dashThickness = a.getDimensionPixelSize(R.styleable.DividerView_dashThickness, 3)
            color = a.getColor(R.styleable.DividerView_divider_line_color, -0x1000000)
            orientation = a.getInt(R.styleable.DividerView_divider_orientation, ORIENTATION_HORIZONTAL)
        } finally {
            a.recycle()
        }

        mPaint = Paint()
        mPaint.setAntiAlias(true)
        mPaint.setColor(color)
        mPaint.setStyle(Paint.Style.STROKE)
        mPaint.setStrokeWidth(dashThickness.toFloat())
        mPaint.setPathEffect(DashPathEffect(floatArrayOf(dashGap.toFloat(), dashLength.toFloat()), 0F))


    }

    override fun onDraw(canvas: Canvas) {
        if (orientation == ORIENTATION_HORIZONTAL) {
            val center = height * 0.5f
            canvas.drawLine(0F, center, width.toFloat(), center, mPaint)
        } else {
            val center = width * 0.5f
            canvas.drawLine(center, 0F, center, height.toFloat(), mPaint)
        }
    }

}