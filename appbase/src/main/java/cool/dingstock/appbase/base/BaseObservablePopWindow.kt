package cool.dingstock.appbase.base

import androidx.viewbinding.ViewBinding
import cool.dingstock.appbase.R
import cool.dingstock.appbase.helper.PopWindowDismissListener
import cool.dingstock.appbase.helper.PopWindowManager
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity

/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/24  10:23
 *
 *  可以被观测消失的 页面
 */
abstract class BaseObservablePopWindow<T : BaseViewModel, VB : ViewBinding> : VMBindingActivity<T, VB>() {

    var popWindowDismissListener: PopWindowDismissListener = PopWindowManager

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.dialog_window_scale_out)
        popWindowDismissListener.onWindowDismiss(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.dialog_window_scale_out)
        popWindowDismissListener.onWindowDismiss(this)
    }

    fun finishNoShowNext() {
        super.finish()
    }
}