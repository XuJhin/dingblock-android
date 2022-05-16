package cool.dingstock.calendar.item

import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.entity.bean.calendar.SmsParameterSelectorEntity
import cool.dingstock.uicommon.R
import cool.dingstock.uicommon.databinding.ParameterSelectorItemLayoutBinding


/**
 * 类名：ParameterSelectorItemBinder
 * 包名：cool.dingstock.calendar.item
 * 创建时间：2021/7/5 7:24 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class ParameterSelectorItemBinder :
    DcBaseItemBinder<SmsParameterSelectorEntity, ParameterSelectorVH>() {

    override fun onConvert(holder: ParameterSelectorVH, data: SmsParameterSelectorEntity) {
        holder.viewBinding.title.text = data.name
        holder.viewBinding.valueTv.text = if(!TextUtils.isEmpty(data.value)) data.value else  "请选择"
        holder.viewBinding.valueTv.setTextColor(
            ContextCompat.getColor(
                context,
                if (TextUtils.isEmpty(data.value)) R.color.color_text_black4 else R.color.color_text_black1
            )
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParameterSelectorVH {
        val binding =
            ParameterSelectorItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ParameterSelectorVH(binding)
    }

}

class ParameterSelectorVH(val viewBinding: ParameterSelectorItemLayoutBinding) :
    BaseViewHolder(viewBinding.root) {

}