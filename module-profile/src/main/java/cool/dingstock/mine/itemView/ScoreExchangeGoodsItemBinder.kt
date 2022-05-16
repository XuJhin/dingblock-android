package cool.dingstock.mine.itemView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.score.ScoreExchangeItemEntity
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.mine.R

class ScoreExchangeGoodsItemBinder :
    DcBaseItemBinder<ScoreExchangeItemEntity, ScoreExchangeGoodsVH>() {

    var goodsExchangeListener: ScoreExchangeGoodsItemDiffCallback.GoodsExchangeClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreExchangeGoodsVH {
        return ScoreExchangeGoodsVH(
            LayoutInflater.from(context).inflate(R.layout.score_exchange_item_layout, parent, false)
        )
    }

    override fun onConvert(holder: ScoreExchangeGoodsVH, data: ScoreExchangeItemEntity) {
        holder.iv.apply {
            load(data.imageUrl, 6f)
        }
        holder.name.text = data.name
        holder.desc.text = data.desc
        holder.exchangeTv.text = if (data.soldOut == false) {
            "兑换"
        } else {
            "补货中"
        }
        holder.exchangeTv.isSelected = data.soldOut == false
        holder.exchangeTv.setOnShakeClickListener {
            UTHelper.commonEvent(
                UTConstant.Score.IntegralP_click_Commodity,
                "ProductName",
                data.name
            )
            goodsExchangeListener?.onGoodsExchangeClick(data)
        }
        when (holder.layoutPosition % 3) {
            0 -> {
                holder.left.hide(true)
                holder.right.hide(false)
            }
            1 -> {
                holder.left.hide(false)
                holder.right.hide(false)
            }
            2 -> {
                holder.left.hide(false)
                holder.right.hide(true)
            }
        }
    }

    override fun onConvert(
        holder: ScoreExchangeGoodsVH,
        data: ScoreExchangeItemEntity,
        payloads: List<Any>
    ) {
        super.onConvert(holder, data, payloads)
        if (payloads.isNotEmpty()) {
            (payloads[0] as? Bundle)?.let {
                val imageUrl = it.getString("imageUrl")
                if (imageUrl != null) {
                    holder.iv.load(data.imageUrl)
                }
                val name = it.getString("name")
                if (name != null) {
                    holder.name.text = data.name
                }
                val desc = it.getString("desc")
                if (desc != null) {
                    holder.desc.text = data.desc
                }
                val enable = it.getString("soldout")
                if (enable != null) {
                    holder.exchangeTv.isEnabled = data.soldOut == false
                }
            }
        }
    }
}

class ScoreExchangeGoodsVH(view: View) : BaseViewHolder(view) {
    val iv = view.findViewById<ShapeableImageView>(R.id.image_iv)
    val name = view.findViewById<TextView>(R.id.name_tv)
    val desc = view.findViewById<TextView>(R.id.desc_tv)
    val exchangeTv = view.findViewById<TextView>(R.id.exchange_tv)
    val left = view.findViewById<View>(R.id.left)
    val right = view.findViewById<View>(R.id.right)
}


class ScoreExchangeGoodsItemDiffCallback : DiffUtil.ItemCallback<ScoreExchangeItemEntity>() {
    override fun areItemsTheSame(
        oldItem: ScoreExchangeItemEntity,
        newItem: ScoreExchangeItemEntity
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ScoreExchangeItemEntity,
        newItem: ScoreExchangeItemEntity
    ): Boolean {
        if (oldItem.imageUrl != newItem.imageUrl) {
            return false
        }
        if (oldItem.name != newItem.name) {
            return false
        }
        if (oldItem.desc != newItem.desc) {
            return false
        }
        if (oldItem.soldOut != newItem.soldOut) {
            return false
        }
        return true
    }

    override fun getChangePayload(
        oldItem: ScoreExchangeItemEntity,
        newItem: ScoreExchangeItemEntity
    ): Any {
        val bundle = Bundle()
        if (oldItem.imageUrl != newItem.imageUrl) {
            bundle.putString("imageUrl", "change")
        }
        if (oldItem.name != newItem.name) {
            bundle.putString("name", "change")
        }
        if (oldItem.desc != newItem.desc) {
            bundle.putString("desc", "change")
        }
        if (oldItem.soldOut != newItem.soldOut) {
            bundle.putString("soldout", "change")
        }
        return bundle
    }

    interface GoodsExchangeClickListener {
        fun onGoodsExchangeClick(data: ScoreExchangeItemEntity)
    }


}

