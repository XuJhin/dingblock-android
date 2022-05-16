package cool.dingstock.imagepicker.views.roundImageView

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import cool.dingstock.imagepicker.R
import kotlin.math.min

open class RoundImageView @JvmOverloads constructor(mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AppCompatImageView(mContext, attrs, defStyleAttr) {
    private val mDrawableRect = RectF()
    private val mBorderRect = RectF()
    private val mShaderMatrix = Matrix()
    private val mBitmapPaint = Paint()
    private val mBorderPaint = Paint()
    private var mType = 0
    private var mBorderColor: Int = 0
    private var mBorderWidth: Float = 0.toFloat()
    private var mBorderConner: Float = 0.toFloat()
    private var mBitmap: Bitmap? = null
    private var mBitmapShader: BitmapShader? = null
    private var mBitmapWidth: Int = 0
    private var mBitmapHeight: Int = 0
    private var mReady: Boolean = false
    private var mSetupPending: Boolean = false
    private var mBorderOverlay: Boolean = false
    var isDisableCircularTransformation: Boolean = false
        set(disableCircularTransformation) {
            if (isDisableCircularTransformation == disableCircularTransformation) {
                return
            }
            field = disableCircularTransformation
            initializeBitmap()
        }
    private var mDrawableRadius: Float = 0.toFloat()
    private var mBorderRadius: Float = 0.toFloat()
    var type: Int
        get() = mType
        set(type) {
            this.mType = type
            setup()
        }

    init {
        if (attrs != null) {
            val typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.RoundImageView, defStyleAttr, 0)
            mType = typedArray.getInt(R.styleable.RoundImageView_view_type, DEFAULT_BORDER_TYPE)
            mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.RoundImageView_border_width, DEFAULT_BORDER_WIDTH).toFloat()
            mBorderColor = typedArray.getColor(R.styleable.RoundImageView_border_color, DEFAULT_BORDER_COLOR)
            mBorderConner = typedArray.getDimensionPixelSize(R.styleable.RoundImageView_border_conner, DEFAULT_BORDER_RADIUS).toFloat()
            mBorderOverlay = typedArray.getBoolean(R.styleable.RoundImageView_border_overlay, false)
            typedArray.recycle()
        }
        initView()
    }

    private fun initView() {
        mReady = true
        if (mSetupPending) {
            setup()
            mSetupPending = false
        }
    }

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        if (adjustViewBounds) {
            throw IllegalArgumentException("adjustViewBounds not supported.")
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (isDisableCircularTransformation) {
            super.onDraw(canvas)
            return
        }
        val bitmap = refreshBitmap()
        if (mBitmap == null) {
            return
        }
        when (mType) {
            TYPE_CIRCLE -> {
                canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mBitmapPaint)
                if (mBorderWidth > 0) {
                    canvas.drawCircle(mBorderRect.centerX(), mBorderRect.centerY(), mBorderRadius, mBorderPaint)
                }
            }
            TYPE_ROUND -> {
                canvas.drawRoundRect(mDrawableRect, mBorderConner, mBorderConner, mBitmapPaint)
                if (mBorderWidth > 0) {
                    canvas.drawRoundRect(mBorderRect, mBorderConner, mBorderConner, mBorderPaint)
                }
            }
            else -> {
                canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mBitmapPaint)
                if (mBorderWidth > 0) {
                    canvas.drawCircle(mBorderRect.centerX(), mBorderRect.centerY(), mBorderRadius, mBorderPaint)
                }
            }
        }
        bitmap?.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setup()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        setup()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        setup()
    }

    fun setBorderColor(@ColorInt borderColor: Int) {
        if (borderColor == mBorderColor) {
            return
        }
        mBorderColor = borderColor
        mBorderPaint.color = mBorderColor
        invalidate()
    }

    @Deprecated("Use {@link #setBorderColor(int)} instead", ReplaceWith("setBorderColor(context.resources.getColor(borderColorRes))"))
    fun setBorderColorResource(@ColorRes borderColorRes: Int) {
        setBorderColor(context.resources.getColor(borderColorRes))
    }

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        initializeBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        initializeBitmap()
    }

    override fun setScaleType(scaleType: ScaleType?) {
        super.setScaleType(scaleType)
        setup()
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        initializeBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        initializeBitmap()
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        return try {
            val bitmap: Bitmap = if (drawable is ColorDrawable) {
                Bitmap.createBitmap(COLOR_DRAWABLE_DIMENSION, COLOR_DRAWABLE_DIMENSION, BITMAP_CONFIG)
            } else {
                Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, BITMAP_CONFIG)
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun initializeBitmap() {
        mBitmap = if (isDisableCircularTransformation) {
            null
        } else {
            getBitmapFromDrawable(drawable)
        }
        setup()
    }

    private fun setup() {
        if (!mReady) {
            mSetupPending = true
            return
        }

        if (width == 0 && height == 0) {
            return
        }

        if (mBitmap == null) {
            postInvalidate()
            return
        }
        realSetup(mBitmap!!)
        updateShaderMatrix()
        postInvalidate()
    }

    private fun refreshBitmap(): Bitmap? {
        if (drawable == null) return null
        if (drawable is BitmapDrawable) {
            return null
        }
        val bitmap: Bitmap = if (drawable is ColorDrawable) {
            Bitmap.createBitmap(COLOR_DRAWABLE_DIMENSION, COLOR_DRAWABLE_DIMENSION, BITMAP_CONFIG)
        } else {
            Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, BITMAP_CONFIG)
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        realSetup(bitmap)
        return bitmap
    }

    private fun realSetup(bitmap: Bitmap) {
        mBitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mBitmapPaint.isAntiAlias = true
        mBitmapPaint.shader = mBitmapShader
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.isAntiAlias = true
        mBorderPaint.color = mBorderColor
        mBorderPaint.strokeWidth = mBorderWidth
        mBitmapHeight = mBitmap!!.height
        mBitmapWidth = mBitmap!!.width
        mBorderRect.set(calculateBounds())
        mBorderRadius = min(
            (mBorderRect.height() - mBorderWidth) / 2.0f,
            (mBorderRect.width() - mBorderWidth) / 2.0f
        )
        mDrawableRect.set(mBorderRect)
        if (mType == TYPE_CIRCLE) {
            if (!mBorderOverlay && mBorderWidth > 0) {
                mDrawableRect.inset(mBorderWidth - 1.0f, mBorderWidth - 1.0f)
            }
        } else {
            mDrawableRect.inset(mBorderWidth / 4, mBorderWidth / 4)
        }
        mDrawableRadius = (mDrawableRect.height() / 2.0f).coerceAtMost(mDrawableRect.width() / 2.0f)
    }

    private fun calculateBounds(): RectF {
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom
        return if (mType == TYPE_CIRCLE) {
            val sideLength = min(availableWidth, availableHeight)
            val left = paddingLeft + (availableWidth - sideLength) / 2f
            val top = paddingTop + (availableHeight - sideLength) / 2f
            RectF(left, top, left + sideLength, top + sideLength)
        } else {
            val left = paddingLeft + mBorderWidth / 2
            val top = paddingTop + mBorderWidth / 2
            RectF(left, top, availableWidth - mBorderWidth / 2, availableHeight - mBorderWidth / 2)
        }
    }

    private fun updateShaderMatrix() {
        val scale: Float
        var dx = 0f
        var dy = 0f
        mShaderMatrix.set(null)
        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / mBitmapHeight.toFloat()
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f
        } else {
            scale = mDrawableRect.width() / mBitmapWidth.toFloat()
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f
        }
        mShaderMatrix.setScale(scale, scale)
        mShaderMatrix.postTranslate((dx + 0.5f).toInt() + mDrawableRect.left, (dy + 0.5f).toInt() + mDrawableRect.top)
        mBitmapShader!!.setLocalMatrix(mShaderMatrix)
    }

    fun setBorderCorner(corner: Int) {
        this.mBorderConner = corner.toFloat()
        setup()
    }

    fun setBorderType(type: Int) {
        this.type = type
        setup()
    }

    fun setBorderWidth(width: Int) {
        this.mBorderWidth = width.toFloat()
        setup()
    }

    fun setBorderOverlay(b: Boolean) {
        this.mBorderOverlay = b
        setup()
    }

    companion object {
        const val TYPE_NONE = 0
        const val TYPE_ROUND = 1
        const val TYPE_CIRCLE = 2
        private const val DEFAULT_BORDER_WIDTH = 0
        private const val DEFAULT_BORDER_RADIUS = 0
        private const val DEFAULT_BORDER_TYPE = TYPE_NONE
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_FILL_COLOR = Color.TRANSPARENT
        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
        private const val COLOR_DRAWABLE_DIMENSION = 2
    }
}