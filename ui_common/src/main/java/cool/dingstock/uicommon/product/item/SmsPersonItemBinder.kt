package cool.dingstock.uicommon.product.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.app.hubert.guide.util.LogUtil
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.entity.bean.calendar.SmsPersonEntity
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.uicommon.databinding.SmsPersonItemLayoutBinding


/**
 * 类名：SmsPersionItemBinder
 * 包名：cool.dingstock.calendar.item
 * 创建时间：2021/7/2 11:46 上午
 * 创建人： WhenYoung
 * 描述：
 **/
class SmsPersonItemBinder() : DcBaseItemBinder<SmsPersonEntity, SmsPersonVH>() {

    override fun onConvert(holder: SmsPersonVH, data: SmsPersonEntity) {
        holder.viewBing.nameTv.text = data.name
        holder.viewBing.nameTv.isSelected = data.selected
        holder.viewBing.triangleIv.hide(!data.selected)
        if (holder.layoutPosition > 0 && LoginUtils.getCurrentUser()?.isVip() != true) {
            holder.viewBing.vipTag.hide(false)
        } else {
            holder.viewBing.vipTag.hide(true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsPersonVH {
        val vb = SmsPersonItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return SmsPersonVH(vb)
    }
}

class SmsPersonVH(val viewBing: SmsPersonItemLayoutBinding) : BaseViewHolder(viewBing.root) {
}