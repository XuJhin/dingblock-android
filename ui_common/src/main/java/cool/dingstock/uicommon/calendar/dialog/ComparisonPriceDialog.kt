package cool.dingstock.uicommon.calendar.dialog

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.fragment.app.BaseBottomFullViewBindingFragment
import androidx.recyclerview.widget.LinearLayoutManager
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.entity.bean.calendar.PriceListResultEntity
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.util.ClipboardHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.widget.CommonEmptyView
import cool.dingstock.calendar.item.PriceComparisonItemBinder
import cool.dingstock.lib_base.util.TimeUtils
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.uicommon.R
import cool.dingstock.uicommon.databinding.CalendarComparisonPriceDialogLayoutBinding


/**
 * 类名：ComparisonPriceDialog
 * 包名：cool.dingstock.calendar.sneaker.price
 * 创建时间：2021/7/13 10:53 上午
 * 创建人： WhenYoung
 * 描述：
 **/
class ComparisonPriceDialog :
        BaseBottomFullViewBindingFragment<CalendarComparisonPriceDialogLayoutBinding>() {


    val adapter by lazy {
        val ada = DcBaseBinderAdapter(arrayListOf())
        ada.addItemBinder(itemBinder)
        return@lazy ada
    }
    val itemBinder by lazy {
        PriceComparisonItemBinder()
    }


    private var priceListResultEntity: PriceListResultEntity? = null


//    override fun getParentLayer(): View = viewBinding.parentLayer
//
//    override fun getContentLayer(): View = viewBinding.contentLayer
//
    @SuppressLint("SetTextI18n")
    override fun initDataEvent() {
        viewBinding.closeIv.setOnShakeClickListener {
            dismiss()
        }

        context?.let {
            adapter.setEmptyView(CommonEmptyView(it, "暂无数据", noBg = true, fullParent = false))
        }
        viewBinding.rv.adapter = adapter
        viewBinding.rv.layoutManager = LinearLayoutManager(context)


        viewBinding.productTitleTv.text = priceListResultEntity?.product?.name ?: ""
        viewBinding.sneakersIv.load(priceListResultEntity?.product?.imageUrl)
        viewBinding.priceTv.text = "发售价：${priceListResultEntity?.product?.price}"
        viewBinding.skuTv.text = priceListResultEntity?.product?.sku ?: ""
        viewBinding.skuTv.setOnShakeClickListener {
            context?.let { it1 ->
                ClipboardHelper.copyMsg(it1, viewBinding.skuTv.text.toString()) {
                    ToastUtil.getInstance()._short(context, "复制成功")
                }
            }
        }
        viewBinding.updateTime.text = "数据更新于" + TimeUtils.formatTimestamp(priceListResultEntity?.product?.updatedAt
                ?: System.currentTimeMillis(), "HH:mm")

        viewBinding.sourceTitleTv.text = priceListResultEntity?.product?.listTitle ?: ""
        try {
            val icon1 = priceListResultEntity?.product?.channelInfo?.get(0)?.icon
            if (!TextUtils.isEmpty(icon1)) {
                viewBinding.sourceIv1.load(icon1)
            } else {
                viewBinding.sourceIv1.setImageResource(R.drawable.calendar_price_dw_icon)
            }
        } catch (e: Exception) {
            viewBinding.sourceIv1.setImageResource(R.drawable.calendar_price_dw_icon)
        }
        try {
            val icon2 = priceListResultEntity?.product?.channelInfo?.get(1)?.icon
            if (!TextUtils.isEmpty(icon2)) {
                viewBinding.sourceIv2.load(icon2)
            } else {
                viewBinding.sourceIv2.setImageResource(R.drawable.calendar_price_nice_icon)
            }
        } catch (e: Exception) {
            viewBinding.sourceIv2.setImageResource(R.drawable.calendar_price_nice_icon)
        }
        adapter.setList(priceListResultEntity?.product?.priceInfoList ?: arrayListOf())


    }


    fun setData(priceListResultEntity: PriceListResultEntity) {
        this.priceListResultEntity = priceListResultEntity
    }

}