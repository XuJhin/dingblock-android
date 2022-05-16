package cool.dingstock.appbase.dialog.remind

import android.view.LayoutInflater
import android.view.ViewGroup
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.databinding.ItemRemindWayBinding
import cool.dingstock.appbase.entity.bean.home.bp.MonitorRemindMsgEntity
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.util.setOnShakeClickListener

class MonitorRemindWayItemBinder :
    BaseViewBindingItemBinder<MonitorRemindMsgEntity, ItemRemindWayBinding>() {

    private var mClickListener: ((pos: Int) -> Unit)? = null

    fun setClickListener(clickListener: ((pos: Int) -> Unit)) {
        mClickListener = clickListener
    }

    override fun provideViewBinding(parent: ViewGroup, viewType: Int): ItemRemindWayBinding {
        return ItemRemindWayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onConvert(vb: ItemRemindWayBinding, data: MonitorRemindMsgEntity) {
        vb.apply {
            ivImg.load(data.icon ?: 0)
            tvMsg.text = data.msg
            ivIsDefault.isSelected = data.isSelected

            root.setOnShakeClickListener {
                mClickListener?.invoke(data.pos)
            }
        }
    }
}