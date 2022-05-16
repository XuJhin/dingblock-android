package cool.dingstock.tide.adapter.viewholder

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import cool.dingstock.appbase.constant.CalendarConstant
import cool.dingstock.appbase.entity.bean.tide.TideItemEntity
import cool.dingstock.appbase.ext.*
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.widget.stickyheaders.SectioningAdapter
import cool.dingstock.imagepre.ImagePreview
import cool.dingstock.imagepre.bean.ImageInfo
import cool.dingstock.lib_base.util.StringUtils
import cool.dingstock.tide.R
import cool.dingstock.tide.databinding.HomeItemTideLayoutBinding
import java.util.ArrayList


/**
 * 类名：HomeTideItemViewHolder
 * 包名：cool.dingstock.tide.adapter.viewholder
 * 创建时间：2021/7/20 4:44 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class HomeTideItemViewHolder(val viewBinding: HomeItemTideLayoutBinding) :
    SectioningAdapter.ItemViewHolder(viewBinding.root) {

    var listener: OnTideItemActionClickListener? = null

    fun bind(data: TideItemEntity) {
        viewBinding.iv.load(data.imageUrl)
        viewBinding.nameTv.text = data.title
        viewBinding.companyIv.load(data.companyLogo)
        viewBinding.companyIv.hide(data.companyLogo.isNullOrEmpty())
        viewBinding.companyTv.text = data.company
        viewBinding.descTv.text = data.desc
        viewBinding.timeTv.text = data.saleDateStr
        viewBinding.priceTv.text = data.price
        viewBinding.root.setOnShakeClickListener {
            listener?.onLookClick(data)
        }
        viewBinding.commentLayer.clockLayer.setOnShakeClickListener {
            listener?.onSubscribeClick(it, data)
        }
        viewBinding.commentLayer.clockTv.isSelected = data.isSubscribe
        viewBinding.commentLayer.clockIv.isSelected = data.isSubscribe
        viewBinding.commentLayer.commentTxt.text = data.commentCount.toString()
        viewBinding.commentLayer.dislikeIcon.isSelected = data.disliked
        viewBinding.commentLayer.dislikeTxt.isSelected = data.disliked
        viewBinding.commentLayer.dislikeTxt.text = data.dislikeCount.toString()
        viewBinding.commentLayer.likeIcon.isSelected = data.liked
        viewBinding.commentLayer.likeTxt.isSelected = data.liked
        viewBinding.commentLayer.likeTxt.text = data.likeCount.toString()
        viewBinding.commentLayer.clockTv.text = if (data.isSubscribe) "已订阅" else "提醒"
        viewBinding.commentLayer.likeLayer.setOnShakeClickListener {
            listener?.onLikeClick(it, data, viewBinding.commentLayer.dislikeTxt.isSelected)
        }
        viewBinding.commentLayer.dislikeLayer.setOnShakeClickListener {
            listener?.onDisLikeClick(it, data, viewBinding.commentLayer.likeTxt.isSelected)
        }
        viewBinding.commentLayer.commentLayer.setOnShakeClickListener {
            listener?.onComment(it, data)
        }
        if (data.label.isNullOrEmpty()) {
            viewBinding.cardLabel.isVisible = false
        } else {
            viewBinding.cardLabel.isVisible = true
            viewBinding.tvGoodLabel.text = data.label
            try {
                data.labelColor?.let {
                    viewBinding.tvGoodLabel.setBackgroundColor(Color.parseColor(data.labelColor))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        when (data.type) {
            CalendarConstant.CommentType.DIGITAL -> {
                viewBinding.iv.apply {
                    layoutParams = layoutParams.apply {
                        width = 110.azDp.toInt()
                        height = 110.azDp.toInt()
                    }
                }
                viewBinding.ipTv.text = data.dealDesc
                viewBinding.ipLayer.hide(TextUtils.isEmpty(data.dealDesc))
                viewBinding.soldOut.isVisible = data.isSoldOut
                viewBinding.soldOutMask.isVisible = data.isSoldOut

                viewBinding.descTv.apply {
                    layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                        topToTop = R.id.price_tv
                        topToBottom = ConstraintLayout.LayoutParams.UNSET
                        bottomToBottom = R.id.price_tv
                        startToEnd = R.id.price_tv
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        topMargin = 0
                    }
                    gravity = Gravity.END
                }
            }
            else -> {
                viewBinding.iv.apply {
                    layoutParams = layoutParams.apply {
                        width = 120.azDp.toInt()
                        height = 120.azDp.toInt()
                    }
                }
                viewBinding.ipTv.text = data.ip
                viewBinding.ipLayer.hide(TextUtils.isEmpty(data.ip))
                viewBinding.soldOut.isVisible = false
                viewBinding.soldOutMask.isVisible = false

                viewBinding.descTv.apply {
                    layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                        topToBottom = R.id.time_tv
                        topToTop = ConstraintLayout.LayoutParams.UNSET
                        bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                        startToEnd = R.id.iv
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        topMargin = 10.dp.toInt()
                    }
                    gravity = Gravity.START
                }
            }
        }
        viewBinding.iv.setOnShakeClickListener {
            val urlList = ArrayList<ImageInfo>()
            val imageInfo = ImageInfo()
            imageInfo.originUrl = data.imageUrl
            imageInfo.thumbnailUrl = data.imageUrl
            if (StringUtils.isEmpty(imageInfo.originUrl)) {
                return@setOnShakeClickListener
            }
            urlList.add(imageInfo)
            ImagePreview.getInstance()
                .setContext(viewBinding.root.context)
                .setIndex(0)
                .setEnableDragClose(true)
                .setFolderName("DingChao")
                .setShowCloseButton(true)
                .setLoadStrategy(ImagePreview.LoadStrategy.Default)
                .setErrorPlaceHolder(R.drawable.img_load)
                .setImageInfoList(urlList)
                .start()
        }
        if (data.isPlayAnimator) {
            startBackgroundAnimator()
            data.isPlayAnimator = false
        }
    }

    private fun startBackgroundAnimator() {
        val showColor =
            Color.parseColor(viewBinding.root.context.resources.getString(R.color.white.toInt()))
        val flashShowColor =
            Color.parseColor(viewBinding.root.context.resources.getString(R.color.tide_rv_scroll_animator_color.toInt()))
        val translationUp = ObjectAnimator.ofInt(
            viewBinding.viewAnimator,
            "backgroundColor", showColor, flashShowColor, showColor,
            flashShowColor, Color.TRANSPARENT
        )
        translationUp.interpolator = LinearInterpolator()
        translationUp.duration = 1000
        translationUp.repeatCount = 0
        translationUp.repeatMode = ValueAnimator.REVERSE
        translationUp.setEvaluator(ArgbEvaluator())
        translationUp.start()
    }
}

interface OnTideItemActionClickListener {
    fun onLookClick(entity: TideItemEntity)

    fun onSubscribeClick(view: View, entity: TideItemEntity)

    fun onComment(view: View, entity: TideItemEntity)

    fun onLikeClick(view: View, entity: TideItemEntity, refreshDislike: Boolean)


    fun onDisLikeClick(view: View, entity: TideItemEntity, refreshLike: Boolean)


}