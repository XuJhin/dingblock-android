package cool.dingstock.monitor.adapter.item

import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.constant.MonitorConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.monitor.MonitorProductBean
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.net.api.monitor.MonitorApi
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.imagepicker.views.roundImageView.RoundImageView
import cool.dingstock.lib_base.util.TimeUtils
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.monitor.R
import cool.dingstock.monitor.dagger.MonitorApiHelper
import java.util.*
import javax.inject.Inject

/**
 * @author wangjiang
 *  CreateAt Time 2021/7/16  12:04
 */

class MonitorProductItemBinder :
    DcBaseItemBinder<MonitorProductBean, MonitorProductItemBinder.MonitorDetailVH>() {

    var clickListener: ItemClickListener? = null

    interface ItemClickListener {
        fun onClickShowMore(position: Int)
        fun onClickShowCount(position: Int)
        fun onItemClick(position: Int, time: String)
        fun loading(forceShow: Boolean)
        fun onDateClick(channelId: String, name: String)
        fun onSearchPriceClick(productId: String)
    }

    @Inject
    lateinit var api: MonitorApi

    init {
        MonitorApiHelper.apiMonitorComponent.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonitorDetailVH {
        return MonitorDetailVH(
            LayoutInflater.from(context)
                .inflate(R.layout.item_monitor_center_channel, parent, false)
        )
    }

    override fun onConvert(holder: MonitorDetailVH, data: MonitorProductBean) {
        holder.apply {
            val channel = data.channel
            channelNameTxt.text = channel?.name
            channel?.iconUrl?.let {
                channelIv.load(it)
            }
            if (data.isShowRightArrow) {
                ivArrow.visibility = View.VISIBLE
            } else {
                ivArrow.visibility = View.GONE
            }
            productIv.load(data.imageUrl, radius = 2f)
            val currentCalendar = Calendar.getInstance()
            currentCalendar.timeInMillis = System.currentTimeMillis()
            val createAtCalendar = Calendar.getInstance()
            currentCalendar.timeInMillis = data.createdAt ?: 0
            if (currentCalendar.get(Calendar.YEAR) == createAtCalendar.get(Calendar.YEAR)) {
                channelDateTxt.text = TimeUtils.formatTimestampS2NoYear(data.createdAt ?: 0)
            } else {
                channelDateTxt.text = TimeUtils.formatTimestampS2NoYear(data.createdAt ?: 0)
            }
            nameTxt.text = data.title
            if (data.detail.size > 0) {
                addChildView(holder, data.detail, data)
            } else {
                actionTxt.hide(true)
            }
            generateShieldingView(holder, data.bizId, data)
            tvAmount.hide(data.stock.isNullOrEmpty())

            shieldingTxt.setOnShakeClickListener {
                clickListener?.onClickShowMore(getDataPosition(holder))
            }
            actionTxt.setOnShakeClickListener {

                clickListener?.onClickShowCount(getDataPosition(holder))
            }
            channelInfoContainer.setOnShakeClickListener {
                data.channelId?.let { it1 ->
                    data.channel?.name?.let { it2 ->
                        clickListener?.onDateClick(
                            it1,
                            it2
                        )
                    }
                }
            }
            itemView.setOnShakeClickListener {
                clickListener?.onItemClick(getDataPosition(holder), channelDateTxt.text.toString())
            }
            holder.searchPriceTv.hide(data.productId.isNullOrEmpty())
            holder.searchPriceTv.setOnShakeClickListener {
                data.productId?.let {
                    clickListener?.onSearchPriceClick(it)
                    UTHelper.commonEvent(UTConstant.Monitor.MyMonitorP_click_ChannelList_price)
                }
            }
            tvAmount.setOnClickListener {
                UTHelper.monitorClickStork()
                UTHelper.commonEvent(UTConstant.Monitor.MyMonitorP_click_ChannelList_Quantity)
                clickListener?.loading(true)
                data.stock?.let {
                    api.fetchStock(it)
                        .subscribe({ res ->
                            clickListener?.loading(false)
                            if (!res.err && res.res != null && res.res?.title != null) {
                                DcUriRequest(context, MonitorConstant.Uri.MONITOR_SHARE)
                                    .putExtra(MonitorConstant.DataParam.STOCK_DATA, res.res!!)
                                    .start()
                            } else {
                                ToastUtil.getInstance()
                                    .makeTextAndShow(context, "数据错误", Toast.LENGTH_SHORT)
                            }
                        }, {
                            clickListener?.loading(false)
                            ToastUtil.getInstance()
                                .makeTextAndShow(context, "数据错误", Toast.LENGTH_SHORT)
                        })
                }
            }
        }
    }


    private fun addChildView(
        holder: MonitorDetailVH,
        detailList: ArrayList<String>,
        item: MonitorProductBean
    ) {
        holder.detailLayer.removeAllViews()
        if (detailList.isEmpty()) {
            holder.actionTxt.visibility = View.GONE
            return
        }
        if (detailList.size <= 2) {
            holder.actionTxt.visibility = View.GONE
        } else {
            holder.actionTxt.visibility = View.VISIBLE
            holder.actionTxt.setTextColor(context.resources.getColor(R.color.color_blue))
            val lp = holder.actionTxt.layoutParams as? ViewGroup.MarginLayoutParams
            if (item.isExpand) {
                holder.actionTxt.setBackgroundColor(Color.TRANSPARENT)
                holder.actionTxt.text = "收起"
                lp?.topMargin = -20.dp.toInt()
            } else {
                holder.actionTxt.setBackgroundColor(context.getColor(R.color.white))
                holder.actionTxt.text = "更多"
                lp?.topMargin = -36.dp.toInt()
            }
            holder.actionTxt.layoutParams = lp
        }
        detailList.forEach { itemStr ->
            val view =
                LayoutInflater.from(context).inflate(R.layout.tv_calendar_detail, null, false)
            val textView = view.findViewById<TextView>(R.id.tv_calendar_item_detail)
            textView.text = itemStr.replace("size", "尺码")
                .replace("status", "状态")
                .replace("price", "价格")
                .replace("date", "发售日期")
                .replace("sku", "货号")
            if (!item.isExpand) {
                if (detailList.indexOf(itemStr) > 1) {
                    textView.visibility = View.GONE
                } else {
                    textView.visibility = View.VISIBLE
                }
            } else {
                textView.visibility = View.VISIBLE
            }
            holder.detailLayer.addView(view)
        }
    }

    private fun generateShieldingView(
        holder: MonitorDetailVH,
        bizId: String?,
        item: MonitorProductBean
    ) {
        if (null == holder.shieldingTxt) {
            return
        }
        if (TextUtils.isEmpty(bizId)) {
            holder.shieldingTxt.visibility = View.GONE
            return
        }
        holder.shieldingTxt.visibility = View.VISIBLE
        val blocked: Boolean = item.blocked
        holder.shieldingTxt.isSelected = blocked
        if (blocked) {
            holder.shieldingTxt.text = "取消屏蔽"
        } else {
            holder.shieldingTxt.text = "屏蔽该商品"
        }
    }


    class MonitorDetailVH(view: View) : BaseViewHolder(view) {
        var channelIv: RoundImageView = view.findViewById(R.id.monitor_item_product_chanel_iv)
        var channelNameTxt: TextView = view.findViewById(R.id.monitor_item_product_channel_txt)
        var channelDateTxt: TextView = view.findViewById(R.id.monitor_item_product_date_txt)
        var productIv: RoundImageView = view.findViewById(R.id.monitor_item_product_iv)
        var nameTxt: TextView = view.findViewById(R.id.monitor_item_product_name_txt)
        var detailLayer: LinearLayout = view.findViewById(R.id.monitor_item_product_detail_layer)
        var actionTxt: TextView = view.findViewById(R.id.monitor_item_product_action_txt)
        var shieldingTxt: TextView = view.findViewById(R.id.monitor_item_product_shielding_txt)
        var channelInfoContainer: ConstraintLayout = view.findViewById(R.id.channelInfoContainer)
        var ivArrow: ImageView = view.findViewById(R.id.iv_arrow)
        var tvAmount: TextView = view.findViewById(R.id.monitor_item_product_amount_txt)
        var searchPriceTv: View = view.findViewById(R.id.search_price_tv)
    }
}


