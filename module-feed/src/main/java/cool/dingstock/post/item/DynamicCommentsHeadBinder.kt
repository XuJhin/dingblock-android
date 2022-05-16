package cool.dingstock.post.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import cool.dingstock.post.R
import cool.dingstock.appbase.entity.bean.circle.DynamicCommentHeaderBean
import cool.dingstock.post.adapter.holder.DynamicCommentsHeadViewHolder
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/18  12:35
 */
class DynamicCommentsHeadBinder(
    private val showHeader: Boolean
) : DcBaseItemBinder<DynamicCommentHeaderBean, DynamicCommentsHeadViewHolder>(){

    var titleTextSize  = 12f

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicCommentsHeadViewHolder {
        return DynamicCommentsHeadViewHolder(LayoutInflater.from(context).inflate(R.layout.dynamic_comments_header_item_layout,parent,false))
    }

    override fun onConvert(holder: DynamicCommentsHeadViewHolder, data: DynamicCommentHeaderBean) {
        if (!showHeader) {
            holder.itemView.layoutParams.height = 0
        } else {
            holder.itemView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        holder.headTxt.text = data.header
        holder.headTxt.textSize = titleTextSize
    }


}