package cool.dingstock.post.item

import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseBinderAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.adapter.itembinder.OnItemClickListener
import cool.dingstock.appbase.adapter.itembinder.OnItemLongClickListener
import cool.dingstock.appbase.constant.CircleConstant
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.circle.*
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.imageload.GlideHelper
import cool.dingstock.appbase.net.api.calendar.CalendarHelper
import cool.dingstock.appbase.net.api.circle.CircleHelper
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.ClipboardHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.widget.leonids.LeonidsUtil
import cool.dingstock.imagepre.ImagePreview
import cool.dingstock.imagepre.bean.ImageInfo
import cool.dingstock.lib_base.json.JSONHelper
import cool.dingstock.lib_base.util.*
import cool.dingstock.post.CustomMovementMethod
import cool.dingstock.post.R
import cool.dingstock.post.adapter.holder.DynamicCommentViewHolder

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/18  11:14
 */
class DynamicCommentItemBinder :
    DcBaseItemBinder<CircleDynamicDetailCommentsBean, DynamicCommentViewHolder>() {


    val VIEW_TYPE = 10231

    //    @StringDef([Style.DETAIL_MAIN, Style.DETAIL_SUB, Style.NORMAL, Style.HOME_RAFFLE])
    @Retention(AnnotationRetention.SOURCE)
    annotation class Style {
        companion object {
            var DETAIL_MAIN = "detail_main"
            var DETAIL_SUB = "detail_sub"
            var NORMAL = "normal"
            var HOME_RAFFLE = "home_raffle"
            var SHOES_SERIES = "shoes_series"
        }
    }

    var mListener: ActionListener? = null

    var replyListener: ReplyListener? = null

    var mainBean: CircleDynamicBean? = null

    @Style
    var mStyle = Style.NORMAL

    private var recycledViewPool: RecyclerView.RecycledViewPool

    var startX = 0f
    var startY = 0f

    init {
        recycledViewPool = RecyclerView.RecycledViewPool()

    }

    interface ActionListener {
        fun onContentClick(
            item: DynamicCommentViewHolder?,
            data: CircleDynamicDetailCommentsBean,
            sectionPos: Int
        )
    }

    interface ReplyListener {
        fun onReplyClick(
            item: DynamicCommentViewHolder?,
            data: CircleDynamicDetailCommentsBean,
            sectionPos: Int
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicCommentViewHolder {
        val dynamicCommentViewHolder = DynamicCommentViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.dynamic_comment_item_layout, parent, false)
        )
        initSecondAdapter(dynamicCommentViewHolder)
        return dynamicCommentViewHolder
    }

    private fun initSecondAdapter(holder: DynamicCommentViewHolder) {
        holder.commentSecondAdapter = DcBaseBinderAdapter(arrayListOf())
        holder.commentSecondItemBinder = CommentSecondItemBinder()
        holder.commentSecondShortItemBinder = CommentSecondShortItemBinder()
        holder.commentSecondAdapter.addItemBinder(
            CircleDynamicDetailCommentsBean::class.java,
            holder.commentSecondItemBinder
        )
        holder.commentSecondAdapter.addItemBinder(
            String::class.java,
            holder.commentSecondShortItemBinder
        )
        holder.subContentRecycler.adapter = holder.commentSecondAdapter
        holder.subContentRecycler.layoutManager =
            object : LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }

                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
    }

    override fun onConvert(
        holder: DynamicCommentViewHolder,
        data: CircleDynamicDetailCommentsBean
    ) {
        setUserInfo(holder, data)
        setTimeTxt(holder, data)
        setLikeInfo(holder, data)
        setContentInfo(holder, data)
        setSubComments(holder, data)
        setImg(holder, data)
    }

    private fun setImg(holder: DynamicCommentViewHolder, data: CircleDynamicDetailCommentsBean) {
        if (StringUtils.isEmpty(data.staticImg?.url)) {
            holder.imgFra.hide(true)
        } else {
            holder.imgFra.hide(false)
            holder.imgGifTag.hide(true)
            //设置图片宽高
            val width: Int = data.staticImg?.width ?: 0
            val height: Int = data.staticImg?.height ?: 0
            val imageIvLayoutParams: ViewGroup.LayoutParams = holder.imgView.layoutParams
            if (width <= 0 || height <= 0) {
                imageIvLayoutParams.width = SizeUtils.dp2px(100f)
                imageIvLayoutParams.height = SizeUtils.dp2px(100f)
            } else if (width > height) {
                imageIvLayoutParams.width = SizeUtils.dp2px(100f)
                imageIvLayoutParams.height =
                    (SizeUtils.dp2px(100f) * height.toFloat() / width).toInt()
            } else {
                imageIvLayoutParams.width =
                    (SizeUtils.dp2px(100f) * width.toFloat() / height).toInt()
                imageIvLayoutParams.height = SizeUtils.dp2px(100f)
            }
            holder.imgView.layoutParams = imageIvLayoutParams
            GlideHelper.loadGifInGlideCacheWithOutWifi(
                context,
                holder.imgView,
                holder.imgGifTag,
                data.dynamicImg?.url,
                data.staticImg!!.url,
                4f,
                true
            )
        }
        holder.imgView.setOnClickListener {
            val urlList = ArrayList<ImageInfo>()
            val imageInfo = ImageInfo()
            imageInfo.originUrl = data.dynamicImg?.url ?: data.staticImg?.url
            imageInfo.thumbnailUrl = data.staticImg?.url
            if (StringUtils.isEmpty(imageInfo.originUrl)) {
                return@setOnClickListener
            }
            urlList.add(imageInfo)
            routeToImagePre(0, urlList)
            if(GlideHelper.isGif(data.dynamicImg?.url)){
                holder.imgGifTag.hide(true)
                holder.imgView.postDelayed({
                    GlideHelper.loadRadiusImage(data.dynamicImg?.url, holder.imgView, context, 4f)
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
            .setLoadStrategy(ImagePreview.LoadStrategy.NetworkAuto)
            .setErrorPlaceHolder(R.drawable.img_load)
            .setImageInfoList(preList)
            .start()
    }

    fun getViewType(): Int {
        return VIEW_TYPE
    }

    private fun setSubComments(
        dynamicHolder: DynamicCommentViewHolder,
        data: CircleDynamicDetailCommentsBean
    ) {

        val subCommentList: ArrayList<CircleDynamicDetailCommentsBean> = ArrayList(
            data.subComments ?: arrayListOf()
        )
        if (CollectionUtils.isEmpty(subCommentList)
            || mStyle == Style.DETAIL_MAIN || mStyle == Style.DETAIL_SUB
        ) {
            dynamicHolder.subContentRecycler.hide(true)
            return
        }
        dynamicHolder.subContentRecycler.hide(false)
        val itemClickListener = object : OnItemClickListener {
            override fun onItemClick(
                adapter: BaseBinderAdapter,
                holder: BaseViewHolder,
                position: Int
            ) {
                val model = when (mStyle) {
                    Style.HOME_RAFFLE -> CircleConstant.Constant.Model_Product
                    Style.SHOES_SERIES -> CircleConstant.Constant.Model_Shoes
                    else -> ""
                }
                DcUriRequest(context, CircleConstant.Uri.CIRCLE_SUB_COMMENTS)
                    .dialogBottomAni()
                    .putUriParameter(
                        CircleConstant.UriParams.ONE_POS,
                        getDataPosition(dynamicHolder).toString()
                    )
                    .putUriParameter(CircleConstant.UriParams.ID, data.objectId ?: "")
                    .putUriParameter(
                        CircleConstant.UriParams.ONE_COMMENT_ID, data.objectId
                            ?: ""
                    )
                    .putExtra(CircleConstant.Extra.KEY_COMMENT, JSONHelper.toJson(data))
                    .putExtra(CircleConstant.Extra.Model, model)
                    .putExtra(CircleConstant.Extra.CIRCLE_DYNAMIC, JSONHelper.toJson(mainBean))
                    .start()
            }
        }
        dynamicHolder.commentSecondItemBinder.onItemClickListener = itemClickListener
        dynamicHolder.commentSecondShortItemBinder.onItemClickListener = itemClickListener

        dynamicHolder.commentSecondAdapter.setList(subCommentList)
        if (data.subCommentsCount > 3) {
            dynamicHolder.commentSecondAdapter.addData(data.subCommentsCount.toString())
        }

        dynamicHolder.commentSecondItemBinder.onItemLongCLickListener =
            object : OnItemLongClickListener {
                override fun onItemClick(
                    adapter: BaseBinderAdapter,
                    holder: BaseViewHolder,
                    position: Int
                ): Boolean {
                    (holder as? CommentSecondItemViewHolder)?.let {
                        ClipboardHelper.showMenu(context, "", holder.textView)
                    }
                    return true
                }
            }
    }

    private fun setContentInfo(
        holder: DynamicCommentViewHolder,
        data: CircleDynamicDetailCommentsBean
    ) {
        holder.contentTxt.isVisible = !StringUtils.isEmpty(data.content)
        if (mStyle == Style.DETAIL_SUB) {
            val mentioned: CircleMentionedBean? = data.mentioned
            if (null == mentioned) {
                holder.contentTxt.text = data.content
            } else {
                val user = mentioned.user
                if (null == user) {
                    holder.contentTxt.text = data.content
                } else {
                    val replayStr = "回复 "
                    val content = replayStr + user.nickName + "： " + data.content
                    val spannable = SpannableString(content)
                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            DcUriRequest(context, MineConstant.Uri.DYNAMIC)
                                .putUriParameter(
                                    MineConstant.PARAM_KEY.ID, user.id
                                        ?: user.objectId ?: ""
                                )
                                .start()
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.color = ContextCompat.getColor(context, R.color.color_blue)
                            ds.isUnderlineText = false
                        }
                    }
                    spannable.setSpan(
                        clickableSpan,
                        replayStr.length, replayStr.length + (user.nickName?.length ?: 0),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    holder.contentTxt.movementMethod = LinkMovementMethod.getInstance()
                    holder.contentTxt.text = spannable

                }
            }
        } else {
            val content: String? = data.content
            holder.contentTxt.text = content
        }
        holder.contentTxt.movementMethod = CustomMovementMethod.getInstance()
        holder.contentTxt.setOnClickListener(View.OnClickListener { v: View? ->
            onItemClickListener?.onItemClick(adapter, holder, getDataPosition(holder))
//                replyListener!!.onReplyClick(holder, data, getDataPosition(holder))
        })
        holder.contentTxt.setOnLongClickListener(View.OnLongClickListener { v: View? ->
            if (null != onItemLongCLickListener) {
                onItemLongCLickListener?.onItemClick(adapter, holder, getDataPosition(holder))
            }
            true
        })
        holder.contentTxt.setBetterLink()
    }

    private fun setLikeInfo(
        holder: DynamicCommentViewHolder,
        data: CircleDynamicDetailCommentsBean
    ) {
        if (data.favorCount <= 0) {
            holder.likeCountTxt.visibility = View.INVISIBLE
        } else {
            holder.likeCountTxt.visibility = View.VISIBLE
            holder.likeCountTxt.text = data.favorCount.toString()
        }
        holder.likeCountTxt.isSelected = data.favored
        holder.likeIcon.isSelected = data.favored
        holder.likeLayer.setOnClickListener {
            LeonidsUtil.execute(holder.likeLayer, data.favored) {
                raisePost(data, holder)
            }
        }
        holder.likeLayer.setOnLongClickListener {
            if (!LoginUtils.isLoginAndRequestLogin(it.context)) {
                raisePost(data, holder)
                return@setOnLongClickListener true
            }
            var runnable: Runnable? = null
            runnable = Runnable {
                if (it.isPressed) {
                    LeonidsUtil.animAndVibrator(it)
                    it.postDelayed(runnable, 100)
                } else {
                    raisePost(data, holder)
                }
            }
            it.postDelayed(runnable, 100)
            true
        }
    }

    private fun raisePost(
        data: CircleDynamicDetailCommentsBean,
        holder: DynamicCommentViewHolder
    ) {
        if (!LoginUtils.isLoginAndRequestLogin(context)) {
            return
        }
        val favored: Boolean = data.favored
        when (mStyle) {
            Style.HOME_RAFFLE, Style.SHOES_SERIES -> {
                CalendarHelper.favoredCommentHomeRaffle(data.objectId, !favored)
                    .subscribe({ res ->
                        favorRequest(res, data, favored, holder)
                    }, { err ->
                        showToastShort(err.message ?: "网络错误")
                    })
            }
            /*Style.SHOES_SERIES -> {
                CalendarHelper.favoredCommentShoesSeries(data.objectId, !favored)
                    .subscribe({ res ->
                        favorRequest(res, data, favored, holder)
                    }, { err ->
                        showToastShort(err.message ?: "网络错误")
                    })
            }*/
            else -> {
                CircleHelper.getInstance().communityPostCommentFavored(
                    data.objectId ?: "", !favored
                )
                    .subscribe({ res ->
                        favorRequest(res, data, favored, holder)
                    }, { err ->
                        showToastShort(err.message ?: "网络错误")
                    })
            }
        }
    }

    private fun favorRequest(
        res: BaseResult<String>,
        data: CircleDynamicDetailCommentsBean,
        favored: Boolean,
        holder: DynamicCommentViewHolder
    ) {
        if (!res.err && res.res != null) {
            val favorCount: Int = data.favorCount
            data.favorCount = (if (!favored) favorCount + 1 else favorCount - 1)
            data.favored = (!favored)
            setLikeInfo(holder, data)
        } else {
            showToastShort(res.msg)
        }
    }


    private fun setTimeTxt(
        holder: DynamicCommentViewHolder,
        data: CircleDynamicDetailCommentsBean
    ) {
        holder.timeTxt.text = TimeUtils.getDynamicTime(data.createdAt)
    }

    private fun setUserInfo(
        holder: DynamicCommentViewHolder,
        data: CircleDynamicDetailCommentsBean
    ) {
        val userBean: CircleDynamicDetailUserBean? = data.user
        if (null == userBean) {
            holder.userNameTxt.visibility = View.INVISIBLE
            holder.userIv.visibility = View.INVISIBLE
            return
        }
        holder.userIv.visibility = View.VISIBLE
        val userId = userBean.objectId
        holder.userIv.setOnClickListener {
            DcUriRequest(context, MineConstant.Uri.DYNAMIC)
                .putUriParameter(MineConstant.PARAM_KEY.ID, userId)
                .start()
        }
        holder.userTag.hide(!userBean.isVerified)
        holder.userIv.apply {
            setBorderColor(ContextCompat.getColor(context, R.color.color_line))
            setBorderWidth(0.5.dp.toInt())
            setAvatarUrl(userBean.avatarUrl)
            userBean.avatarPendantUrl?.let { url -> setPendantUrl(url) }
        }
        holder.userNameTxt.visibility = View.VISIBLE
        holder.userNameTxt.text = userBean.nickName
        holder.userNameTxt.setTextColor(if (userBean.isVip) ContextCompat.getColor(holder.itemView.context, R.color.colorTextVip) else
            ContextCompat.getColor(holder.itemView.context, R.color.common_grey_blue_txt_color))
        val isSuitUpMedal = !userBean.achievementIconUrl.isNullOrEmpty()
        holder.imgMedal.hide(!isSuitUpMedal)
        if (isSuitUpMedal) {
            holder.imgMedal.load(userBean.achievementIconUrl)
            holder.imgMedal.setOnShakeClickListener {
                UTHelper.commonEvent(UTConstant.Circle.DynamicP_click_ComMedal, "id", userBean.achievementId)
                DcUriRequest(context, MineConstant.Uri.MEDAL_DETAIL)
                    .putUriParameter(MineConstant.PARAM_KEY.ID, userId)
                    .putUriParameter(
                        MineConstant.PARAM_KEY.MEDAL_ID,
                        userBean.achievementId
                    )
                    .start()
            }
        }
    }


    private fun showMenu() {

    }


}