package cool.dingstock.uicommon.mine.item

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cool.dingstock.appbase.ext.invisible
import cool.dingstock.appbase.ext.visual
import cool.dingstock.appbase.imageload.GlideHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.entity.bean.mine.AccountInfoBriefEntity
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.uicommon.R

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/10/21 16:23
 * @Version:         1.1.0
 * @Description:
 */
class FollowItemBinder : DcBaseItemBinder<AccountInfoBriefEntity, FollowItemViewHolder>() {
    enum class ShowWhere {
        defaultWhere, Search
    }

    var showWhere: ShowWhere = ShowWhere.defaultWhere
    var margen = 0.dp

    lateinit var clickFollowStateListener: ClickFollowStateListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowItemViewHolder {
        return FollowItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.mine_item_follow, parent, false)
        )
    }

    override fun onConvert(holder: FollowItemViewHolder, data: AccountInfoBriefEntity) {
        val user = LoginUtils.getCurrentUser()
        holder.avatar.apply {
            setAvatarUrl(data.avatarUrl)
            data.avatarPendantUrl?.let { if (it.isNotEmpty()) setPendantUrl(it) }
        }
        holder.tvNickname.text = data.nickName
        holder.tvNickname.setTextColor(ContextCompat.getColor(context, if (data.isVip) R.color.colorTextVip else R.color.color_text_black1))
        holder.tvFollowState.isSelected = data.followed
        if (data.followed) {
            holder.tvFollowState.text = "已关注"
        } else {
            holder.tvFollowState.text = "关注"
        }
        if (data.isVerified) {
            holder.userIsVerified.visual()
        } else {
            holder.userIsVerified.invisible()
        }
        holder.tvFollowState.setOnShakeClickListener {
            if (!LoginUtils.isLoginAndRequestLogin(context)) {
                return@setOnShakeClickListener
            }
            if (::clickFollowStateListener.isInitialized) {
                clickFollowStateListener.clickFollow(data, getDataPosition(holder))
            }
        }

        if (null != user && user.id == data.id) {
            holder.tvFollowState.visibility = View.GONE
        } else {
            holder.tvFollowState.visibility = View.VISIBLE
        }
        holder.tvUserFollowedNumber.apply {
            text = "${data.fansCount}粉丝"
        }
        val layoutParams = holder.avatar.layoutParams
        if (showWhere == ShowWhere.Search) {
            Logger.e(data.nickName, ",isStart:${data.isStart},isEnd:${data.isEnd}")
            when {
                data.isStart && data.isEnd -> {
                    holder.itemView.setBackgroundResource(R.drawable.search_card_all_bg)
                }
                data.isStart && !data.isEnd -> {
                    holder.itemView.setBackgroundResource(R.drawable.search_card_top_bg)
                }
                !data.isStart && data.isEnd -> {
                    holder.itemView.setBackgroundResource(R.drawable.search_card_bottom_bg)
                }
                !data.isStart && !data.isEnd -> {
                    holder.itemView.setBackgroundResource(R.drawable.search_card_not_bg)
                }
            }

            val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
            layoutParams.marginStart = 12.dp.toInt()
            layoutParams.marginEnd = 12.dp.toInt()
            holder.itemView.layoutParams = layoutParams

        } else {
            layoutParams.width = 35.dp.toInt()
            layoutParams.height = 35.dp.toInt()
            holder.tvUserFollowedNumber.hide(true)
        }
        holder.avatar.layoutParams = layoutParams
    }

}

interface ClickFollowStateListener {
    fun clickFollow(item: AccountInfoBriefEntity, position: Int)
}