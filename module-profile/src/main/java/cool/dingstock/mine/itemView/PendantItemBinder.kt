package cool.dingstock.mine.itemView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.entity.bean.mine.PendantDetail
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.ext.loadWithCache
import cool.dingstock.mine.databinding.ItemPendantBinding

class PendantItemBinder: BaseViewBindingItemBinder<PendantDetail, ItemPendantBinding>() {
    override fun provideViewBinding(parent: ViewGroup, viewType: Int): ItemPendantBinding {
        return ItemPendantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onConvert(vb: ItemPendantBinding, data: PendantDetail) {
        with(vb) {
            root.isSelected = data.selected
            selectedIcon.isVisible = data.selected
            pendantIv.loadWithCache(data.imageUrl)
            data.cornerIcon?.let {
                Glide.with(cornerIv)
                    .load(it)
                    .skipMemoryCache(false)
                    .dontAnimate()
                    .into(cornerIv)
            }
            pendantNameTv.text = data.name
            defaultIv.post {
                defaultIv.layoutParams = defaultIv.layoutParams.apply {
                    width = (pendantIv.measuredWidth * 0.75).toInt()
                    height = (pendantIv.measuredHeight * 0.75).toInt()
                }
            }
        }
    }
}