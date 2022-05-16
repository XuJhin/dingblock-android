package cool.dingstock.calendar.item

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.entity.bean.calendar.SmsPersonInputEntity
import cool.dingstock.uicommon.databinding.SmsPersonInputItemLayoutBinding


/**
 * 类名：SmsPersonInputItemLayout
 * 包名：cool.dingstock.calendar.item
 * 创建时间：2021/7/5 10:53 上午
 * 创建人： WhenYoung
 * 描述：
 **/
class SmsPersonInputItemBinder : DcBaseItemBinder<SmsPersonInputEntity, SmsPersonInputVH>() {

    var listener: OnFocusChangeListener? = null

    override fun onConvert(holder: SmsPersonInputVH, data: SmsPersonInputEntity) {
        if(data.require){
            holder.viewBinding.titleTv.text = data.fieldName + "（必填）"
        }else{
            holder.viewBinding.titleTv.text = data.fieldName
        }
        holder.viewBinding.edit.setOnFocusChangeListener { v, hasFocus ->
            listener?.onChange(
                hasFocus,
                holder.viewBinding.edit,
                data
            )
        }
        holder.viewBinding.edit.setText(data.value)
        holder.viewBinding.edit.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                data.value = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsPersonInputVH {
        val binding =
            SmsPersonInputItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return SmsPersonInputVH(binding)
    }


    interface OnFocusChangeListener {
        fun onChange(hasFocus: Boolean, editView: EditText, data: SmsPersonInputEntity)
    }
}


class SmsPersonInputVH(val viewBinding: SmsPersonInputItemLayoutBinding) :
    BaseViewHolder(viewBinding.root) {

}