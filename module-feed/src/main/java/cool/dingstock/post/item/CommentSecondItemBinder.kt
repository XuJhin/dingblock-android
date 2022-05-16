package cool.dingstock.post.item

import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.UIConstant
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicDetailCommentsBean
import cool.dingstock.appbase.entity.bean.circle.CircleUserBean
import cool.dingstock.appbase.net.api.calendar.CalendarHelper
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.util.ClipboardHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.widget.CenterAlignImageSpan
import cool.dingstock.appbase.widget.menupop.OptionMenu
import cool.dingstock.appbase.widget.menupop.PopupMenuView
import cool.dingstock.appbase.widget.menupop.PopupView
import cool.dingstock.imagepre.ImagePreview
import cool.dingstock.imagepre.bean.ImageInfo
import cool.dingstock.lib_base.util.StringUtils
import cool.dingstock.appbase.customerview.betterlinktv.BetterLinkTv
import cool.dingstock.appbase.customerview.betterlinktv.SpannableLocation
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.post.R

/**
 * @author WhenYoung
 *  CreateAt Time 2020/11/2  16:22
 */
class CommentSecondItemBinder : DcBaseItemBinder<CircleDynamicDetailCommentsBean, CommentSecondItemViewHolder>() {
    override fun onConvert(holder: CommentSecondItemViewHolder, subComment: CircleDynamicDetailCommentsBean) {
        val dp12 = SizeUtils.dp2px(12f)
        val dp3 = SizeUtils.dp2px(3f)
        //回复评论的用户
        var user = subComment.user
        if (user == null) {
            holder.itemView.visibility = View.GONE
            return
        }
        val replyUser = user
        holder.itemView.visibility = View.VISIBLE
        //此条回复提及到的用户 可能为空
        val mentioned = subComment.mentioned
        var mentionUser: CircleUserBean?
        mentionUser = mentioned?.user
        var content = ""
        var spannable: SpannableString
        var list = arrayListOf<SpannableLocation>()
        val imgPlaceholder = " "
        val colonStr = "："
        val replyStr = " 回复 "
        val lookImgStr = "查看图片"
        if (mentionUser == null || StringUtils.isEmpty(mentionUser.nickName)) {
            if (!StringUtils.isEmpty(subComment.staticImg?.url)) { //这里判断是否有图片
                content =
                    replyUser.nickName + "${colonStr}${imgPlaceholder}${lookImgStr} ${subComment.content ?: ""}" //这里添加这个空格主要是为了避免图片的问题
                val splImgIcon = SpannableLocation(
                    CenterAlignImageSpan(
                        context,
                        R.drawable.look_img_icon
                    ),
                    replyUser.nickName.length + colonStr.length,
                    replyUser.nickName.length + colonStr.length + imgPlaceholder.length
                )
                list.add(splImgIcon)
                val splImg =
                    SpannableLocation(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_blue)),
                        replyUser.nickName.length + colonStr.length + imgPlaceholder.length,
                        replyUser.nickName.length + colonStr.length + imgPlaceholder.length + lookImgStr.length
                    )
                list.add(splImg)
                //需要添加图片的点击事件
                addImgClick(
                    subComment,
                    list,
                    replyUser.nickName.length + colonStr.length,
                    replyUser.nickName.length + colonStr.length + imgPlaceholder.length + lookImgStr.length
                )
            } else {
                content = replyUser.nickName + colonStr + (subComment.content ?: "")
            }
            //添加评论人 的点击事件 和 颜色变化
            spannable = SpannableString(content)

            addTxtClick(replyUser.objectId, list, 0, replyUser.nickName.length)
        } else {
            if (!StringUtils.isEmpty(subComment.staticImg?.url)) { //这里判断是否有图片
                content =
                    replyUser.nickName + replyStr + mentionUser.nickName + colonStr + imgPlaceholder + lookImgStr + " " + (subComment.content
                        ?: "")
                val splImgIcon = SpannableLocation(
                    CenterAlignImageSpan(
                        context,
                        R.drawable.look_img_icon
                    ),
                    replyUser.nickName.length + replyStr.length + (mentionUser.nickName?.length
                        ?: 0) + colonStr.length,
                    replyUser.nickName.length + replyStr.length + (mentionUser.nickName?.length
                        ?: 0) + colonStr.length + imgPlaceholder.length
                )
                list.add(splImgIcon)
                val splImg =
                    SpannableLocation(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_blue)),
                        replyUser.nickName.length + replyStr.length + (mentionUser.nickName?.length
                            ?: 0) + colonStr.length + imgPlaceholder.length,
                        replyUser.nickName.length + replyStr.length + (mentionUser.nickName?.length
                            ?: 0) + colonStr.length + imgPlaceholder.length + lookImgStr.length
                    )
                list.add(splImg)

                addImgClick(
                    subComment, list, replyUser.nickName.length + replyStr.length + (mentionUser.nickName?.length
                        ?: 0) + colonStr.length,
                    replyUser.nickName.length + replyStr.length + (mentionUser.nickName?.length
                        ?: 0) + colonStr.length + imgPlaceholder.length + lookImgStr.length
                )
            } else {
                content =
                    replyUser.nickName + replyStr + mentionUser.nickName + colonStr + (subComment.content ?: "")
            }

            spannable = SpannableString(content)
            val mentionIndex = (replyUser.nickName + replyStr).length

            addTxtClick(replyUser.objectId, list, 0, replyUser.nickName.length)

            addTxtClick(
                mentionUser.id ?: mentionUser.objectId
                ?: "", list, mentionIndex, mentionIndex + mentionUser.nickName!!.length
            )
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(adapter, holder, getDataPosition(holder))
        }
        holder.textView.setOnClickListener {
            onItemClickListener?.onItemClick(adapter, holder, getDataPosition(holder))
        }
        holder.textView.text = spannable
        holder.textView.setBetterLink()
        holder.textView.setBetterLinkSpan(list)
        holder.textView.movementMethod = LinkMovementMethod.getInstance()
        //设置padding
        when {
            getDataPosition(holder) == 0 && getDataPosition(holder) != (adapter.data.count() - 1) -> {
                holder.textView.setPadding(dp12, dp12, dp12, dp3)
            }
            getDataPosition(holder) != 0 && getDataPosition(holder) == (adapter.data.count() - 1) -> {
                holder.textView.setPadding(dp12, dp3, dp12, dp12)
            }
            getDataPosition(holder) == 0 && getDataPosition(holder) == (adapter.data.count() - 1) -> {
                holder.textView.setPadding(dp12, dp12, dp12, dp12)
            }
            else -> {
                holder.textView.setPadding(dp12, dp3, dp12, dp3)
            }
        }
    }

    private fun addImgClick(
        subComment: CircleDynamicDetailCommentsBean,
        list: ArrayList<SpannableLocation>,
        startIndex: Int,
        endIndex: Int
    ) {
        val imgClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val imageInfo = ImageInfo()
                imageInfo.originUrl = subComment.dynamicImg?.url ?: subComment.staticImg?.url
                imageInfo.thumbnailUrl = subComment.staticImg?.url
                val imgs = arrayListOf(imageInfo)
                routeToImagePre(imgs)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(context, R.color.color_blue)
            }
        }
        val spanableLocation = SpannableLocation(imgClick, startIndex, endIndex)

        list.add(spanableLocation)
    }

    private fun addTxtClick(userId: String?, list: ArrayList<SpannableLocation>, startIndex: Int, endIndex: Int) {
        val imgClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                userId?.let {
                    DcUriRequest(context, MineConstant.Uri.DYNAMIC)
                        .putUriParameter(MineConstant.PARAM_KEY.ID, userId)
                        .start()
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(context, R.color.color_blue)
            }
        }
        val spanableLocation = SpannableLocation(imgClick, startIndex, endIndex)
        list.add(spanableLocation)
    }

    private fun routeToImagePre(preList: ArrayList<ImageInfo>) {
        if (preList.isEmpty()) {
            return
        }
        ImagePreview.getInstance()
            .setContext(context)
            .setIndex(0)
            .setEnableDragClose(true)
            .setFolderName("DingChao")
            .setShowCloseButton(true)
            .setLoadStrategy(ImagePreview.LoadStrategy.NetworkAuto)
            .setErrorPlaceHolder(R.drawable.img_load)
            .setImageInfoList(preList)
            .start()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentSecondItemViewHolder {
        val inflate =
            LayoutInflater.from(context).inflate(R.layout.circle_view_dynamic_subcomment_layout, parent, false)
        return CommentSecondItemViewHolder(inflate)
    }

    private fun showMenu(subComment: CircleDynamicDetailCommentsBean, text: String, view: View) {
        val popupMenuView = PopupMenuView(context)
        val optionMenuList: MutableList<OptionMenu> = ArrayList()
        val isMine = (subComment.user?.objectId ?: "") == LoginUtils.getCurrentUser()?.id ?: "1"

        if (isMine) {
            val menu = OptionMenu()
            menu.id = UIConstant.MenuId.DEL
            menu.title = "删除"
            optionMenuList.add(menu)
            //先不做删除
            return
        } else {
            val menu = OptionMenu()
            menu.id = UIConstant.MenuId.COPY
            menu.title = "复制"
            optionMenuList.add(menu)
            val menu1 = OptionMenu()
            menu1.id = UIConstant.MenuId.REPORT
            menu1.title = "举报"
            optionMenuList.add(menu1)
        }
        popupMenuView.menuItems = optionMenuList
        popupMenuView.setSites(PopupView.SITE_TOP)
        popupMenuView.show(view)
        popupMenuView.setOnMenuClickListener { _: Int, menu: OptionMenu? ->
            when (menu?.id) {
                UIConstant.MenuId.DEL -> {
                    //删除
                }
                UIConstant.MenuId.COPY -> {
                    ClipboardHelper.copyInfo(text)
                    showToastShort("已复制")
                }
                UIConstant.MenuId.REPORT -> {
                    CalendarHelper.productPostCommentReport(subComment.objectId)
                        .subscribe({ res ->
                            if (!res.err) {
                                showSuccessDialog("举报成功")
                            }
                        }, {
                        })
                }
            }
            return@setOnMenuClickListener true
        }
    }
}

class CommentSecondItemViewHolder(view: View) : BaseViewHolder(view) {
    val textView =
        view.findViewById<BetterLinkTv>(R.id.circle_view_dynamic_subcomment_better_link_txt)
}