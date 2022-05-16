package cool.dingstock.uicommon.find.list.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.entity.bean.mine.AccountInfoBriefEntity
import cool.dingstock.appbase.entity.bean.topic.TalkTopicEntity
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.imageload.GlideHelper
import cool.dingstock.uicommon.R
import cool.dingstock.uicommon.mine.item.FollowItemBinder
import java.lang.Exception

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/10/21 11:41
 * @Version:         1.1.0
 * @Description:
 */
class TopicSearchItemBinder : DcBaseItemBinder<TalkTopicEntity,AllTopicItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllTopicItemViewHolder {
        return AllTopicItemViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.all_item_topic_list, parent, false))
    }

    override fun onConvert(holder: AllTopicItemViewHolder, data: TalkTopicEntity) {
        holder.ivArrow.hide(true)
        holder.tvTitle.text = "#${data.name}"
        holder.tvDesc.text = data.desc
        GlideHelper.loadRadiusImage(data.imageUrl, holder.rivCover, holder.itemView, 8f)
        when{
            data.isStart&&data.isEnd->{
                holder.itemView.setBackgroundResource(R.drawable.search_card_all_bg)
            }
            data.isStart&&!data.isEnd->{
                holder.itemView.setBackgroundResource(R.drawable.search_card_top_bg)
            }
            !data.isStart&&data.isEnd->{
                holder.itemView.setBackgroundResource(R.drawable.search_card_bottom_bg)
            }
            !data.isStart&&!data.isEnd->{
                holder.itemView.setBackgroundResource(R.drawable.search_card_not_bg)
            }
        }
        val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
        layoutParams.marginStart = 12.dp.toInt()
        layoutParams.marginEnd = 12.dp.toInt()
        holder.itemView.layoutParams = layoutParams

    }
}