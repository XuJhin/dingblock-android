package cool.dingstock.appbase.widget

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StyleRes
import com.google.android.material.bottomsheet.BottomSheetDialog

class KeyBoardBottomSheetDialog : BottomSheetDialog {
    constructor(context: Context) : super(context)
    constructor(context: Context, @StyleRes theme: Int) : super(context, theme)
    protected constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener
    ) : super(context, cancelable, cancelListener)

    override fun dismiss() {
        hideKeyBoard()
        super.dismiss()
    }

    fun hideKeyBoard() {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.let { imm ->
            window?.currentFocus?.let {
                imm.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
    }

    fun showKeyBoard() {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.let { imm ->
            window?.currentFocus?.let {
                it.postDelayed({
                    imm.showSoftInput(it, 0)
                }, 150)
            }
        }
    }
}
