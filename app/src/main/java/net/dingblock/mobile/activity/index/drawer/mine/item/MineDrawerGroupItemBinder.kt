package net.dingblock.mobile.activity.index.drawer.mine.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.binder.BaseItemBinder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.entity.bean.drawer.MineDrawerEntity
import net.dingblock.mobile.R

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/6/17 16:27
 * @Version:         1.1.0
 * @Description:
 */
class MineDrawerGroupItemBinder : BaseItemBinder<MineDrawerEntity, MineDrawerGroupViewHolder>() {
    override fun convert(holder: MineDrawerGroupViewHolder, data: MineDrawerEntity) {
        holder.tvGroupName.text = data.head
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MineDrawerGroupViewHolder {
        return MineDrawerGroupViewHolder.create(parent)
    }
}

class MineDrawerGroupViewHolder(itemView: View) : BaseViewHolder(itemView) {
    val tvGroupName: TextView = itemView.findViewById(R.id.tv_mine_drawer_group)

    companion object {
        fun create(viewGroup: ViewGroup): MineDrawerGroupViewHolder {
            return MineDrawerGroupViewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.item_mine_drawer_group, viewGroup, false)
            )
        }
    }
}