package cool.dingstock.calendar.widget

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import cool.dingstock.appbase.ext.azDp
import cool.dingstock.appbase.util.EditTextUtils
import cool.dingstock.calendar.R
import cool.dingstock.lib_base.util.SizeUtils


/**
 * 类名：Sms
 * 包名：cool.dingstock.calendar.widget
 * 创建时间：2021/7/2 5:07 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class SmsEditHintBubblesView(context: Context) : PopupWindow(context) {

    lateinit var contentTv: TextView
    lateinit var rootView: View
    var parent:View?= null
    var showV:View? = null
    var offSet:Int = 0

    init {
        rootView = LayoutInflater.from(context)
            .inflate(R.layout.sms_edit_hint_bubbles_view_layout, null, false)
        contentView = rootView
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        setBackgroundDrawable(null)
        contentTv = rootView.findViewById(R.id.content_tv)
    }


    fun getWindowHeight(): Int {
        return 20.azDp.toInt() + EditTextUtils.getTotalLineHeight(contentTv,contentTv.lineHeight.toFloat(),contentTv.text.toString(),SizeUtils.getWidth()-(37*2).azDp.toInt())
    }


    fun showTopWithView(parent:View,view: View, offSet: Int) {
        this.parent = parent
        this.showV = view
        this.offSet = offSet
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        showAtLocation(parent,Gravity.NO_GRAVITY,0,location[1] - getWindowHeight()+offSet)
    }

    fun refreshLocation(){
        showV?.postDelayed(Runnable {
            parent?.let { showV?.let {
                showTopWithView(parent!!,showV!!,offSet)
            } }
        },100)
    }




}