package cool.dingstock.uicommon.product.dialog

import android.content.Context
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import cool.dingstock.appbase.dialog.BaseCenterDialog
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.uicommon.R


/**
 * 类名：SmsTipsDialog
 * 包名：cool.dingstock.uicommon.product.dialog
 * 创建时间：2021/7/7 3:21 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class SmsTipsDialog(context: Context) : BaseCenterDialog(context) {

    override fun getLayoutId(): Int {
        return R.layout.sms_tips_dialog_layout
    }

    init {
        findViewById<TextView>(R.id.tv).movementMethod = ScrollingMovementMethod.getInstance()
        findViewById<View>(R.id.btn).setOnShakeClickListener {
            dismiss()
        }
    }

}