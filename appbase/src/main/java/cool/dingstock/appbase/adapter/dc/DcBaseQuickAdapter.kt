package cool.dingstock.appbase.adapter.dc

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/16  15:23
 */
abstract class DcBaseQuickAdapter <T,VH : BaseViewHolder>(layoutRes:Int,data:ArrayList<T>) : BaseQuickAdapter<T,VH>(layoutRes,data){
    open fun showSuccessDialog(text: String?) {
        TipDialog.show(text,WaitDialog.TYPE.SUCCESS)
                .setCancelable(true)

    }

    open fun showSuccessDialog(@StringRes resId: Int) {
        val successString: String = context.resources.getString(resId)
        showSuccessDialog(successString)
    }

    open fun showFailedDialog(text: String?) {
        TipDialog.show(text,WaitDialog.TYPE.ERROR)
            .setCancelable(true)
    }

    open fun showWaringDialog(@StringRes resId: Int) {
        val waringString: String = context.resources.getString(resId)
        showWaringDialog(waringString)
    }

    open fun showWaringDialog(text: String?) {
        TipDialog.show(text,WaitDialog.TYPE.WARNING)
            .setCancelable(true)
    }

    fun notifyDataItemChanged(position:Int){
        notifyItemChanged(position+headerLayoutCount)
    }

    fun notifyDataItemRemoved(position:Int){
        super.notifyItemRemoved(position+headerLayoutCount)
    }

    fun getDataPosition(holder: BaseViewHolder):Int{
        return holder.layoutPosition - headerLayoutCount
    }


}