package cool.dingstock.mine.dialog

import android.annotation.SuppressLint
import android.content.Context
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.dialog.BaseCenterBindingDialog
import cool.dingstock.appbase.entity.bean.score.ScoreExchangeResultEntity
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.ClipboardHelper
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.mine.databinding.ScoreExchangeSuccessDialogLayoutBinding

/**
 * 兑换商品成功弹窗
 */
class ScoreExchangeSuccessDialog(context: Context) :
    BaseCenterBindingDialog<ScoreExchangeSuccessDialogLayoutBinding>(context) {


    init {
        viewBinding.closeIv.setOnClickListener {
            this.dismiss()
        }
        setCancelable(true)
    }

    @SuppressLint("SetTextI18n")
    fun setData(
        data: ScoreExchangeResultEntity,
        productName: String
    ): ScoreExchangeSuccessDialog {
        viewBinding.titleTv.text = data.title
        viewBinding.descTv.text = data.desc
        viewBinding.confirmTv.text = data.buttonStr
        viewBinding.imgIv.apply {
            load(data.imgUrl, radius = 6f)
        }
        when (data.type) {
            ScoreExchangeResultEntity.Type.vip -> {
                viewBinding.confirmTv.apply {
                    text = "查看我的VIP"
                    setOnClickListener {
                        //路由
                        DcUriRequest(context, data.link).start()
                        dismiss()
                    }
                }
            }
            ScoreExchangeResultEntity.Type.reality -> {
                viewBinding.confirmTv.apply {
                    text = "去领取"
                    setOnClickListener {
                        DcUriRequest(context, MineConstant.Uri.EXCHANGE_RECORD).start()
                        dismiss()
                    }
                }
            }
            ScoreExchangeResultEntity.Type.coupon -> {
                viewBinding.confirmTv.apply {
                    text = "复制券码"
                    setOnClickListener {
                        ClipboardHelper.copyMsg(context, data.code ?: "") {
                            ToastUtil.getInstance()._short(context, "已复制到剪切板")
                            dismiss()
                        }
                    }
                }
            }
            ScoreExchangeResultEntity.Type.achievement -> {
                viewBinding.confirmTv.apply {
                    setOnClickListener {
                        DcUriRequest(context, data.link).start()
                        dismiss()
                    }
                }
            }
        }
        UTHelper.commonEvent(
            UTConstant.Score.IntegralP_exposure_SuccessfulRedemptionPopup,
            "button",
            viewBinding.confirmTv.text.toString(),
            "ProductName",
            productName
        )
        return this
    }
}