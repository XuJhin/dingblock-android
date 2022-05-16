package cool.dingstock.monitor.adapter.item

import android.view.LayoutInflater
import android.view.ViewGroup
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.entity.bean.monitor.MonitorLogBean
import cool.dingstock.appbase.ext.load
import cool.dingstock.lib_base.util.TimeUtils
import cool.dingstock.monitor.databinding.ItemMonitorLogBinding

/**
 * @author wj
 *  CreateAt Time 2021/10/27  11:19
 */

class MonitorLogItemBinder : BaseViewBindingItemBinder<MonitorLogBean, ItemMonitorLogBinding>() {
    override fun provideViewBinding(parent: ViewGroup, viewType: Int): ItemMonitorLogBinding {
        return ItemMonitorLogBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onConvert(vb: ItemMonitorLogBinding, data: MonitorLogBean) {
        vb.apply {
            iv.load(data.avatarUrl)
            tvTime.text = TimeUtils.formatTimestampCustom(data.createdAt, "MM/dd HH:mm")
            tvDesc.text = data.content
        }
    }
}