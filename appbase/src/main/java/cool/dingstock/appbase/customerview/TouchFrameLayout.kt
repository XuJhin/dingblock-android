package cool.dingstock.appbase.customerview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/18  10:33
 */
class TouchFrameLayout(context: Context,attributeSet: AttributeSet) : FrameLayout(context,attributeSet){

    var onDisTouchListener:OnTouchListener? =null

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        onDisTouchListener?.onTouch(this,ev)
        return super.dispatchTouchEvent(ev)
    }


}