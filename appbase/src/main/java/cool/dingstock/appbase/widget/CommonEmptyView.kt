package cool.dingstock.appbase.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import cool.dingstock.appbase.R

class CommonEmptyView(context: Context, attributeSet: AttributeSet?) :
    FrameLayout(context, attributeSet) {

    val mRootView: View
    val tv: TextView
    val emptyIv: ImageView

    init {
        mRootView = LayoutInflater.from(context).inflate(R.layout.common_empty_layout, this, true)
        tv = rootView.findViewById(R.id.empty_tv)
        emptyIv = rootView.findViewById(R.id.empty_iv)
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        mRootView.layoutParams = params
    }

    constructor(context: Context) : this(context, null) {


    }

    constructor(context: Context, text: String?, noBg: Boolean, fullParent: Boolean) : this(
        context,
        null
    ) {
        if (noBg) {
            mRootView.findViewById<View>(R.id.m_root_view).background = null
        }
        if (fullParent) {
            val params =
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            mRootView.layoutParams = params
        }
        if (text != null) {
            tv.text = text
        }
    }

    fun setShowBg(noBg: Boolean) {
        if (noBg) {
            mRootView.findViewById<View>(R.id.m_root_view).background = null
        }
    }

    fun setFullParent(fullParent: Boolean) {
        if (fullParent) {
            val params =
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            mRootView.layoutParams = params
        }
    }

    fun setText(text: String): CommonEmptyView {
        tv.text = text
        return this
    }

    fun setTextColor(color: Int): CommonEmptyView {
        try {
            tv.setTextColor(color)
        } catch (e: Exception) {
            tv.setTextColor(ContextCompat.getColor(context, R.color.color_text_black2))
        }
        return this
    }

    fun getTextView(): TextView {
        return tv
    }

    fun setImageResource(@DrawableRes resId: Int): CommonEmptyView {
        emptyIv.setImageResource(resId)
        return this
    }

    fun setIsMarginTop(isNeedMarginTop: Boolean): CommonEmptyView {
        if (!isNeedMarginTop) {
            val params = emptyIv.layoutParams as? MarginLayoutParams
            params?.topMargin = 0
            emptyIv.layoutParams = params
        }
        return this
    }
}