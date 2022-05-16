package net.dingblock.mobile.activity.index.drawer.mine.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.chad.library.adapter.base.binder.BaseItemBinder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.entity.bean.drawer.MineDrawerItemEntity
import net.dingblock.mobile.R

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/6/17 16:33
 * @Version:         1.1.0
 * @Description:
 */
class MineDrawerItemBinder : BaseItemBinder<MineDrawerItemEntity, MineDrawerViewHolder>() {
    override fun convert(holder: MineDrawerViewHolder, data: MineDrawerItemEntity) {
        holder.tvItemName.text = data.name
        val resources = holder.itemView.context.resources
        val imageId = resources.getIdentifier(data.resId, "drawable", context.packageName)
        holder.ivItemIcon.setImageResource(imageId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MineDrawerViewHolder {
        return MineDrawerViewHolder.create(parent)
    }
}

class MineDrawerViewHolder(itemView: View) : BaseViewHolder(itemView) {
    val tvItemName: TextView = itemView.findViewById(R.id.tv_drawer_mine)
    val ivItemIcon: AppCompatImageView = itemView.findViewById(R.id.iv_drawer_mine)

    companion object {
        fun create(viewGroup: ViewGroup): MineDrawerViewHolder {
            return MineDrawerViewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.item_mine_drawer, viewGroup, false)
            )
        }
    }
}