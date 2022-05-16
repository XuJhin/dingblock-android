package cool.dingstock.tide.item

import android.view.LayoutInflater
import android.view.ViewGroup
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.entity.bean.tide.TideCompanyFilterEntity
import cool.dingstock.tide.databinding.TideFilterItemLayoutBinding


/**
* 类名：TideFilterItemBinder
* 包名：cool.dingstock.tide.item
* 创建时间：2021/7/20 5:32 下午
* 创建人： WhenYoung
* 描述：
**/
class TideFilterItemBinder : BaseViewBindingItemBinder<TideCompanyFilterEntity, TideFilterItemLayoutBinding>(){

    override fun provideViewBinding(parent: ViewGroup, viewType: Int): TideFilterItemLayoutBinding {
        return TideFilterItemLayoutBinding.inflate(LayoutInflater.from(context),parent,false)
    }

    override fun onConvert(vb: TideFilterItemLayoutBinding, data: TideCompanyFilterEntity) {
        vb.nameTv.text = data.company
        vb.nameTv.isSelected = data.isSelected
    }

}

