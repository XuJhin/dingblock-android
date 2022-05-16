package cool.dingstock.appbase.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.scwang.smart.refresh.layout.SmartRefreshLayout


/**
 * 类名：MyTouchSmartRefreshLayout
 * 包名：cool.dingstock.appbase.widget
 * 创建时间：2021/8/10 3:13 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class MyTouchSmartRefreshLayout(context: Context?, attrs: AttributeSet?) :
    SmartRefreshLayout(context, attrs) {

    var isNoTouch = false

    override fun dispatchTouchEvent(e: MotionEvent?): Boolean {
        if(isNoTouch){
            return false
        }
        return super.dispatchTouchEvent(e)
    }


}