package cool.dingstock.setting.ui.shield.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.binder.BaseItemBinder
import cool.dingstock.appbase.entity.bean.mine.AccountInfoBriefEntity
import cool.dingstock.appbase.ext.loadAvatar
import cool.dingstock.uicommon.setting.R

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/11/2 11:00
 * @Version:         1.7.2
 * @Description:
 */
class ShieldItemBinder : BaseItemBinder<AccountInfoBriefEntity, ShieldViewHolder>() {
	var listener: ShieldListener? = null
	override fun convert(holder: ShieldViewHolder, data: AccountInfoBriefEntity) {
		holder.rivAccountAvatar.loadAvatar(data.avatarUrl)
		holder.tvNickname.text = data.nickName
		holder.ivTag.visibility = if (data.isVerified) {
			View.VISIBLE
		} else {
			View.INVISIBLE
		}
		holder.tvShieldState.isSelected = true
		holder.tvShieldState.setOnClickListener {
			listener?.onShieldListener(data = data, position = holder.adapterPosition)
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShieldViewHolder {
		return ShieldViewHolder(LayoutInflater
				.from(parent.context)
				.inflate(R.layout.item_shield_accont, parent, false))
	}
}

interface ShieldListener {
	fun onShieldListener(data: AccountInfoBriefEntity, position: Int)
}