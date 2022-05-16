package cool.dingstock.post.adapter.holder

import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.post.R

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/18  12:36
 */
class DynamicCommentsHeadViewHolder(itemView:View) : BaseViewHolder(itemView){

    var headTxt: TextView = itemView.findViewById(R.id.circle_head_comment_head_txt)


}