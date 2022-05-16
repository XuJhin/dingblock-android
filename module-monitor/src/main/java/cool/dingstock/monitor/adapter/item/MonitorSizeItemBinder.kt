package cool.dingstock.monitor.adapter.item

import android.view.LayoutInflater
import android.view.ViewGroup
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.entity.bean.sku.Size
import cool.dingstock.monitor.databinding.ItemSkuSizeBinding

class MonitorSizeItemBinder(var sizeSupport: Boolean = true): BaseViewBindingItemBinder<Size, ItemSkuSizeBinding>() {
    override fun provideViewBinding(parent: ViewGroup, viewType: Int): ItemSkuSizeBinding {
        return ItemSkuSizeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onConvert(vb: ItemSkuSizeBinding, data: Size) {
        vb.name.text = data.size
        vb.name.isSelected = data.selected
        if (!sizeSupport) {
            vb.name.isEnabled = false
            vb.root.isEnabled = false
        }
    }
}