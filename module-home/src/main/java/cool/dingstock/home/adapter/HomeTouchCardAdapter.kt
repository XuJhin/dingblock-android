package cool.dingstock.home.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cool.dingstock.appbase.entity.bean.home.TransverseCardData
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.home.ui.dingchao.HomeTopComponentView
import cool.dingstock.home.utils.CardViewHelper
import cool.dingstock.home.widget.card.HomeCardItem
import cool.dingstock.lib_base.widget.card.touch.TouchCardAdapter


/**
 * 类名：HomeTouchCardAdapter
 * 包名：cool.dingstock.home.adapter
 * 创建时间：2021/8/7 5:10 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class HomeTouchCardAdapter(val context: Context) :
    TouchCardAdapter<TransverseCardData, HomeTouchVh>() {
    var onItemClickFun: ((entity: TransverseCardData) -> kotlin.Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeTouchVh {
        val item = HomeCardItem(context, null)
        item.setSize(
            CardViewHelper.cardWidth.toInt(),
            CardViewHelper.cardHeight.toInt()
        )
        return HomeTouchVh(item)
    }

    override fun onBindViewHolder(holder: HomeTouchVh, position: Int) {
        holder.homeCardItem.setData(data.size - position - 1, data[position])
        holder.homeCardItem.setOnShakeClickListener {
            onItemClickFun?.invoke(data[position])
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


    fun setOnItemClick(onItemClick: ((entity: TransverseCardData) -> Unit)) {
        this.onItemClickFun = onItemClick
    }

}

class HomeTouchVh(val homeCardItem: HomeCardItem) : RecyclerView.ViewHolder(homeCardItem)