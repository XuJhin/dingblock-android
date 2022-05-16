package cool.dingstock.appbase.widget.paydialog

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import cool.dingstock.appbase.R
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.widget.banner.Banner
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.databinding.PayDialogLayoutBinding
import cool.dingstock.appbase.dialog.BaseCenterBindingDialog
import cool.dingstock.appbase.entity.bean.home.HomeBanner
import cool.dingstock.appbase.entity.bean.mine.VipActivityBean
import cool.dingstock.appbase.entity.bean.mine.VipPricesBean
import cool.dingstock.appbase.ext.toFormatStr
import cool.dingstock.appbase.net.api.mine.MineHelper
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.StringUtils
import java.lang.Exception

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/24  16:30
 */
class PayDialog(context: Context, activityType: String, private val mobClickName: String) : BaseCenterBindingDialog<PayDialogLayoutBinding>(context) {

    private val vipChoicerList by lazy {
        arrayListOf(viewBinding.vipDialogChoicerLayout.vipLin1,
                viewBinding.vipDialogChoicerLayout.vipLin2,
                viewBinding.vipDialogChoicerLayout.vipLin3)
    }

    private val vipChoicerOldTvList by lazy {
        arrayListOf(viewBinding.vipDialogChoicerLayout.vip1OldPriceTv,
                viewBinding.vipDialogChoicerLayout.vip2OldPriceTv,
                viewBinding.vipDialogChoicerLayout.vip3OldPriceTv)
    }

    var mProtocol = ""
    private var currentVipPricesBean: VipPricesBean? = null
    var mPrices: List<VipPricesBean>? = null

    init {
        val attributes = window?.attributes
        attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT
        window?.attributes = attributes
        initView()
        initData(activityType)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }

    private fun initData(activityType: String) {
        MineHelper.getInstance().getActivityGoods(activityType, object : ParseCallback<VipActivityBean> {
            override fun onSucceed(data: VipActivityBean?) {
                data?.let {
                    viewBinding.vipDialogChoicerLayout.goodsLayer.visibility = View.VISIBLE
                    viewBinding.payItemBanner.visibility = View.VISIBLE
                    mPrices = it.prices
                    mProtocol = it.protocol ?: ""
                    setBanner(it.banners)
                    setGoods(it.prices)
                }
            }

            override fun onFailed(errorCode: String?, errorMsg: String?) {
                viewBinding.vipDialogChoicerLayout.goodsLayer.visibility = View.GONE
                viewBinding.payItemBanner.visibility = View.GONE
                Logger.d("getActivityGoods$errorMsg")
            }
        })

    }

    private fun setGoods(prices: List<VipPricesBean>?) {
        viewBinding.vipDialogChoicerLayout.apply {
            if (prices == null || prices.isEmpty() || prices.size < 3) {
                goodsLayer.visibility = View.GONE
                return
            }
            goodsLayer.visibility = View.VISIBLE
            val price1 = prices[0]
            val price2 = prices[1]
            val price3 = prices[2]

            vipTag1Tv.text = price1.discount?.times(10)?.div(price1.price
                    ?: 1.0)?.toFormatStr("%.1f") + "折" ?: ""
            vip1MonthTv.text = price1.day
            vip1CurrentPriceTv.text = price1.discount?.div(100)?.toInt().toString()
            vip1OldPriceTv.text = price1.price.div(100).toInt().toString()

            vipTag2Tv.text = price2.discount?.times(10)?.div(price2.price
                    ?: 1.0)?.toFormatStr("%.1f") + "折" ?: ""
            vip2MonthTv.text = price2.day
            vip2CurrentPriceTv.text = price2.discount?.div(100)?.toInt().toString()
            vip2OldPriceTv.text = price2.price.div(100).toInt().toString()

            vipTag3Tv.text = price3.discount?.times(10)?.div(price3.price
                    ?: 1.0)?.toFormatStr("%.1f") + "折" ?: ""
            vip3MonthTv.text = price3.day
            vip3CurrentPriceTv.text = price3.discount?.div(100)?.toInt().toString()
            vip3OldPriceTv.text = price3.price.div(100).toInt().toString()
            //默认选中第三个
            onChoiceClick(2)
        }
    }

    private fun initView() {
        vipChoicerOldTvList.forEach {
            it.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG // 设置中划线并加清晰
        }
        vipChoicerList.forEachIndexed { index, linearLayout ->
            linearLayout.setOnClickListener { onChoiceClick(index) }
        }
        viewBinding.cancelIv.setOnClickListener {
            dismiss()
        }
        viewBinding.payItemBanner.setCircleMargeBottom(0)
        viewBinding.payItemBanner.setShowType(Banner.ShowType.Type2)
        viewBinding.payItemBanner.setBANNER_PERCENT(0.267)
        viewBinding.payItemBanner.setCircleDrawableRes(R.drawable.pay_banner_dot_selector)
        viewBinding.payItemBanner.updateView()

        setProtocol()
    }

    private fun onChoiceClick(index: Int) {
        vipChoicerList.forEach {
            it.isSelected = false
        }
        vipChoicerOldTvList.forEach {
            it.setTextColor(Color.parseColor("#959595"))
        }
        vipChoicerList[index].isSelected = true
        vipChoicerOldTvList[index].setTextColor(Color.parseColor("#C79270"))
        try {
            currentVipPricesBean = mPrices?.get(index)
        } catch (e: Exception) {
        }
    }


    private fun setBanner(getBannerItems: List<HomeBanner>?) {
        if (getBannerItems == null || getBannerItems.isEmpty()) {
            viewBinding.payItemBanner.visibility = View.GONE
            return
        }
        viewBinding.payItemBanner.setBannerItemClickListener { linkedUrl: String?, _: Int ->
            if (!StringUtils.isEmpty(linkedUrl)) {//不跳转
                DcUriRequest(context, linkedUrl!!).start()
                UTHelper.commonEvent(UTConstant.Pay.VipP_click_Banner, linkedUrl)
            }
        }
        viewBinding.payItemBanner.setData(getBannerItems)
    }

    private fun setProtocol() {
        val p1 = "《盯链会员服务协议》"
        val str = "点击开通即表示同意《盯链会员服务协议》"
        val sp = SpannableString(str)
        val cs1 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                DcUriRequest(context, mProtocol).start()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#FFFFFF")
                ds.isUnderlineText = false
                ds.clearShadowLayer()
            }
        }
        sp.setSpan(cs1, str.indexOf(p1), str.indexOf(p1) + p1.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        viewBinding.protocolTv.text = sp
        viewBinding.protocolTv.highlightColor = Color.TRANSPARENT
        viewBinding.protocolTv.movementMethod = LinkMovementMethod.getInstance()
    }

    fun setOnWehchatPayClick(listener: OnPayClickListener) {
        viewBinding.wechatPayFra.setOnClickListener {
            currentVipPricesBean?.goodsId?.let {
                listener.onPayClick(it, mobClickName)
            }
        }
    }

    fun setOnAliPayClick(listener: OnPayClickListener) {
        viewBinding.aliPayFra.setOnClickListener {
            currentVipPricesBean?.goodsId?.let {
                listener.onPayClick(it, mobClickName)
            }
        }
    }

    interface OnPayClickListener {
        fun onPayClick(goodsId: String, mobClickName: String)
    }
}