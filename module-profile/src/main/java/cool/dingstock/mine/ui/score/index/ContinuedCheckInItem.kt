package cool.dingstock.mine.ui.score.index

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.entity.bean.score.SignCalendarEntity
import cool.dingstock.appbase.entity.bean.score.SignCalendarReward
import cool.dingstock.appbase.ext.azDp
import cool.dingstock.appbase.ext.textColor
import cool.dingstock.appbase.ext.visual
import cool.dingstock.lib_base.util.ScreenUtils
import cool.dingstock.mine.R
import cool.dingstock.mine.ui.score.index.ContinuedCheckInItem.ContinuedCheckInVH

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/5/25 17:14
 * @Version:         1.1.0
 * @Description:
 */
class ContinuedCheckInItem : DcBaseItemBinder<SignCalendarEntity, ContinuedCheckInVH>() {
	override fun onConvert(holder: ContinuedCheckInVH, data: SignCalendarEntity) {
		holder.tvCheckedName.text = data.desc
		holder.tvCheckedScore.text = data.credit
		val adapterPosition = holder.absoluteAdapterPosition
		when (data.reward) {
			SignCalendarReward.normal -> {
				showVip(holder, false)
				holder.tvCheckedDoubleTag.visual(false)
				holder.contentChecked.setBackgroundResource(R.drawable.shape_continue_checked_in_normal)
			}
			SignCalendarReward.VIP1 -> {
				holder.tvCheckedDoubleTag.visual(false)
				showVip(holder, true)
				updateVipInfo(holder, "1天体验")
			}
			SignCalendarReward.VIP3 -> {
				showVip(holder, true)
				holder.tvCheckedDoubleTag.visual(false)
				updateVipInfo(holder, "3天体验")
			}
			SignCalendarReward.double -> {
				showVip(holder, false)
				holder.tvCheckedDoubleTag.visual(true)
				holder.contentChecked.setBackgroundResource(R.drawable.shape_continue_checked_in_normal)
			}
		}
		if (data.isSigned == true) {
			holder.tvCheckedVipDesc.visual(false)
			holder.contentChecked.setBackgroundResource(R.drawable.shape_continue_checked)
			holder.tvCheckedScore.visibility = View.INVISIBLE
			holder.ivCheckedIn.visual(true)
			holder.ivCheckedIn.setImageResource(R.drawable.mine_score_signed)
			holder.tvCheckedName.textColor(R.color.color_blue)
		} else {
			holder.tvCheckedName.textColor(R.color.color_text_black4)
		}
		//隐藏分割线
		if (adapterPosition % 7 == 6) {
			val layoutParams = holder.dividerChecked.layoutParams as LinearLayout.LayoutParams
			layoutParams.width = 0
			layoutParams.marginStart = 0
			layoutParams.marginEnd = 0
		} else {
			val layoutParams = holder.dividerChecked.layoutParams as LinearLayout.LayoutParams
			layoutParams.width = holder.dividerWidth.toInt()
			layoutParams.marginStart = 1.azDp.toInt()
			layoutParams.marginEnd = 4.azDp.toInt()
		}
	}

	private fun updateVipInfo(holder: ContinuedCheckInVH, message: String) {
		holder.contentChecked.setBackgroundResource(R.drawable.shape_continue_vip)
		holder.tvCheckedVipDesc.text = message
	}

	/**
	 * vip相关背景图标是否可见
	 */
	private fun showVip(holder: ContinuedCheckInVH, visible: Boolean) {
		if (visible) {
			holder.ivCheckedIn.visual(true)
			holder.ivCheckedIn.setImageResource(R.drawable.mine_score_vip_tag)
			holder.tvCheckedScore.visual(false)
			holder.tvCheckedVipDesc.visual(true)
		} else {
			holder.ivCheckedIn.visual(false)
			holder.ivCheckedIn.setImageResource(R.drawable.mine_score_vip_tag)
			holder.tvCheckedScore.visual(true)
			holder.tvCheckedVipDesc.visual(false)
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContinuedCheckInVH {
		return ContinuedCheckInVH.create(parent)
	}

	class ContinuedCheckInVH(itemView: View) : BaseViewHolder(itemView) {
		val ivCheckedIn: ImageView = itemView.findViewById(R.id.mine_iv_checked_in)
		val tvCheckedName: TextView = itemView.findViewById(R.id.mine_tv_name_check_in)
		val tvCheckedScore: TextView = itemView.findViewById(R.id.mine_tv_score_checked_in)
		val contentChecked: FrameLayout = itemView.findViewById(R.id.ui_checked_in_layout_bg)
		val tvCheckedDoubleTag: TextView = itemView.findViewById(R.id.mine_tv_checked_in_double)
		val tvCheckedVipDesc: TextView = itemView.findViewById(R.id.mine_checked_in_desc)
		val dividerChecked: View = itemView.findViewById(R.id.mine_checked_divider)
		var dividerWidth = 0f

		init {
			val screenWidth = ScreenUtils.getScreenWidth(itemView.context)
			val dividerZone = screenWidth - 14.azDp * 2 - 31.azDp * 7 - 13.azDp * 2
			dividerWidth = dividerZone / 6f - 8.azDp
		}

		companion object {
			fun create(parent: ViewGroup): ContinuedCheckInVH {
				return ContinuedCheckInVH(LayoutInflater
						.from(parent.context)
						.inflate(R.layout.mine_item_continued_check_in, parent, false))
			}
		}
	}
}

class CheckedItemDiffCallback : DiffUtil.ItemCallback<SignCalendarEntity>() {
	override fun areItemsTheSame(oldItem: SignCalendarEntity, newItem: SignCalendarEntity): Boolean {
		return oldItem.desc == newItem.desc
	}

	override fun areContentsTheSame(oldItem: SignCalendarEntity, newItem: SignCalendarEntity): Boolean {
		if (oldItem.isSigned != newItem.isSigned) {
			return false
		}
		if (oldItem.credit != newItem.credit) {
			return false
		}
		if (oldItem.reward != newItem.reward) {
			return false
		}
		return true
	}
}