package cool.dingstock.mine.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import cool.dingstock.appbase.ext.inflate
import cool.dingstock.mine.R
import cool.dingstock.mine.databinding.SelectLotteryTypeDialogBinding
import cool.dingstock.mine.enums.LotteryNoteState

class SelectLotteryTypeDialog(
    context: Context,
    private val onDismiss: () -> Unit,
    private val onSelect: (LotteryNoteState) -> Unit
): Dialog(context) {
    private val viewBinding by inflate<SelectLotteryTypeDialogBinding>()

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setWindowAnimations(R.style.dc_bottomSheet_animation)
    }

    override fun show() {
        super.show()
        val attributes = window?.attributes
        attributes?.width = WindowManager.LayoutParams.MATCH_PARENT
        attributes?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = attributes
        window?.setGravity(Gravity.BOTTOM)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(viewBinding) {
            allTv.setOnClickListener {
                dismiss()
                onSelect(LotteryNoteState.ALL)
            }
            failedTv.setOnClickListener {
                dismiss()
                onSelect(LotteryNoteState.NOT_GET)
            }
            successTv.setOnClickListener {
                dismiss()
                onSelect(LotteryNoteState.GET)
            }
        }
        setOnDismissListener {
            onDismiss()
        }
    }
}