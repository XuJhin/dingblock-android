package cool.dingstock.home.adapter.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.entity.bean.home.fashion.FashionEntity
import cool.dingstock.appbase.ext.textColor
import cool.dingstock.home.R
import cool.dingstock.home.databinding.ItemFashionBrandBinding

class FashionBrandItem: BaseViewBindingItemBinder<FashionEntity, ItemFashionBrandBinding>() {
    override fun provideViewBinding(parent: ViewGroup, viewType: Int): ItemFashionBrandBinding {
        return ItemFashionBrandBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onConvert(vb: ItemFashionBrandBinding, data: FashionEntity) {
        Glide.with(vb.ivBrandLogo.context)
            .load(data.imageUrl)
            .into(vb.ivBrandLogo)
        vb.tvBrandName.text = data.name
        vb.tvBrandName.textSize = if (data.selected) 12f else 11f
        vb.tvBrandName.textColor(if (data.selected) R.color.color_text_absolutely_white else R.color.color_text_white_a50)
    }
}