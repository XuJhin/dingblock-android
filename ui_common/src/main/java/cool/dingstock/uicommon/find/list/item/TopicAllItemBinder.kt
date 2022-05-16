package cool.dingstock.uicommon.find.list.item

import android.view.LayoutInflater
import android.view.ViewGroup
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.entity.bean.topic.TalkTopicEntity
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.uicommon.R

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/10/21 11:41
 * @Version:         1.1.0
 * @Description:
 */
class TopicAllItemBinder : DcBaseItemBinder<TalkTopicEntity,AllTopicItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllTopicItemViewHolder {
        return AllTopicItemViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.all_item_topic_list, parent, false))
    }

    override fun onConvert(holder: AllTopicItemViewHolder, data: TalkTopicEntity) {
        holder.ivArrow.hide(data.hideArrow)
        holder.tvTitle.text = "#${data.name}"
        holder.tvDesc.text = data.desc
        holder.rivCover.load(data.imageUrl)
    }
}