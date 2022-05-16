package cool.dingstock.appbase.widget.dialog.common

import android.app.Dialog
import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import cool.dingstock.appbase.R
import cool.dingstock.appbase.databinding.CommonDcTitleImgDialogLayoutBinding
import cool.dingstock.appbase.ext.inflateBindingLazyBy
import cool.dingstock.appbase.ext.load


class DcTitleImgDialog
private constructor(context: Context, builder: Builder) : Dialog(context, R.style.CommonDialog3) {

    val vb by inflateBindingLazyBy<CommonDcTitleImgDialogLayoutBinding>(LayoutInflater.from(context))

    init {
        setContentView(vb.root)

        vb.tvTitle.text = builder.title
        vb.contentTv.movementMethod = ScrollingMovementMethod.getInstance()
        vb.contentTv.text = builder.content
        vb.confirmTv.text = builder.confirmTxt
        vb.cancelTv.text = builder.cancelTxt
        vb.tvTitle.text = builder.title
        vb.ivImg.load(builder.imgUrl)
        vb.cancelTv.setOnClickListener {
            dismiss()
        }
        vb.confirmTv.setOnClickListener {
            if (builder.confirmDismiss) {
                dismiss()
            }
            builder.onConfirmClickListener?.onClick(vb.confirmTv)
        }
        val lp: WindowManager.LayoutParams? = window?.attributes
        lp?.apply {
            lp.gravity = Gravity.CENTER
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        }
        window?.attributes = lp
    }

    class Builder(
        val context: Context,
        var title: String? = "提示",
        var content: String? = "content",
        var confirmTxt: String = "确定",
        var cancelTxt: String = "取消",
        var imgUrl: String = "",
        var onConfirmClickListener: View.OnClickListener? = null,
        var confirmDismiss: Boolean = true
    ) {
        fun content(content: String?): Builder {
            this.content = content
            return this
        }

        fun cancelTxt(cancelTxt: String): Builder {
            this.cancelTxt = cancelTxt
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

        fun imgUrl(imgUrl: String): Builder {
            this.imgUrl = imgUrl
            return this
        }

//        fun confirmDismiss(confirmDismiss: Boolean): Builder {
//            this.confirmDismiss = confirmDismiss
//            return this
//        }

        fun onConfirmClick(onConfirmClickListener: View.OnClickListener?): Builder {
            this.onConfirmClickListener = onConfirmClickListener
            return this
        }

        fun builder(): DcTitleImgDialog {
            return DcTitleImgDialog(context, this)
        }
    }
}