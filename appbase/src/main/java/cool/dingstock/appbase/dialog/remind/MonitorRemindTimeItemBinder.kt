package cool.dingstock.appbase.dialog.remind

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.databinding.ItemRemindTimeBinding
import cool.dingstock.appbase.entity.bean.home.bp.MonitorRemindMsgEntity
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.util.setOnShakeClickListener

class MonitorRemindTimeItemBinder :
    BaseViewBindingItemBinder<MonitorRemindMsgEntity, ItemRemindTimeBinding>() {

    private var mClickListener: ((pos: Int) -> Unit)? = null

    fun setClickListener(clickListener: ((pos: Int) -> Unit)) {
        mClickListener = clickListener
    }

    override fun provideViewBinding(parent: ViewGroup, viewType: Int): ItemRemindTimeBinding {
        return ItemRemindTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onConvert(vb: ItemRemindTimeBinding, data: MonitorRemindMsgEntity) {
        vb.apply {
            iv.hide(!(data.isSelected))
            root.isSelected = data.isSelected
            tv.isSelected=data.isSelected
            if (data.isSelected) {
                tv.typeface = Typeface.DEFAULT_BOLD
            } else {
                tv.typeface = Typeface.DEFAULT
            }
            tv.text = data.msg
            root.setOnShakeClickListener {
                mClickListener?.invoke(data.pos)
            }
        }
    }
}