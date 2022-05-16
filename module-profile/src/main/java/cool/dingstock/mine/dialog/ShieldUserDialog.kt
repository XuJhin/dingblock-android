package cool.dingstock.mine.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.TextView
import cool.dingstock.appbase.ext.hide
import cool.dingstock.mine.R

class ShieldUserDialog(context: Context) : Dialog(context) {
    var shield: Boolean = false
    var showCancel: Boolean = true
    var root: View = View.inflate(context, R.layout.dialog_shield_user, null)
    var tvShield: TextView = root.findViewById(R.id.tv_shield)
    var tvCancel: TextView = root.findViewById(R.id.cancel)
    var tvCancelFollow: TextView = root.findViewById(R.id.tv_cancel_follow)
    var userId: String = ""
    lateinit var shieldListener: ShieldListener

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setWindowAnimations(R.style.dc_bottomSheet_animation)
        setContentView(root, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT))
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
        if (!showCancel) {
            tvCancelFollow.hide()
        }
        tvCancelFollow.setOnClickListener {
            if (::shieldListener.isInitialized) {
                shieldListener.onCancelFollowListener(userId)
                dismiss()
            }
        }
        tvCancel.setOnClickListener { dismiss() }
        tvShield.apply {
            text = if (shield) {
                "取消屏蔽"
            } else {
                "屏蔽用户"
            }
            setOnClickListener {
                if (::shieldListener.isInitialized) {
                    shieldListener.onShieldListener(userId, shield)
                    dismiss()
                }
            }
        }
    }

}

interface ShieldListener {
    fun onShieldListener(userId: String, shield: Boolean)
    fun onCancelFollowListener(userId: String)
}