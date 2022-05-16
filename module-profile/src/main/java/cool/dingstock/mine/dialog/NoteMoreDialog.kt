package cool.dingstock.mine.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import androidx.core.view.isVisible
import cool.dingstock.appbase.ext.inflate
import cool.dingstock.mine.R
import cool.dingstock.mine.databinding.NoteMoreDialogBinding

class NoteMoreDialog(
    context: Context,
    private val showReselect: Boolean = false,
    private val onReselect: () -> Unit,
    private val onDelete: () -> Unit
): Dialog(context) {
    private val viewBinding by inflate<NoteMoreDialogBinding>()

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
            reselectTv.isVisible = showReselect
            reselectTv.setOnClickListener {
                dismiss()
                onReselect()
            }
            deleteTv.setOnClickListener {
                dismiss()
                onDelete()
            }
        }
    }
}