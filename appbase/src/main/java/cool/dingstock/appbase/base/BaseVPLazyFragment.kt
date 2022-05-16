package cool.dingstock.appbase.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import cool.dingstock.appbase.mvp.BaseFragment
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.status.LoadingDialogStatus
import cool.dingstock.appbase.mvvm.status.ViewStatus
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.widget.stateview.StatusView
import cool.dingstock.lib_base.util.ToastUtil
import java.lang.reflect.ParameterizedType


/**
 * AndroidX 下的懒加载fragment.由于SetMax的存在 可以直接在resume中作网络请求
 */
open class BaseVPLazyFragment<VM : BaseViewModel, VB : ViewBinding> : BaseFragment() {

    protected var mStatusView: StatusView? = null

    var routerObserver: Observer<String> = Observer {
        DcUriRequest(requireContext(), it).start()
    }

    var rootView: View? = null
    lateinit var viewModel: VM
    lateinit var viewBinding: VB

    companion object {
        const val TAG = "BaseVpLazyFragment"
    }

    private var hasLoad = false
    private var loadAtFirst = true
    protected fun resetState() {
        hasLoad = false
        loadAtFirst = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val type = (javaClass.genericSuperclass as? ParameterizedType)?.actualTypeArguments
        val type1 = type?.get(0) as Class<VM>
        viewModel = ViewModelProvider(this)[type1]
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (rootView == null) {
            val arr = (javaClass.genericSuperclass as? ParameterizedType)?.actualTypeArguments
            val cls = arr?.get(1) as? Class<VB>
            val inflate = cls?.getDeclaredMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.java
            )
            viewBinding = inflate?.invoke(null, inflater, container, false) as VB
            rootView = viewBinding.root
            resetState()
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initStatusView(rootView as ViewGroup)
        initObserver()
    }

    private fun initObserver() {
        viewModel.statusViewLiveData.observe(viewLifecycleOwner, Observer { viewStatus ->
            when (viewStatus) {
                is ViewStatus.Loading -> {
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
                    mStatusView?.hideEmptyView()
                    mStatusView?.hideLoadingView()
                    mStatusView?.showErrorView(viewStatus.msg)
                }
            }
        })
        viewModel.loadingDialogLiveData.observe(viewLifecycleOwner, Observer { dialogStatus ->
            when (dialogStatus) {
                is LoadingDialogStatus.Loading -> {
                    showLoadingDialog(dialogStatus.msg)
                }
                is LoadingDialogStatus.Hide -> {
                    hideLoadingDialog()
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
        viewModel.toastLiveData.observe(viewLifecycleOwner, Observer {
            ToastUtil.getInstance()._short(requireContext(), it)
        })

        viewModel.routerLiveData.observe(viewLifecycleOwner, routerObserver)


    }

    fun initStatusView(view: ViewGroup) {
        if (mStatusView == null) {
            mStatusView = StatusView.newBuilder()
                .with(requireContext())
                .rootView(view)
                .fullscreen(true)
                .build()
            mStatusView?.showLoadingView()
        }
    }

    open fun showLoadingDialog(msg: String? = "加载中...") {
        TipDialog.show(msg)
            .setCancelable(true)
    }

    open fun hideLoadingDialog() {
        WaitDialog.dismiss()
    }

    open fun showSuccessDialog(text: String?) {
        TipDialog.show(text, WaitDialog.TYPE.SUCCESS)
            .setCancelable(true)
    }

    open fun showSuccessDialog(@StringRes resId: Int) {
        TipDialog.show(resId, WaitDialog.TYPE.SUCCESS)
            .setCancelable(true)
    }

    open fun showFailedDialog(@StringRes resId: Int) {
        TipDialog.show(resId, WaitDialog.TYPE.ERROR)
            .setCancelable(true)
    }


    open fun showFailedDialog(text: String?) {
        TipDialog.show(text, WaitDialog.TYPE.ERROR)
            .setCancelable(true)
    }

    open fun showWaringDialog(@StringRes resId: Int) {
        TipDialog.show(resId, WaitDialog.TYPE.WARNING)
            .setCancelable(true)
    }

    open fun showWaringDialog(text: String?) {
        TipDialog.show(text, WaitDialog.TYPE.WARNING)
            .setCancelable(true)
    }


    override fun onResume() {
        super.onResume()
        if (loadAtFirst) {
            if (hasLoad) {
                return
            } else {
                lazyInit()
                hasLoad = true
                return
            }
        }
        lazyInit()
        hasLoad = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (rootView?.parent as? ViewGroup)?.removeView(rootView)
    }

    override fun onDestroy() {
        hasLoad = false
        loadAtFirst = true
        super.onDestroy()
        routerObserver.let {
            viewModel.routerLiveData.let {
                viewModel.routerLiveData.removeObserver(routerObserver)
            }
        }
    }


    protected open fun lazyInit() {

    }
}