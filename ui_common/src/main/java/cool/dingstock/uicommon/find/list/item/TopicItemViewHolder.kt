package cool.dingstock.uicommon.find.list.item

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.imagepicker.views.roundImageView.RoundImageView
import cool.dingstock.uicommon.R

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/22  12:28
 */
class TopicItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
    val topicConverIv = itemView.findViewById<ImageView>(R.id.topic_conver_iv)
    val topicNameTv = itemView.findViewById<TextView>(R.id.topic_name_tv)
    val topicDecTv = itemView.findViewById<TextView>(R.id.topic_dec_tv)
    val nextIv = itemView.findViewById<ImageView>(R.id.next_iv)
    val itemLayerArr = arrayListOf<View>(itemView.findViewById(R.id.item_layer_1),itemView.findViewById(R.id.item_layer_2))
    val itemIvArr = arrayListOf<RoundImageView>(itemView.findViewById(R.id.item_iv_1),itemView.findViewById(R.id.item_iv_2))
    val itemVideoPlayIvArr = arrayListOf<ImageView>(itemView.findViewById(R.id.video_play_iv_1),itemView.findViewById(R.id.video_play_iv_2))
    val itemDecTvArr = arrayListOf<TextView>(itemView.findViewById(R.id.item_dec_tv_1),itemView.findViewById(R.id.item_dec_tv_2))
    val itemUserIvArr = arrayListOf<RoundImageView>(itemView.findViewById(R.id.item_user_iv_1),itemView.findViewById(R.id.item_user_iv_2))
    val itemUserNickTvArr = arrayListOf<TextView>(itemView.findViewById(R.id.item_user_nick_tv_1),itemView.findViewById(R.id.item_user_nick_tv_2))
    val itemPostTimeTvArr = arrayListOf<TextView>(itemView.findViewById(R.id.item_post_time_tv_1),itemView.findViewById(R.id.item_post_time_tv_2))

}