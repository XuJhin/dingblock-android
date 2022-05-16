package cool.dingstock.monitor.adapter.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.entity.bean.home.bp.ClueProductBean
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.lib_base.util.TimeUtils
import cool.dingstock.monitor.R

/**
 * @author wangjiang
 *  CreateAt Time 2021/7/9  12:04
 */

class MonitorCouponItemBinder : DcBaseItemBinder<ClueProductBean, MonitorCouponItemBinder.OrderDetailVH>() {
    var showChanel = true

    interface ClickListener {
        fun onItemClick(link: String)
        fun onChanelBarClick(channelId: String)
    }

    var itemClickListener: ClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailVH {
        return OrderDetailVH(LayoutInflater.from(context).inflate(R.layout.item_monitor_coupon, parent, false))
    }


    override fun onConvert(holder: OrderDetailVH, data: ClueProductBean) {
        holder.apply {
            mTvTitle.text = data.channel?.name
            mTvDate.text = data.createdAt?.let { TimeUtils.formatTimestampCustom(it, "MM-dd HH:mm") }
            mIvCouponGoodImg.load(data.imageUrl)
            mIvCouponIcon.load(data.channel?.iconUrl)
            mTvCouponMsg.text = data.title
            if (data.type != "coupon") {
                mLlCouPon.hide(true)
                mTvBugGood.hide(false)
            } else {
                mLlCouPon.hide(false)
                mTvBugGood.hide(true)
                mTvDenomination.text = data.couponPrice
                mTvCondition.text = data.couponConditions
            }
            mIvArrow.hide(!showChanel)
            mLlChannelBar.setOnShakeClickListener {
                data.channelId?.let { it1 -> itemClickListener?.onChanelBarClick(it1) }
            }

            itemView.setOnShakeClickListener {
                data.link?.let { it1 -> itemClickListener?.onItemClick(it1) }
            }
        }
    }

    class OrderDetailVH(view: View) : BaseViewHolder(view) {
        val mTvBugGood: TextView = view.findViewById(R.id.tv_buy_good)
        val mTvCouponMsg: TextView = view.findViewById(R.id.tv_coupon_name)
        val mIvCouponGoodImg: ImageView = view.findViewById(R.id.iv_coupon_good_img)
        val mTvDate: TextView = view.findViewById(R.id.tv_date)
        val mTvTitle: TextView = view.findViewById(R.id.tv_title)
        val mIvCouponIcon: ImageView = view.findViewById(R.id.iv_coupon_icon)
        val mLlCouPon: View = view.findViewById(R.id.ll_coupon)
        val mTvDenomination: TextView = view.findViewById(R.id.tv_denomination)
        val mTvCondition: TextView = view.findViewById(R.id.tv_condition)
        val mLlChannelBar: LinearLayout = view.findViewById(R.id.ll_channel_bar)
        val mIvArrow: ImageView = view.findViewById(R.id.iv_arrow)
    }
}




