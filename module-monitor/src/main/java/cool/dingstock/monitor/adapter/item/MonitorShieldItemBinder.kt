package cool.dingstock.monitor.adapter.item

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.entity.bean.monitor.MonitorProductBean
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.monitor.R

/**
 * @author wj
 *  CreateAt Time 2021/7/9  12:04
 */

class MonitorShieldItemBinder : DcBaseItemBinder<MonitorProductBean, MonitorShieldItemBinder.MonitorShieldVH>() {

    private var mClickListener: ((bizId: String, blocked: Boolean, position: Int) -> Unit)? = null

    fun setClickListener(clickListener: ((bizId: String, blocked: Boolean, position: Int) -> Unit)) {
        mClickListener = clickListener
    }

    override fun onConvert(holder: MonitorShieldVH, data: MonitorProductBean) {
        holder.apply {
            iv.load(data.imageUrl)
            tvTitle.text = data.title
            generateShieldingView(holder, data.bizId, data)
            tvCancel.setOnShakeClickListener {
                data.bizId?.let { it1 -> mClickListener?.invoke(it1, !data.blocked, getDataPosition(holder)) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonitorShieldVH {
        return MonitorShieldVH(LayoutInflater.from(context).inflate(R.layout.item_monitor_shield, parent, false))
    }

    private fun generateShieldingView(holder: MonitorShieldVH, bizId: String?, item: MonitorProductBean) {
        if (TextUtils.isEmpty(bizId)) {
            holder.tvCancel.visibility = View.GONE
            return
        }
        holder.tvCancel.visibility = View.VISIBLE
        val blocked: Boolean = item.blocked
        if (blocked) {
            holder.tvCancel.text = "取消屏蔽"
        } else {
            holder.tvCancel.text = "屏蔽该商品"
        }
    }

    class MonitorShieldVH(view: View) : BaseViewHolder(view) {
        var iv: ImageView = view.findViewById(R.id.iv)
        var tvTitle: TextView = view.findViewById(R.id.tv_title)
        var tvCancel: TextView = view.findViewById(R.id.tv_cancel)
    }
}