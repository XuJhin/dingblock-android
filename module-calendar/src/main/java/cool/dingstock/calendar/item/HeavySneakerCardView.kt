package cool.dingstock.calendar.item

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import cool.dingstock.appbase.constant.CircleConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.home.CalenderProductEntity
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.inflateBindingLazy
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.ClipboardHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.calendar.databinding.HeavySeneakerCardLayoutBinding

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/28  11:56
 */
class HeavySneakerCardView(context: Context, val entity: CalenderProductEntity) :
    FrameLayout(context) {

    private val vb =
        inflateBindingLazy<HeavySeneakerCardLayoutBinding>(LayoutInflater.from(context), this, true)

    init {
        vb.apply {
            if (entity.dealCount == null || entity.dealCount == 0) {
                dealNumberLayer.hide(true)
                flGoodId.layoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 140f)
                flSalePrice.layoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 87f)
                onekeySearchPriceLayer.layoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 103f)
            } else {
                dealNumberLayer.hide(false)
                flGoodId.layoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 90f)
                flSalePrice.layoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 70f)
                onekeySearchPriceLayer.layoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 85f)
                dealNumberLayer.layoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 50f)
                dealTv.text = entity.dealCount.toString().plus("条")
            }
            dealNumberLayer.setOnShakeClickListener {
                UTHelper.commonEvent(UTConstant.SaleDetails.HotSaleP_click_transaction)
//                DcUriRequest(getContext(), CircleConstant.Uri.FIND_TOPIC_DETAIL)
//                    .putUriParameter(CircleConstant.UriParams.PRODUCT_ID, entity.id)
//                    .putUriParameter(CircleConstant.UriParams.KEYWORD, entity.name)
//                    .putUriParameter(CircleConstant.UriParams.TOPIC_DETAIL_ID,  MobileHelper.getInstance().configData.dealTalkId)
//                    .start()
                DcUriRequest(getContext(), CircleConstant.Uri.DEAL_DETAILS)
                    .putUriParameter(CircleConstant.UriParams.ID, entity.id)
                    .start()
            }



            iv.load(entity.imageUrl)
            topBg.load(entity.topBgUrl)
            try {
                val color = Color.parseColor(entity.color ?: "#000000")
                bottomBg.setBackgroundColor(color)
            } catch (e: Exception) {
            }
            nameTv.text = entity.name
            skuTv.text = entity.sku ?: ""
            if (TextUtils.isEmpty(entity.marketPrice)) {
                marketPriceLeftIcon.hide(true)
                marketPriceTv.text = "-"
            } else {
                marketPriceLeftIcon.hide(false)
                marketPriceTv.text = entity.marketPrice
            }
            val price: String? = entity.price
            if (TextUtils.isEmpty(price) || "null".equals(price, ignoreCase = true)) {
                priceTv.text = "未知"
            } else {
                priceTv.text = "$price"
            }
            nameTv.setOnLongClickListener {
                val text = nameTv.text.toString()
                if (!TextUtils.isEmpty(text)) {
                    ClipboardHelper.showMenu(context, text, it)
                }
                return@setOnLongClickListener true
            }
            skuTv.setOnLongClickListener {
                val text = skuTv.text.toString()
                if (!TextUtils.isEmpty(text)) {
                    ClipboardHelper.showMenu(context, text, it)
                }
                return@setOnLongClickListener true
            }
        }
    }

    fun setOnSearchPriceClick(onClick: (id: String) -> Unit) {
        vb.onekeySearchPriceLayer.setOnShakeClickListener {
            entity.id?.let {
                onClick(it)
            }
        }
    }
}