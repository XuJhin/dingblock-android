package cool.dingstock.calendar.item

import android.annotation.SuppressLint
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.entity.bean.calendar.SmsFiledEntity
import cool.dingstock.appbase.ext.azDp
import cool.dingstock.appbase.ext.hide
import cool.dingstock.uicommon.R
import cool.dingstock.uicommon.databinding.SmsPersonFiledItemLayoutBinding


/**
 * 类名：SmsPersonFiledItemBinder
 * 包名：cool.dingstock.calendar.item
 * 创建时间：2021/7/5 4:42 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class SmsPersonFiledItemBinder : DcBaseItemBinder<SmsFiledEntity, SmsPersonFiledVH>() {

    @SuppressLint("SetTextI18n")
    override fun onConvert(holder: SmsPersonFiledVH, data: SmsFiledEntity) {
        holder.itemView.hide(!data.needShow)
        if (!TextUtils.isEmpty(data.value)) {
            if (data.key == "smsRaffleName") {
                if(data.value?.length == 2 && data.homeField.needSpace){
                    data.value = "${data.value?.get(0)?:""} ${data.value?.get(1)?:""}"
                }
                holder.viewBinding.tv.text = "${data.filedName}：${data.value}"
                holder.viewBinding.tv.maxWidth = 200.azDp.toInt()
            } else {
                holder.viewBinding.tv.text = "${data.filedName}：${data.value}"
                holder.viewBinding.tv.maxWidth = Int.MAX_VALUE
            }
        } else {
            val sp = SpannableStringBuilder("${data.filedName}：未填写")
            sp.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        context,
                        R.color.color_red
                    )
                ), sp.length - 3, sp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            holder.viewBinding.tv.text = sp
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsPersonFiledVH {
        val viewBinding =
            SmsPersonFiledItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return SmsPersonFiledVH(viewBinding)
    }
}

class SmsPersonFiledVH(val viewBinding: SmsPersonFiledItemLayoutBinding) :
    BaseViewHolder(viewBinding.root) {

}