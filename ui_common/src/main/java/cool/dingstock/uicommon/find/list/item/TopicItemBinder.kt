package cool.dingstock.uicommon.find.list.item

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.entity.bean.topic.TalkTopicEntity
import cool.dingstock.appbase.entity.bean.topic.TopicPost
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.ext.loadAvatar
import cool.dingstock.appbase.util.setSvgColorRes
import cool.dingstock.lib_base.util.TimeUtils
import cool.dingstock.uicommon.R

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/22  12:27
 */
class TopicItemBinder : DcBaseItemBinder<TalkTopicEntity, TopicItemViewHolder>(){
    val LINK_TYPE_VIDEO = "VIDEO"

	@SuppressLint("SetTextI18n")
	override fun onConvert(holder: TopicItemViewHolder, data: TalkTopicEntity) {
		holder.nextIv.setSvgColorRes(R.drawable.common_svg_right_arrow,R.color.white,0.5f)
		holder.topicConverIv.load(data.imageUrl)
		holder.topicNameTv.text = "#${data.name}"
		holder.topicDecTv.text = data.desc
		if(data.posts==null||data.posts?.size == 0){
			holder.itemLayerArr.forEach {
				it.hide()
			}
			return
		}
		if(data.posts?.size ==1){
			showItemLayer(holder,0,true)
			showItemLayer(holder,1,false)
			setItem(holder,0,data.posts!![0])
			return
		}
		if((data.posts?.size?:0) >= 2){
			showItemLayer(holder,0,true)
			showItemLayer(holder,1,true)
			setItem(holder,0,data.posts!![0])
			setItem(holder,1,data.posts!![1])
			return
		}
	}

    private fun showItemLayer(holder: TopicItemViewHolder,index : Int,isShow: Boolean){
        holder.itemLayerArr[index].hide(!isShow)
    }

    private fun setItem(holder: TopicItemViewHolder, index : Int, data: TopicPost){
        holder.itemIvArr[index].load(data.showImage)
        holder.itemDecTvArr[index].text = data.content
        holder.itemUserIvArr[index].loadAvatar(data.user?.avatarUrl)
        holder.itemUserNickTvArr[index].text = data.user?.nickName ?: ""
        holder.itemPostTimeTvArr[index].text = TimeUtils.getDynamicTime(data.createdAt?:System.currentTimeMillis())
        if(LINK_TYPE_VIDEO == data.webpageLink?.type){
            holder.itemVideoPlayIvArr[index].hide(false)
        }else{
            holder.itemVideoPlayIvArr[index].hide(true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicItemViewHolder {
        return TopicItemViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_topic_list_layout, parent, false))
    }

}