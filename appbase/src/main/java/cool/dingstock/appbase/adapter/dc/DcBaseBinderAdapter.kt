package cool.dingstock.appbase.adapter.dc

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.chad.library.adapter.base.BaseBinderAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import cool.dingstock.appbase.adapter.delegation.HolderReloadDelegation

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/18  9:44
 */
open class DcBaseBinderAdapter(list: ArrayList<Any>) : BaseBinderAdapter(list), LoadMoreModule {

    private val reloadDelegations = arrayListOf<HolderReloadDelegation>()

    //扩展属性
    var parentPosition = -1
    var parentData:Any? = null

    private var lifecycle: Lifecycle? = null

    fun registerReloadDelegations(holderReloadDelegation: HolderReloadDelegation) {
        reloadDelegations.add(holderReloadDelegation)
    }

    //一定要现在 registerReloadDelegations之后调用
    fun registerReloadLifecycle(lifecycle: Lifecycle) {
        this.lifecycle = lifecycle
        for (delegation in reloadDelegations) {
            lifecycle.addObserver(delegation)
        }
    }


    override fun convert(holder: BaseViewHolder, item: Any) {
        super.convert(holder, item)
        for (delegation in reloadDelegations) {
            delegation.onHolderConvert(item, holder, lifecycle)
        }
    }


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

    fun notifyDataItemChanged(position: Int) {
        notifyItemChanged(position + headerLayoutCount)
    }

    fun getDataList(): MutableList<Any> {
        return this.data
    }

    fun notifyDataItemRemoved(position: Int) {
        super.notifyItemRemoved(position + headerLayoutCount)
    }

    fun getDataPosition(holder: BaseViewHolder): Int {
        return holder.layoutPosition - headerLayoutCount
    }

}