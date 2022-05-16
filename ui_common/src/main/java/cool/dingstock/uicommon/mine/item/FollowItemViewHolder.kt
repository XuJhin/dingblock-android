package cool.dingstock.uicommon.mine.item

import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.imagepicker.views.AvatarView
import cool.dingstock.imagepicker.views.roundImageView.RoundImageView
import cool.dingstock.uicommon.R

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/10/21 16:24
 * @Version:         1.1.0
 * @Description:
 */
class FollowItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
    val avatar: AvatarView = itemView.findViewById(R.id.mine_follow_user_avatar)
    val tvNickname: TextView = itemView.findViewById(R.id.mine_follow_user_nickname)
    val tvFollowState: TextView = itemView.findViewById(R.id.mine_follow_state)
    val userIsVerified: RoundImageView = itemView.findViewById(R.id.iv_flag_is_verified)
    val tvUserFollowedNumber: TextView = itemView.findViewById(R.id.tv_account_follow_number)
}