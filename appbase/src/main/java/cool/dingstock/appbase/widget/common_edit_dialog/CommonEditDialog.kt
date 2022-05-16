package cool.dingstock.appbase.widget.common_edit_dialog

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText
import cool.dingstock.appbase.databinding.CommonEditDialogLayoutBinding
import cool.dingstock.appbase.dialog.BaseCenterBindingDialog


class CommonEditDialog private constructor(context: Context, builder: Builder) : BaseCenterBindingDialog<CommonEditDialogLayoutBinding>(context) {

    init {
        viewBinding.title.text = builder.title
        viewBinding.confirmTv.text = builder.confirmTxt
        viewBinding.cancelTv.text = builder.cancelTxt
        if (builder.maxLength != -1) {
            viewBinding.edit.filters = arrayOf(InputFilter.LengthFilter(builder.maxLength))
        }
        viewBinding.confirmTv.setOnClickListener {
            if (builder.confirmDismiss) {
                dismiss()
            }
            builder.onConfirmClickListener?.onConfirmClick(viewBinding.edit, this)
        }
        viewBinding.cancelTv.setOnClickListener {
            if (builder.cancelDismiss) {
                dismiss()
            }
            builder.onCancelClickListener?.onCancelClick(viewBinding.edit)
        }
        viewBinding.edit.hint = builder.hint
        viewBinding.edit.setText(builder.content)
        viewBinding.confirmTv.isEnabled = viewBinding.edit.text.isNotEmpty()
        viewBinding.edit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewBinding.confirmTv.isEnabled = viewBinding.edit.text.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    fun getEditTxt(): String {
        return viewBinding.edit.text.toString()
    }

    class Builder(val context: Context,
                  var title: String? = null,
                  var hint: String? = null,
                  var content: String? = null,
                  var maxLength: Int = -1,
                  var confirmTxt: String = "确认",
                  var cancelTxt: String = "取消",
                  var onConfirmClickListener: OnConfirmClickListener? = null,
                  var confirmDismiss: Boolean = true,
                  var onCancelClickListener: OnCancelClickListener? = null,
                  var cancelDismiss: Boolean = true) {
        constructor(context: Context) : this(context, null, null, "", -1, "确认", "取消",null, true, null, true) {
        }

        fun content(content: String?): Builder {
            this.content = content
            return this
        }

        fun hint(hint: String?): Builder {
            this.hint = hint
            return this
        }

        fun confirmTxt(confirmTxt: String): Builder {
            this.confirmTxt = confirmTxt
            return this
        }

        fun cancelTxt(cancelTxt: String): Builder {
            this.cancelTxt = cancelTxt
            return this
        }

        fun title(title: String): Builder {
            this.title = title
            return this
        }

        fun maxLength(maxLength: Int): Builder {
            this.maxLength = maxLength
            return this
        }

        fun confirmDismiss(confirmDismiss: Boolean): Builder {
            this.confirmDismiss = confirmDismiss
            return this
        }

        fun onConfirmClick(onConfirmClickListener: OnConfirmClickListener?): Builder {
            this.onConfirmClickListener = onConfirmClickListener
            return this
        }

        fun cancelDismiss(cancelDismiss: Boolean): Builder {
            this.cancelDismiss = cancelDismiss
            return this
        }

//        fun onCancelClick(onCancelClickListener: OnCancelClickListener?): Builder {
//            this.onCancelClickListener = onCancelClickListener
//            return this
//        }

        fun builder(): CommonEditDialog {
            return CommonEditDialog(context, this)
        }
    }

    interface OnConfirmClickListener {
        fun onConfirmClick(edit: EditText, dialog: CommonEditDialog)
    }

    interface OnCancelClickListener {
        fun onCancelClick(edit: EditText)
    }
}