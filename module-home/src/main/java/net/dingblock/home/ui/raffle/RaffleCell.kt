package net.dingblock.home.ui.raffle

import android.view.ViewGroup
import com.chad.library.adapter.base.binder.BaseItemBinder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.home.databinding.BlockItemRaffleBinding

//class RaffleCell : BaseItemBinder<RaffleEntity,RaffleViewHolder>() {
//    override fun convert(holder: RaffleViewHolder, data: RaffleEntity) {
//
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaffleViewHolder {
//    }
//}

class RaffleViewHolder(binder: BlockItemRaffleBinding):BaseViewHolder(binder.root){

}
data class RaffleEntity(val name: String)