package cool.dingstock.post.item

import android.view.LayoutInflater
import android.view.ViewGroup
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.entity.bean.circle.HomeTopicEntity
import cool.dingstock.appbase.ext.hide
import cool.dingstock.post.R
import cool.dingstock.post.databinding.HomeItemHotTopicBinding

/**
 * @author wangjiang
 *  CreateAt Time 2021/9/23  16:44
 */
class HomeHotTopicTitleItemBinder : BaseViewBindingItemBinder<HomeTopicEntity, HomeItemHotTopicBinding>() {

    override fun provideViewBinding(parent: ViewGroup, viewType: Int): HomeItemHotTopicBinding {
        return HomeItemHotTopicBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onConvert(vb: HomeItemHotTopicBinding, data: HomeTopicEntity) {
        vb.apply {
//            iv.load(data.imgUrl)
            tvMsg.text = data.name
            tvMsg.isSelected = data.isSelect
            vb.root.isSelected = data.isSelect
            when(data.label){
                "hot"->{
                    tipIv.hide(false)
                    tipIv.setImageResource(R.drawable.post_ic_home_topic_hot)
                }
                "new"->{
                    tipIv.hide(false)
                    tipIv.setImageResource(R.drawable.post_ic_home_topic_new)
                }
                else->{
                    tipIv.hide()
                }
            }

        }
    }
}


