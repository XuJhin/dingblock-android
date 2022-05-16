package cool.dingstock.appbase.widget.common_edit_dialog

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText
import cool.dingstock.appbase.dialog.BaseCenterBindingDialog
import cool.dingstock.appbase.databinding.CommonEditInputDialogLayoutBinding

class CommonEditInputDialog private constructor(context: Context, builder: Builder) :
    BaseCenterBindingDialog<CommonEditInputDialogLayoutBinding>(context) {

    init {
        viewBinding.apply {
            tvDesc.text = builder.desc
            tvHint.text = builder.hintMsg
            tvTitle.text = builder.title
            tvConfirm.text = builder.confirmTxt

            if (builder.maxLength != -1) {
                edtInput.filters = arrayOf(InputFilter.LengthFilter(builder.maxLength))
                tvNumRem.text = "0/" + builder.maxLength
            }
            tvConfirm.setOnClickListener {
                if (builder.confirmDismiss) {
                    dismiss()
                }
                builder.onConfirmClickListener?.onConfirmClick(edtInput, this@CommonEditInputDialog)
            }
            edtInput.hint = builder.hint

            val currentTime = System.currentTimeMillis()
            val isTimeReady =
                (builder.nextChangeTime ?: 0L) < currentTime || builder.nextChangeTime == 0L

            tvConfirm.isEnabled = edtInput.text.isNotEmpty() && isTimeReady


            edtInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    var spaceNum = 0
                    val inputLength = edtInput.text.length
                    edtInput.text.toString().forEach {
                        if (it == ' ') {
                            spaceNum += 1
                        }
                    }

                    val nowTime = System.currentTimeMillis()
                    val timeReady =
                        (builder.nextChangeTime ?: 0L) < nowTime || builder.nextChangeTime == 0L
                    val isBtnEnable =  edtInput.text.isNotEmpty() && spaceNum != inputLength && timeReady

                    tvConfirm.isEnabled = isBtnEnable
                    if(isBtnEnable)  tvHint.text = ""
                    tvNumRem.text = edtInput.text.length.toString() + "/" + builder.maxLength
                }

                override fun afterTextChanged(s: Editable) {
                }
            })
            edtInput.setText(builder.content)
        }
    }

    fun getEditTxt(): String {
        return viewBinding.edtInput.text.toString()
    }

    fun setHintTxt(txt: String) {
        viewBinding.tvHint.text = txt
    }

    class Builder(
        val context: Context,
        var title: String? = null,
        var desc: String? = null,
        var hint: String? = null,
        var nextChangeTime: Long? = 0L,
        var content: String? = null,
        var hintMsg: String? = null,
        var maxLength: Int = -1,
        var confirmTxt: String = "确认",
        var onConfirmClickListener: OnConfirmClickListener? = null,
        var confirmDismiss: Boolean = true,
    ) {
        constructor(context: Context) : this(
            context,
            null,
            null,
            "",
            0L,
            "",
            "",
            -1,
            "确认",
            null,
            true
        )

        fun nextChangeTime(nextChangeTime: Long?): Builder {
            this.nextChangeTime = nextChangeTime
            return this
        }

        fun content(content: String?): Builder {
            this.content = content
            return this
        }

        fun desc(desc: String?): Builder {
            this.desc = desc
            return this
        }

        fun hintMsg(hintMsg: String?): Builder {
            this.hintMsg = hintMsg
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

        fun builder(): CommonEditInputDialog {
            return CommonEditInputDialog(context, this)
        }
    }

    interface OnConfirmClickListener {
        fun onConfirmClick(edit: EditText, dialog: CommonEditInputDialog)
    }
}