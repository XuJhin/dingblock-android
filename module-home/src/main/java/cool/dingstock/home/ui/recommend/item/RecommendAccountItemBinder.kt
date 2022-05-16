package cool.dingstock.home.ui.recommend.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.entity.bean.mine.AccountInfoBriefEntity
import cool.dingstock.appbase.ext.loadAvatar
import cool.dingstock.imagepicker.views.roundImageView.RoundImageView
import cool.dingstock.home.R

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/11/4 14:54
 * @Version:         1.1.0
 * @Description:
 */
class RecommendAccountItemBinder : DcBaseItemBinder<AccountInfoBriefEntity, RecommendViewHolder>() {
    var checkListener: CheckListener? = null
    override fun onConvert(holder: RecommendViewHolder, data: AccountInfoBriefEntity) {
        holder.avatar.loadAvatar(data.avatarUrl)
		holder.nickname.text = data.nickName
		holder.fansCount.text = "${data.fansCount}人关注"
        if (data.isVerified) {
            holder.ivTag.visibility = View.VISIBLE
        } else {
            holder.ivTag.visibility = View.INVISIBLE
        }
        holder.checkBox.isSelected = !data.notFollow
        holder.layoutCheckBox.setOnClickListener {
            data.notFollow = !data.notFollow
            holder.checkBox.isSelected = !data.notFollow
            checkListener?.onCheckListener(data.id, holder.checkBox.isSelected)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendViewHolder {
        return RecommendViewHolder.createView(parent)
    }

}

interface CheckListener {
    fun onCheckListener(id: String, selected: Boolean)
}


/**
 * viewHolder
 */
class RecommendViewHolder(itemView: View) : BaseViewHolder(itemView) {
    val avatar = itemView.findViewById<RoundImageView>(R.id.avatar_recommend_account)
    val nickname = itemView.findViewById<TextView>(R.id.nickname_recommend_account)
    val fansCount = itemView.findViewById<TextView>(R.id.tv_account_recommend_account)
    val checkBox = itemView.findViewById<TextView>(R.id.checkbox_recommend_account)
    val ivTag = itemView.findViewById<ImageView>(R.id.iv_flag_is_verified)
    val layoutCheckBox = itemView.findViewById<LinearLayout>(R.id.layout_check_box)

    companion object {
        fun createView(parent: ViewGroup): RecommendViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_recommmend_account, parent, false)
            return RecommendViewHolder(itemView = itemView)
        }
    }

}