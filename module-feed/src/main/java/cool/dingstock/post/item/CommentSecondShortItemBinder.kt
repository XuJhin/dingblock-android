package cool.dingstock.post.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.post.R

/**
 * @author WhenYoung
 *  CreateAt Time 2020/11/2  16:22
 */
class CommentSecondShortItemBinder : DcBaseItemBinder<String, CommentSecondShortItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentSecondShortItemViewHolder {
        val inflate =
            LayoutInflater.from(context).inflate(R.layout.dynamic_subcomment_count, parent, false)
        return CommentSecondShortItemViewHolder(inflate)
    }

    override fun onConvert(holder: CommentSecondShortItemViewHolder, data: String) {
        holder.textView.text = "共${data}条回复 >"
        holder.itemView.setPadding(0, 0, 0, 0)
        val dp12 = SizeUtils.dp2px(12f)
        val dp3 = SizeUtils.dp2px(3f)
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
}

class CommentSecondShortItemViewHolder(view: View) : BaseViewHolder(view) {
    val textView = view.findViewById<TextView>(R.id.dynamic_subcomment_count_txt)
}