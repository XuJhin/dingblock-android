package cool.dingstock.appbase.widget.image

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import cool.dingstock.appbase.ext.load
import cool.dingstock.lib_base.util.SizeUtils
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class FindImageLayout @JvmOverloads constructor(mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ViewGroup(mContext, attrs, defStyleAttr) {

    private var DEFAULT_MARGIN = SizeUtils.dp2px(2f)

    companion object {
        const val TAG = "FindImageLayout"
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth - paddingStart - paddingBottom
        val height = width / 4
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, height.toInt())
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val parentHeight = (measuredWidth - 3 * DEFAULT_MARGIN) / 4
        var prevChildRight = 0
        var prevChildBottom = 0
        for (i in 0.until(childCount)) {
            val childView = getChildAt(i)
            childView.layout(prevChildRight, 0, prevChildRight + parentHeight, parentHeight)
            prevChildRight += parentHeight + DEFAULT_MARGIN
            prevChildBottom += parentHeight + DEFAULT_MARGIN
        }
    }

    fun setImageList(imageList: MutableList<String>) {
        if (imageList.size == 0) {
            return
        }
        removeAllViews()
        val cornerRadius: Float = SizeUtils.dp2px(8f).toFloat()
        imageList.forEachIndexed { index, s ->
            val imageView = ImageView(context)
            addView(imageView, layoutParams)
            if (index == 0 && imageList.size == 1) {
                imageView.load(s,
                        radius = 8f,
                        cornerType = RoundedCornersTransformation.CornerType.ALL)
            } else {
                when (index) {
                    0 -> {
                        imageView.load(s,
                                radius = 8f,
                                cornerType = RoundedCornersTransformation.CornerType.LEFT)
                    }
                    imageList.size - 1 -> {
                        imageView.load(s,
                                radius = 8f,
                                cornerType = RoundedCornersTransformation.CornerType.RIGHT)
                    }
                    else -> {
                        imageView.load(s,
                                radius = 0f,
                                cornerType = RoundedCornersTransformation.CornerType.ALL)
                    }
                }
            }
        }

    }
}