package cool.dingstock.mine.itemView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.binder.BaseItemBinder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.entity.bean.mine.VipPrivilegeEntity
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.mine.R

class VipMineItemBinder : BaseItemBinder<VipPrivilegeEntity, VipMinePrivilegeViewHolder>() {

    private var vipItemClick: ((url:String) -> Unit)? = null
    fun setVipClick(click: ((url:String) -> Unit)) {
        vipItemClick = click
    }

    override fun convert(holder: VipMinePrivilegeViewHolder, data: VipPrivilegeEntity) {
        holder.ivState.hide(data.cornerIcon.isNullOrEmpty())
        if (!data.cornerIcon.isNullOrEmpty()) {
            holder.ivState.load(data.cornerIcon)
        }

        holder.img.load(data.userPageImageUrl)
        holder.tv.text = data.name
        holder.itemView.setOnClickListener {
            vipItemClick?.invoke(data.userPageForwardUrl?:"")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VipMinePrivilegeViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.vip_mine_item_layout, parent, false)
        return VipMinePrivilegeViewHolder(view)
    }
}

class VipMinePrivilegeViewHolder(view: View) : BaseViewHolder(view) {
    val img: ImageView = view.findViewById(R.id.img)
    val tv: TextView = view.findViewById(R.id.tv)
    val ivState: ImageView = view.findViewById(R.id.iv_state)
}