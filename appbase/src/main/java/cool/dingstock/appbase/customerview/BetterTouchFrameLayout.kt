package cool.dingstock.appbase.customerview

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import cool.dingstock.lib_base.util.SizeUtils
import kotlin.math.abs

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/17  15:44
 */
class BetterTouchFrameLayout(context: Context,attributeSet: AttributeSet) : FrameLayout(context,attributeSet){
    private var startX = 0.0f
    private var startY = 0.0f

    private var orientation = ORIENTATION.UNKNOWN
    private val needOrientationList = arrayListOf<RectF>()

    private var orientationFactorH = 20f
    private var orientationFactorV = 20f


    enum class ORIENTATION{
        HORIZONTAL,VERTICAL,UNKNOWN
    }

    /**
     *
     * 是否限制 只在一个方向滑动
     *
     * */
    var enableAstrictOrientation = true

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            when (ev.action){
                MotionEvent.ACTION_DOWN-> {
                    enableAstrictOrientation = checkEnableAstrictOrientation(it)
                    startX = it.x
                    startY = it.y
                }
                MotionEvent.ACTION_MOVE-> {
                    if(!enableAstrictOrientation){
                        return super.dispatchTouchEvent(ev)
                    }
                    if(convertEvent(it)){
                        return super.dispatchTouchEvent(ev)
                    }
//                    Log.e("dispatchTouchEvent", "check-----------------------------------------------:")
                    if(abs(it.x - startX)>SizeUtils.dp2px(orientationFactorH)){
                        orientation = ORIENTATION.HORIZONTAL
                        startX = ev.x
                        startY = ev.y
                    }else if(abs(it.y - startY) >SizeUtils.dp2px(orientationFactorV)){
                        orientation = ORIENTATION.VERTICAL
                        startX = ev.x
                        startY = ev.y
                    }

                }
                MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL-> {
                    orientation = ORIENTATION.UNKNOWN
                    enableAstrictOrientation = true
                }
                else ->{ }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun convertEvent(ev: MotionEvent): Boolean {
        return when (orientation) {
            ORIENTATION.HORIZONTAL -> {
                ev.setLocation(ev.x, startY)
                Log.e("dispatchTouchEvent", "HORIZONTAL:" + ev.x + "," + ev.y)
                true
            }
            ORIENTATION.VERTICAL -> {
                ev.setLocation(startX, ev.y)
                Log.e("dispatchTouchEvent", "VERTICAL:" + ev.x + "," + ev.y)
                true
            }
            else -> {
                Log.e("dispatchTouchEvent", "UN_KNOW:" + ev.x + "," + ev.y)
                false
            }
        }
    }


    /**
     * 判断是否能限制 只允许一个方向滑动
     * */
    private fun checkEnableAstrictOrientation(ev:MotionEvent):Boolean{
        for(rect in needOrientationList){
            if(rect.contains(ev.x,ev.y)){
                return true
            }
        }
        return false
    }


    fun addAstrictRect(rectF: RectF){
        needOrientationList.add(rectF)
    }

    fun haveAddAstrictRected():Boolean{
        return needOrientationList.size>0
    }

    fun cleanUnAstrictRects(){
        needOrientationList.clear()
    }

}