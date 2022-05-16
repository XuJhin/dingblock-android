package cool.dingstock.home.adapter.item

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.constant.*
import cool.dingstock.appbase.entity.bean.circle.CircleBadge
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.bean.circle.CircleImageBean
import cool.dingstock.appbase.entity.bean.circle.CircleVideoBean
import cool.dingstock.appbase.entity.event.circle.EventFavored
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.ext.visual
import cool.dingstock.appbase.imageload.GlideHelper
import cool.dingstock.appbase.net.api.circle.CircleHelper
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.ClipboardHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.widget.leonids.LeonidsUtil
import cool.dingstock.appbase.widget.menupop.OptionMenu
import cool.dingstock.appbase.widget.menupop.PopupMenuView
import cool.dingstock.appbase.widget.menupop.PopupView
import cool.dingstock.home.databinding.ItemNewSaleBinding
import cool.dingstock.imagepre.ImagePreview
import cool.dingstock.imagepre.bean.ImageInfo
import cool.dingstock.lib_base.util.StringUtils
import cool.dingstock.lib_base.util.TimeUtils
import cool.dingstock.post.R
import cool.dingstock.post.RoundBackgroundColorSpan
import cool.dingstock.post.item.LINK_TYPE_VIDEO
import cool.dingstock.post.item.PostItemShowWhere
import cool.dingstock.post.view.DcVideoPlayer
import cool.dingstock.post.view.PostImgView
import org.greenrobot.eventbus.EventBus

class NewSaleItem: BaseViewBindingItemBinder<CircleDynamicBean, ItemNewSaleBinding>() {
    var maxLineLimit = true
    var maxLineLimitCount = 6

    var postListener: (() -> Unit)? = null
    var showWhere: PostItemShowWhere = PostItemShowWhere.Default

    override fun provideViewBinding(parent: ViewGroup, viewType: Int): ItemNewSaleBinding {
        return ItemNewSaleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onConvert(vb: ItemNewSaleBinding, data: CircleDynamicBean) {
        if (!maxLineLimit) {
            vb.root.setPadding(0)
            vb.root.setBackgroundColor(ContextCompat.getColor(vb.root.context, R.color.color_gray))
        }
        data.user?.let { user ->
            vb.ivBrandLogo.apply {
                setBorderColor(ContextCompat.getColor(context, R.color.color_line))
                setBorderWidth(0.5.dp.toInt())
                setAvatarUrl(user.avatarUrl)
                setPendantUrl(user.avatarPendantUrl)
            }
            vb.tvBrandName.text = user.nickName
            vb.tvBrandName.isSelected = user.isVip == true
            vb.ivMedal.hide(user.achievementIconUrl.isNullOrEmpty())
            if (!user.achievementIconUrl.isNullOrEmpty()) {
                vb.ivMedal.load(user.achievementIconUrl)
                vb.ivMedal.setOnShakeClickListener {
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
            if (user.isVerified == true) {
                vb.userIsVerified.visual()
            } else {
                vb.userIsVerified.hide()
            }
        }
        vb.tvTime.text = TimeUtils.getFashionTime(data.createdAt)
        setImage(vb, data)
        setWebPageViewLink(vb, data)
        setContentInfo(vb, data)
        setBottom(vb, data)
        setGodComment(vb, data)
    }

    @SuppressLint("SetTextI18n")
    private fun setBottom(
        vb: ItemNewSaleBinding,
        data: CircleDynamicBean
    ) {
        vb.postLayoutComment.setOnClickListener {
            dynamicComment(data)
        }
        vb.postLayoutRaise.setOnClickListener {
            dynamicRaise(data, vb)
        }
        vb.postLayoutRaise.setOnLongClickListener {
            if (!LoginUtils.isLoginAndRequestLogin(it.context)) {
                raisePost(data, vb)
                return@setOnLongClickListener true
            }
            var runnable: Runnable? = null
            runnable = Runnable {
                if (it.isPressed) {
                    LeonidsUtil.animAndVibrator(it)
                    it.postDelayed(runnable, 100)
                } else {
                    if (!data.favored) {
                        raisePost(data, vb)
                    }
                }
            }
            it.postDelayed(runnable, 100)
            true
        }
        vb.tvPostRaise.text = if (data.favorCount <= 0) {
            "点赞"
        } else {
            "${data.favorCount}"
        }
        vb.tvPostComment.text = if (data.commentCount <= 0) {
            "评论"
        } else {
            "${data.commentCount}"
        }
        vb.ivPostRaise.isSelected = data.favored
        vb.tvPostRaise.isSelected = data.favored
        vb.tvViewCount.text = "浏览${data.viewCount}"
    }

    private fun dynamicRaise(
        data: CircleDynamicBean,
        vb: ItemNewSaleBinding
    ) {
        handAction(data, "点赞")
        UTHelper.commonEvent(UTConstant.Circle.Dynamic_click_Like)
        LeonidsUtil.execute(vb.postLayoutRaise, data.favored) {
            raisePost(data, vb)
        }
    }

    private fun dynamicComment(data: CircleDynamicBean) {
        handAction(data, "评论")
        UTHelper.commonEvent(UTConstant.Circle.Dynamic_click_Comment)
        if (postListener == null) {
            commentPost(data)
        } else {
            postListener?.invoke()
        }
    }

    /**
     * 设置图片
     */
    private fun setImage(vb: ItemNewSaleBinding, item: CircleDynamicBean?) {
        val images = item?.images
        if (images.isNullOrEmpty()) {
            vb.postImgView.visibility = View.GONE
            return
        }
        vb.postImgView.setImage(item)
        vb.postImgView.onPostImgViewClickListener =
            object : PostImgView.OnPostImgViewClickListener {
                override fun onItemClick() {
                    handAction(item, "其他")
                    routeToDetail(context, item, false)
                }
            }
        vb.postImgView.onImageItemClickListener =
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
                        UTHelper.commonEvent(UTConstant.Circle.Dynamic_click_image, item.user?.nickName?.let { setUtNickName(it) })
                        routeToImagePre(position, urlList)
                    }
                }
            }
    }

    private fun setVideoPlayer(
        url: String?,
        imageUrl: String?,
        title: String?,
        duration: Int?,
        vb: ItemNewSaleBinding,
        item: CircleDynamicBean
    ) {
        val thumb = ImageView(context).apply {
            setImageResource(R.drawable.empty_find)
            Glide.with(context).load(imageUrl).into(this)
        }
        vb.detailPlayer.apply {
            thumbImageViewLayout.isVisible = true
            thumbImageView = thumb
            isCollected = item.isCollect ?: false
            isFavored = item.favored
            favorCount = item.favorCount
            commentCount = item.commentCount
            shareHide = true
            collectHide = true
            setUpLazy(url, true, null, null, title)
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
            playTag = adapter.hashCode().toString()
            holder?.let {
                playPosition = getDataPosition(it)
            }
            isAutoFullWithSize = false
            setIsTouchWiget(false)
            videoDuration = duration ?: 0
            setActionListener(object : DcVideoPlayer.ActionListener {
                override fun clickShare(view: View) {

                }

                override fun clickCollect(view: View) {

                }

                override fun clickComment(view: View) {
                    dynamicComment(item)
                }

                override fun clickRaise(view: View) {
                    dynamicRaise(item, vb)
                }

                override fun longClickRaise(view: View?, favored: Boolean) {
                    view?.let { raise ->
                        raise.setOnLongClickListener {
                            if (!LoginUtils.isLoginAndRequestLogin(it.context)) {
                                raisePost(item, vb)
                                return@setOnLongClickListener true
                            }
                            var runnable: Runnable? = null
                            runnable = Runnable {
                                if (it.isPressed) {
                                    LeonidsUtil.animAndVibrator(it)
                                    it.postDelayed(runnable, 100)
                                } else {
                                    if (!item.favored) {
                                        raisePost(item, vb)
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

    private fun setWebPageViewLink(vb: ItemNewSaleBinding, item: CircleDynamicBean) {
        val webpageLink = item.webpageLink
        val videoBean: CircleVideoBean? = item.video
        if (webpageLink != null) {
            if (!webpageLink.title.isNullOrEmpty() || !webpageLink.link.isNullOrEmpty()) {
                vb.linkLayout.linkDecTv.setEnableClick(false)
                vb.linkLayout.circleItemDynamicLinkLayer.visibility = View.VISIBLE
                if (!TextUtils.isEmpty(webpageLink.title)) {
                    vb.linkLayout.linkDecTv.text = webpageLink.title
                    vb.linkLayout.linkDecTv.setBetterLink()
                } else {
                    vb.linkLayout.linkDecTv.text = webpageLink.link
                    vb.linkLayout.linkDecTv.setBetterLink()
                }
                vb.linkLayout.linkDecTv.isEnabled = false
                vb.linkLayout.linkDecTv.setBetterLink()

                vb.linkLayout.circleItemDynamicLinkLayer.setOnClickListener {
                    handAction(item, "其他")
                    UTHelper.commonEvent(UTConstant.Circle.Dynamic_click_CommodityLink, item.user?.nickName?.let { setUtNickName(it) })
                    routeToLink(context, webpageLink.link ?: "")
                }
            }

            if (showWhere == PostItemShowWhere.Detail) {
                vb.webPageVideoLayer.visibility = View.GONE
                return
            }
            if (webpageLink.type == LINK_TYPE_VIDEO) {
                if (webpageLink.imageUrl.isNullOrEmpty()) {
                    vb.webPageVideoLayer.visibility = View.GONE
                } else {
                    vb.webPageVideoLayer.visibility = View.VISIBLE
                    setVideoPlayer(null, webpageLink.imageUrl, webpageLink.title, webpageLink.duration, vb, item)
                    vb.detailPlayer.dynamicId = item.id
                }
            } else {
                vb.webPageVideoLayer.visibility = View.GONE
            }
        } else if (videoBean != null) {
            if (showWhere == PostItemShowWhere.Detail) {
                vb.webPageVideoLayer.visibility = View.GONE
                return
            }
            if (TextUtils.isEmpty(videoBean.url)) {
                vb.webPageVideoLayer.visibility = View.GONE
            } else {
                vb.webPageVideoLayer.visibility = View.VISIBLE
                setVideoPlayer(videoBean.url!!, videoBean.imageUrl, videoBean.title, videoBean.duration, vb, item)
                vb.detailPlayer.dynamicId = "${item.id}${CircleConstant.Extra.AUTO_PLAY}"
            }
            vb.linkLayout.circleItemDynamicLinkLayer.visibility = View.GONE
        } else {
            vb.webPageVideoLayer.visibility = View.GONE
            vb.linkLayout.circleItemDynamicLinkLayer.visibility = View.GONE
        }
    }

    /**
     * 设置文本信息
     */
    private fun setContentInfo(vb: ItemNewSaleBinding, entity: CircleDynamicBean) {
        val content: String? = entity.content
        val tvPostContent = vb.tvContent
        val allTxt = vb.tvAll
        if (content.isNullOrEmpty()) {
            tvPostContent.visibility = View.GONE
            allTxt.visibility = View.GONE
            return
        }
        tvPostContent.apply {
            visibility = View.VISIBLE
        }
        tvPostContent.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                val lineCount: Int = tvPostContent.lineCount
                if (maxLineLimit) {
                    if (lineCount > maxLineLimitCount) {
                        allTxt.visibility = View.VISIBLE
                        allTxt.setOnClickListener {
                            handAction(entity, "其他")
                            routeToDetail(context, entity, false)
                        }
                    } else {
                        allTxt.visibility = View.GONE
                    }
                    tvPostContent.maxLines = maxLineLimitCount
                } else {
                    tvPostContent.maxLines = Int.MAX_VALUE
                    allTxt.visibility = View.GONE
                }
                tvPostContent.viewTreeObserver.removeOnPreDrawListener(this)
                return false
            }
        })
        //todo  好像是已经取消了（写在这里）
        val badge: CircleBadge? = entity.badge
        if (null != badge && !TextUtils.isEmpty(badge.color) && !TextUtils.isEmpty(badge.text)) {
            val spannableString = SpannableString(badge.text + content)
            spannableString.setSpan(
                RoundBackgroundColorSpan(
                    Color.parseColor(badge.color),
                Color.parseColor("#FFFFFF")),
                0, badge.text!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            tvPostContent.text = spannableString
        } else {
            tvPostContent.text = content
        }

        tvPostContent.setOnLongClickListener {
            showMenu(vb, entity)
            true
        }
        tvPostContent.setOnClickListener {
            handAction(entity, "其他")
            routeToDetail(context, entity, false)
        }
        //优化 显示链接
        tvPostContent.setBetterLink()
    }

    private fun setGodComment(vb: ItemNewSaleBinding, item: CircleDynamicBean) {
        val hotComment = item.hotComment
        if (null == hotComment) {
            vb.godComment.goodCommentLayer.visibility = View.GONE
            return
        }
        vb.godComment.goodCommentLayer.visibility = View.VISIBLE
        vb.godComment.godCommentTv.setGodComment(hotComment.userMap?.nickName ?: "", hotComment.content
            ?: "")
        vb.godComment.godCommentTv.setOnClickListener {
            handAction(item, "其他")
            routeToDetail(context, item, false)
        }
        if (hotComment.staticImg == null) {
            vb.godComment.godCommentImgLayer.hide(true)
            return
        }
        hotComment.staticImg?.let {
            vb.godComment.godCommentImgLayer.hide(false)
            GlideHelper.loadGifInGlideCacheWithOutWifi(context, vb.godComment.godCommentIv, vb.godComment.godCommentGifTag, hotComment.dynamicImg?.url, it.url, 6f, true)
        }
        vb.godComment.godCommentIv.setOnClickListener {
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
            if(GlideHelper.isGif(hotComment.dynamicImg?.url)){
                vb.godComment.godCommentGifTag.hide(true)
                vb.godComment.godCommentGifTag.postDelayed({
                    GlideHelper.loadRadiusImage(hotComment.dynamicImg?.url, vb.godComment.godCommentIv, context, 4f)
                }, 300)
            }
        }
    }

    private fun routeToImagePre(index: Int, preList: ArrayList<ImageInfo>) {
        if (preList.isEmpty()) {
            return
        }
        ImagePreview.getInstance()
            .setContext(context)
            .setIndex(index)
            .setEnableDragClose(true)
            .setFolderName("DingChao")
            .setShowCloseButton(true)
            .setLoadStrategy(ImagePreview.LoadStrategy.Default)
            .setErrorPlaceHolder(R.drawable.img_load)
            .setImageInfoList(preList)
            .start()
    }

    private fun routeToVideoPlay(mContext: Context, item: CircleDynamicBean) {
        val webpageLink = item.webpageLink
        if (webpageLink?.type != null && webpageLink.type == "VIDEO") {
            DcUriRequest(mContext, CommonConstant.Uri.VIDEO_VIEW)
                .putUriParameter(CommonConstant.UriParams.CIRCLE_DYNAMIC_ID, item.id ?: "")
                .putUriParameter(
                    CommonConstant.UriParams.CIRCLE_VIDEO_COVER, webpageLink.imageUrl
                    ?: "")
                .putUriParameter(
                    CommonConstant.UriParams.CIRCLE_VIDEO_TITLE, webpageLink.title
                    ?: "")
                .start()
        } else {
            routeToLink(mContext, webpageLink?.link ?: "")
        }
    }

    private fun routeToLink(mContext: Context, link: String) {
        DcUriRequest(mContext, link)
            .start()
    }

    private fun routeToDetail(mContext: Context, entity: CircleDynamicBean, isAutoComment: Boolean) {
        if (showWhere == PostItemShowWhere.Detail) {
            return
        }
        UTHelper.commonEvent(UTConstant.Fashion.TrendyListP_click_Allcard)
        DcUriRequest(mContext, CircleConstant.Uri.CIRCLE_DETAIL)
            .putUriParameter(CircleConstant.UriParams.ID, entity.id ?: "")
            .putUriParameter(CircleConstant.UriParams.IS_AUTO_COMMENT, isAutoComment.toString())
            .putExtra(CircleConstant.UriParams.DETAIL_FROM_FASHION, entity.isFashion)
            .putExtra(CircleConstant.UriParams.DETAIL_FROM_FASHION_LIST, true)
            .start()
    }

    private fun commentPost(entity: CircleDynamicBean) {
        routeToDetail(context, entity, true)
    }

    private fun raisePost(entity: CircleDynamicBean, vb: ItemNewSaleBinding) {
        if (!LoginUtils.isLoginAndRequestLogin(context)) {
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
                }
            }, {})
    }

    private fun pushEventFavored(entity: CircleDynamicBean, favored: Boolean) {
        EventBus.getDefault().post(EventFavored(entity.id ?: "", favored, entity))
    }

    private fun showMenu(vb: ItemNewSaleBinding, entity: CircleDynamicBean) {
        val popupMenuView = PopupMenuView(context)
        val optionMenuList: MutableList<OptionMenu> = ArrayList()
        val optionMenu = OptionMenu("复制")
        optionMenu.id = UIConstant.MenuId.COPY
        optionMenuList.add(optionMenu)
        popupMenuView.menuItems = optionMenuList
        popupMenuView.setSites(PopupView.SITE_TOP)
        popupMenuView.show(vb.tvContent)
        popupMenuView.setOnMenuClickListener { _: Int, _: OptionMenu? ->
            ClipboardHelper.copyInfo(entity.content?.trim())
            true
        }
    }

    private fun handAction(entity: CircleDynamicBean?, action: String) {
        entity.let {
            val userName = it?.user?.id?.let { it1 -> setUtNickName(it1) }
            val isNoImages = entity?.images.isNullOrEmpty()
            if (isHaveVideo(it)) {
                //视频动态
                UTHelper.commonEvent(UTConstant.Circle.Dynamic_click_VideoDynamic, "UserName", userName, "ActionName", action)
            } else if (!isNoImages) {
                //图文动态
                UTHelper.commonEvent(UTConstant.Circle.Dynamic_click_GraphicDynamic, "UserName", userName, "ActionName", action)
            } else {
                //文字动态
                UTHelper.commonEvent(UTConstant.Circle.Dynamic_click_TextDynamic, "UserName", userName, "ActionName", action)
            }
        }
    }

    private fun isHaveVideo(item: CircleDynamicBean?): Boolean {
        return (item?.webpageLink != null && item.webpageLink?.type == LINK_TYPE_VIDEO) || item?.video != null
    }

    private fun setUtNickName(id: String): String {
        return when (id) {
            "BofjN680M1" -> "盯链官方"
            "snbgCVAQoS" -> "盯链线报"
            "Aym3iCcizC" -> "盯链秒杀日记"
            else -> "其他"
        }
    }
}
