package cool.dingstock.appbase.mvp.lazy

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.status.LoadingDialogStatus
import cool.dingstock.appbase.mvvm.status.ViewStatus

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/8  14:58
 */
abstract class CacheLzyFragment<Vm : BaseViewModel,VB:ViewBinding> : VmBindingLazyFragment<Vm,VB>() {
    /**
     * 是否成功加载缓存
     * */
    var loadCacheSuccess = false


    override fun onFragmentFirstVisible() {
        loadCache()
        onLazy()
    }

    abstract override fun initVariables(rootView: View?, container: ViewGroup?, savedInstanceState: Bundle?)

    abstract override fun initListeners()

    /**
     * 子类实现加载缓存
     * */
    abstract fun loadCache()

    abstract override fun onLazy()

    override fun initBaseViewModelObserver(){
        viewModel.statusViewLiveData.observe(viewLifecycleOwner, Observer { viewStatus ->
            when (viewStatus) {
                is ViewStatus.Loading -> {
                    //如果显示了缓存就不需要在显示StatusView 的加载
                    if(loadCacheSuccess){
                        return@Observer
                    }
                    mStatusView?.hideEmptyView()
                    mStatusView?.hideErrorView()
                    mStatusView?.showLoadingView()
                }
                is ViewStatus.Success -> {
                    mStatusView?.hideLoadingView()
                    mStatusView?.hideEmptyView()
                    mStatusView?.hideErrorView()
                }
                is ViewStatus.Empty -> {
                    mStatusView?.hideLoadingView()
                    mStatusView?.hideErrorView()
                    mStatusView?.showEmptyView(viewStatus.msg)
                }
                is ViewStatus.Error -> {
                    //如果显示了缓存就不需要在显示StatusView 的失败
                    if(loadCacheSuccess){
                        return@Observer
                    }
                    mStatusView?.hideEmptyView()
                    mStatusView?.hideLoadingView()
                    mStatusView?.showErrorView(viewStatus.msg)
                    mStatusView?.setOnErrorViewClick {
                        onStatusViewErrorClick()
                    }
                }
            }
        })
        viewModel.loadingDialogLiveData.observe(viewLifecycleOwner, Observer { dialogStatus ->
            when (dialogStatus) {
                is LoadingDialogStatus.Loading -> {
                    showLoadingDialog(dialogStatus.msg)
                }
                is LoadingDialogStatus.Success -> {
                    hideLoadingDialog()
                    showSuccessDialog(dialogStatus.msg)
                }
                is LoadingDialogStatus.Error -> {
                    hideLoadingDialog()
                    showFailedDialog(dialogStatus.msg)
                }
            }
        })

        routerObserver = Observer {
            DcRouter(it).start()
        }
        viewModel.routerLiveData.observe(viewLifecycleOwner, routerObserver)
    }

    override fun showErrorView() {
        if(loadCacheSuccess){
            return
        }
        super.showErrorView()
    }

    override fun showEmptyView(text: String?) {
        if(loadCacheSuccess){
            return
        }
        super.showEmptyView(text)
    }

    override fun showLoadingView() {
        if(loadCacheSuccess){
            return
        }
        super.showLoadingView()
    }



}