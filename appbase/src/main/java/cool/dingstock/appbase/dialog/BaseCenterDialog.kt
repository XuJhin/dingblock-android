package cool.dingstock.appbase.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.*
import androidx.annotation.StyleRes
import cool.dingstock.appbase.R
import cool.dingstock.appbase.util.StatusBarUtil


abstract class BaseCenterDialog : Dialog {
    protected val rootView: View
    protected val contentView: View
    var mCancelable = true

    constructor(context: Context, @StyleRes themeResId: Int) : super(context, themeResId)

    constructor(context: Context) : this(context, R.style.CommonDialog)

    init {
        rootView = LayoutInflater.from(context).inflate(R.layout.dc_base_dialog_layout, null, false)
        setContentView(rootView)
        contentView = if(getLayoutId()!=0){
            LayoutInflater.from(context).inflate(getLayoutId(), rootView as ViewGroup, false)
        }else{
            providerContentView(rootView)!!
        }
        (rootView as? ViewGroup)?.addView(contentView)
        window?.setWindowAnimations(R.style.ScaleDialogAni)
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )
        window?.statusBarColor = Color.TRANSPARENT
        window?.navigationBarColor = Color.TRANSPARENT
        val lp: WindowManager.LayoutParams? = window?.attributes
        lp?.apply {
            lp.gravity = Gravity.CENTER
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            lp.width = WindowManager.LayoutParams.MATCH_PARENT //宽高可设置具体大小
        }
        window?.attributes = lp
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        rootView.setOnClickListener {
            if (mCancelable) {
                dismiss()
            }
        }
        contentView.setOnClickListener {
        }
        window?.let {
            StatusBarUtil.setTranslucent(it)
        }
    }


    abstract fun getLayoutId(): Int

    open fun providerContentView(parent:View):View?{
        return null
    }

    override fun setCanceledOnTouchOutside(cancel: Boolean) {
        super.setCanceledOnTouchOutside(cancel)
        mCancelable = cancel
    }

    override fun setCancelable(flag: Boolean) {
        super.setCancelable(flag)
        mCancelable = flag
    }
}