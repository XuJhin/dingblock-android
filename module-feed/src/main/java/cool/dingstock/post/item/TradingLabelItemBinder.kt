package cool.dingstock.post.item

import android.view.LayoutInflater
import android.view.ViewGroup
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.post.databinding.TradingLabelItemLayoutBinding


/**
 * 类名：TradingPriceSizeItemBinder
 * 包名：cool.dingstock.post.item
 * 创建时间：2021/11/13 10:04 上午
 * 创建人： WhenYoung
 * 描述：
 **/
class TradingLabelItemBinder :
    BaseViewBindingItemBinder<String, TradingLabelItemLayoutBinding>() {
    override fun provideViewBinding(
        parent: ViewGroup,
        viewType: Int
    ): TradingLabelItemLayoutBinding {
        return TradingLabelItemLayoutBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
    }

    override fun onConvert(vb: TradingLabelItemLayoutBinding, data: String) {
        vb.tv.text = data
    }
}