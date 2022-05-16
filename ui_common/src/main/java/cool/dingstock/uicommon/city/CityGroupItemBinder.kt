package cool.dingstock.uicommon.city

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.entity.bean.circle.City
import cool.dingstock.appbase.entity.bean.circle.GroupCityBean
import cool.dingstock.appbase.ext.dp
import cool.dingstock.uicommon.R
import cool.dingstock.uicommon.databinding.ItemCityGroupBinding
import io.cabriole.decorator.LinearDividerDecoration

class CityGroupItemBinder(
    private val onClick: (City) -> Unit
): BaseViewBindingItemBinder<GroupCityBean, ItemCityGroupBinding>() {
    override fun provideViewBinding(parent: ViewGroup, viewType: Int): ItemCityGroupBinding {
        return ItemCityGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onConvert(vb: ItemCityGroupBinding, data: GroupCityBean) {
        vb.titleTv.text = data.title
        with(vb.cityRv) {
            if (adapter == null) {
                adapter = CityAdapter(data.citys, onClick)
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(
                    LinearDividerDecoration.create(
                    color = ContextCompat.getColor(vb.root.context, R.color.color_gray),
                    size = 1.dp.toInt()
                ))
            } else {
                (adapter as? CityAdapter)?.let {
                    it.items = data.citys
                    it.notifyDataSetChanged()
                }
            }
        }
    }
}