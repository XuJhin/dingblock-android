package cool.dingstock.appbase.dialog

import android.content.Context
import android.text.method.ScrollingMovementMethod
import cool.dingstock.appbase.databinding.MineDialogLogisticsInfoBinding
import cool.dingstock.appbase.entity.bean.box.ReceiveRecordEntity
import cool.dingstock.appbase.entity.bean.score.ScoreRecordEntity
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.util.ClipboardHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.lib_base.util.ToastUtil


/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/5/26 10:47
 * @Version:         1.1.0
 * @Description:
 */
class LogisticsInfoDialog(context: Context) : BaseCenterBindingDialog<MineDialogLogisticsInfoBinding>(context) {

    interface OnClickListener {
        fun onClickConfirm()
    }

    var onClickListener: OnClickListener? = null


    fun setData(entity: ScoreRecordEntity): LogisticsInfoDialog {
        viewBinding.apply {
            infoTv.movementMethod = ScrollingMovementMethod.getInstance()
            infoTv.text = "收货人: ".plus(entity.consignee)
                    .plus("\n手机号码: +86 ").plus(entity.consignPhone)
                    .plus("\n详细地址: ").plus(entity.address)
            titleTv.text = entity.title
            descTv.text = entity.subTitle
            confirmTv.text = entity.detailButtonStr
            when (entity.orderStatus) {
                "sent" -> {
                    descTv.text = entity.expressCo.plus(": ").plus(entity.expressId)
                    confirmTv.setOnClickListener {
                        ClipboardHelper.copyMsg(context, entity.expressId) {
                            ToastUtil.getInstance()._short(context, "复制成功")
                            dismiss()
                        }
                    }
                }
                "prepare" -> {
                    confirmTv.setOnClickListener {
                        dismiss()
                    }
                }
                else -> {
                    confirmTv.setOnClickListener {
                        dismiss()
                    }
                }
            }
        }
        return this
    }

    fun setData(entity: ReceiveRecordEntity): LogisticsInfoDialog {
        viewBinding.apply {
            infoTv.movementMethod = ScrollingMovementMethod.getInstance()
            infoTv.text = "收货人: ".plus(entity.address?.name)
                    .plus("\n手机号码: ").plus(entity.address?.mobile)
                    .plus("\n详细地址: ").plus(entity.address?.province
                            .plus(entity.address?.city)
                            .plus(entity.address?.district)
                            .plus(entity.address?.address))
            titleTv.text = entity.title
            descTv.text = entity.subTitle
            confirmTv.text = entity.btnTitle
            when (entity.state) {
                "sent" -> {
                    descTv.text = entity.expressName.plus(": ").plus(entity.expressNo)
                    confirmTv.setOnClickListener {
                        ClipboardHelper.copyMsg(context, entity.expressNo) {
                            ToastUtil.getInstance()._short(context, "复制成功")
                            dismiss()
                        }
                    }
                }
                "pending" -> {
                    confirmTv.setOnClickListener {
                        dismiss()
                    }
                }
                else -> {
                    confirmTv.setOnClickListener {
                        dismiss()
                    }
                }
            }
        }
        return this
    }

    fun setData(name: String, phone: String, address: String): LogisticsInfoDialog {
        viewBinding.apply {
            closeTv.hide(false)
            closeTv.setOnShakeClickListener {
                dismiss()
            }
            infoTv.movementMethod = ScrollingMovementMethod.getInstance()
            descTv.hide(true)
            infoTv.text = "收货人: ".plus(name)
                    .plus("\n手机号码: ").plus(phone)
                    .plus("\n详细地址: ").plus(address)
            titleTv.text = "确认领取"
            confirmTv.text = "立即领取"
            confirmTv.setOnClickListener {
                onClickListener?.onClickConfirm()
                dismiss()
            }
        }
        return this
    }
}