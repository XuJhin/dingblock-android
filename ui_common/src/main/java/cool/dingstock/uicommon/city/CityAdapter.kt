package cool.dingstock.uicommon.city

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cool.dingstock.appbase.entity.bean.circle.City
import cool.dingstock.uicommon.databinding.ItemCityBinding

class CityAdapter(
    var items: List<City>,
    private val onClick: (City) -> Unit
): RecyclerView.Adapter<CityAdapter.ViewHolder>() {
    inner class ViewHolder(private val vb: ItemCityBinding): RecyclerView.ViewHolder(vb.root) {
        fun bind(cityBean: City) {
            vb.nameTv.text = cityBean.name
            vb.root.setOnClickListener {
                onClick(cityBean)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vb = ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(vb)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}