package cool.dingstock.uicommon.product.dialog

import android.view.View
import androidx.fragment.app.BaseBottomFullViewBindingFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseBinderAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.adapter.itembinder.OnItemClickListener
import cool.dingstock.appbase.entity.bean.calendar.SmsParameterOptionsEntity
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.calendar.item.SmsParameterOptionsItemBinder
import cool.dingstock.uicommon.databinding.SelectorParameterDialogLayoutBinding


/**
 * 类名：SelectParameterDialog
 * 包名：cool.dingstock.calendar.sms.dialog
 * 创建时间：2021/7/6 10:25 上午
 * 创建人： WhenYoung
 * 描述：
 **/
class SelectParameterDialog :
    BaseBottomFullViewBindingFragment<SelectorParameterDialogLayoutBinding>() {

    val mAdapter by lazy { DcBaseBinderAdapter(arrayListOf()) }
    val itemBinder by lazy { SmsParameterOptionsItemBinder() }

    var title: String = ""
    var options = arrayListOf<SmsParameterOptionsEntity<String>>()
    var selPosition = -1
    private var onSelConfirm: ((selStr: String) -> Unit)? = null


//    override fun getParentLayer(): View = viewBinding.parentLayer
//
//    override fun getContentLayer(): View = viewBinding.contentLayer
//
    override fun initDataEvent() {
        mAdapter.addItemBinder(itemBinder)
        viewBinding.rv.adapter = mAdapter
        viewBinding.rv.layoutManager = GridLayoutManager(context, 4)

        viewBinding.title.text = title
        viewBinding.continueTv.isEnabled = selPosition >= 0

        itemBinder.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(
                adapter: BaseBinderAdapter,
                holder: BaseViewHolder,
                position: Int
            ) {
                options.forEach {
                    it.selected = false
                }
                selPosition = position
                options[position].selected = true
                viewBinding.continueTv.isEnabled = selPosition >= 0
                mAdapter.notifyDataSetChanged()
            }
        }
        viewBinding.continueTv.setOnShakeClickListener {
            if (selPosition != -1) {
                options.get(selPosition).let {
                    onSelConfirm?.invoke(it.data)
                }
                dismiss()
            } else {
                dismiss()
            }
        }
        viewBinding.closeIv.setOnShakeClickListener {
            dismiss()
        }

    }


    fun setData(title: String, options: ArrayList<String>, selStr: String) {
        this.title = title
        this.options.clear()
        selPosition = -1
        options.forEachIndexed { i, it ->
            val entity = SmsParameterOptionsEntity(it)
            entity.selected = it == selStr
            if(it == selStr){
                selPosition = i
            }
            this.options.add(entity)
        }
        mAdapter.setList(this.options)
    }

    fun setOnSelConfirm(onSelConfirm: (selStr: String) -> Unit) {
        this.onSelConfirm = onSelConfirm
    }


}