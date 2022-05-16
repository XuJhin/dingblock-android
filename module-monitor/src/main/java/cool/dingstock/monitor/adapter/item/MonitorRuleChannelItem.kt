package cool.dingstock.monitor.adapter.item

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.entity.bean.monitor.ChannelBean
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.visual
import cool.dingstock.monitor.databinding.ItemRuleChannelBinding

class MonitorRuleChannelItem: BaseViewBindingItemBinder<ChannelBean, ItemRuleChannelBinding>() {
    override fun provideViewBinding(parent: ViewGroup, viewType: Int): ItemRuleChannelBinding {
        return ItemRuleChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onConvert(vb: ItemRuleChannelBinding, data: ChannelBean) {
        vb.channelNameTv.text = data.name
        Glide.with(vb.channelIv).load(data.iconUrl).into(vb.channelIv)
        if (data.keywordsSetting == 1) {
            vb.keywordRuleState.setTextColor(Color.parseColor("#438FFF"))
            vb.keywordRuleState.text = "已设置"
        } else {
            vb.keywordRuleState.setTextColor(Color.parseColor("#6A7181"))
            vb.keywordRuleState.text = "未设置"
        }
        when (data.sizesSetting) {
            -1 -> {
                vb.sizeRule.hide()
                vb.sizeRuleState.hide()
            }
            0 -> {
                vb.sizeRule.visual()
                vb.sizeRuleState.visual()
                vb.sizeRule.setTextColor(Color.parseColor("#6A7181"))
                vb.sizeRuleState.setTextColor(Color.parseColor("#6A7181"))
                vb.sizeRuleState.text = "未设置"
            }
            1 -> {
                vb.sizeRule.visual()
                vb.sizeRuleState.visual()
                vb.sizeRule.setTextColor(Color.parseColor("#6A7181"))
                vb.sizeRuleState.setTextColor(Color.parseColor("#438FFF"))
                vb.sizeRuleState.text = "已设置"
            }
        }
    }
}