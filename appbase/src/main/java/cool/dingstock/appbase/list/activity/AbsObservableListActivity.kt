package cool.dingstock.appbase.list.activity

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import cool.dingstock.appbase.R
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.helper.PopWindowDismissListener
import cool.dingstock.appbase.helper.PopWindowManager
import cool.dingstock.appbase.list.AbsListViewModel
import cool.dingstock.appbase.mvvm.activity.VMActivity
import cool.dingstock.appbase.mvvm.status.PageLoadState


/**
 * 列表Activity的简单封装
 *已处理好加载更多等逻辑
 *
 * 可以被观测消失的 页面
 *
 */
abstract class AbsObservableListActivity<VM : AbsListViewModel,VB:ViewBinding> : AbsListActivity<VM,VB>() {

    var popWindowDismissListener : PopWindowDismissListener = PopWindowManager

    override fun finish() {
        super.finish()
        popWindowDismissListener.onWindowDismiss(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.dialog_window_scale_out)
        popWindowDismissListener.onWindowDismiss(this)
    }


}