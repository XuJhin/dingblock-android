package cool.dingstock.appbase.widget.commontitledialog

import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.View
import cool.dingstock.appbase.databinding.CommonTitleDialogLayoutBinding
import cool.dingstock.appbase.dialog.BaseCenterBindingDialog


class CommonTitleDialog private constructor(context: Context, builder: Builder) : BaseCenterBindingDialog<CommonTitleDialogLayoutBinding>(context) {

    init {
        viewBinding.apply {
            title.text = builder.title
            contentTv.movementMethod = ScrollingMovementMethod.getInstance()
            contentTv.text = builder.content
            confirmTv.text = builder.confirmTxt
            confirmTv.setOnClickListener{
                if(builder.confirmDismiss){
                    dismiss()
                }
                builder.onConfirmClickListener?.onClick(confirmTv)
            }
        }
    }

    class Builder(val context:Context,
                  var title:String? =null,
                  var content:String? = null,
                  var confirmTxt:String ="好的",
                  var onConfirmClickListener:View.OnClickListener?=null,
                  var confirmDismiss:Boolean = true){
        constructor(context: Context) : this(context, null, null, "我知道了", null, true)

        fun content(content:String?):Builder{
            this.content = content
            return this
        }

        fun confirmTxt(confirmTxt:String):Builder{
            this.confirmTxt = confirmTxt
            return this
        }

        fun title(title:String):Builder{
            this.title = title
            return this
        }

//        fun confirmDismiss(confirmDismiss:Boolean):Builder{
//            this.confirmDismiss = confirmDismiss
//            return this
//        }

        fun onConfirmClick(onConfirmClickListener:View.OnClickListener?):Builder{
            this.onConfirmClickListener = onConfirmClickListener
            return this
        }

        fun builder():CommonTitleDialog{
            return CommonTitleDialog(context,this)
        }
    }
}



