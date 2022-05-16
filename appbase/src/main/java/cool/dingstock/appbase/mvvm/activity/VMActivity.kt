package cool.dingstock.appbase.mvvm.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cool.dingstock.appbase.base.BaseDcActivity
import cool.dingstock.appbase.ext.azDp
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.mvvm.status.LoadingDialogStatus
import cool.dingstock.appbase.mvvm.status.ViewStatus
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.lib_base.util.SizeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import java.lang.reflect.ParameterizedType

abstract class VMActivity<VM : BaseViewModel> : BaseDcActivity() {
    lateinit var viewModel: VM
    lateinit var routerObserver: Observer<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        generateViewModel()?.let {
            viewModel = it
            lifecycle.addObserver(viewModel)
        }

        initBundleData()
        super.onCreate(savedInstanceState)
        initFakeStatusBar()
        initBaseViewModelObserver()
    }

    override fun initStatusView() {
        super.initStatusView()
        mStatusView?.setOnErrorViewClick {
            onStatusViewErrorClick()
        }
    }

    protected open fun initFakeStatusBar() {
        val fakeStatusBar = fakeStatusView()
        fakeStatusBar?.let {
            val layoutParams = fakeStatusBar.layoutParams
            layoutParams.height = getStatusHeight()
            fakeStatusBar.layoutParams = layoutParams
        }

        bottomBarView()?.apply {
            val lp = layoutParams
            lp.height = SizeUtils.getNavigationBarHeight().coerceAtLeast(34.dp.toInt())
            layoutParams = lp
        }
    }

    protected fun getStatusHeight(): Int {
        val height = SizeUtils.getStatusBarHeight(this)
        return if (height == 0) 25.azDp.toInt() else height
    }

    protected open fun initBaseViewModelObserver() {
        viewModel.statusViewLiveData.observe(this, Observer { viewStatus ->
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
                    mStatusView?.setOnErrorViewClick {
                        onStatusViewErrorClick()
                    }
                }
            }
        })
        viewModel.loadingDialogLiveData.observe(this, Observer { dialogStatus ->
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
        routerObserver = Observer {
            DcRouter(it).start()
        }
        viewModel.routerLiveData.observe(this, routerObserver)
        viewModel.toastLiveData.observe(this) {
            ToastUtil.getInstance()._short(this, it)
        }
    }

    final override fun initVariables(savedInstanceState: Bundle?) {
        initViewAndEvent(savedInstanceState)
    }

    abstract override fun getLayoutId(): Int

    protected open fun fakeStatusView(): View? {
        return null
    }

    protected open fun bottomBarView(): View? {
        return null
    }


    abstract fun initViewAndEvent(savedInstanceState: Bundle?)

    abstract override fun initListeners()

    /**
     * 获取viewModel
     */
    open fun generateViewModel(): VM? {
        val type = (javaClass.genericSuperclass as? ParameterizedType)?.actualTypeArguments
        val type1 = type?.get(0) as? Class<VM>
        type1?.let {
            return ViewModelProvider(this)[it]
        }
        return null
    }

    open fun initBundleData() {}

    open fun onStatusViewErrorClick() {

    }

    /**
     * 带点击重试的errorView
     */
    fun showErrorView(errorMessage: String, action: () -> Unit?) {
        runOnUiThread {
            if (mStatusView == null) {
                initStatusView()
            }
            mStatusView?.hideLoadingView()
            mStatusView?.showErrorView("$errorMessage,点击重试")
            mStatusView?.setOnErrorViewClick {
                showLoadingView()
                action()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        routerObserver.let {
            viewModel.routerLiveData.let {
                viewModel.routerLiveData.removeObserver(routerObserver)
            }
        }
    }

}