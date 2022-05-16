package cool.dingstock.home

import android.view.View
import android.view.ViewTreeObserver
import cool.dingstock.lib_base.util.Logger


/**
 * 类名：ViewTouchBindHelper
 * 包名：cool.dingstock.home
 * 创建时间：2021/8/9 9:48 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class ViewTouchBindHelper {

    var changeView: View? = null
    var targetView: View? = null
    lateinit var getChangeView: () -> View?
    lateinit var getTargetView: () -> View?

    var isAddedListener = false
    var isEnd = false
    val listener by lazy {
        ViewTreeObserver.OnDrawListener {
            if (isEnd) {
                return@OnDrawListener
            }
            if (changeView == null) {
                changeView = getChangeView()
            }
            if (targetView == null) {
                targetView = getTargetView()
            }

            val locationArr = IntArray(2)
            changeView?.getLocationInWindow(locationArr)
            val lar = IntArray(2)
            (targetView?.parent as? View)?.getLocationInWindow(lar)

            targetView?.x =
                locationArr[0].toFloat() - lar[0]
            targetView?.y =
                locationArr[1].toFloat() - lar[1]

        }
    }


    fun bind(getChangeView: () -> View?, getTargetView: () -> View?) {
        this.getChangeView = getChangeView
        this.getTargetView = getTargetView
        if (changeView == null) {
            changeView = getChangeView()
        }
        if (targetView == null) {
            targetView = getTargetView()
        }
        if (targetView != null) {
            isEnd = false
            addListener()
        }
    }

    fun addListener() {
        if (!isAddedListener) {
            isAddedListener = true
            targetView?.viewTreeObserver?.addOnDrawListener(listener)
        }
    }


    fun onStart() {
        if (isEnd == true) {
            isEnd = false
            addListener()
        }

    }

    fun onStop() {
        if (isEnd == false) {
            isEnd = true
            isAddedListener = false
            targetView?.viewTreeObserver?.removeOnDrawListener(listener)
        }
    }

    fun onDestroy() {
        isAddedListener = false
        targetView?.viewTreeObserver?.removeOnDrawListener(listener)
        targetView = null
        changeView = null
    }


}