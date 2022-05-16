package cool.dingstock.appbase.uikit.widget.magicindicator

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import cool.dingstock.appbase.uikit.widget.magicindicator.abs.IPagerNavigator

class MagicIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(
    context, attrs, defStyleAttr
) {
    fun onPageSelected(position: Int) {
        mNavigator?.onPageSelected(position)
    }

    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        mNavigator?.onPageScrolled(position, positionOffset, positionOffsetPixels)
    }


    fun onPageScrollStateChanged(state: Int) {
        mNavigator?.onPageScrollStateChanged(state)
    }

    var mNavigator: IPagerNavigator? = null
        set(value) {
            mNavigator?.onDetachFromMagicIndicator()
            field = value
            removeAllViews()
            if (value is View) {
                val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                addView(mNavigator as View, lp)
                mNavigator?.onAttachToMagicIndicator()
            }
        }
}