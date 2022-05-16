package cool.dingstock.calendar.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.entity.bean.calendar.PriceInfoListEntity
import cool.dingstock.uicommon.databinding.PriceComparisonItemLayoutBinding


/**
 * 类名：PriceComparisonItemBinder
 * 包名：cool.dingstock.calendar.item
 * 创建时间：2021/7/13 11:39 上午
 * 创建人： WhenYoung
 * 描述：
 **/
class PriceComparisonItemBinder : DcBaseItemBinder<PriceInfoListEntity, PriceComparisonVH>() {
    override fun onConvert(holder: PriceComparisonVH, data: PriceInfoListEntity) {
        holder.viewBinding.sizeTv.text = data.size
        when {
            data.priceList.size == 1 -> {
                if (data.priceList[0].key == "dw") {
                    holder.viewBinding.priceTv1.text = data.priceList[0].price
                    holder.viewBinding.priceTv2.text = "-"
                } else if (data.priceList[0].key == "n") {
                    holder.viewBinding.priceTv1.text = "-"
                    holder.viewBinding.priceTv2.text = data.priceList[0].price
                }
            }
            data.priceList.size >= 2 -> {
                if (data.priceList[0].price == "") {
                    holder.viewBinding.priceTv1.text = "-"
                } else {
                    holder.viewBinding.priceTv1.text = data.priceList[0].price
                }
                if (data.priceList[1].price == "") {
                    holder.viewBinding.priceTv2.text = "-"
                } else {
                    holder.viewBinding.priceTv2.text = data.priceList[1].price
                }
            }
            else -> {
                holder.viewBinding.priceTv1.text = "-"
                holder.viewBinding.priceTv2.text = "-"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceComparisonVH {
        return PriceComparisonVH(
                PriceComparisonItemLayoutBinding.inflate(
                        LayoutInflater.from(
                                context
                        ), parent, false
                )
        )
    }
}

class PriceComparisonVH(val viewBinding: PriceComparisonItemLayoutBinding) :
        BaseViewHolder(viewBinding.root) {

}