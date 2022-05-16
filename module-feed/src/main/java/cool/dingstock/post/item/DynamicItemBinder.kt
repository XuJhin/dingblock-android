package cool.dingstock.post.item

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseBinderAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.constant.*
import cool.dingstock.appbase.entity.bean.account.DcLoginUser
import cool.dingstock.appbase.entity.bean.circle.*
import cool.dingstock.appbase.entity.event.circle.EventCollectChange
import cool.dingstock.appbase.entity.event.circle.EventDynamicVoter
import cool.dingstock.appbase.entity.event.circle.EventFavored
import cool.dingstock.appbase.entity.event.circle.EventFollowerChange
import cool.dingstock.appbase.entity.event.relation.EventFollowChange
import cool.dingstock.appbase.entity.event.relation.EventFollowFailed
import cool.dingstock.appbase.ext.*
import cool.dingstock.appbase.helper.IMHelper
import cool.dingstock.appbase.imageload.GlideHelper
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.net.api.circle.CircleApi
import cool.dingstock.appbase.net.api.circle.CircleHelper
import cool.dingstock.appbase.net.api.mine.MineHelper
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.share.SHARE_POST_DETAIL
import cool.dingstock.appbase.share.ShareParams
import cool.dingstock.appbase.share.ShareType
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.*
import cool.dingstock.appbase.widget.leonids.LeonidsUtil
import cool.dingstock.appbase.widget.menupop.OptionMenu
import cool.dingstock.appbase.widget.menupop.PopupMenuView
import cool.dingstock.appbase.widget.menupop.PopupView
import cool.dingstock.appbase.widget.vote.VoteListener
import cool.dingstock.imagepre.ImagePreview
import cool.dingstock.imagepre.bean.ImageInfo
import cool.dingstock.lib_base.stroage.ConfigSPHelper
import cool.dingstock.lib_base.util.*
import cool.dingstock.post.R
import cool.dingstock.post.adapter.DynamicBinderAdapter
import cool.dingstock.post.adapter.OverlayListener
import cool.dingstock.post.adapter.PostListener
import cool.dingstock.post.adapter.VoteClickListener
import cool.dingstock.post.adapter.holder.DynamicItemViewHolder
import cool.dingstock.post.dagger.PostApiHelper
import cool.dingstock.post.dialog.OverlayActionDialog
import cool.dingstock.post.dialog.PostActionListener
import cool.dingstock.post.view.DcVideoPlayer
import cool.dingstock.post.view.PostImgView
import cool.dingstock.post.widget.PostLotteryDetailsView
import org.greenrobot.eventbus.EventBus
import java.util.*
import javax.inject.Inject
import kotlin.collections.set

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/18  9:24
 */
const val LINK_TYPE_VIDEO = "VIDEO"

class DynamicItemBinder(val mContext: Context) :
        DcBaseItemBinder<CircleDynamicBean, DynamicItemViewHolder>(), PostListener, OverlayListener {

    @Inject
    lateinit var circleApi: CircleApi

    init {
        PostApiHelper.apiPostComponent.inject(this)
    }

    private var showWhere: PostItemShowWhere = PostItemShowWhere.Default
    var hideTime = false
    var onConvertListener: OnConvertListener<CircleDynamicBean, DynamicItemViewHolder>? = null

    fun updateShowWhere(showWhere: PostItemShowWhere) {
        this.showWhere = showWhere
    }

    /**
     * 点击话题触发
     * */
    var topicClickAction: (() -> Unit)? = null

    /**
     * 基础页点击
     */
    var basicClickAction: (() -> Unit)? = null

    private val holders: ArrayList<DynamicItemViewHolder> = arrayListOf()

    private fun setUtNickName(id: String): String {
        return when (id) {
            "BofjN680M1" -> "盯链官方"
            "snbgCVAQoS" -> "盯链线报"
            "Aym3iCcizC" -> "盯链秒杀日记"
            else -> "其他"
        }
    }

    private fun handAction(entity: CircleDynamicBean?, action: String) {
        entity.let {
            if (entity?.isDeal == true) {
                return
            }
            val userName = it?.user?.id?.let { it1 -> setUtNickName(it1) }
            val isNoImages = entity?.images.isNullOrEmpty()
            if (isHaveVideo(it)) {
                //视频动态
                UTHelper.commonEvent(
                        UTConstant.Circle.Dynamic_click_VideoDynamic,
                        "UserName",
                        userName,
                        "ActionName",
                        action
                )
            } else if (!isNoImages) {
                //图文动态
                UTHelper.commonEvent(
                        UTConstant.Circle.Dynamic_click_GraphicDynamic,
                        "UserName",
                        userName,
                        "ActionName",
                        action
                )
            } else {
                //文字动态
                UTHelper.commonEvent(
                        UTConstant.Circle.Dynamic_click_TextDynamic,
                        "UserName",
                        userName,
                        "ActionName",
                        action
                )
            }
        }
    }

    /**
     * @param targetIsCollect 表示目的 是否是 收藏 true 为收藏  false 为取消收藏
     * */
    private fun handCollect(entity: CircleDynamicBean?, targetIsCollect: Boolean) {
        entity?.let {
            it.user?.id?.let { it1 -> setUtNickName(it1) }
            val isNoImages = entity.images.isNullOrEmpty()
            if (entity.isDeal) {
                //交易动态
                UTHelper.commonEvent(
                        if (targetIsCollect) UTConstant.Circle.Dynamic_click_collect
                        else UTConstant.Circle.Dynamic_click_cancel_collect, "type", "交易动态"
                )
            } else if (isHaveVideo(it)) {
                //视频动态
                UTHelper.commonEvent(
                        if (targetIsCollect) UTConstant.Circle.Dynamic_click_collect
                        else UTConstant.Circle.Dynamic_click_cancel_collect, "type", "视频动态"
                )
            } else if (!isNoImages) {
                //图文动态
                UTHelper.commonEvent(
                        if (targetIsCollect) UTConstant.Circle.Dynamic_click_collect
                        else UTConstant.Circle.Dynamic_click_cancel_collect, "type", "图文动态"
                )
            } else {
                //文字动态
                UTHelper.commonEvent(
                        if (targetIsCollect) UTConstant.Circle.Dynamic_click_collect
                        else UTConstant.Circle.Dynamic_click_cancel_collect, "type", "文字动态"
                )
            }
        }
    }

    private fun handTradingAction(entity: CircleDynamicBean?, actionName: String) {
        if (entity?.isDeal != true) {
            return
        }
        UTHelper.commonEvent(
                UTConstant.Circle.Dynamic_click_TradingDynamicP,
                "ActionName",
                actionName
        )
    }


    init {
        onItemClickListener =
                object : cool.dingstock.appbase.adapter.itembinder.OnItemClickListener {
                    override fun onItemClick(
                            adapter: BaseBinderAdapter,
                            holder: BaseViewHolder,
                            position: Int
                    ) {
                        this@DynamicItemBinder.defaultItemClick(adapter, position)
                    }
                }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicItemViewHolder {
        val holder = DynamicItemViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_layout, parent, false)
        )
        holders.add(holder)
        return holder
    }

    var postListener: PostListener? = null
    var voteClickListener: VoteClickListener? = null

    /**
     * 是否忽略 最大行数限制
     */
    var maxLineLimit = true
    private var maxLineLimitCount: Int = 6
    var origin: String = ""
    private var showOverlay = true
    private var hideTalkMap = false

    /**
     * 当在话题详情页面展示时
     * 隐藏底部话题入口
     */
    fun hideTalkInfo() {
        hideTalkMap = true
    }

    /**
     * 是否忽略 真香，评论发event
     */
    private val ignoreEvent = false

    override fun onConvert(holder: DynamicItemViewHolder, data: CircleDynamicBean) {
        setBasicView(holder, data)
        setContentInfo(holder, data)
        setImage(holder, data)
        setWebPageViewLink(holder, data)
        setTalk(holder, data)
        setVote(holder, data)
        setHotRank(holder, data)
        setBottom(holder, data)
        setGodComment(holder, data)
        setTradingLayer(holder, data)
        setSeries(holder, data)
        setLottery(holder, data)
        setParty(holder, data)
        setItem(holder)
        setViewCount(holder, data)
    }

    private fun setBasicView(holder: DynamicItemViewHolder, data: CircleDynamicBean) {
        //如果这个view没有被加入到RecyclerView 中就不需要设置
        holder.postSenderId = data.user?.id ?: ""
        onConvertListener?.onConvert(holder, data)
        holder.userLayer.visibility = if (data.user != null) View.VISIBLE else View.GONE
        data.user?.let {
            setUserInfo(holder, it, data)
            holder.ivOverlay.apply {
                val hide =
                        data.hideOverLay || data.showWhere == ShowWhere.DETAIL || data.isFashion
                                || !showOverlay || showWhere == PostItemShowWhere.SERIES_DETAIL
                                || showWhere == PostItemShowWhere.SearchResult
                visibility = if (hide) View.GONE else View.VISIBLE
            }
            setPostHeader(holder, data)
        }
        if (showWhere == PostItemShowWhere.BASIC) {
            holder.ivOverlay.hide()
            holder.basicBtn.apply {
                isVisible = true
                setOnClickListener {
                    basicClickAction?.invoke()
                }
            }
        }
    }

    private fun setViewCount(holder: DynamicItemViewHolder, item: CircleDynamicBean) {
        val isDynamicDetail = showWhere == PostItemShowWhere.Detail
        val isSeriesDetail = item.isSeries
        val isDealDynamic = item.isDeal

        var isShowViewCount = false
        if (isDynamicDetail) isShowViewCount = true
        if (isDynamicDetail && isDealDynamic) isShowViewCount = false
        if (isSeriesDetail) isShowViewCount = false

        holder.tvViewCount.hide(!isShowViewCount)

        if (isShowViewCount) {
            holder.tvViewCount.text = item.viewCount.toString().plus("浏览")
        }
    }

    fun defaultItemClick(adapter: BaseBinderAdapter, position: Int) {
        val entity = adapter.getItem(position)
        if (entity is CircleDynamicBean) {
            handAction(entity, "其他")
            routeToDetail(mContext, entity, false)
        }
    }

    private fun setPostHeader(holder: DynamicItemViewHolder, item: CircleDynamicBean) {
        val user: CircleUserBean? = item.user
        val currentUser = LoginUtils.getCurrentUser()
        if (null == user) {
            holder.userLayer.visibility = View.GONE
            return
        }
        if (item.showWhere === ShowWhere.DETAIL) {
            if (user.id == currentUser?.id || user.isBlock) {
                holder.followUser.visibility = View.GONE
            } else {
                if (user.followed == true) {
                    holder.followUser.visibility = View.GONE
                } else {
                    holder.followUser.visibility = View.VISIBLE
                }
            }
        } else if (showWhere === PostItemShowWhere.FollowHomePage) {
            if (IMHelper.followIdList.isEmpty()) {
                holder.followUser.visibility = View.VISIBLE
            }
        } else {
            holder.followUser.visibility = View.GONE
        }
        holder.followUser.setOnClickListener {
            if (showWhere === PostItemShowWhere.FollowHomePage) {
                UTHelper.commonEvent(UTConstant.Circle.HomeP_click_Attention_tab_Attention)
            }
            handAction(item, "其他")
            followClick(item, holder)
        }
        //更新地址
        if (item.locationName.isNullOrEmpty()) {
            holder.tvPostLocationInfo.hide()
        } else {
            holder.tvPostLocationInfo.visibility = View.VISIBLE
            val locationShowText = "· ${item.locationName} ${item.distance ?: ""}"
            holder.tvPostLocationInfo.apply {
                post {
                    val originTextWidth: Float = this.paint.measureText(locationShowText)
                    //获取控件长度
                    val textViewWidth: Int = this.width
                    if (textViewWidth >= originTextWidth) {
                        this.text = locationShowText
                    } else {
                        val distanceLength = item.distance?.length ?: 0
                        if (distanceLength == 0) {
                            this.text = locationShowText
                        } else {
                            val totalLength = locationShowText.length
                            val splice = totalLength - distanceLength
                            var prefixText: String =
                                    locationShowText.substring(0, splice)
                            val suffixText =
                                    ".. " + locationShowText.substring(splice, locationShowText.length)
                            var prefixWidth: Float = this.paint.measureText(prefixText)
                            val suffixWidth: Float = this.paint.measureText(suffixText)
                            if (suffixWidth > textViewWidth) {
                                this.text = locationShowText
                            } else {
                                while (textViewWidth - prefixWidth < suffixWidth) {
                                    prefixText = prefixText.substring(0, prefixText.length - 1)
                                    //关键
                                    prefixWidth = this.paint.measureText(prefixText)
                                }
                                //能塞满
                                this.text = prefixText.plus(suffixText)
                            }
                        }
                        //获取指定省略位置
                    }
                }
            }
        }


        if (origin != DynamicBinderAdapter.MINE) {
            holder.ivUserAvatar.setOnClickListener {
                handAction(item, "其他")
                handTradingAction(item, "头像")
                //如果在动态详情，并且当前动态是自己发布的就不需要跳转详情
                if (item.showWhere == ShowWhere.DETAIL && user.id ?: "" == currentUser?.id) return@setOnClickListener
                routeToUserDetail(item)
            }
            holder.tvUserNickname.setOnClickListener {
                handAction(item, "其他")
                //如果在动态详情，并且当前动态是自己发布的就不需要跳转详情
                if (item.showWhere == ShowWhere.DETAIL && user.id ?: "" == currentUser?.id) return@setOnClickListener
                routeToUserDetail(item)
            }
        }
    }

    private fun setGodComment(holder: DynamicItemViewHolder, item: CircleDynamicBean) {
        val hotComment = item.hotComment
        if (null == hotComment) {
            holder.godCommentLayer.visibility = View.GONE
            return
        }
        holder.godCommentLayer.visibility = View.VISIBLE
        holder.godCommentTv.setGodComment(
                hotComment.userMap?.nickName ?: "", hotComment.content
                ?: ""
        )
        holder.godCommentTv.setOnClickListener {
            onItemClickListener?.onItemClick(adapter, holder, getDataPosition(holder))
        }
        if (hotComment.staticImg == null) {
            holder.godCommentImgLayer.hide(true)
            return
        }
        hotComment.staticImg?.let {
            holder.godCommentImgLayer.hide(false)
            GlideHelper.loadGifInGlideCacheWithOutWifi(
                    mContext,
                    holder.godCommentIv,
                    holder.godComentGifTag,
                    hotComment.dynamicImg?.url,
                    it.url,
                    6f,
                    true
            )
        }
        holder.godCommentIv.setOnClickListener {
            val urlList = ArrayList<ImageInfo>()
            val imageInfo = ImageInfo()
            imageInfo.originUrl = hotComment.dynamicImg?.url ?: hotComment.staticImg?.url
            imageInfo.thumbnailUrl = hotComment.staticImg?.url
            if (StringUtils.isEmpty(imageInfo.originUrl)) {
                return@setOnClickListener
            }
            handAction(item, "其他")
            urlList.add(imageInfo)
            routeToImagePre(0, urlList)
            if (GlideHelper.isGif(hotComment.dynamicImg?.url)) {
                holder.godComentGifTag.hide(true)
                holder.godComentGifTag.postDelayed({
                    GlideHelper.loadRadiusImage(
                            hotComment.dynamicImg?.url,
                            holder.godCommentIv,
                            context,
                            4f
                    )
                }, 300)
            }
        }
    }

    //交易
    private fun setTradingLayer(holder: DynamicItemViewHolder, item: CircleDynamicBean) {
        if (!item.isDeal) {
            holder.tradingDetailsLayer.removeAllViews()
            holder.tradingLayer.hide(true)
            holder.tvPostContent.hide(false)
            holder.tradingDetailsLayer.hide(true)
            return
        }
        holder.tvPostContent.hide(true)
        holder.tvAllText.hide(true)
        holder.tradingDetailsLayer.hide(false)
        holder.tradingTv.setOnShakeClickListener {
            routeToDetail(context, item, false)
        }
        holder.tradingLayer.hide(false)
        holder.tradingTagTv.text = item.label
        holder.tradingTagTv.hide(item.label.isNullOrEmpty())
        try {
            if (item.labelColor != null) {
                holder.tradingTagTv.textColor(item.labelColor!!)
                holder.singleSizeTv.textColor(item.labelColor!!)
                holder.tradingTagTv.setShape(
                        4f.azDp,
                        Color.parseColor(item.labelColor!!.replaceFirst("#", "#1A"))
                )
                holder.singleSizeTv.setShape(
                        4f.azDp,
                        Color.parseColor(item.labelColor!!.replaceFirst("#", "#1A"))
                )
            } else {
                holder.tradingTagTv.textColor(R.color.color_green)
                holder.singleSizeTv.textColor(R.color.color_green)
                holder.tradingTagTv.setShape(
                        4f.azDp,
                        ContextCompat.getColor(context, R.color.deal_label_bg)
                )
                holder.singleSizeTv.setShape(
                        4f.azDp,
                        ContextCompat.getColor(context, R.color.deal_label_bg)
                )
            }
        } catch (e: java.lang.Exception) {
            holder.tradingTagTv.setBackgroundResource(R.drawable.post_trading_tag_tv_bg)
            holder.singleSizeTv.setBackgroundResource(R.drawable.post_trading_tag_tv_bg)
            holder.tradingTagTv.textColor(R.color.color_green)
            holder.singleSizeTv.textColor(R.color.color_green)
        }
        holder.tradingTv.text = item.content
        holder.tradingTv.setBetterLink()

        holder.tradingTv.maxLines = Int.MAX_VALUE
        val textWidth = SizeUtils.getWidth() - (12 + 15) * 2.azDp
        val lineCount = TextViewLinesUtil.getTextViewLines(
                holder.tradingTv,
                textWidth.toInt()
        )
        if (maxLineLimit) {
            if (lineCount > maxLineLimitCount) {
                holder.tradingAllTv.visibility = View.VISIBLE
                holder.tradingAllTv.setOnClickListener {
                    handAction(item, "其他")
                    routeToDetail(mContext, item, false)
                }
            } else {
                holder.tradingAllTv.visibility = View.GONE
            }
            holder.tradingTv.maxLines = maxLineLimitCount
        } else {
            holder.tradingTv.maxLines = Int.MAX_VALUE
            holder.tradingAllTv.visibility = View.GONE
        }
        if (showWhere == PostItemShowWhere.Detail) {
            holder.listTradingProductLayer.hide(true)
            holder.itemSizeLayer.hide(true)
            holder.tradingDetailsLayer.hide(false)
            setTradingDetails(holder, item)
        } else {
            holder.itemSizeLayer.hide(false)
            holder.tradingDetailsLayer.hide(true)
            holder.tradingDetailsLayer.removeAllViews()
            if (item.product != null) {
                holder.listTradingProductLayer.hide(false)
                holder.tradingProductIv.load(item.product?.imageUrl)
                holder.tradingProductNameTv.text = item.product?.name
                holder.tradingProductPublishCountTv.text = item.product?.dealCount
                holder.listTradingProductLayer.setOnShakeClickListener {
                    handTradingAction(item, "该商品x用户在发布")
                    routerToTradingTopic(item.product!!)
                }
            } else {
                holder.listTradingProductLayer.hide(true)
            }

            //显示多size
            when {
                item.goodsSizeList.isNullOrEmpty() -> {
                    holder.itemSizeLayer.hide(true)
                }
                item.goodsSizeList?.size == 1 -> {
                    holder.itemSizeLayer.hide(false)
                    holder.singlePriceLayer.hide(false)
                    holder.singleSizeTv.hide(false)
                    holder.manySizeLayer.removeAllViews()
                    holder.singleSizeTv.text = item.goodsSizeList?.get(0)?.size ?: ""
                    holder.singleSizeTv.hide(item.hideSizeSegment == true)
                    holder.singlePriceTv.text = item.goodsSizeList?.get(0)?.price ?: ""
                    holder.singlePriceTv.hide(item.goodsSizeList?.get(0)?.price.isNullOrEmpty())
                    holder.singlePriceSymbolTv.hide(item.goodsSizeList?.get(0)?.price.isNullOrEmpty())
                    holder.tradingTagTv.apply {
                        setPadding(
                                4.azDp.toInt(),
                                1.azDp.toInt(),
                                4.azDp.toInt(),
                                1.azDp.toInt()
                        )
                        val lp = layoutParams as? ViewGroup.MarginLayoutParams
                        lp?.marginEnd = 5.azDp.toInt()
                        layoutParams = lp
                    }
                }
                else -> {
                    holder.itemSizeLayer.hide(false)
                    holder.manySizeLayer.hide(false)
                    holder.singlePriceLayer.hide(true)
                    holder.singleSizeTv.hide(true)
                    setTradingManySize(holder, item)
                    holder.tradingTagTv.apply {
                        setPadding(
                                10.azDp.toInt(),
                                8.azDp.toInt(),
                                10.azDp.toInt(),
                                8.azDp.toInt()

                        )
                        val lp = layoutParams as? ViewGroup.MarginLayoutParams
                        lp?.marginEnd = 8.azDp.toInt()
                        layoutParams = lp
                    }
                }
            }
        }
    }

    private fun setTradingManySize(holder: DynamicItemViewHolder, item: CircleDynamicBean) {
        val needShowSizeList = arrayListOf<String>()
        var tagWidth = holder.tvRaiseCount.paint.measureText(item.label ?: "") + 3.azDp
        if (item.label.isNullOrEmpty()) {
            tagWidth = 0f
        }
        val contentMaxWidth = SizeUtils.getWidth() - ((12 + 15) * 2).azDp - (20 + 5).azDp - tagWidth
        //如果是这样 新建的实列，measureText返回值是DP 如果是在XML inflate出来的返回值 是 px
        val paint = Paint()
        paint.textSize = SizeUtils.sp2px(12f).toFloat()
        var contentCurrentWidth = 0f
        kotlin.run {
            item.goodsSizeList?.forEachIndexed { _, entity ->
                val sizeWidth = paint.measureText(entity.size) + (20 + 8).azDp
                contentCurrentWidth += sizeWidth
                if (contentCurrentWidth < contentMaxWidth) {
                    needShowSizeList.add(entity.size ?: "--码")
                } else {//放不下了，需要把最后一个替换为 "更多"
                    //长度回滚到计算本次添加之前
                    contentCurrentWidth -= sizeWidth
                    //判断添加 "更多" 后是否满足长度
                    contentCurrentWidth += paint.measureText("更多") + 28.azDp
                    if (contentCurrentWidth < contentMaxWidth) {
                        needShowSizeList.add("更多")
                    } else {//不满足移除最接近 "更多" 的字符串
                        while (contentCurrentWidth > contentMaxWidth) {
                            val lastStr = needShowSizeList.removeLast()
                            contentCurrentWidth -= paint.measureText(lastStr) + (20 + 8).azDp
                        }
                        needShowSizeList.add("更多")
                    }
                    return@run
                }
            }
        }
        holder.manySizeLayer.removeAllViews()
        needShowSizeList.forEachIndexed { index, it ->
            val sizeItemLayout = LayoutInflater.from(context)
                    .inflate(R.layout.post_trading_many_size_item_layout, holder.itemSizeLayer, false)
            val sizeTv = sizeItemLayout.findViewById<TextView>(R.id.size_tv)
            sizeTv.text = it
            if (index == needShowSizeList.size - 1) {
                val lp = sizeTv.layoutParams as? ViewGroup.MarginLayoutParams
                lp?.marginEnd = 0
                sizeTv.layoutParams = lp
            }
            holder.manySizeLayer.addView(sizeItemLayout)
        }
    }

    private fun setTradingDetails(holder: DynamicItemViewHolder, item: CircleDynamicBean) {
        if (holder.isInitDetails.get()) {
            return
        }
        holder.isInitDetails.set(true)
        holder.tradingDetailsLayer.addView(holder.tradingPriceVb.root)
        holder.tradingPriceVb.postTradingLookCountTv.text = item.countStr
        holder.tradingDetailsOtherPublishTv.text = item.otherProductsStr
        holder.tradingDetailsOtherPublishLayer.isVisible = !item.otherProductsStr.isNullOrEmpty()
        holder.tradingDetailsOtherPublishLayer.setOnShakeClickListener {
            val user = item.user ?: return@setOnShakeClickListener
            handTradingAction(item, "ta正在发布x件商品")
            DcUriRequest(context, MineConstant.Uri.DYNAMIC)
                    .putUriParameter(MineConstant.PARAM_KEY.ID, user.objectId ?: "")
                    .putUriParameter(
                            MineConstant.PARAM_KEY.USER_DYNAMIC_PAGE,
                            MineConstant.TRADING_PAGE
                    )
                    .start()
        }

        holder.tradingPriceVb.postTradingDetailsSendAddressTv.text = "发货地 ".plus(item.senderAddress)
        holder.tradingPriceVb.postTradingDetailsSendAddressTv.hide(item.senderAddress.isNullOrEmpty())
        holder.tradingPriceVb.postTradingDetailsSendAddressIv.hide(item.senderAddress.isNullOrEmpty())

        if (item.product != null) {
            holder.tradingPriceVb.tradingProductLayer.hide(false)
            holder.tradingPriceVb.tradingProductIv.load(item.product?.imageUrl)
            holder.tradingPriceVb.tradingProductNameTv.text = item.product?.name
            holder.tradingPriceVb.tradingProductPublishCountTv.text = item.product?.dealCount
            holder.tradingPriceVb.tradingProductLayer.setOnShakeClickListener {
                handTradingAction(item, "该商品x用户在发布")
                routerToTradingTopic(item.product!!)
            }
        } else {
            holder.tradingPriceVb.tradingProductLayer.hide(true)
        }



        if (item.goodsSizeList.isNullOrEmpty()) {
            holder.tradingPriceVb.tradingDetailsSizeLayer.hide(true)
        } else if (item.goodsSizeList?.size ?: 0 == 1) {
            holder.tradingPriceVb.singlePriceTv.text = item.goodsSizeList?.get(0)?.price
            holder.tradingPriceVb.singlePriceLayer.hide(item.goodsSizeList?.get(0)?.price.isNullOrEmpty())
            holder.tradingPriceVb.singleSizeTv.text = item.goodsSizeList?.get(0)?.size ?: ""
            holder.tradingPriceVb.singleSizeTv.hide(item.hideSizeSegment == true)
            holder.tradingPriceVb.singleSizeLayer.hide(false)
            holder.tradingPriceVb.tradingNoPriceSizeLayer.hide(true)
            holder.tradingPriceVb.tradingHavePriceSizeLayer.hide(true)
            holder.tradingPriceVb.tradingTagTv.text = item.label
            if (item.labelColor != null) {
                holder.tradingPriceVb.tradingTagTv.textColor(item.labelColor!!)
                holder.tradingPriceVb.singleSizeTv.textColor(item.labelColor!!)
                holder.tradingPriceVb.tradingTagTv.setShape(
                        4f.azDp,
                        Color.parseColor(item.labelColor!!.replaceFirst("#", "#1A"))
                )
                holder.tradingPriceVb.singleSizeTv.setShape(
                        4f.azDp,
                        Color.parseColor(item.labelColor!!.replaceFirst("#", "#1A"))
                )
            } else {
                holder.tradingPriceVb.tradingTagTv.textColor(R.color.color_green)
                holder.tradingPriceVb.singleSizeTv.textColor(R.color.color_green)
                holder.tradingPriceVb.tradingTagTv.setShape(
                        4f.azDp,
                        ContextCompat.getColor(context, R.color.deal_label_bg)
                )
                holder.tradingPriceVb.singleSizeTv.setShape(
                        4f.azDp,
                        ContextCompat.getColor(context, R.color.deal_label_bg)
                )
            }

        } else {
            holder.tradingPriceVb.singleSizeLayer.hide(true)
            var noPrice = true
            kotlin.run {
                item.goodsSizeList?.forEach {
                    if (!it.price.isNullOrEmpty()) {
                        noPrice = false
                        return@run
                    }
                }
            }
            if (noPrice) {
                holder.tradingPriceVb.tradingNoPriceSizeLayer.removeAllViews()
                //尺码 1
                holder.tradingPriceVb.tradingNoPriceSizeLayer.hide(false)
                holder.tradingPriceVb.tradingHavePriceSizeLayer.hide(true)
                //添加标签
                if (!item.label.isNullOrEmpty()) {
                    val labelLayout = LayoutInflater.from(context)
                            .inflate(
                                    R.layout.post_trading_many_size_item_layout,
                                    holder.tradingPriceVb.tradingNoPriceSizeLayer,
                                    false
                            )
                    val labelTv = labelLayout.findViewById<TextView>(R.id.size_tv)

                    labelTv.apply {
                        text = item.label
                        if (item.labelColor != null) {
                            setShape(
                                    4f.azDp,
                                    Color.parseColor(item.labelColor!!.replaceFirst("#", "#1A"))
                            )
                            textColor(item.labelColor!!)
                        } else {
                            textColor(R.color.color_green)
                            setShape(
                                    4f.azDp,
                                    ContextCompat.getColor(context, R.color.deal_label_bg)
                            )
                        }
                    }
                    val lp = labelLayout.layoutParams as? ViewGroup.MarginLayoutParams
                    lp?.topMargin = 8.azDp.toInt()
                    labelLayout.layoutParams = lp
                    holder.tradingPriceVb.tradingNoPriceSizeLayer.addView(labelLayout)
                }

                item.goodsSizeList?.forEach {
                    val sizeItemLayout = LayoutInflater.from(context)
                            .inflate(
                                    R.layout.post_trading_many_size_item_layout,
                                    holder.tradingPriceVb.tradingNoPriceSizeLayer,
                                    false
                            )
                    val sizeTv = sizeItemLayout.findViewById<TextView>(R.id.size_tv)
                    val lp = sizeItemLayout.layoutParams as? ViewGroup.MarginLayoutParams
                    lp?.topMargin = 8.azDp.toInt()
                    sizeItemLayout.layoutParams = lp
                    sizeTv.text = it.size
                    holder.tradingPriceVb.tradingNoPriceSizeLayer.addView(sizeItemLayout)
                }
            } else {
                //尺码2
                holder.tradingPriceVb.tradingNoPriceSizeLayer.hide(true)
                holder.tradingPriceVb.tradingHavePriceSizeLayer.hide(false)
                if (holder.tradingPriceSizeAdapter == null) {
                    holder.tradingPriceSizeAdapter = DcBaseBinderAdapter(arrayListOf())
                    holder.tradingPriceSizeAdapter?.addItemBinder(TradingPriceSizeItemBinder())
                    holder.tradingPriceSizeAdapter?.addItemBinder(TradingLabelItemBinder())
                    holder.tradingPriceSizeLayoutManager =
                            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
                }
                holder.tradingPriceVb.tradingHavePriceSizeRv.adapter =
                        holder.tradingPriceSizeAdapter
                holder.tradingPriceVb.tradingHavePriceSizeRv.layoutManager =
                        holder.tradingPriceSizeLayoutManager
                holder.tradingPriceVb.tradingHavePriceSizeRv.addItemDecoration(object :
                        RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                            outRect: Rect,
                            view: View,
                            parent: RecyclerView,
                            state: RecyclerView.State
                    ) {
                        super.getItemOffsets(outRect, view, parent, state)
                        if (parent.getChildAdapterPosition(view) % 2 == 0) {
                            outRect.set(0, 0, 2.5.azDp.toInt(), 0)
                        } else {
                            outRect.set(2.5.azDp.toInt(), 0, 0, 0)
                        }
                    }
                })
                val sizeLabelList = arrayListOf<Any>()
                if (!item.label.isNullOrEmpty()) {
                    sizeLabelList.add(item.label ?: "")
                }
                item.goodsSizeList?.let {
                    sizeLabelList.addAll(it)
                }
                if (sizeLabelList.size > 6) {
                    holder.tradingPriceSizeAdapter?.setList(sizeLabelList.subList(0, 6))
                    holder.tradingPriceVb.tradingDetailsExpandLayer.hide(false)
                } else {
                    holder.tradingPriceSizeAdapter?.setList(sizeLabelList)
                    holder.tradingPriceVb.tradingDetailsExpandLayer.hide(true)
                }
                holder.tradingPriceVb.tradingDetailsExpandLayer.setOnShakeClickListener {
                    holder.tradingPriceVb.tradingDetailsExpandLayer.hide(true)
                    holder.tradingPriceSizeAdapter?.setList(sizeLabelList)
                }
            }
        }
    }


    private fun setSeries(holder: DynamicItemViewHolder, item: CircleDynamicBean) {
        holder.detailsScoreRatingBar.rating = (item.score ?: 0).toFloat()
        holder.headerScoreRatingBar.rating = (item.score ?: 0).toFloat()
        holder.certificationFailReasonTv.text = item.blockedReason
        if (item.isSeries) {
            if (showWhere == PostItemShowWhere.Detail) {
                holder.headerScoreLayer.hide(true)
                holder.detailsScoreLayer.hide(false)
            } else {
                holder.headerScoreLayer.hide(false)
                holder.detailsScoreLayer.hide(true)
            }
        } else {
            holder.headerScoreLayer.hide(true)
            holder.detailsScoreLayer.hide(true)
        }

    }

    private fun setLottery(holder: DynamicItemViewHolder, item: CircleDynamicBean) {
        val lotteryEntity = item.lotteryMap
        if (lotteryEntity == null) {
            holder.lotteryStateLayer.hide(true)
            holder.lotteryDetailsLayer.hide(true)
            holder.lotteryDetailsLayer.removeAllViews()
            holder.postLotteryView = null
        } else {
            if (showWhere != PostItemShowWhere.Detail) {
                holder.lotteryStateTv.text = lotteryEntity.getStatus().title
                holder.lotteryStateLayer.hide(false)
                holder.lotteryDetailsLayer.hide(true)
                holder.lotteryDetailsLayer.removeAllViews()
                holder.postLotteryView = null
            } else {
                holder.lotteryStateLayer.hide(true)
                holder.lotteryDetailsLayer.hide(false)
                if (holder.postLotteryView == null) {
                    holder.postLotteryView = PostLotteryDetailsView(context)
                    holder.lotteryDetailsLayer.addView(holder.postLotteryView)
                    holder.postLotteryView?.setData(lotteryEntity)
                }
            }
        }
    }

    private fun setParty(holder: DynamicItemViewHolder, item: CircleDynamicBean) {
        holder.partyLayer.hide(
                item.talkMap?.activity == null || showWhere == PostItemShowWhere.PartyDetails
        )
        holder.partyIv.load(item.talkMap?.activity?.icon)
        holder.partyNameTv.text = item.talkMap?.activity?.title
        holder.partyLayer.setOnShakeClickListener {
            UTHelper.commonEvent(
                    if (showWhere == PostItemShowWhere.Detail)
                        UTConstant.Circle.Dynamic_click_DetailActivity else
                        UTConstant.Circle.Dynamic_click_ListActivity,
                    "name",
                    item.talkMap?.activity?.title
            )
            DcUriRequest(context, CircleConstant.Uri.FIND_PARTY_DETAIL)
                    .putUriParameter(CircleConstant.UriParams.ID, item.talkMap?.activity?.id)
                    .start()
        }
    }


    private fun setItem(holder: DynamicItemViewHolder) {
        //不需要默认边距
        when (showWhere) {
            PostItemShowWhere.Detail -> {
                val layoutParams = holder.rootView.layoutParams as? ConstraintLayout.LayoutParams
                layoutParams?.marginStart = 0
                layoutParams?.marginEnd = 0
                holder.rootView.layoutParams = layoutParams
                holder.contentLayer.radius = 0f
                val lp = holder.bottomSpace.layoutParams
                lp.height = 8.azDp.toInt()
                holder.bottomSpace.layoutParams = lp
                holder.bottomLine.hide(true)
                holder.contentLayer.setBackgroundResource(R.color.white)
            }
            PostItemShowWhere.TalkDetail -> {
                val layoutParams = holder.rootView.layoutParams as? ConstraintLayout.LayoutParams
                layoutParams?.marginStart =
                        mContext.resources.getDimension(R.dimen.card_out_distance).toInt()
                layoutParams?.marginEnd =
                        mContext.resources.getDimension(R.dimen.card_out_distance).toInt()
                holder.rootView.layoutParams = layoutParams
                holder.contentLayer.radius = 8.azDp
                val lp = holder.bottomSpace.layoutParams
                lp.height = 12.azDp.toInt()
                holder.bottomSpace.layoutParams = lp
                holder.bottomSpace.background = null
                holder.bottomLine.hide(true)

            }
            PostItemShowWhere.SERIES_DETAIL -> {
                val layoutParams = holder.rootView.layoutParams as? ConstraintLayout.LayoutParams
                layoutParams?.marginStart = 12.azDp.toInt()
                layoutParams?.marginEnd = 12.azDp.toInt()
                holder.rootView.layoutParams = layoutParams
                holder.contentLayer.radius = 0f
                val lp = holder.bottomSpace.layoutParams
                lp.height = 0.azDp.toInt()
                holder.bottomSpace.layoutParams = lp
                //需要分割线

                if (getDataPosition(holder) == adapter.data.size - 1) {
                    holder.bottomLine.hide(true)
                    holder.contentLayer.setBackgroundResource(R.drawable.common_item_bottom_radius_8)
                } else {
                    holder.bottomLine.hide(false)
                    holder.contentLayer.setCardBackgroundColor(context.getColor(R.color.white))
                }
            }
            else -> {
                val layoutParams = holder.rootView.layoutParams as? ConstraintLayout.LayoutParams
                layoutParams?.marginStart =
                        mContext.resources.getDimension(R.dimen.card_out_distance).toInt()
                layoutParams?.marginEnd =
                        mContext.resources.getDimension(R.dimen.card_out_distance).toInt()
                holder.rootView.layoutParams = layoutParams
                holder.contentLayer.radius = 8.azDp
                val lp = holder.bottomSpace.layoutParams
                lp.height = 12.azDp.toInt()
                holder.bottomSpace.layoutParams = lp
                holder.bottomSpace.background = null
                holder.bottomLine.hide(true)
            }
        }
    }

    private fun setTalk(holder: DynamicItemViewHolder, item: CircleDynamicBean?) {
        val talkMap = item?.talkMap
        if (null == talkMap || talkMap.name.isNullOrEmpty() || hideTalkMap || item.isSeries) {
            holder.layoutTalk?.visibility = View.GONE
        } else {
            talkMap.let {
                //根据后台是否显示
                holder.layoutTalk?.visibility =
                        if (true == item.isShowTalk) View.VISIBLE else View.GONE
                holder.topicTitleTv?.text = talkMap.name
                holder.layoutTalk?.setOnClickListener {
                    if (item.isFashion) return@setOnClickListener
                    UTHelper.commonEvent(UTConstant.Circle.Dynamic_click_Topic, item.talkMap!!.name)
                    handAction(item, "其他")
                    topicClickAction?.invoke()
                    DcUriRequest(mContext, CircleConstant.Uri.FIND_TOPIC_DETAIL)
                            .putUriParameter(CircleConstant.UriParams.TOPIC_DETAIL_ID, talkMap.id!!)
                            .putExtra(CircleConstant.UriParams.DETAIL_FROM_FASHION, item.isFashion)
                            .start()
                }
            }
        }
    }

    private fun setHotRank(holder: DynamicItemViewHolder, item: CircleDynamicBean) {
        if (
                showWhere == PostItemShowWhere.Index ||
                showWhere == PostItemShowWhere.BASIC ||
                showWhere == PostItemShowWhere.HomeRecommend ||
                showWhere == PostItemShowWhere.TalkHomePage ||
                showWhere == PostItemShowWhere.DealHomePage ||

                showWhere == PostItemShowWhere.Profile ||
                showWhere == PostItemShowWhere.SearchResult ||

                showWhere == PostItemShowWhere.Detail ||

                showWhere == PostItemShowWhere.MineCollection ||
                showWhere == PostItemShowWhere.TalkDetail ||
                showWhere == PostItemShowWhere.FollowHomePage
        ) {
            if (item.discussMap != null) {
                holder.hotRankLayer.hide(false)
                holder.hotRankTitleTv.text = item.discussMap?.title ?: ""

                holder.hotRankLayer.setOnShakeClickListener {
                    UTHelper.commonEvent(if (showWhere == PostItemShowWhere.Detail) UTConstant.Circle.DynamicP_click_HotList
                    else UTConstant.Circle.Dynamic_click_HotList, "name", item.discussMap?.title
                            ?: "")
                    DcUriRequest(context, CircleConstant.Uri.HOT_RANK_DETAIL)
                            .putUriParameter(CircleConstant.UriParams.ID, item.discussMap?.id ?: "")
                            .start()
                }
            } else {
                holder.hotRankLayer.hide(true)
            }
        }
    }

    private fun setVote(holder: DynamicItemViewHolder, item: CircleDynamicBean) {
        val voteView = holder.voteView
        if (item.voteOptions.size <= 0) {
            voteView.visibility = View.GONE
            return
        }
        voteView.visibility = View.VISIBLE
        val voteItems: List<VoteOptionEntity> = item.voteOptions
        val voteData = LinkedHashMap<String, Int>()
        for ((title, count) in voteItems) {
            voteData[title] = count
        }
        voteView.initVoteData(item.voteOptions, item.user?.id == LoginUtils.getCurrentUser()?.id)
        voteView.setVoteListener(object : VoteListener {
            override fun onItemClick(view: View, index: Int, status: Boolean) {
                val voteOptions = item.voteOptions
                UTHelper.commonEvent(UTConstant.Circle.Dynamic_click_vote, "postId", item.id)
                if (voteClickListener != null) {
                    voteClickListener?.onVoteClick(
                            index,
                            voteOptions[index],
                            item.id,
                            getDataPosition(holder)
                    )
                } else {
                    val currentUser = LoginUtils.getCurrentUser()
                    if (null == currentUser) {
                        DcUriRequest(mContext, AccountConstant.Uri.INDEX)
                                .start()
                        return
                    }
                    voteCircle(
                            context,
                            voteOptions[index],
                            item.id ?: "",
                            index
                    )
                }
            }

            override fun initAnimation() {}
        })
    }

    private fun setBottom(holder: DynamicItemViewHolder, item: CircleDynamicBean) {
        holder.hideCollection(showWhere != PostItemShowWhere.Detail || item.isSeries)
        holder.tvCollection.isSelected = item.isCollect == true
        holder.imageViewCollection.isSelected = item.isCollect == true
        holder.tvRaiseCount.text = if (item.favorCount <= 0) {
            "点赞"
        } else {
            "${item.favorCount}"
        }
        holder.tvCommentCount.text = if (item.commentCount <= 0) {
            "评论"
        } else {
            "${item.commentCount}"
        }
        holder.ivRaiseSelected.isSelected = item.favored
        holder.tvRaiseCount.isSelected = item.favored
        holder.layoutComment.setOnClickListener {
            dynamicComment(item, holder)
        }
        holder.layoutRaise.setOnClickListener {
            dynamicRaise(item, holder, holder.layoutRaise)
        }
        holder.layoutRaise.setOnLongClickListener {
            if (!LoginUtils.isLoginAndRequestLogin(it.context)) {
                raise(item, holder)
                return@setOnLongClickListener true
            }
            var runnable: Runnable? = null
            runnable = Runnable {
                if (it.isPressed) {
                    LeonidsUtil.animAndVibrator(it)
                    it.postDelayed(runnable, 100)
                } else {
                    if (!item.favored) {
                        raise(item, holder)
                    }
                }
            }
            it.postDelayed(runnable, 100)
            true
        }

        holder.layoutShare.setOnClickListener {
            dynamicShare(item, holder)
        }
        holder.postCollectionLayer.setOnShakeClickListener {
            dynamicCollect(item, holder)
        }
        val isSeries = item.isSeries
        val certificationFail = item.blockedReason?.isNotEmpty() == true
        if (isSeries) {
            if (certificationFail) {
                holder.itemBottomActionLayer.hide(true)
                if (showWhere == PostItemShowWhere.Detail) {
                    holder.certificationFailLayer.hide(true)
                } else {
                    holder.certificationFailLayer.hide(false)
                }
            } else {
                holder.itemBottomActionLayer.hide(false)
                holder.certificationFailLayer.hide(true)
            }
        } else {
            holder.itemBottomActionLayer.hide(false)
            holder.certificationFailLayer.hide(true)
        }
        if (showWhere == PostItemShowWhere.BASIC) {
            holder.itemBottomActionLayer.hide(true)
        }
        if (certificationFail) {
            holder.reEditCertificationTv.setOnShakeClickListener {
                UTHelper.commonEvent(UTConstant.Circle.MyP_click_ReEdit)
                (mContext as AppCompatActivity).supportFragmentManager.authDialogShow(id = item.id)
            }
        }
    }

    private fun dynamicComment(
            item: CircleDynamicBean,
            holder: DynamicItemViewHolder
    ) {
        handAction(item, "评论")
        handTradingAction(item, "评论")
        UTHelper.commonEvent(UTConstant.Circle.Dynamic_click_Comment)
        if (postListener == null) {
            commentPost(item, getDataPosition(holder))
        } else {
            postListener?.commentPost(item, getDataPosition(holder))
        }
    }

    private fun dynamicRaise(
            item: CircleDynamicBean,
            holder: DynamicItemViewHolder,
            view: View
    ) {
        handAction(item, "点赞")
        handTradingAction(item, "点赞")
        UTHelper.commonEvent(UTConstant.Circle.Dynamic_click_Like)
        LeonidsUtil.execute(view, item.favored) {
            raise(item, holder)
        }
    }

    private fun dynamicCollect(
            item: CircleDynamicBean,
            holder: DynamicItemViewHolder
    ) {
        if (postListener == null) {
            collectPost(item, getDataPosition(holder))
        } else {
            postListener?.collectPost(item, getDataPosition(holder))
        }
    }

    fun dynamicShare(
            item: CircleDynamicBean,
            holder: DynamicItemViewHolder
    ) {
        handAction(item, "分享")
        handTradingAction(item, "分享")
        UTHelper.commonEvent(UTConstant.Circle.Dynamic_click_Share)
        if (postListener == null) {
            sharePost(item, getDataPosition(holder))
        } else {
            postListener?.sharePost(item, getDataPosition(holder))
        }
    }

    /**
     * 设置图片
     */
    private fun setImage(holder: DynamicItemViewHolder, item: CircleDynamicBean?) {
        val images = item?.images
        if (images.isNullOrEmpty()) {
            holder.postImgView.visibility = View.GONE
            return
        }
        holder.postImgView.setImage(item)
        holder.postImgView.onPostImgViewClickListener =
                object : PostImgView.OnPostImgViewClickListener {
                    override fun onItemClick() {
                        onItemClickListener?.onItemClick(adapter, holder, getDataPosition(holder))
                    }
                }
        holder.postImgView.onImageItemClickListener =
                object : PostImgView.OnImageItemClickListener {
                    override fun onImgClick(entity: Any, position: Int) {
                        handAction(item, "其他")
                        if (entity is CircleImageBean) {
                            val urlList = ArrayList<ImageInfo>()
                            if (images.isEmpty()) {
                                return
                            }
                            for (circleImageBean in images) {
                                val imageInfo = ImageInfo()
                                imageInfo.thumbnailUrl = circleImageBean.url
                                imageInfo.originUrl = circleImageBean.url
                                urlList.add(imageInfo)
                            }
                            UTHelper.commonEvent(
                                    UTConstant.Circle.Dynamic_click_image,
                                    item.user?.nickName?.let { setUtNickName(it) })
                            routeToImagePre(position, urlList)
                        }
                    }
                }
    }

    private fun routeToImagePre(
            index: Int,
            preList: ArrayList<ImageInfo>
    ) {
        if (preList.isEmpty()) {
            return
        }
        ImagePreview.getInstance()
                .setContext(mContext)
                .setIndex(index)
                .setEnableDragClose(true)
                .setFolderName("DingChao")
                .setShowCloseButton(true)
                .setLoadStrategy(ImagePreview.LoadStrategy.Default)
                .setErrorPlaceHolder(R.drawable.img_load)
                .setImageInfoList(preList)
                .start()
    }

    private fun setWebPageViewLink(holder: DynamicItemViewHolder, item: CircleDynamicBean) {
        val webpageLink = item.webpageLink
        val videoBean: CircleVideoBean? = item.video
        if (webpageLink != null) {
            if (!webpageLink.title.isNullOrEmpty() || !webpageLink.link.isNullOrEmpty()) {
                holder.linkDecTv.setEnableClick(false)
                holder.layoutLink.visibility = View.VISIBLE
                if (!TextUtils.isEmpty(webpageLink.title)) {
                    holder.linkDecTv.text = webpageLink.title
                    holder.linkDecTv.setBetterLink()
                } else {
                    holder.linkDecTv.text = webpageLink.link
                    holder.linkDecTv.setBetterLink()
                }
                holder.linkDecTv.isEnabled = false
                holder.linkDecTv.setBetterLink()

                holder.layoutLink.setOnClickListener {
                    handAction(item, "其他")
                    UTHelper.commonEvent(
                            UTConstant.Circle.Dynamic_click_CommodityLink,
                            item.user?.nickName?.let { setUtNickName(it) })
                    routeToLink(mContext, webpageLink.link ?: "")
                }
            }
            if (showWhere == PostItemShowWhere.Detail) {
                holder.ivWebLinkVideoLayout.visibility = View.GONE
                return
            }
            if (webpageLink.type == LINK_TYPE_VIDEO) {
                if (webpageLink.imageUrl.isNullOrEmpty()) {
                    holder.ivWebLinkVideoLayout.visibility = View.GONE
                } else {
                    holder.ivWebLinkVideoLayout.visibility = View.VISIBLE
                    setVideoPlayer(
                            null,
                            webpageLink.imageUrl,
                            webpageLink.title,
                            webpageLink.duration,
                            holder,
                            item
                    )
                    holder.detailPlayer.dynamicId = item.id
                }
            } else {
                holder.ivWebLinkVideoLayout.visibility = View.GONE
            }
        } else if (videoBean != null) {
            if (showWhere == PostItemShowWhere.Detail) {
                holder.ivWebLinkVideoLayout.visibility = View.GONE
                return
            }
            if (TextUtils.isEmpty(videoBean.url)) {
                holder.ivWebLinkVideoLayout.visibility = View.GONE
            } else {
                holder.ivWebLinkVideoLayout.visibility = View.VISIBLE
                setVideoPlayer(
                        videoBean.url!!,
                        videoBean.imageUrl,
                        videoBean.title,
                        videoBean.duration,
                        holder,
                        item
                )
                holder.detailPlayer.dynamicId = "${item.id}${CircleConstant.Extra.AUTO_PLAY}"
            }
            holder.layoutLink.visibility = View.GONE
        } else {
            holder.ivWebLinkVideoLayout.visibility = View.GONE
            holder.layoutLink.visibility = View.GONE
        }

    }

    private fun setVideoPlayer(
            url: String?,
            imageUrl: String?,
            title: String?,
            duration: Int?,
            holder: DynamicItemViewHolder,
            item: CircleDynamicBean
    ) {
        val thumb = ImageView(context).apply {
            setImageResource(R.drawable.empty_find)
            Glide.with(context).load(imageUrl).into(this)
        }
        holder.detailPlayer.apply {
            thumbImageViewLayout.isVisible = true
            thumbImageView = thumb
            isCollected = item.isCollect ?: false
            isFavored = item.favored
            favorCount = item.favorCount
            commentCount = item.commentCount
            url?.let {
                setUpLazy(url, true, null, null, title)
            }
            setThumbPlay(false)
            shrinkImageRes = R.drawable.ic_icon_shrink
            enlargeImageRes = R.drawable.ic_icon_enlarge
            titleTextView.isVisible = true
            titleTextView.text = title
            backButton.isVisible = false
            fullscreenButton.setOnClickListener {
                UTHelper.commonEvent(UTConstant.Circle.Dynamic_click_FullScreen)
                startWindowFullscreen(context, actionBar = false, statusBar = true)
            }
            dismissControlTime = 5000
            playTag = adapter.hashCode().toString()
            playPosition = getDataPosition(holder)
            isAutoFullWithSize = false
            setIsTouchWiget(false)
            videoDuration = duration ?: 0
            setVideoCallBack()
            setActionListener(object : DcVideoPlayer.ActionListener {
                override fun clickShare(view: View) {
                    dynamicShare(item, holder)
                }

                override fun clickCollect(view: View) {
                    dynamicCollect(item, holder)
                }

                override fun clickComment(view: View) {
                    dynamicComment(item, holder)
                }

                override fun clickRaise(view: View) {
                    dynamicRaise(item, holder, view)
                }

                override fun longClickRaise(view: View?, favored: Boolean) {
                    view?.let { raise ->
                        raise.setOnLongClickListener {
                            if (!LoginUtils.isLoginAndRequestLogin(it.context)) {
                                raise(item, holder)
                                return@setOnLongClickListener true
                            }
                            var runnable: Runnable? = null
                            runnable = Runnable {
                                if (it.isPressed) {
                                    LeonidsUtil.animAndVibrator(it)
                                    it.postDelayed(runnable, 100)
                                } else {
                                    if (!item.favored) {
                                        raise(item, holder)
                                    }
                                }
                            }
                            it.postDelayed(runnable, 100)
                            true
                        }
                    }
                }

            })

        }
    }

    private fun raise(
            item: CircleDynamicBean,
            holder: DynamicItemViewHolder
    ) {
        if (postListener == null) {
            raisePost(item, getDataPosition(holder))
        } else {
            postListener?.raisePost(item, getDataPosition(holder))
        }
    }

    private fun routeToLink(mContext: Context, link: String) {
        DcUriRequest(mContext, link).start()
    }

    /**
     * 设置文本信息
     */
    private fun setContentInfo(holder: DynamicItemViewHolder, entity: CircleDynamicBean) {
        val content: String? = entity.content
        val tvPostContent = holder.tvPostContent
        val allTxt = holder.tvAllText
        if (content.isNullOrEmpty()) {
            tvPostContent.visibility = View.GONE
            allTxt.visibility = View.GONE
            return
        }
        tvPostContent.apply {
            visibility = View.VISIBLE
            text = content
        }
        tvPostContent.maxLines = Int.MAX_VALUE
        val textWidth = SizeUtils.getWidth() - (12 + 15).azDp
        val lineCount = TextViewLinesUtil.getTextViewLines(tvPostContent, textWidth.toInt())
        if (maxLineLimit) {
            if (lineCount > maxLineLimitCount) {
                allTxt.visibility = View.VISIBLE
                allTxt.setOnClickListener {
                    handAction(entity, "其他")
                    routeToDetail(mContext, entity, false)
                }
            } else {
                allTxt.visibility = View.GONE
            }
            tvPostContent.maxLines = maxLineLimitCount
        } else {
            tvPostContent.maxLines = Int.MAX_VALUE
            allTxt.visibility = View.GONE
        }

        tvPostContent.setOnLongClickListener {
            showMenu(holder, entity)
            true
        }
        tvPostContent.setOnClickListener {
            handAction(entity, "其他")
            routeToDetail(mContext, entity, false)
        }
        //优化 显示链接
        tvPostContent.setBetterLink(0, entity.contentLink ?: arrayListOf())
    }

    private fun showMenu(holder: DynamicItemViewHolder, entity: CircleDynamicBean) {
        val popupMenuView = PopupMenuView(mContext)
        val optionMenuList: MutableList<OptionMenu> = ArrayList()
        val optionMenu = OptionMenu("复制")
        optionMenu.id = UIConstant.MenuId.COPY
        optionMenuList.add(optionMenu)
        popupMenuView.menuItems = optionMenuList
        popupMenuView.setSites(PopupView.SITE_TOP)
        popupMenuView.show(holder.tvPostContent)
        popupMenuView.setOnMenuClickListener { _: Int, _: OptionMenu? ->
            ClipboardHelper.copyInfo(entity.content?.trim())
            true
        }
    }

    private fun routeToDetail(
            mContext: Context,
            entity: CircleDynamicBean,
            isAutoComment: Boolean
    ) {
        if (showWhere == PostItemShowWhere.Detail) {
            return
        }
        if (showWhere == PostItemShowWhere.SERIES_DETAIL) {
            UTHelper.commonEvent(UTConstant.Shoes.ShoeseStoreP_click_AuthenticationDetails)
        }
        UTHelper.commonEvent(
                UTConstant.Circle.EnterSource_DynamicDetailsP,
                "position",
                if (showWhere == PostItemShowWhere.TalkDetail) "发现" else "首页"
        )
        DcUriRequest(mContext, CircleConstant.Uri.CIRCLE_DETAIL)
                .putUriParameter(CircleConstant.UriParams.ID, entity.id ?: "")
                .putUriParameter(CircleConstant.UriParams.IS_AUTO_COMMENT, isAutoComment.toString())
                .putUriParameter(CircleConstant.UriParams.DETAIL_FROM_SHOES, entity.isSeries.toString())
                .putExtra(CircleConstant.UriParams.DETAIL_FROM_FASHION, entity.isFashion)
                .start()
    }

    private fun routerToTradingTopic(product: TradingProductEntity) {
        DcUriRequest(mContext, CircleConstant.Uri.DEAL_DETAILS)
                .putUriParameter(CircleConstant.UriParams.ID, product.id)
                .start()
    }


    /**
     *设置用户信息
     */
    private fun setUserInfo(
            holder: DynamicItemViewHolder,
            user: CircleUserBean,
            item: CircleDynamicBean
    ) {
        holder.ivMedal.hide(user.achievementIconUrl.isNullOrEmpty())
        if (!user.achievementIconUrl.isNullOrEmpty()) {
            holder.ivMedal.load(user.achievementIconUrl)
            holder.ivMedal.setOnShakeClickListener {
                when (showWhere) {
                    PostItemShowWhere.SERIES_DETAIL -> {
                        UTHelper.commonEvent(
                                UTConstant.Circle.ShoeseStoreP_click_Medal,
                                "id",
                                user.achievementId
                        )
                    }
                    PostItemShowWhere.Detail -> {
                        UTHelper.commonEvent(
                                UTConstant.Circle.DynamicP_click_Medal,
                                "id",
                                user.achievementId
                        )
                    }
                    PostItemShowWhere.Profile -> {
                        if (user.id == LoginUtils.getCurrentUser()?.id) {
                            UTHelper.commonEvent(
                                    UTConstant.Circle.MyP_click_DynamicMedal,
                                    "id",
                                    user.achievementId
                            )
                        } else {
                            UTHelper.commonEvent(
                                    UTConstant.Circle.OthersP_click_DynamicMedal,
                                    "id",
                                    user.achievementId
                            )
                        }
                    }
                    else -> {
                        UTHelper.commonEvent(
                                UTConstant.Circle.HomeP_click_Medal,
                                "id",
                                user.achievementId
                        )
                    }
                }
                DcUriRequest(context, MineConstant.Uri.MEDAL_DETAIL)
                        .putUriParameter(MineConstant.PARAM_KEY.ID, user.id)
                        .putUriParameter(MineConstant.PARAM_KEY.MEDAL_ID, user.achievementId)
                        .start()
            }
        }

        holder.ivUserAvatar.apply {
            setBorderColor(ContextCompat.getColor(context, R.color.color_line))
            setBorderWidth(0.5.dp.toInt())
            setAvatarUrl(user.avatarUrl)
            setPendantUrl(user.avatarPendantUrl)
        }
        holder.tvUserNickname.text = user.nickName
        holder.tvUserNickname.isSelected = user.isVip == true
        if (user.isVerified!!) {
            holder.ivUserTag.visual()
        } else {
            holder.ivUserTag.hide()
        }
        when {
            //如果在推荐中，并且认证了显示认证，其他情况显示时间
            (!TextUtils.isEmpty(user.briefIntro)) && showWhere === PostItemShowWhere.HomeRecommend -> {
                holder.userTime.text = user.briefIntro
            }
            else -> {
                holder.userTime.hide(hideTime)
                holder.userTime.text = TimeUtils.getDynamicTime(item.createdAt)
            }
        }
        //显示在首页推荐中 或者 动态详情中
        if (showWhere == PostItemShowWhere.HomeRecommend || showWhere == PostItemShowWhere.TalkDetail
                || showWhere == PostItemShowWhere.TalkHomePage || showWhere == PostItemShowWhere.DealHomePage
                || showWhere == PostItemShowWhere.HotTalkDetails
        ) {
            if (item.isSticky) {
                setHotTagVisible(holder, true)
            } else {
                setHotTagVisible(holder, false)
            }
        } else {
            setHotTagVisible(holder, false)
        }

        holder.ivOverlay.setOnClickListener {
            handAction(item, "其他")
            handTradingAction(item, "更多")
            clickOverlay(item, getDataPosition(holder))
        }
    }

    private fun routeToUserDetail(data: CircleDynamicBean) {
        val user = data.user ?: return
        if (!data.isSeries) {
            DcUriRequest(mContext, MineConstant.Uri.DYNAMIC)
                    .putUriParameter(MineConstant.PARAM_KEY.ID, user.objectId ?: "")
                    .start()
        } else {
            DcUriRequest(mContext, MineConstant.Uri.DYNAMIC)
                    .putUriParameter(MineConstant.PARAM_KEY.ID, user.objectId ?: "")
                    .putUriParameter(MineConstant.PARAM_KEY.USER_DYNAMIC_PAGE, MineConstant.SERIES_PAGE)
                    .start()
        }
    }

    private fun setHotTagVisible(holder: DynamicItemViewHolder, visible: Boolean) {
        if (visible) {
            holder.iconHotTag.visibility = View.VISIBLE
            holder.iconHotTag.load(MobileHelper.getInstance().configData.hotPostIcon)
        } else {
            holder.iconHotTag.visibility = View.GONE
        }
    }

    private fun pushEventFavored(entity: CircleDynamicBean, favored: Boolean) {
        if (!ignoreEvent) {
            EventBus.getDefault().post(EventFavored(entity.id ?: "", favored, entity))
        }
    }

    /**
     * @param voteItemIndex 点击项在整个投票列表中的位置
     */
    fun voteCircle(
            context: Context,
            voteOptionEntity: VoteOptionEntity,
            postId: String,
            voteItemIndex: Int
    ) {
        circleApi.voteCircle(voteOptionEntity, postId, voteItemIndex)
                .subscribe({ res ->
                    if (!res.err && res.res != null) {
                        EventBus.getDefault().post(EventDynamicVoter(postId, res.res!!))
                    } else {
                        ToastUtil.getInstance()._short(context, res.msg)
                    }
                }, {
                    ToastUtil.getInstance()._short(context, it.message)
                })
    }

    private fun checkLogin(): DcLoginUser? {
        val user = AccountHelper.getInstance().user
        if (null == user) {
            DcUriRequest(mContext, AccountConstant.Uri.INDEX)
                    .start()
            return null
        }
        return user
    }

    override fun raisePost(entity: CircleDynamicBean, position: Int) {
        if (!LoginUtils.isLoginAndRequestLogin(mContext)) {
            return
        }
        val favored: Boolean = entity.favored
        CircleHelper.getInstance().communityPostFavored(entity.id, !favored)
                .subscribe({ res ->
                    if (!res.err && res.res != null) {
                        entity.favored = !favored
                        if (favored) {
                            entity.favorCount = entity.favorCount - 1
                        } else {
                            entity.favorCount = entity.favorCount + 1
                        }
                        pushEventFavored(entity, !favored)
                    } else {
                        ToastUtil.getInstance()._short(context, res.msg)
                    }
                }, { err ->
                    ToastUtil.getInstance()._short(context, err.message ?: "")
                })
    }

    override fun collectPost(entity: CircleDynamicBean, position: Int) {
        if (!LoginUtils.isLoginAndRequestLogin(mContext)) {
            return
        }
        handAction(entity, "收藏")
        handTradingAction(entity, "收藏")
        handCollect(entity, entity.isCollect != true)
        circleApi.collectPost(entity.id ?: "").subscribe({
            if (!it.err) {
                entity.isCollect = !(entity.isCollect ?: false)
                ToastUtil.getInstance()
                        ._short(context, if (entity.isCollect == true) "收藏成功" else "取消收藏成功")
                EventBus.getDefault().post(EventCollectChange(entity.id ?: "", entity))
            } else {
                ToastUtil.getInstance()._short(context, it.msg)
            }
        }, {
            ToastUtil.getInstance()._short(context, it.message ?: "")
        })
    }


    override fun commentPost(entity: CircleDynamicBean, position: Int) {
        routeToDetail(mContext, entity, true)
    }

    override fun sharePost(entity: CircleDynamicBean, position: Int) {
        val params = ShareParams()
        val title: String = if (entity.user != null) {
            entity.content ?: ""
        } else {
            mContext.resources.getString(R.string.circle_dynamic_share_title)
        }
        var imageUrl: String? = null
        if (null != entity.images && entity.images?.size ?: 0 > 0) {
            imageUrl = entity.images?.get(0)?.url
        } else {
            if (entity.user != null && !entity.user!!.avatarUrl.isNullOrEmpty()) {
                imageUrl = entity.user!!.avatarUrl
            } else {
                params.imageBitmap =
                        ContextCompat.getDrawable(context, R.drawable.default_avatar_rectangle)
                                ?.toBitmap()
            }
        }
        val content = "来自${entity.user?.nickName ?: ""}的动态"
        val linkUrl: String? = entity.shareLink
        val type = ShareType.CONFIG
        params.title = title
        params.content = content
        params.imageUrl = imageUrl
        params.link = linkUrl
        params.mpPath = SHARE_POST_DETAIL + entity.id
        type.params = params
        val shareDialog = cool.mobile.account.share.ShareDialog(mContext)
        shareDialog.shareType = type
        shareDialog.show()
        circleApi.trackShare(entity.id).subscribe({}, {})
    }

    override fun clickOverlay(data: CircleDynamicBean?, position: Int) {
        data?.let {
            showOverlayDialog(data, position)
        }
    }

    private fun followClick(
            data: CircleDynamicBean?,
            holder: DynamicItemViewHolder
    ) {
        if (checkLogin() == null) {
            return
        }
        data?.let {
            handTradingAction(it, "关注")
            MineHelper.getInstance().switchFollowState(true, it.user?.objectId
                    ?: "", object : ParseCallback<Int> {
                override fun onSucceed(data: Int?) {
                    EventBus.getDefault()
                            .post(EventFollowerChange(it.user?.objectId ?: "", true, data ?: 0))
                    ToastUtil.getInstance()._short(mContext, "关注成功")
                    EventBus.getDefault().post(EventFollowChange())
                    holder.followUser.visibility = View.GONE
                }

                override fun onFailed(errorCode: String?, errorMsg: String?) {
                    EventBus.getDefault().post(EventFollowFailed(errorMsg))
                    holder.followUser.visibility = View.VISIBLE
                }
            })
        }
    }

    //显示对话框
    private fun showOverlayDialog(entity: CircleDynamicBean, position: Int) {
        val overlayActionDialog: OverlayActionDialog =
                OverlayActionDialog.instance(entity, position)
        overlayActionDialog.updateShowWhere(showWhere)
        overlayActionDialog.setActionListener(object : PostActionListener {
            override fun postReportResult(isSuccess: Boolean, msg: String?, index: Int?) {
                if (isSuccess) {
                    showSuccessDialog(msg)
                    overlayActionDialog.dismissAllowingStateLoss()
                } else {
                    showFailedDialog(msg)
                }
            }

            override fun userBlockResult(isSuccess: Boolean, msg: String?, index: Int?) {
                if (isSuccess) {
                    showSuccessDialog(msg)
                    index?.let {
                        if (showWhere == PostItemShowWhere.Profile) {
                            return@let
                        }
                    }
                    overlayActionDialog.dismissAllowingStateLoss()
                } else {
                    showFailedDialog(msg)
                }
            }

            override fun postBlockResult(isSuccess: Boolean, msg: String?, index: Int?) {
                if (isSuccess) {
                    showSuccessDialog(msg)
                    overlayActionDialog.dismissAllowingStateLoss()
                } else {
                    showFailedDialog(msg)
                }
            }

            override fun deletePostResult(isSuccess: Boolean, msg: String?, index: Int?) {
                if (isSuccess) {
                    showSuccessDialog("删除成功")
                    overlayActionDialog.dismissAllowingStateLoss()
                } else {
                    showFailedDialog(msg)
                }
            }
        })
        (mContext as? AppCompatActivity?)?.supportFragmentManager?.let {
            overlayActionDialog.showDialog(it, "overlay")
        }
    }

    private fun isHaveVideo(item: CircleDynamicBean?): Boolean {
        return (item?.webpageLink != null && item.webpageLink?.type == LINK_TYPE_VIDEO) || item?.video != null
    }

    fun onTimerUpdate(onTimeEnd: (() -> Unit)?) {
        holders.forEach {
            it.postLotteryView?.updateState(onTimeEnd)
        }
    }

}

interface OnConvertListener<T, H> {
    fun onConvert(h: H, t: T)
}
