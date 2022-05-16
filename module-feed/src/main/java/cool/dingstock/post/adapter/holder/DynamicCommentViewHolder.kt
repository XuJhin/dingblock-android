package cool.dingstock.post.adapter.holder

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.customerview.betterlinktv.BetterLinkTv
import cool.dingstock.imagepicker.views.AvatarView
import cool.dingstock.post.R
import cool.dingstock.post.item.CommentSecondItemBinder
import cool.dingstock.post.item.CommentSecondShortItemBinder

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/18  11:16
 */
class DynamicCommentViewHolder(view: View) : BaseViewHolder(view) {
	val userIv: AvatarView = view.findViewById(R.id.circle_item_dynamic_comment_user_iv)
	val userTag: View = view.findViewById(R.id.group_item_dynamic_user_verified)
	val userNameTxt: TextView = view.findViewById(R.id.circle_item_dynamic_comment_username_txt)
	val timeTxt: TextView = view.findViewById(R.id.circle_item_dynamic_comment_time_txt)
	val contentTxt: BetterLinkTv = view.findViewById(R.id.circle_item_dynamic_comment_content_txt)
	val likeCountTxt: TextView = view.findViewById(R.id.circle_item_dynamic_comment_like_txt)
	val likeIcon: ImageView = view.findViewById(R.id.circle_item_dynamic_comment_like_icon)
	val likeLayer: LinearLayout = view.findViewById(R.id.circle_item_dynamic_comment_like_layer)
	val subContentRecycler: RecyclerView =
			view.findViewById(R.id.circle_item_dynamic_comment_subContent_recycler)
	val imgView = view.findViewById<ImageView>(R.id.circle_item_dynamic_comment_content_img)
	val imgFra = view.findViewById<View>(R.id.img_fra)
	val imgGifTag = view.findViewById<View>(R.id.circle_item_dynamic_comment_content_gif_iv)
	val imgMedal = view.findViewById<ImageView>(R.id.iv_medal)
	lateinit var commentSecondAdapter: DcBaseBinderAdapter
	lateinit var commentSecondItemBinder: CommentSecondItemBinder
	lateinit var commentSecondShortItemBinder: CommentSecondShortItemBinder
}