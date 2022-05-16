package cool.dingstock.post.item

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import cool.dingstock.appbase.SpanUtils
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.entity.bean.circle.TradingSizePriceEntity
import cool.dingstock.post.databinding.TradingPriceSizeItemLayoutBinding


/**
 * 类名：TradingPriceSizeItemBinder
 * 包名：cool.dingstock.post.item
 * 创建时间：2021/11/13 10:04 上午
 * 创建人： WhenYoung
 * 描述：
 **/
class TradingPriceSizeItemBinder :
    BaseViewBindingItemBinder<TradingSizePriceEntity, TradingPriceSizeItemLayoutBinding>() {
    override fun provideViewBinding(
        parent: ViewGroup,
        viewType: Int
    ): TradingPriceSizeItemLayoutBinding {
        return TradingPriceSizeItemLayoutBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
    }

    override fun onConvert(vb: TradingPriceSizeItemLayoutBinding, data: TradingSizePriceEntity) {
        if (data.price.isNullOrEmpty()) {
            SpanUtils.with(vb.priceTv).append("私聊").setForegroundColor(Color.parseColor("#438FFF")).create()
        } else {
            SpanUtils.with(vb.priceTv).append(data.price?:"")
                .setForegroundColor(Color.parseColor("#438FFF"))
                .append(" 元").setForegroundColor(Color.parseColor("#3D3E45")).create()
        }
        vb.sizeTv.text = data.size
    }
}