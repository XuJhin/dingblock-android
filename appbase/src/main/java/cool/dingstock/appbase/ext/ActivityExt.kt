package cool.dingstock.appbase.ext

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import cool.dingstock.appbase.toast.TopToast
import cool.dingstock.lib_base.thread.ThreadPoolHelper
import cool.dingstock.lib_base.util.Logger


fun Activity.hideKeyboard() {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (imm.isActive && this.currentFocus != null) {
        if (this.currentFocus?.windowToken != null) {
            imm.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
        }
    }
}

fun androidx.fragment.app.Fragment.hideKeyboard() {
    val imm = this.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (imm.isActive && this.activity?.currentFocus != null) {
        if (this.activity?.currentFocus?.windowToken != null) {
            imm.hideSoftInputFromWindow(this.activity?.currentFocus?.windowToken, 0)
        }
    }
}

fun Activity.getColorRes(@ColorRes id: Int) = ContextCompat.getColor(this, id)

fun Activity.getDrawableRes(@DrawableRes id: Int) =
    ContextCompat.getDrawable(this, id)

fun Activity.showToastShort(@StringRes resId: Int) {
    showToastShort(this.resources.getString(resId))
}

fun Activity.showToastLong(@StringRes resId: Int) {
    showToastLong(this.resources.getString(resId))
}

fun Activity.showToastShort(@StringRes resId: Int, vararg formatArgs: Any?) {
    showToastShort(this.resources.getString(resId, *formatArgs))
}

fun Activity.showToastLong(@StringRes resId: Int, vararg formatArgs: Any?) {
    showToastLong(this.resources.getString(resId, *formatArgs))
}

/**
 * Please use the new method.
 * See the [.showToastShort]
 */
fun Activity.showToastShort(text: CharSequence?) {
    Logger.i(this.javaClass.simpleName + " The toast context: " + text)
    ThreadPoolHelper.getInstance().runOnUiThread {
        var str = ""
        if (text != null) {
            str = text.toString()
        }
        TopToast.INSTANCE.showToast(this, str, Toast.LENGTH_SHORT)
    }
}

/**
 * Please use the new method.
 * See the [.showToastLong]
 */
fun Activity.showToastLong(text: CharSequence) {
    Logger.i(this.javaClass.simpleName + " The toast context: " + text)
    ThreadPoolHelper.getInstance()
        .runOnUiThread { TopToast.INSTANCE.showToast(this, text.toString(), Toast.LENGTH_LONG) }
}