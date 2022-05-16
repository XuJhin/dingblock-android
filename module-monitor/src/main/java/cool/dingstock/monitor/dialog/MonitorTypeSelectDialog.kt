package cool.dingstock.monitor.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import cool.dingstock.appbase.entity.bean.monitor.CateEntity
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.monitor.R
import cool.dingstock.monitor.databinding.MonitorTypeSelectDialogBinding
import cool.dingstock.uicommon.databinding.DialogFlexItemLayoutBinding

class MonitorTypeSelectDialog(context: Context) : Dialog(context) {
    val viewBinding: MonitorTypeSelectDialogBinding =
        MonitorTypeSelectDialogBinding.inflate(LayoutInflater.from(context))

    val list = arrayListOf<CateEntity>()
    var mOnConfirmClick: ((list: ArrayList<CateEntity>) -> Unit)? = null

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setWindowAnimations(R.style.DC_bottom_dialog_animation)
        setContentView(
            viewBinding.rootView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        viewBinding.rootView.setOnShakeClickListener {
            dismiss()
        }
        viewBinding.rootCard.setOnClickListener {

        }
        viewBinding.confirmButton.setOnShakeClickListener {
            list.forEachIndexed { index, entity ->
                entity.isSelect =
                    viewBinding.filterGroup.getChildAt(index).findViewById<View>(R.id.tv).isSelected
            }
            mOnConfirmClick?.invoke(list)
            dismiss()
        }
    }

    fun setFilter(list: ArrayList<CateEntity>) {
        viewBinding.filterGroup.removeAllViews()
        this.list.clear()
        this.list.addAll(list)
        list.forEach { entity ->
            val vb = DialogFlexItemLayoutBinding.inflate(LayoutInflater.from(context))
            vb.tv.text = entity.name
            vb.tv.isSelected = entity.isSelect
            vb.root.setOnShakeClickListener {
                vb.tv.isSelected = !vb.tv.isSelected
                if (vb.tv.isSelected) {
                    if (entity.isAll) {
                        //其他全不选中
                        unSelIsNotAllItem()
                    } else {
                        unSelIsAllItem()
                    }
                } else {
                    checkAllSelIsNot()
                }
            }
            viewBinding.filterGroup.addView(vb.root)
        }
    }

    fun unSelIsNotAllItem() {
        list.forEachIndexed { index, tideCompanyFilterEntity ->
            if (!tideCompanyFilterEntity.isAll) {
                viewBinding.filterGroup.getChildAt(index).findViewById<View>(R.id.tv).isSelected =
                    false
            }
        }
    }

    fun unSelIsAllItem() {
        kotlin.run out@{
            list.forEachIndexed { index, tideCompanyFilterEntity ->
                if (tideCompanyFilterEntity.isAll) {
                    viewBinding.filterGroup.getChildAt(index)
                        .findViewById<View>(R.id.tv).isSelected = false
                    return@out
                }
            }
        }
    }

    /**
     * 判断不是"全部"的都没有被选中，那么就选中全部
     * */
    fun checkAllSelIsNot() {
        var isNotAllisNotSel = true
        kotlin.run out@{
            list.forEachIndexed { index, tideCompanyFilterEntity ->
                if (!tideCompanyFilterEntity.isAll) {
                    if (viewBinding.filterGroup.getChildAt(index)
                            .findViewById<View>(R.id.tv).isSelected
                    ) {
                        isNotAllisNotSel = false
                        return@out
                    }
                }
            }
        }
        kotlin.run out@{
            list.forEachIndexed { index, tideCompanyFilterEntity ->
                if (tideCompanyFilterEntity.isAll) {
                    viewBinding.filterGroup.getChildAt(index)
                        .findViewById<View>(R.id.tv).isSelected = isNotAllisNotSel
                    return@out
                }
            }
        }
    }

    fun updateAllItem() {
        list.forEachIndexed { index, entity ->
            viewBinding.filterGroup.getChildAt(index).findViewById<View>(R.id.tv).isSelected =
                entity.isSelect
        }
    }

    fun setOnConfirmClick(onConfirmClick: (list: ArrayList<CateEntity>) -> Unit) {
        this.mOnConfirmClick = onConfirmClick
    }

    override fun show() {
        super.show()
        val attributes = window!!.attributes
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT
        window!!.attributes = attributes
        window!!.setGravity(Gravity.BOTTOM)
        updateAllItem()
    }
}