package cool.dingstock.monitor.ui.manager.itemView

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.binder.BaseItemBinder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.entity.bean.monitor.MonitorMenuItemEntity
import cool.dingstock.monitor.R

class MenuChildItemBinder : BaseItemBinder<MonitorMenuItemEntity, MenuChildItemBinder.ChildViewHolder>() {
	inner class ChildViewHolder(itemView: View) : BaseViewHolder(itemView) {
		val tvMenuItemName: TextView = itemView.findViewById(R.id.monitor_item_area_txt)
	}

	override fun convert(holder: ChildViewHolder, data: MonitorMenuItemEntity) {
		holder.tvMenuItemName.apply {
			text = data.name
			isSelected = data.isSelected
			if (data.isSelected) {
				typeface = Typeface.DEFAULT_BOLD
				textSize = 16f
			} else {
				typeface = Typeface.DEFAULT
				textSize = 13f
			}
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
		return ChildViewHolder(LayoutInflater.from(context)
				.inflate(R.layout.monitor_item_area, parent, false)
		)
	}
}