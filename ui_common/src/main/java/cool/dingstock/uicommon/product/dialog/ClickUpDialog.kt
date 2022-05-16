package cool.dingstock.uicommon.product.dialog

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import cool.dingstock.appbase.dialog.BaseCenterDialog
import cool.dingstock.uicommon.R


class ClickUpDialog(context: Context) : BaseCenterDialog(context) {

    var tvName: TextView? = null
    var tvNum: TextView? = null
    override fun getLayoutId(): Int {
        return R.layout.click_up_dialog_layout
    }

    fun setData(name: String, num: Int) {
        tvName?.text = name
        tvNum?.text = "总共获得${num}个赞"
    }

    init {
        findViewById<ImageView>(R.id.dialog_cover).setOnClickListener {
            dismiss()
        }
        tvName = findViewById(R.id.tv_user_name)
        tvName?.setOnClickListener {
            dismiss()
        }
        tvNum = findViewById(R.id.tv_click_up_count)
        tvNum?.setOnClickListener {
            dismiss()
        }
    }
}