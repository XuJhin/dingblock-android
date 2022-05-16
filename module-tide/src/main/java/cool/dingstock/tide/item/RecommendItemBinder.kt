package cool.dingstock.tide.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.entity.bean.tide.GoodItem
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.lib_base.util.TimeUtils
import cool.dingstock.tide.databinding.LayoutRecommendChaowanItemBinding
import cool.dingstock.tide.databinding.LayoutRecommendItemBinding

/**
 * @author wangjiang
 *  CreateAt Time 2021/8/30  11:11
 */
class RecommendItemBinder : BaseViewBindingItemBinder<GoodItem, LayoutRecommendChaowanItemBinding>() {
    interface OnClickActionListener {
        fun onItemClick(id: String)
    }

    var onClickActionListener: OnClickActionListener? = null

    override fun provideViewBinding(parent: ViewGroup, viewType: Int): LayoutRecommendChaowanItemBinding {
        return LayoutRecommendChaowanItemBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onConvert(vb: LayoutRecommendChaowanItemBinding, data: GoodItem) {
        vb.iv.load(data.imageUrl)
        vb.iv.setOnShakeClickListener {
            onClickActionListener?.onItemClick(data.id)
        }
        when {
            data.saleDateStr != null -> {
                vb.saleTime.isVisible = true
                vb.saleTime.text = data.saleDateStr
            }
            data.saleDate != null -> {
                vb.saleTime.isVisible = true
                vb.saleTime.text = TimeUtils.formatTimestampM(data.saleDate!!)
            }
            else -> {
                vb.saleTime.isVisible = false
            }
        }
    }
}