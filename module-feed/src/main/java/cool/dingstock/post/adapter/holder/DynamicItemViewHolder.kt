package cool.dingstock.post.adapter.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.willy.ratingbar.ScaleRatingBar
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.customerview.betterlinktv.BetterLinkTv
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.widget.vote.VoteView
import cool.dingstock.imagepicker.views.AvatarView
import cool.dingstock.post.R
import cool.dingstock.post.databinding.PostTradingDetailsLayoutBinding
import cool.dingstock.post.view.DcVideoPlayer
import cool.dingstock.post.view.GodCommentTextView
import cool.dingstock.post.view.PostImgView
import cool.dingstock.post.widget.PostLotteryDetailsView
import java.util.concurrent.atomic.AtomicBoolean

class DynamicItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
    val basicBtn: View = itemView.findViewById(R.id.basic_btn)
    val rootView: View = itemView.findViewById(R.id.rootView)
    val contentLayer: CardView = itemView.findViewById(R.id.content_Layer)
    val bottomSpace: View = itemView.findViewById(R.id.bottom_space)
    val itemBottomActionLayer: View = itemView.findViewById(R.id.item_bottom_action_layer)
    val certificationFailLayer: View = itemView.findViewById(R.id.certification_fail_layer)
    val reEditCertificationTv: TextView = itemView.findViewById(R.id.re_edit_certification_tv)
    val certificationFailReasonTv: TextView = itemView.findViewById(R.id.certification_fail_reason_tv)

    val headerScoreLayer: View = itemView.findViewById(R.id.header_score_layer)
    val headerScoreRatingBar: ScaleRatingBar = itemView.findViewById(R.id.header_score_rating_bar)

    val detailsScoreLayer: View = itemView.findViewById(R.id.details_score_layer)
    val detailsScoreRatingBar: ScaleRatingBar = itemView.findViewById(R.id.details_score_rating_bar)

    //头部id
    val userLayer: ConstraintLayout = itemView.findViewById(R.id.circle_item_dynamic_user_head_layer)
    val ivUserAvatar: AvatarView = itemView.findViewById(R.id.group_item_dynamic_user_head_iv)
    val ivUserTag: ShapeableImageView = itemView.findViewById(R.id.userIsVerified)
    val tvPostLocationInfo: TextView = itemView.findViewById(R.id.group_item_dynamic_location)
    val tvUserNickname: TextView = itemView.findViewById(R.id.group_item_dynamic_user_name_txt)
    val userTime: TextView = itemView.findViewById(R.id.group_item_dynamic_user_time_txt)
    val iconHotTag: ImageView = itemView.findViewById(R.id.icon_post_tag_hot)
    val ivOverlay: ImageView = itemView.findViewById(R.id.post_header_overlay)
    val followUser: TextView = itemView.findViewById(R.id.tv_follow_user)
    val ivMedal: ImageView = itemView.findViewById(R.id.iv_medal)

    //文字内容
    val tvPostContent: BetterLinkTv = itemView.findViewById(R.id.circle_item_dynamic_content_txt)
    val tvAllText: TextView = itemView.findViewById(R.id.circle_item_dynamic_all_txt)

    //Image
    val postImgView: PostImgView = itemView.findViewById(R.id.post_img_view)

    //webLink
    val layoutLink: ViewGroup = itemView.findViewById(R.id.circle_item_dynamic_link_layer)
    val ivWebLinkVideoLayout: ViewGroup = itemView.findViewById(R.id.webPage_video_layer)
    val detailPlayer: DcVideoPlayer = itemView.findViewById(R.id.detail_player)

    //link
    val linkDecTv: BetterLinkTv = itemView.findViewById(R.id.link_dec_tv)

    //评论信息
    val layoutRaise: ConstraintLayout =
            itemView.findViewById(R.id.post_layout_raise) //circle_item_dynamic_like_layer
    val layoutComment: ConstraintLayout = itemView.findViewById(R.id.post_layout_comment) //
    val layoutShare: ConstraintLayout =
            itemView.findViewById(R.id.post_layout_share) //circle_item_dynamic_share_layer
    val tvRaiseCount: TextView = itemView.findViewById(R.id.tv_post_raise)
    val tvCommentCount: TextView = itemView.findViewById(R.id.tv_post_comment)
    val ivRaiseSelected: ImageView = itemView.findViewById(R.id.iv_post_raise)

    //投票内容
    val voteView: VoteView = itemView.findViewById(R.id.layout_dynamic_vote)

    //话题相关
    val layoutTalk: View? = itemView.findViewById(R.id.layout_circle_post_talk)
    val topicTitleTv: TextView? = itemView.findViewById(R.id.topic_title_tv)

    //神评论
    val godCommentLayer: View = itemView.findViewById(R.id.good_comment_layer)
    val godCommentTv: GodCommentTextView = itemView.findViewById(R.id.god_comment_tv)
    val godCommentImgLayer: View = itemView.findViewById(R.id.god_comment_img_layer)
    val godCommentIv: ImageView = itemView.findViewById(R.id.god_comment_iv)
    val godComentGifTag: View = itemView.findViewById(R.id.god_comment_gif_tag)

    //底部的线
    var postSenderId = ""

    val tradingDetailsOtherPublishLayer: LinearLayoutCompat = itemView.findViewById(R.id.post_trading_details_other_publish_layer)
    val tradingDetailsOtherPublishTv: TextView = itemView.findViewById(R.id.post_trading_details_other_publish_tv)
    val tradingLayer: ViewGroup = itemView.findViewById(R.id.trading_layer)
    val itemSizeLayer: ViewGroup = itemView.findViewById(R.id.item_size_layer)
    val singlePriceLayer: ViewGroup = itemView.findViewById(R.id.single_price_layer)
    val singleSizeTv: TextView = itemView.findViewById(R.id.single_size_tv)
    val singlePriceTv: TextView = itemView.findViewById(R.id.single_price_tv)
    val singlePriceSymbolTv: TextView = itemView.findViewById(R.id.single_price_symbol_tv)
    val manySizeLayer: ViewGroup = itemView.findViewById(R.id.many_size_layer)
    val tradingTv: BetterLinkTv = itemView.findViewById(R.id.trading_tv)
    val tradingAllTv: TextView = itemView.findViewById(R.id.trading_all_tv)
    val tradingTagTv: TextView = itemView.findViewById(R.id.trading_tag_tv)
    val tradingDetailsLayer: ViewGroup = itemView.findViewById(R.id.trading_details_layer)
    val listTradingProductLayer: ViewGroup = itemView.findViewById(R.id.list_trading_product_layer)
    val tradingProductIv: ImageView = itemView.findViewById(R.id.trading_product_iv)
    val tradingProductNameTv: TextView = itemView.findViewById(R.id.trading_product_name_tv)
    val tradingProductPublishCountTv: TextView = itemView.findViewById(R.id.trading_product_publish_count_tv)
    val bottomLine: View = itemView.findViewById(R.id.bottom_lin)

    var isInitDetails = AtomicBoolean(false)
    val tradingPriceVb by lazy {
        PostTradingDetailsLayoutBinding.inflate(LayoutInflater.from(itemView.context), tradingDetailsLayer, false)
    }

    var tradingPriceSizeAdapter: DcBaseBinderAdapter? = null
    var tradingPriceSizeLayoutManager: RecyclerView.LayoutManager? = null

    //收藏
    val postCollectionLayer: ViewGroup = itemView.findViewById(R.id.post_layout_collection)
    val imageViewCollection: ImageView = itemView.findViewById(R.id.imageView_collection)
    val tvCollection: TextView = itemView.findViewById(R.id.tv_collection)

    //抽奖
    val lotteryStateTv: TextView = itemView.findViewById(R.id.lottery_state_tv)
    val lotteryStateLayer: View = itemView.findViewById(R.id.lottery_state_layer)
    val lotteryDetailsLayer: ViewGroup = itemView.findViewById(R.id.lottery_details_layer)
    var postLotteryView: PostLotteryDetailsView? = null

    val partyLayer: View = itemView.findViewById(R.id.party_layer)
    val partyNameTv: TextView = itemView.findViewById(R.id.party_name_tv)
    val partyIv: ImageView = itemView.findViewById(R.id.party_iv)

    val tvViewCount: TextView = itemView.findViewById(R.id.tv_view_count)

    //热议标签
    val hotRankLayer: View = itemView.findViewById(R.id.hot_rank_layer)
    val hotRankLogoIv: ImageView = itemView.findViewById(R.id.iv_hot_rank_logo)
    val hotRankTitleTv: TextView = itemView.findViewById(R.id.tv_hot_rank_title)

    fun hideCollection(hide: Boolean) {
        postCollectionLayer.hide(hide)
    }
}