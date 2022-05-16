package cool.dingstock.appbase.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import cool.dingstock.lib_base.util.SizeUtils

class HintConstraintLayout @JvmOverloads constructor(mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(mContext, attrs, defStyleAttr) {
    override fun dispatchDraw(canvas: Canvas?) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        val backgroundColor = Color.WHITE
        val mArrowHeight: Float = SizeUtils.dp2px(8f).toFloat()
        val mArrowWidth: Float = SizeUtils.dp2px(8f).toFloat()
        val mShadowThickness: Float = SizeUtils.dp2px(2f).toFloat()
        val mRadius: Float = SizeUtils.dp2px(6f).toFloat()
        val mArrowOffset: Float = SizeUtils.dp2px(16f).toFloat()
        val mShadowColor = Color.parseColor("#16000000")
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = backgroundColor
        paint.style = Paint.Style.FILL
        // set Xfermode for source and shadow overlap
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OVER)
        // draw round corner rectangle
        paint.color = backgroundColor
        canvas?.drawRoundRect(RectF(0f, mArrowHeight.toFloat(), (measuredWidth - mShadowThickness).toFloat(), (measuredHeight - mShadowThickness).toFloat()),
                mRadius.toFloat(), mRadius.toFloat(), paint)
        // draw arrow
        val path = Path()
        val startPoint = mArrowOffset
        path.moveTo(startPoint.toFloat(), mArrowHeight)
        path.lineTo((startPoint + mArrowWidth).toFloat(), mArrowHeight.toFloat())
        path.lineTo((startPoint + mArrowWidth / 2).toFloat(), 0f)
        path.close()
        canvas?.drawPath(path, paint)

        // draw shadow
        if (mShadowThickness > 0) {
            paint.maskFilter = BlurMaskFilter(mShadowThickness, BlurMaskFilter.Blur.OUTER)
            paint.color = mShadowColor
            canvas?.drawRoundRect(RectF(mShadowThickness.toFloat(), (mArrowHeight + mShadowThickness).toFloat(),
                    (measuredWidth - mShadowThickness).toFloat(),
                    (measuredHeight - mShadowThickness).toFloat()), mRadius.toFloat(), mRadius.toFloat(), paint)
        }

        super.dispatchDraw(canvas)
    }
}