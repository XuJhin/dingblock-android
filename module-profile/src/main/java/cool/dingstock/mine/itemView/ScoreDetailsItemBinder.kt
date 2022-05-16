package cool.dingstock.mine.itemView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.binder.BaseItemBinder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.entity.bean.score.ScoreRecordEntity
import cool.dingstock.lib_base.util.TimeUtils
import cool.dingstock.mine.R

class ScoreDetailsItemBinder() : BaseItemBinder<ScoreRecordEntity, ScoreDetailsVH>() {
    override fun convert(holder: ScoreDetailsVH, data: ScoreRecordEntity) {
        holder.name.text = data.eventName
        holder.time.text = TimeUtils.formatTimestamp(data.createdAt, "yyyy-MM-dd HH:mm")
        if (data.amount > 0) {
            holder.count.text = "+" + data.amount.toString()
        } else {
            holder.count.text = data.amount.toString()
        }
        holder.count.setTextColor(
            if (data.amount > 0)
                ContextCompat.getColor(
                    context,
                    R.color.color_text_black1
                ) else ContextCompat.getColor(context, R.color.color_red)
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreDetailsVH {
        return ScoreDetailsVH(
            LayoutInflater.from(context).inflate(R.layout.score_details_item_layout, parent, false)
        )
    }

}

class ScoreDetailsVH(view: View) : BaseViewHolder(view) {

    val name: TextView = view.findViewById(R.id.title)
    val time: TextView = view.findViewById(R.id.time)
    val count: TextView = view.findViewById(R.id.count_tv)

}