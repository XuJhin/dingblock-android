package cool.dingstock.monitor.ui.manager.itemView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.binder.BaseItemBinder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.monitor.R

class MenuHeaderItemBinder : BaseItemBinder<String, MenuHeaderItemBinder.HeaderViewHolder>() {
	inner class HeaderViewHolder(itemView: View) : BaseViewHolder(itemView)

	override fun convert(holder: HeaderViewHolder, data: String) {
		holder.itemView.findViewById<TextView>(R.id.tv_monitor_menu_header).text = "- $data -"
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
		return HeaderViewHolder(LayoutInflater.from(context)
				.inflate(R.layout.item_monitor_menu_header, parent, false)
		)
	}
}