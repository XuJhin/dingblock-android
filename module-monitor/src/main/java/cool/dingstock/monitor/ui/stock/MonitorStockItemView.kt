package cool.dingstock.monitor.ui.stock

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cool.dingstock.appbase.adapter.multiadapter.core.ItemViewDelegate
import cool.dingstock.appbase.entity.bean.monitor.SkuEntity
import cool.dingstock.monitor.R

class MonitorStockItemView : ItemViewDelegate<SkuEntity, MonitorStockItemView.MonitorStockViewHolder>() {

    inner class MonitorStockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): MonitorStockViewHolder {
        return MonitorStockViewHolder(LayoutInflater.from(context).inflate(R.layout.item_size, parent, false))
    }

    override fun onBindViewHolder(holder: MonitorStockViewHolder, item: SkuEntity) {
        holder.itemView.findViewById<TextView>(R.id.tv_size_desc).text = item.title
        holder.itemView.findViewById<TextView>(R.id.tv_szie_amount).text = item.count.toString()
    }
}

