package cool.dingstock.setting.ui.shield.item

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.imagepicker.views.roundImageView.RoundImageView
import cool.dingstock.uicommon.setting.R

class ShieldViewHolder(itemView: View) : BaseViewHolder(itemView) {
    val rivAccountAvatar: RoundImageView = itemView.findViewById(R.id.setting_shield_user_avatar)
    val tvNickname: TextView = itemView.findViewById(R.id.setting_shield_user_nickname)
    val tvShieldState: TextView = itemView.findViewById(R.id.setting_shield_state)
    val ivTag: ImageView = itemView.findViewById(R.id.iv_flag_is_verified)
}