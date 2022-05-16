package cool.dingstock.uicommon.find.list.item

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView
import cool.dingstock.uicommon.R

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/10/20 17:32
 * @Version:         1.1.0
 * @Description:
 */
class AllTopicItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
    val rivCover: ShapeableImageView = itemView.findViewById(R.id.iv_topic_cover)
    val tvTitle: TextView = itemView.findViewById(R.id.tv_topic_title)
    val tvDesc: TextView = itemView.findViewById(R.id.tv_topic_desc)
    val layoutRoot: LinearLayout = itemView.findViewById(R.id.layout_root)
    val ivArrow: ImageView = itemView.findViewById(R.id.iv_arrow)
}