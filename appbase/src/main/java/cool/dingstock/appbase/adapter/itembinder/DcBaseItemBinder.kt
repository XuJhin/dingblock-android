package cool.dingstock.appbase.adapter.itembinder

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseBinderAdapter
import com.chad.library.adapter.base.binder.BaseItemBinder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import cool.dingstock.appbase.toast.TopToast
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.lib_base.util.Logger

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/18  9:49
 */
abstract class DcBaseItemBinder<T, VH : BaseViewHolder> : BaseItemBinder<T, VH>() {
    var onItemClickListener: OnItemClickListener? = null
    var onItemLongCLickListener: OnItemLongClickListener? = null

    fun getDataPosition(holder: BaseViewHolder): Int {
        return holder.layoutPosition - adapter.headerLayoutCount
    }

    override fun convert(holder: VH, data: T) {
        holder.itemView.setOnShakeClickListener {
            onItemClickListener?.let {
                it.onItemClick(adapter, holder, getDataPosition(holder))
            }
        }
        holder.itemView.setOnLongClickListener {
            onItemLongCLickListener?.let {
                return@setOnLongClickListener it.onItemClick(
                    adapter,
                    holder,
                    getDataPosition(holder)
                )
            }
            return@setOnLongClickListener true
        }
        onConvert(holder, data)
    }

    override fun convert(holder: VH, data: T, payloads: List<Any>) {
        super.convert(holder, data, payloads)
        onConvert(holder, data, payloads)
    }

    abstract fun onConvert(holder: VH, data: T)

    open fun onConvert(holder: VH, data: T, payloads: List<Any>) {

    }

    open fun showSuccessDialog(text: String?) {
        TipDialog.show(text, WaitDialog.TYPE.SUCCESS)
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

    open fun showToastShort(text: CharSequence) {
        Logger.i(this.javaClass.simpleName + " The toast context: " + text)
        TopToast.INSTANCE.showToast(context, text.toString(), Toast.LENGTH_SHORT)
    }

    open fun onItemShow(holder: BaseViewHolder?, position: Int) {


    }

    var showEndPosition = -1

    fun registerItemShow() {
        //todo 这里的性能有待优化 先不用
        adapter.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                (adapter.recyclerView.layoutManager as? LinearLayoutManager)?.let {
                    val findFirstVisibleItemPosition = it.findFirstVisibleItemPosition()
                    if (showEndPosition != findFirstVisibleItemPosition) {
                        showEndPosition = findFirstVisibleItemPosition
                        onItemShow(
                            recyclerView.findViewHolderForLayoutPosition(
                                findFirstVisibleItemPosition
                            ) as? BaseViewHolder, findFirstVisibleItemPosition
                        )
                    }
                }
            }
        })
    }

    fun setOnItemClickListener(
        onClick: (
            adapter: BaseBinderAdapter,
            holder: BaseViewHolder,
            position: Int
        ) -> Unit
    ) {
        onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(
                adapter: BaseBinderAdapter,
                holder: BaseViewHolder,
                position: Int
            ) {
                onClick(adapter, holder, position)
            }
        }
    }

}

interface OnItemClickListener {
    fun onItemClick(adapter: BaseBinderAdapter, holder: BaseViewHolder, position: Int)
}

interface OnItemLongClickListener {
    fun onItemClick(adapter: BaseBinderAdapter, holder: BaseViewHolder, position: Int): Boolean
}