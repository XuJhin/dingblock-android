package cool.dingstock.tide.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import cool.dingstock.appbase.entity.bean.tide.TideDealInfoFilterEntity
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.tide.R
import cool.dingstock.tide.databinding.DialogFilterItemLayoutBinding
import cool.dingstock.tide.databinding.TideFilterDialogBinding

class TideDealInfoFilterDialog(context: Context) : Dialog(context) {
    val viewBinding: TideFilterDialogBinding =
        TideFilterDialogBinding.inflate(LayoutInflater.from(context))
    private val list = arrayListOf<TideDealInfoFilterEntity>()
    private var mOnConfirmClick: (() -> Unit)? = null

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
        viewBinding.titleTv.text = "交易信息"
        viewBinding.rootView.setOnShakeClickListener {
            dismiss()
        }
        viewBinding.rootCard.setOnClickListener {

        }
        viewBinding.confirmButton.setOnShakeClickListener {
            list.forEachIndexed { index, tideDealInfoFilterEntity ->
                tideDealInfoFilterEntity.isSelected =
                    viewBinding.filterGroup.getChildAt(index).findViewById<View>(R.id.tv).isSelected
            }
            mOnConfirmClick?.invoke()
            dismiss()
        }
    }

    fun setFilter(list: ArrayList<TideDealInfoFilterEntity>) {
        viewBinding.filterGroup.removeAllViews()
        this.list.clear()
        this.list.addAll(list)
        list.forEach { entity->
            val vb = DialogFilterItemLayoutBinding.inflate(LayoutInflater.from(context))
            vb.tv.text = entity.dealDesc
            vb.tv.isSelected = entity.isSelected
            vb.root.setOnShakeClickListener {
                vb.tv.isSelected = !vb.tv.isSelected
                if(vb.tv.isSelected){
                    if(entity.isAll){
                        //其他全不选中
                        unSelIsNotAllItem()
                    }else{
                        unSelIsAllItem()
                    }
                }else{
                    checkAllSelIsNot()
                }
            }
            viewBinding.filterGroup.addView(vb.root)
        }
    }

    private fun unSelIsNotAllItem(){
        list.forEachIndexed { index, tideDealInfoFilterEntity ->
            if(!tideDealInfoFilterEntity.isAll){
                viewBinding.filterGroup.getChildAt(index).findViewById<View>(R.id.tv).isSelected = false
            }
        }
    }

    private fun unSelIsAllItem(){
        kotlin.run out@{
            list.forEachIndexed { index, tideDealInfoFilterEntity ->
                if(tideDealInfoFilterEntity.isAll){
                    viewBinding.filterGroup.getChildAt(index).findViewById<View>(R.id.tv).isSelected = false
                    return@out
                }
            }
        }

    }

    /**
     * 判断不是"全部"的都没有被选中，那么就选中全部
     * */
    private fun checkAllSelIsNot(){
        var isNotAllisNotSel = true
        kotlin.run out@{
            list.forEachIndexed { index, tideDealInfoFilterEntity ->
                if(!tideDealInfoFilterEntity.isAll){
                    if( viewBinding.filterGroup.getChildAt(index).findViewById<View>(R.id.tv).isSelected){
                        isNotAllisNotSel = false
                        return@out
                    }
                }
            }
        }
        kotlin.run out@{
            list.forEachIndexed {index, tideDealInfoFilterEntity ->
                if(tideDealInfoFilterEntity.isAll){
                    viewBinding.filterGroup.getChildAt(index).findViewById<View>(R.id.tv).isSelected = isNotAllisNotSel
                    return@out
                }
            }
        }

    }

    private fun updateAllItem() {
        list.forEachIndexed { index, tideDealInfoFilterEntity ->
            viewBinding.filterGroup.getChildAt(index).findViewById<View>(R.id.tv).isSelected =
                tideDealInfoFilterEntity.isSelected
        }
    }

    fun setOnConfirmClick(onConfirmClick: () -> Unit) {
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