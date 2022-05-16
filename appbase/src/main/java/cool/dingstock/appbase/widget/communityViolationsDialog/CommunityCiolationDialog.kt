package cool.dingstock.appbase.widget.communityViolationsDialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import cool.dingstock.appbase.R
import cool.dingstock.appbase.databinding.CommunityViolationsDialogLayoutBinding
import cool.dingstock.appbase.ext.inflateBindingLazyBy

/**
 * @author WhenYoung
 *  CreateAt Time 2020/11/4  19:24
 */
//宽高可设置具体大小
class CommunityCiolationDialog(context: Context) : Dialog(context, R.style.CommonDialog) {
    val vb by inflateBindingLazyBy<CommunityViolationsDialogLayoutBinding>(LayoutInflater.from(context))

    init {
        setContentView(vb.root)
        val lp: WindowManager.LayoutParams? = window?.attributes
        lp?.gravity = Gravity.CENTER
        lp?.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp?.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = lp
        vb.confirmTv.setOnClickListener {
            dismiss()
        }
    }
}