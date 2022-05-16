package cool.dingstock.appbase.adapter.itembinder

import android.view.LayoutInflater
import android.view.ViewGroup
import cool.dingstock.appbase.databinding.ItemSkuBinding
import cool.dingstock.appbase.entity.bean.sku.Size

class SizeItemBinder(var sizeSupport: Boolean = true): BaseViewBindingItemBinder<Size, ItemSkuBinding>() {
    override fun provideViewBinding(parent: ViewGroup, viewType: Int): ItemSkuBinding {
        return ItemSkuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onConvert(vb: ItemSkuBinding, data: Size) {
        vb.name.text = data.size
        vb.name.isSelected = data.selected
        if (!sizeSupport) {
            vb.name.isEnabled = false
            vb.root.isEnabled = false
        }
    }
}