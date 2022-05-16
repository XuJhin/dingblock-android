package cool.dingstock.appbase.widget.dialog.common

import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.View
import cool.dingstock.appbase.databinding.CommonDcTitleDialogLayoutBinding
import cool.dingstock.appbase.dialog.BaseCenterBindingDialog

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/3/1 18:41
 * @Version:         1.1.0
 * @Description:
 */
//宽高可设置具体大小
class DcTitleDialog
private constructor(context: Context, builder: Builder) : BaseCenterBindingDialog<CommonDcTitleDialogLayoutBinding>(context) {


    init {
        viewBinding.apply {
            tvTitle.text = builder.title
            contentTv.movementMethod = ScrollingMovementMethod.getInstance()
            contentTv.text = builder.content
            confirmTv.text = builder.confirmTxt
            cancelTv.text = builder.cancelTxt
            tvTitle.text = builder.title
            cancelTv.setOnClickListener {
                dismiss()
            }
            confirmTv.setOnClickListener {
                if (builder.confirmDismiss) {
                    dismiss()
                }
                builder.onConfirmClickListener?.onClick(confirmTv)
            }
        }
    }


    class Builder @JvmOverloads constructor(val context: Context,
				  var title: String? = "提示",
				  var content: String? = "content",
				  var confirmTxt: String = "确定",
				  var cancelTxt: String = "取消",
				  var onConfirmClickListener: View.OnClickListener? = null,
				  var confirmDismiss: Boolean = true) {
        fun content(content: String?): Builder {
            this.content = content
            return this
        }

        fun cancelTxt(cancelTxt: String): Builder {
            this.cancelTxt = cancelTxt
            return this
        }

        fun confirmTxt(confirmTxt: String): Builder {
            this.confirmTxt = confirmTxt
            return this
        }

        fun title(title: String): Builder {
            this.title = title
            return this
        }

//        fun confirmDismiss(confirmDismiss: Boolean): Builder {
//            this.confirmDismiss = confirmDismiss
//            return this
//        }

        fun onConfirmClick(onConfirmClickListener: View.OnClickListener?): Builder {
            this.onConfirmClickListener = onConfirmClickListener
            return this
        }

        fun builder(): DcTitleDialog {
            return DcTitleDialog(context, this)
        }
    }
}