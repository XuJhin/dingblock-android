package cool.dingstock.appbase.adapter.itembinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.R
import cool.dingstock.appbase.entity.bean.home.NoticeItemEntity
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.invisible
import cool.dingstock.appbase.ext.visual
import cool.dingstock.appbase.imageload.GlideHelper
import cool.dingstock.imagepicker.views.roundImageView.RoundImageView
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.lib_base.util.TimeUtils
import cool.dingstock.appbase.customerview.betterlinktv.BetterLinkTv
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.imagepicker.views.AvatarView

/**
 * 用户评论、回复、点赞时使用的itemView
 */
class NoticeReplyItem :
    DcBaseItemBinder<NoticeItemEntity, NoticeReplyItem.NoticeReplyViewHolder>() {

    var onNoticeClickListener: OnNoticeClickListener? = null

    class NoticeReplyViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val rIvUserAvatar: AvatarView = itemView.findViewById(R.id.riv_avatar)
        val tvUserName: TextView = itemView.findViewById(R.id.tv_user_name)
        val tvNoticeContent: BetterLinkTv = itemView.findViewById(R.id.tv_content)
        val tvNoticeTime: TextView = itemView.findViewById(R.id.tv_time)
        val tvCircleMessage: TextView = itemView.findViewById(R.id.tv_circle_content_txt)
        val tvFollow: TextView = itemView.findViewById(R.id.tv_follow_user)
        val rivCircleCover: RoundImageView = itemView.findViewById(R.id.iv_circle_content_cover)
        val layoutCircleContent: FrameLayout = itemView.findViewById(R.id.layout_circle_content)
        val rLayoutUsers: RelativeLayout = itemView.findViewById(R.id.fLayout_users)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeReplyViewHolder {
        return NoticeReplyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_notice_reply, parent, false)
        )
    }

    override fun onConvert(holder: NoticeReplyViewHolder, data: NoticeItemEntity) {
        holder.tvNoticeContent.maxLines = 1
        holder.tvNoticeContent.isSingleLine = data.type != "recommend" && data.type != "report"
        if (data.type == "recommend") {
            hideFollowStatus(holder)
            holder.tvUserName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.color_text_black1))
            holder.tvUserName.text = data.title
            holder.tvNoticeContent.maxLines = Int.MAX_VALUE
            holder.tvNoticeContent.text = data.content
            holder.rIvUserAvatar.visibility = View.GONE

        } else if (data.type == "report") {
            hideFollowStatus(holder)
            holder.tvUserName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.color_text_black1))
            holder.tvUserName.text = data.title
            holder.tvNoticeContent.maxLines = Int.MAX_VALUE
            holder.tvNoticeContent.text = data.content
            holder.rIvUserAvatar.visibility = View.GONE
        } else {
            data.user.let {
                holder.tvUserName.setTextColor(if (it.isVip == true) ContextCompat.getColor(holder.itemView.context, R.color.colorTextVip) else
                    ContextCompat.getColor(holder.itemView.context, R.color.color_text_black1))
                holder.tvUserName.text = it.nickName
                holder.rIvUserAvatar.apply {
                    setAvatarUrl(it.avatarUrl)
                    setPendantUrl(it.avatarPendantUrl)
                }
            }
            holder.rIvUserAvatar.visibility = View.VISIBLE
            when (data.type) {
                "reply" -> {
                    hideFollowStatus(holder)
                    setNoticeContentMsg("回复了你:${data.content}", holder.tvNoticeContent)
                }
                "comment" -> {
                    hideFollowStatus(holder)
                    setNoticeContentMsg("评论了你:${data.content}", holder.tvNoticeContent)
                }
                "favor" -> {
                    hideFollowStatus(holder)
                    if (data.favorCount == 1) {
                        setNoticeContentMsg("点赞了你的动态", holder.tvNoticeContent)
                    } else {
                        setNoticeContentMsg("共${data.favorCount}人点赞了你的动态", holder.tvNoticeContent)
                    }
                }
                "follow" -> {
                    setNoticeContentMsg("关注了你", holder.tvNoticeContent)
                    showFollowStatus(holder)
                    holder.tvFollow.isSelected = data.user.followed ?: false
                    if (data.user.followed == true) {
                        holder.tvFollow.text = "已关注"
                    } else {
                        holder.tvFollow.text = "关注"
                    }
                }
                "comment_favor" -> {
                    hideFollowStatus(holder)
                    if (data.favorCount == 1) {
                        setNoticeContentMsg("点赞了你的评论", holder.tvNoticeContent)
                    } else {
                        setNoticeContentMsg("共${data.favorCount}人点赞了你的评论", holder.tvNoticeContent)
                    }
                }
                "follow_post" -> {
                    if (data.original?.imageUrl.isNullOrEmpty()) {
                        showFollowStatus(holder)
                        holder.tvFollow.isSelected = false
                        holder.tvFollow.text = "详情"
                    } else {
                        hideFollowStatus(holder)
                    }
                    setNoticeContentMsg("发布了动态: ${data.original?.content}", holder.tvNoticeContent)
                }
            }
        }
        holder.tvNoticeTime.text = TimeUtils.getDynamicTime(data.createdAt)
        if (data.original?.imageUrl.isNullOrEmpty() && data.type == "follow_post") {
            holder.tvCircleMessage.hide()
        } else if (data.original?.imageUrl.isNullOrEmpty()) {
            holder.rivCircleCover.hide()
            holder.tvCircleMessage.visual()
            holder.tvCircleMessage.text = data.original?.content
        } else {
            holder.rivCircleCover.visual()
            holder.tvCircleMessage.hide()
            GlideHelper.loadRadiusImage(
                data.original?.imageUrl,
                holder.rivCircleCover, holder.itemView, 4f
            )
        }
        if (data.favorCount > 1) {
            holder.rLayoutUsers.visual()
            holder.rLayoutUsers.removeAllViews()
            val imageSize = SizeUtils.dp2px(28f)
            var marginStart = 0
            if (data.favor?.users.isNullOrEmpty()) {
                return
            }
            data.favor?.users?.forEachIndexed { index, circleUserBean ->
                val userAvatar = RoundImageView(holder.itemView.context)
                userAvatar.type = RoundImageView.TYPE_CIRCLE
                userAvatar.setBorderWidth(SizeUtils.dp2px(1f))
                userAvatar.setBorderColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.white
                    )
                )
                holder.rLayoutUsers.addView(userAvatar, imageSize, imageSize)
                val layoutParams: RelativeLayout.LayoutParams =
                    userAvatar.layoutParams as RelativeLayout.LayoutParams
                layoutParams.marginStart = marginStart
                userAvatar.layoutParams = layoutParams
                marginStart += imageSize / 2
                if (index == 3 && data.favorCount > 3) {
                    Glide.with(holder.itemView)
                        .load(R.drawable.icon_overlay_white)
                        .into(userAvatar)
                } else {
                    GlideHelper.loadCircle(
                        circleUserBean.avatarUrl, userAvatar,
                        holder.itemView,
                        R.drawable.default_avatar
                    )

                }
            }
        } else {
            holder.rLayoutUsers.hide()
        }
        holder.rIvUserAvatar.setOnShakeClickListener {
            onNoticeClickListener?.onClickAvatar(data.user.objectId ?: "")
        }
        holder.tvFollow.setOnShakeClickListener {
            if (data.type != "follow_post") {
                onNoticeClickListener?.onClickFollow(
                    data.user.followed!!,
                    data.user.objectId ?: "",
                    getDataPosition(holder)
                )
            } else {
                onItemClickListener?.onItemClick(adapter, holder, getDataPosition(holder))
            }
        }
        holder.rLayoutUsers.setOnShakeClickListener {
            if (data.type == "comment_favor") {
                onNoticeClickListener?.onClickFavorList(data.commentId, data.type)
            } else {
                onNoticeClickListener?.onClickFavorList(data.original?.objectId, data.type)
            }
        }
        holder.tvNoticeContent.setBetterLink()
        holder.tvNoticeContent.enableClickLink = false
        holder.tvNoticeContent.setOnShakeClickListener {
            onItemClickListener?.onItemClick(adapter, holder, getDataPosition(holder))
        }
    }


    /**
     * 隐藏右边内容区域(当用户关注时)
     */
    private fun hideRightMessageZone(holder: NoticeReplyViewHolder) {
        holder.layoutCircleContent.invisible()
    }

    private fun showFollowStatus(holder: NoticeReplyViewHolder) {
        holder.tvFollow.visual()
        holder.tvCircleMessage.hide()
        holder.rivCircleCover.hide()
        holder.layoutCircleContent.setBackgroundResource(R.color.transparent)
    }

    private fun hideFollowStatus(holder: NoticeReplyViewHolder) {
        holder.layoutCircleContent.setBackgroundResource(R.drawable.shape_notice_item_right)
        holder.tvFollow.hide()
        holder.tvCircleMessage.hide()
        holder.rivCircleCover.hide()
    }

    private fun setNoticeContentMsg(s: String, tvNoticeContent: TextView) {
        tvNoticeContent.text = s
    }
}

interface OnNoticeClickListener {
    fun onClickAvatar(userId: String)
    fun onClickFavorList(objectId: String?, type: String)
    fun onClickFollow(followState: Boolean, userId: String, index: Int)
}