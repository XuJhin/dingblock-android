package cool.dingstock.uicommon.widget

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import cool.dingstock.uicommon.R


/**
 * 类名：MonthTabViewHolder
 * 包名：cool.dingstock.uicommon.widget
 * 创建时间：2021/7/20 3:39 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class MonthTabViewHolder(val context:Context,tabView: View) {
    var tabTitle: TextView? = null
    var tabMonth: TextView? = null
    var layout: View? = null

    init {
        tabTitle = tabView.findViewById(R.id.home_sneaker_tab_text)
        layout = tabView.findViewById(R.id.month_layer)
        tabMonth = tabView.findViewById(R.id.tv_month)
    }

    fun setSelect(isSelected: Boolean) {
        tabMonth?.setTextColor(ContextCompat.getColor(context,if(isSelected) R.color.white else R.color.calendar_month_un_sel_txt_color))
        tabTitle?.setTextColor(ContextCompat.getColor(context,if(isSelected) R.color.white else R.color.calendar_month_un_sel_txt_color))
        layout?.isSelected = isSelected
    }

}