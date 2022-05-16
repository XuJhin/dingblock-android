package cool.dingstock.appbase.mvp.lazy

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.status.LoadingDialogStatus
import cool.dingstock.appbase.mvvm.status.ViewStatus
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.SizeUtils
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import java.lang.reflect.ParameterizedType


/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/8  16:49
 */
abstract class VmLazyFragment<VM : BaseViewModel> : LazyDcFragment<DCLazyFragmentPresenter<*>>() {
    lateinit var routerObserver: Observer<String>
    private var mCompositeDisposable = CompositeDisposable()
    lateinit var viewModel: VM
    protected var isCreated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        generateViewModel()?.let {
            viewModel = it
            lifecycle.addObserver(viewModel)
            Logger.e("onCreate", "viewModel:$viewModel")
        }
        isCreated = true
    }


    override fun initPresenter(): DCLazyFragmentPresenter<*>? {
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFakeStatusBar()
        initStatusView()
        initBaseViewModelObserver()
    }

    protected open fun initBaseViewModelObserver() {
        viewModel.statusViewLiveData.observe(viewLifecycleOwner) { viewStatus ->
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
        }
        viewModel.loadingDialogLiveData.observe(viewLifecycleOwner, { dialogStatus ->
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
        viewModel.toastLiveData.observe(viewLifecycleOwner) {
            context?.let { ctx ->
                ToastUtil.getInstance()._short(ctx, it)
            }
        }

        routerObserver = Observer {
            DcRouter(it).start()
        }
        viewModel.routerLiveData.observe(viewLifecycleOwner, routerObserver)

    }


    override fun onFragmentFirstVisible() {
        super.onFragmentFirstVisible()
        onLazy()
    }

    protected open fun addDisposable(disposable: Disposable?) {
        mCompositeDisposable.add(disposable!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
        routerObserver.let {
            viewModel.routerLiveData.let {
                viewModel.routerLiveData.removeObserver(routerObserver)
            }
        }
    }

    /**
     * 获取viewModel ， 默认通过反射获取，也可以通过子类重写
     */
    open fun generateViewModel(): VM? {
        val type = (javaClass.genericSuperclass as? ParameterizedType)?.actualTypeArguments
        val type1 = type?.get(0) as? Class<VM>
        type1?.let {
            return ViewModelProvider(this)[it]
        }
        return null
    }

    private fun initFakeStatusBar() {
        val fakeStatusBar = fakeStatusView()
        fakeStatusBar?.let {
            val layoutParams = fakeStatusBar.layoutParams
            layoutParams.height = SizeUtils.getStatusBarHeight(requireContext())
            fakeStatusBar.layoutParams = layoutParams
        }
    }

    abstract override fun getLayoutId(): Int

    protected open fun fakeStatusView(): View? {
        return null
    }

    abstract override fun initVariables(
        rootView: View?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )

    abstract override fun initListeners()

    /**
     * fragment 第一次可见 的懒加载
     *
     * */
    abstract fun onLazy()

    /**
     * 当errorView被点击
     * */
    open fun onStatusViewErrorClick() {

    }

}
