package cool.dingstock.calendar.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.entity.bean.calendar.SmsParameterOptionsEntity
import cool.dingstock.uicommon.databinding.SmsParameterOptionsItemLayoutBinding


/**
 * 类名：SmsParameterSelecteableItemBinder
 * 包名：cool.dingstock.calendar.item
 * 创建时间：2021/7/6 10:40 上午
 * 创建人： WhenYoung
 * 描述：
 **/
class SmsParameterOptionsItemBinder : DcBaseItemBinder<SmsParameterOptionsEntity<String>, SmsParameterOptionsVH>() {

    override fun onConvert(holder: SmsParameterOptionsVH, data: SmsParameterOptionsEntity<String>) {
        holder.viewBinding.tv.text = data.data
        holder.viewBinding.tv.isSelected = data.selected
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsParameterOptionsVH {
        val binding = SmsParameterOptionsItemLayoutBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return SmsParameterOptionsVH(binding)
    }

}

class SmsParameterOptionsVH(val viewBinding: SmsParameterOptionsItemLayoutBinding) :
    BaseViewHolder(viewBinding.root) {

}