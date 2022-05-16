package cool.dingstock.uicommon.product.item

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.home.*
import cool.dingstock.appbase.router.ExternalRouter
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LoginUtils.getCurrentUser
import cool.dingstock.appbase.util.LoginUtils.isLoginAndRequestLogin
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.widget.commondialog.CommonDialog
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.lib_base.util.StringUtils
import cool.dingstock.uicommon.R

/**
 * @author wangjiang
 *  CreateAt Time 2021/7/1  15:40
 */

class HomeSmsRaffleDetailItemBinder : DcBaseItemBinder<SmsRaffleBean, HomeSmsRaffleDetailItemBinder.HomeSmsRegistrationDetailVH>() {

    private val showWhere = ShowWhere.REGION_ITEM
    var whiteList: List<String> = emptyList()

    interface ActionListener {
        fun onShareClick(smsRaffleBean: SmsRaffleBean?)
        fun onAlarmClick(smsRaffleBean: SmsRaffleBean?)
        fun onSmsClick(smsRaffleBean: SmsRaffleBean?)
    }

    var mListener: ActionListener? = null

    enum class ShowWhere {
        PRODUCT_DETAILS, REGION_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeSmsRegistrationDetailVH {
        return HomeSmsRegistrationDetailVH(LayoutInflater.from(context).inflate(R.layout.home_item_sms_registration_detail, parent, false))
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onConvert(holder: HomeSmsRegistrationDetailVH, data: SmsRaffleBean) {
        holder.apply {
            val brand: SmsBrandBean = data.brand!!
            if (TextUtils.isEmpty(brand.imageUrl)) {
                iv.setImageResource(R.drawable.common_item_bg_radius_4)
            } else {
                Glide.with(context)
                        .load(brand.imageUrl)
                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .into(iv)
            }
            namTxt.text = brand.name
            timeTxt.text = data.releaseDateString
            methodTxt.text = data.method
//            setActionIcon(this, data)
            shareIcon.setOnClickListener {
                if (null != mListener) {
                    mListener!!.onShareClick(data)
                }
            }
            line.visibility = if (data.isLast) View.GONE else View.VISIBLE

            item.setOnClickListener {
                UTHelper.commonEvent(UTConstant.Calendar.SMSLottery_click_RegistrationList)
                onItemClick(data)
            }
            joinedLin.setOnClickListener {
                onItemClick(data)
            }
            if (showWhere == ShowWhere.PRODUCT_DETAILS) {
                item.setPadding(SizeUtils.dp2px(12f), 0, SizeUtils.dp2px(12f), 0)
            } else {
                item.setPadding(0, 0, 0, 0)
            }
            if (data.isLast) { //是最后一个又是第一个
                if (data.isStart) {
                    item.setBackgroundResource(R.drawable.common_item_bg_radius_6)
                } else { //是最后一个 但不是第一个
                    item.setBackgroundResource(R.drawable.common_item_bg_radius_bottom_6)
                }
            } else {
                if (data.isStart) { //第一个但不是最后一个
                    item.setBackgroundResource(R.drawable.common_item_bg_radius_top_6)
                } else { //不是最后一个也不是第一个
                    item.setBackgroundColor(Color.WHITE)
                }
            }

            when (data.smsStatus) {
                "going" -> {
                    tvAction.text = "登记"
                    tvAction.setTextColor(Color.parseColor("#FFFFFF"))
                    tvAction.background = context.getDrawable(R.drawable.blue_r100_bg)
                    tvAction.setOnShakeClickListener {
                        UTHelper.commonEvent(UTConstant.Calendar.SMSLottery_click_Register)
                        mListener!!.onSmsClick(data)
                    }
                }
                "unStart" -> {
                    tvAction.text = "未开始"
                    setAlarmIcon(this, data)
                    tvAction.setTextColor(Color.parseColor("#3D3E45"))
                    tvAction.background = context.getDrawable(R.drawable.sms_action_bg)
                    tvAction.setOnShakeClickListener {
                        if (getCurrentUser()?.id in whiteList) {
                            mListener!!.onSmsClick(data)
                        } else {
                            showToastShort("暂未开始")
                        }
                    }
                }
                "over" -> {
                    tvAction.text = "已结束"
                    tvAction.setTextColor(Color.parseColor("#C0C2CE"))
                    tvAction.background = context.getDrawable(R.drawable.sms_action_bg)
                    tvAction.setOnShakeClickListener {
                        if (getCurrentUser()?.id in whiteList) {
                            mListener!!.onSmsClick(data)
                        } else {
                            showToastShort("已结束")
                        }
                    }
                }
            }
        }
    }

    private fun onItemClick(data: SmsRaffleBean) {
        if (!isLoginAndRequestLogin(context)) {
            return
        }
        if (StringUtils.isEmpty(data.link)) {
            CommonDialog.Builder(context)
                    .content(if (StringUtils.isEmpty(data.message)) "当前无跳转链接" else data.message)
                    .builder()
                    .show()
            return
        }
        ExternalRouter.route(data.link)
    }

    private fun setAlarmIcon(holder: HomeSmsRegistrationDetailVH, data: SmsRaffleBean) {
        if (data.notifyDate - System.currentTimeMillis() > 0) {
            holder.spaceView.visibility = View.VISIBLE
            holder.alarmIcon.visibility = View.VISIBLE
            holder.alarmIcon.setImageResource(R.drawable.alert_clock_icon)
            holder.alarmIcon.setOnClickListener {
                if (null != mListener) {
                    mListener!!.onAlarmClick(data)
                }
            }
            return
        }
        holder.alarmIcon.visibility = View.GONE
        holder.spaceView.visibility = View.GONE
    }

    class HomeSmsRegistrationDetailVH(view: View) : BaseViewHolder(view) {
        val iv: ImageView = view.findViewById(R.id.home_item_region_detail_iv)
        val namTxt: TextView = view.findViewById(R.id.home_item_region_detail_name_txt)
        val timeTxt: TextView = view.findViewById(R.id.home_item_region_detail_time_txt)
        val methodTxt: TextView = view.findViewById(R.id.home_item_region_detail_method_txt)
        val shareIcon: ImageView = view.findViewById(R.id.home_item_region_detail_share_icon)
        val alarmIcon: ImageView = view.findViewById(R.id.home_item_region_detail_action_icon)
        val joinedLin: ViewGroup = view.findViewById(R.id.home_item_region_detail_joined_lin)
        val line: View = view.findViewById(R.id.home_item_region_detail_line)
        val item: RelativeLayout = view.findViewById(R.id.root_item)
        val spaceView: View = view.findViewById(R.id.space_view)
        val tvAction: TextView = view.findViewById(R.id.tv_action)
    }
}