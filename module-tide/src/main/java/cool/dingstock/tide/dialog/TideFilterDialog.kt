package cool.dingstock.tide.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import cool.dingstock.appbase.entity.bean.tide.TideCompanyFilterEntity
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.tide.R
import cool.dingstock.tide.databinding.DialogFilterItemLayoutBinding
import cool.dingstock.tide.databinding.TideFilterDialogBinding


/**
 * 类名：TideFilterDialog
 * 包名：cool.dingstock.tide.dialog
 * 创建时间：2021/7/21 12:04 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class TideFilterDialog(context: Context) : Dialog(context) {
    val viewBinding: TideFilterDialogBinding =
        TideFilterDialogBinding.inflate(LayoutInflater.from(context))
    private val list = arrayListOf<TideCompanyFilterEntity>()
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
        viewBinding.rootView.setOnShakeClickListener {
            dismiss()
        }
        viewBinding.rootCard.setOnClickListener {

        }
        viewBinding.confirmButton.setOnShakeClickListener {
            list.forEachIndexed { index, tideCompanyFilterEntity ->
                tideCompanyFilterEntity.isSelected =
                    viewBinding.filterGroup.getChildAt(index).findViewById<View>(R.id.tv).isSelected
            }
            mOnConfirmClick?.invoke()
            dismiss()
        }
    }

    fun setFilterTitle(title: String) {
        viewBinding.titleTv.text = title
    }

    fun setFilter(list: ArrayList<TideCompanyFilterEntity>) {
        viewBinding.filterGroup.removeAllViews()
        this.list.clear()
        this.list.addAll(list)
        list.forEach { entity->
            val vb = DialogFilterItemLayoutBinding.inflate(LayoutInflater.from(context))
            vb.tv.text = entity.company
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
        list.forEachIndexed { index, tideCompanyFilterEntity ->
            if(!tideCompanyFilterEntity.isAll){
                viewBinding.filterGroup.getChildAt(index).findViewById<View>(R.id.tv).isSelected = false
            }
        }
    }

    private fun unSelIsAllItem(){
        kotlin.run out@{
            list.forEachIndexed { index, tideCompanyFilterEntity ->
                if(tideCompanyFilterEntity.isAll){
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
            list.forEachIndexed { index, tideCompanyFilterEntity ->
                if(!tideCompanyFilterEntity.isAll){
                    if( viewBinding.filterGroup.getChildAt(index).findViewById<View>(R.id.tv).isSelected){
                        isNotAllisNotSel = false
                        return@out
                    }
                }
            }
        }
        kotlin.run out@{
            list.forEachIndexed {index, tideCompanyFilterEntity ->
                if(tideCompanyFilterEntity.isAll){
                    viewBinding.filterGroup.getChildAt(index).findViewById<View>(R.id.tv).isSelected = isNotAllisNotSel
                    return@out
                }
            }
        }

    }

    private fun updateAllItem() {
        list.forEachIndexed { index, tideCompanyFilterEntity ->
            viewBinding.filterGroup.getChildAt(index).findViewById<View>(R.id.tv).isSelected =
                tideCompanyFilterEntity.isSelected
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