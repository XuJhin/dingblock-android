package cool.dingstock.mine.dialog

import android.content.Context
import android.text.method.LinkMovementMethod
import cool.dingstock.appbase.dialog.BaseCenterBindingDialog
import cool.dingstock.mine.databinding.ScoreCommonDesDialogLayoutBinding

class ScoreCommonDescDialog(context: Context) : BaseCenterBindingDialog<ScoreCommonDesDialogLayoutBinding>(context) {
    init {
        viewBinding.confirmTv.setOnClickListener {
            dismiss()
        }
        viewBinding.descTv.movementMethod = LinkMovementMethod.getInstance()
    }

    fun setData(title: String, desc: String): ScoreCommonDescDialog {
        viewBinding.titleTv.text = title
        viewBinding.descTv.text = desc

        return this
    }
}