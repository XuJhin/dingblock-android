package cool.dingstock.post.view

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.LeadingMarginSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import cool.dingstock.appbase.ext.inflateBindingLazy
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.post.databinding.GodCommentTvLayoutBinding


class GodCommentTextView(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {
    val viewBinding = inflateBindingLazy<GodCommentTvLayoutBinding>(LayoutInflater.from(context),this,false)

    init {
        addView(viewBinding.root)
    }

    @SuppressWarnings("all")
    fun setGodComment(commenter: String, comment: String) {
        viewBinding.commenterTv.text = "$commenter:"
        var retract = 0
        //标签长度
        retract += 45
        //品论人 的 marginStart
        retract += 1
        //评论到评论人 的 marginStart
        retract += 3
        retract = SizeUtils.dp2px(retract.toFloat())
        val textPaint: TextPaint = viewBinding.commenterTv.paint
        val commenterLength = textPaint.measureText(viewBinding.commenterTv.text.toString()).toInt()
        retract += commenterLength

//        val spannableString = getSpannableString(retract, comment)
        viewBinding.commentTv.text = comment
        viewBinding.commentTv.setBetterLink(retract)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        viewBinding.commentTv.setOnClickListener(l)
        viewBinding.commenterTv.setOnClickListener(l)
        viewBinding.bgV.setOnClickListener(l)
    }


}