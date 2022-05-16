package cool.dingstock.post.view

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.LinearLayout
import cool.dingstock.appbase.ext.azDp
import cool.dingstock.lib_base.util.Logger
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


/**
 * 类名：EnableTouchMoreLayout
 * 包名：cool.dingstock.post.view
 * 创建时间：2021/9/29 10:13 上午
 * 创建人： WhenYoung
 * 描述：
 **/
class EnableTouchMoreLayout(context: Context, att: AttributeSet) : LinearLayout(context, att) {
    var leftContent: View? = null
    var rightContent: View? = null
    var downX = 0f
    var beginChangeX: Float? = null

    var leftStartX = 0f
    var rightStartX = 0f
    var isTouchRightShow = false
    val maxWidth = 40.azDp
    private var onShowedRightTriggerFun: (() -> Unit)? = null

    private var onRotateListener: ((angleTo: Float) -> Unit)? = null
    private var onTextListener: ((isShow: Boolean) -> Unit)? = null

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        var enableScroll = true
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.x
                leftStartX = leftContent?.x ?: 0f
                rightStartX = rightContent?.x ?: 0f
                beginChangeX = null
                return super.dispatchTouchEvent(ev)
            }
            MotionEvent.ACTION_MOVE -> {
                if (abs(ev.x - downX) > ViewConfiguration.get(context).scaledTouchSlop) {
                    val direction = if (ev.x - downX > 0) -1 else 1
                    enableScroll = leftContent?.canScrollHorizontally(direction) == true
                    if (!enableScroll) {
                        if (beginChangeX == null) {
                            beginChangeX = ev.x
                        }
                    }
                }
                //控制 箭头以及查看全部状态
                if (abs((rightContent?.x ?: 0f) - rightStartX) > maxWidth / 2) {
                    //显示 释放跳转
                    onTextListener?.invoke(true)
                } else {
                    //显示 查看全部
                    onTextListener?.invoke(false)
                }
                val distanceX = abs((rightContent?.x ?: 0f) - rightStartX)
                val start = maxWidth / 2 - 10.azDp
                val end = maxWidth / 2 + 10.azDp
                val angle = min(end - start, max(0f, (distanceX - start))) / (end - start) * 180f
                onRotateListener?.invoke(angle)
            }
            MotionEvent.ACTION_UP -> {
                onRotateListener?.invoke(0f)
                if (abs((rightContent?.x ?: 0f) - rightStartX) > maxWidth / 2) {
                    onShowedRightTriggerFun?.invoke()
                }
                fun setAni(view: View, targetX: Float) {
                    view.animate()?.apply {
                        setDuration(100)
                        translationX(targetX)
                        setListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator?) {
                            }

                            override fun onAnimationEnd(animation: Animator?) {
                                view.x = targetX
                            }

                            override fun onAnimationCancel(animation: Animator?) {
                            }

                            override fun onAnimationRepeat(animation: Animator?) {
                            }

                        })
                    }?.start()
                }
                leftContent?.let {
                    setAni(it, leftStartX)
                }
                rightContent?.let {
                    setAni(it, rightStartX)
                }
            }
        }
        if (!enableScroll) {
            //这里执行拖动问题,不能往右边滑动了，开始拖动"rightContent"出来
            if (leftContent?.canScrollHorizontally(1) != true) {
                isTouchRightShow = true
                val offset = max(ev.x - (beginChangeX ?: 0f), (-maxWidth))
                if (offset < 0) {
                    leftContent?.x = leftStartX + offset
                    rightContent?.x = rightStartX + offset
                }
            }
            return true
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        return true
    }

    fun setOnShowedRightTrigger(f: () -> Unit) {
        onShowedRightTriggerFun = f
    }

    fun setOnRotateListener(f: ((angleTo: Float) -> Unit)) {
        onRotateListener = f
    }

    fun setOnTextListener(f: (isShow: Boolean) -> Unit) {
        onTextListener = f
    }
}



