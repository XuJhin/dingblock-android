package cool.dingstock.appbase.customerview.scroll

import android.graphics.Outline
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.core.view.NestedScrollingChild

fun View.isUnder(rawX: Float, rawY: Float): Boolean {
    val xy = IntArray(2)
    getLocationOnScreen(xy)
    return rawX.toInt() in xy[0]..(xy[0] + width) && rawY.toInt() in xy[1]..(xy[1] + height)
}

fun ViewGroup.findFirst(recursively: Boolean, predict: (View)->Boolean): View? {
    for (i in 0 until childCount) {
        val v = getChildAt(i)
        if (predict(v)) {
            return v
        }
        if (recursively) {
            return (v as? ViewGroup)?.findFirst(recursively, predict) ?: continue
        }
    }
    return null
}

fun ViewGroup.findChildUnder(rawX: Float, rawY: Float): View? {
    return findFirst(false) { it.isUnder(rawX, rawY) }
}

fun View.findScrollableTarget(rawX: Float, rawY: Float, dScrollY: Int): View? {
    return when {
        !isUnder(rawX, rawY) -> null
        canScrollVertically(dScrollY) -> this
        this !is ViewGroup -> null
        else -> {
            var t: View? = null
            for (i in 0 until childCount) {
                t = getChildAt(i).findScrollableTarget(rawX, rawY, dScrollY)
                if (t != null) {
                    break
                }
            }
            t
        }
    }
}

fun View.canNestedScrollVertically(): Boolean = this is NestedScrollingChild
        && (this.canScrollVertically(1) || this.canScrollVertically(-1))

fun View.canNestedScrollHorizontally(): Boolean = this is NestedScrollingChild
        && (this.canScrollHorizontally(1) || this.canScrollHorizontally(-1))

fun ViewGroup.findHorizontalNestedScrollingTarget(rawX: Float, rawY: Float): View? {
    for (i in 0 until childCount) {
        val v = getChildAt(i)
        if (!v.isUnder(rawX, rawY)) {
            continue
        }
        if (v.canNestedScrollHorizontally()) {
            return v
        }
        if (v !is ViewGroup) {
            continue
        }
        val t = v.findHorizontalNestedScrollingTarget(rawX, rawY)
        if (t != null) {
            return t
        }
    }
    return null
}

fun ViewGroup.findVerticalNestedScrollingTarget(rawX: Float, rawY: Float): View? {
    for (i in 0 until childCount) {
        val v = getChildAt(i)
        if (!v.isUnder(rawX, rawY)) {
            continue
        }
        if (v.canNestedScrollVertically()) {
            return v
        }
        if (v !is ViewGroup) {
            continue
        }
        val t = v.findVerticalNestedScrollingTarget(rawX, rawY)
        if (t != null) {
            return t
        }
    }
    return null
}

fun View.setRoundRect(radius: Float) {
    clipToOutline = true
    outlineProvider = object: ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(0, 0, view.width, view.height, radius)
        }
    }
}

fun View.setLayoutSize(layoutWidth: Int, layoutHeight: Int) {
    layoutParams?.width = layoutWidth
    layoutParams?.height = layoutHeight
    requestLayout()
}

fun View.stopScroll() {
    val e = MotionEvent.obtain(
        SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
        MotionEvent.ACTION_DOWN, (left + right) / 2F, (top + bottom) / 2F, 0
    )
    dispatchTouchEvent(e)
    e.action = MotionEvent.ACTION_CANCEL
    dispatchTouchEvent(e)
    e.recycle()
}

fun <T: Number> T.constrains(min: T, max: T): T = when {
    this.toDouble() < min.toDouble() -> min
    this.toDouble() > max.toDouble() -> max
    else -> this
}