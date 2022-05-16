package cool.dingstock.appbase.adapter.itembinder

import android.graphics.Color
import cool.dingstock.appbase.R
import cool.dingstock.appbase.databinding.CircleItemDynamicEditAddBinding
import cool.dingstock.appbase.util.setSvgColor
import cool.dingstock.appbase.widget.recyclerview.item.BaseItem
import cool.dingstock.appbase.widget.recyclerview.item.BaseViewHolder

class CircleDynamicEditAddItem(data: String) :
    BaseItem<String, CircleItemDynamicEditAddBinding>(data) {
    override fun getViewType(): Int {
        return VIEW_TYPE
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.circle_item_dynamic_edit_add
    }

    override fun onReleaseViews(
        holder: BaseViewHolder,
        sectionKey: Int,
        sectionViewPosition: Int
    ) {
    }

    override fun onSetViewsData(holder: BaseViewHolder, sectionKey: Int, sectionViewPosition: Int) {
        viewBinding!!.addIv.setSvgColor(R.drawable.monitor_svg_add, Color.parseColor("#C0C2CE"))
    }

    companion object {
        const val VIEW_TYPE = 100
    }
}