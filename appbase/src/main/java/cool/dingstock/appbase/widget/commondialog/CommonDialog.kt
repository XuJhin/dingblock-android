package cool.dingstock.appbase.widget.commondialog

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.view.View
import cool.dingstock.appbase.databinding.CommonDialogLayoutBinding
import cool.dingstock.appbase.dialog.BaseCenterBindingDialog

class CommonDialog private constructor(context: Context, builder: Builder) :
        BaseCenterBindingDialog<CommonDialogLayoutBinding>(context) {

    init {
        viewBinding.apply {
            if (builder.specialTextContent != null) {
                contentTv.text = builder.specialTextContent
                contentTv.highlightColor = Color.TRANSPARENT
                contentTv.movementMethod = LinkMovementMethod.getInstance()
            } else {
                contentTv.text = builder.content
            }
            if (!builder.confirmTxtColor.isNullOrEmpty()) {
                try {
                    confirmTv.setTextColor(Color.parseColor(builder.confirmTxtColor))
                } catch (e: Exception) {
                }
            }
            if (!builder.cancelTxtColor.isNullOrEmpty()) {
                try {
                    cancelTv.setTextColor(Color.parseColor(builder.cancelTxtColor))
                } catch (e: Exception) {
                }
            }
            if (builder.cancelTxt.isNullOrEmpty()) {
                cancelTv.visibility = View.GONE
                centerLine.visibility = View.GONE
            } else {
                cancelTv.visibility = View.VISIBLE
                cancelTv.text = builder.cancelTxt
                centerLine.visibility = View.VISIBLE
            }
            confirmTv.text = builder.confirmTxt
            confirmTv.setOnClickListener {
                dismiss()
                builder.onConfirmClickListener?.onClick(confirmTv)
            }
            cancelTv.setOnClickListener {
                dismiss()
                builder.onCancelClickListener?.onClick(cancelTv)
            }
        }
    }

    class Builder(
            val context: Context,
            var content: String? = null,
            var confirmTxt: String = "好的",
            var cancelTxt: String? = null,
            var confirmTxtColor: String? = null,
            var cancelTxtColor: String? = null,
            var onConfirmClickListener: View.OnClickListener? = null,
            var onCancelClickListener: View.OnClickListener? = null,
            var specialTextContent: SpannableStringBuilder? = null
    ) {

        constructor(context: Context) : this(context, null, "好的", null, null, null)


        fun specialContent(specialTextContent: SpannableStringBuilder?): Builder {
            this.specialTextContent = specialTextContent
            return this
        }

        fun content(content: String?): Builder {
            this.content = content
            return this
        }

        fun confirmTxt(confirmTxt: String): Builder {
            this.confirmTxt = confirmTxt
            return this
        }

        fun cancelTxt(cancelTxt: String?): Builder {
            this.cancelTxt = cancelTxt
            return this
        }

        fun setConfirmTxtColor(color: String?): Builder {
            this.confirmTxtColor = color
            return this
        }

        fun setCancelTxtColor(color: String?): Builder {
            this.cancelTxtColor = color
            return this
        }

        fun onConfirmClick(onConfirmClickListener: View.OnClickListener?): Builder {
            this.onConfirmClickListener = onConfirmClickListener
            return this
        }

        fun onCancelClick(onCancelClickListener: View.OnClickListener?): Builder {
            this.onCancelClickListener = onCancelClickListener
            return this
        }

        fun builder(): CommonDialog {
            return CommonDialog(context, this)
        }
    }
}



