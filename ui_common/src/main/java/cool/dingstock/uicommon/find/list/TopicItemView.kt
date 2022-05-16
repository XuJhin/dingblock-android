package cool.dingstock.uicommon.find.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import cool.dingstock.appbase.adapter.multiadapter.core.ItemViewBinder
import cool.dingstock.appbase.entity.bean.topic.TalkTopicEntity
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.imageload.GlideHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.uicommon.R

open class TopicItemView : ItemViewBinder<TalkTopicEntity, TopicItemView.TopicItemViewHolder>() {

    class TopicItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rivCover: ShapeableImageView = itemView.findViewById(R.id.iv_topic_cover)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_topic_title)
        val tvDesc: TextView = itemView.findViewById(R.id.tv_topic_desc)
        val layoutRoot: LinearLayout = itemView.findViewById(R.id.layout_root)
        val ivArrow: ImageView = itemView.findViewById(R.id.iv_arrow)
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): TopicItemViewHolder {
        return TopicItemViewHolder(inflater.inflate(R.layout.all_item_topic_list, parent, false))
    }

    override fun onBindViewHolder(holder: TopicItemViewHolder, item: TalkTopicEntity) {
        holder.ivArrow.hide(item.hideArrow)
        holder.tvTitle.text = "#${item.name}"
        holder.tvDesc.text = item.desc
        GlideHelper.loadRadiusImage(item.imageUrl, holder.rivCover, holder.itemView, 8f)

        val userInfo = LoginUtils.getCurrentUser()
    }
}