package cool.dingstock.home.adapter.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.entity.bean.home.fashion.FashionEntity
import cool.dingstock.home.databinding.ItemAllFashionBrandBinding

class AllFashionBrandItem: BaseViewBindingItemBinder<FashionEntity, ItemAllFashionBrandBinding>() {
    override fun provideViewBinding(parent: ViewGroup, viewType: Int): ItemAllFashionBrandBinding {
        return ItemAllFashionBrandBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onConvert(vb: ItemAllFashionBrandBinding, data: FashionEntity) {
        Glide.with(vb.ivBrandLogo.context)
            .load(data.imageUrl)
            .into(vb.ivBrandLogo)
        vb.tvBrandName.text = data.name
    }
}