package cool.dingstock.appbase.lazy

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import com.kongzue.dialogx.interfaces.DialogLifecycleCallback
import cool.dingstock.appbase.R
import cool.dingstock.appbase.constant.AccountConstant
import cool.dingstock.appbase.entity.bean.account.DcLoginUser
import cool.dingstock.appbase.mvp.BaseFragment
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.toast.TopToast
import cool.dingstock.appbase.widget.dialog.RKAlertDialog
import cool.dingstock.appbase.widget.stateview.StatusView
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.ScreenUtils


abstract class LazyFragment : BaseFragment() {

    var isLoaded = false


    override fun onResume() {
        super.onResume()
        if (!isLoaded && !isHidden) {
            lazyInit()
            isLoaded = true
        }
    }

    override fun onDestroyView() {
        Log.e("LazyFragment", "onDestroyView")
        super.onDestroyView()
        rootView?.let { (it.parent as? ViewGroup)?.removeView(it) }
        isLoaded = false
    }

    abstract fun lazyInit()
    protected var rootView: View? = null

    protected var mStatusView: StatusView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e("LazyFragment", "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        initVariables(view, null, savedInstanceState)
        // 初始化监听
        initListeners()
        // 初始化P层
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e("LazyFragment", "onViewCreated")
        Logger.d(this.javaClass.simpleName)
        if (rootView == null) {
            // 初始化布局
            rootView = inflater.inflate(getLayoutId(), container, false)
        }
        return rootView
    }


    protected open fun getStatusBarHeight(): Int {
        return ScreenUtils.getStatusHeight(requireContext())
    }

    /**
     * 增加方法,设置状态栏的高度
     */
    protected open fun resetStatusBarHeight() {
        if (rootView != null) {
            val statusView = rootView!!.findViewById<View>(R.id.fake_status_bar) ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                statusView.layoutParams.height = getStatusBarHeight()
            } else {
                statusView.layoutParams.height = 0
            }
        }
    }

    open fun makeAlertDialog(): AlertDialog.Builder? {
        return RKAlertDialog.make(activity)
    }

    open fun showLoadingDialog(text: String?) {
        WaitDialog.show(text).setCancelable(true).show()
    }

    open fun showLoadingDialog(@StringRes resId: Int) {
        WaitDialog.show(resId).setCancelable(true).show()
    }

    open fun showLoadingDialog() {
        WaitDialog.show(R.string.common_loading_tip).setCancelable(true).show()
    }

    open fun hideLoadingDialog() {
        WaitDialog.dismiss()
    }

    open fun showSuccessDialog(text: String?) {
        TipDialog.show(text, WaitDialog.TYPE.SUCCESS)
            .setCancelable(true)
            .dialogLifecycleCallback = object : DialogLifecycleCallback<WaitDialog?>() {
            override fun onDismiss(dialog: WaitDialog?) {}
        }
    }

    open fun showSuccessDialog(@StringRes resId: Int) {
        TipDialog.show(getString(resId), WaitDialog.TYPE.SUCCESS)
            .setCancelable(true)
            .dialogLifecycleCallback = object : DialogLifecycleCallback<WaitDialog?>() {
            override fun onDismiss(dialog: WaitDialog?) {}
        }
    }

    open fun showFailedDialog(@StringRes resId: Int) {
        TipDialog.show(getString(resId), WaitDialog.TYPE.ERROR)
            .setCancelable(true)
            .dialogLifecycleCallback = object : DialogLifecycleCallback<WaitDialog?>() {
            override fun onDismiss(dialog: WaitDialog?) {}
        }
    }


    open fun showFailedDialog(text: String?) {
        TipDialog.show(text, WaitDialog.TYPE.ERROR)
            .setCancelable(true)
            .dialogLifecycleCallback = object : DialogLifecycleCallback<WaitDialog?>() {
            override fun onDismiss(dialog: WaitDialog?) {}
        }
    }

    open fun showWaringDialog(@StringRes resId: Int) {
        TipDialog.show(resId, WaitDialog.TYPE.WARNING)
            .setCancelable(true)
            .dialogLifecycleCallback = object : DialogLifecycleCallback<WaitDialog?>() {
            override fun onDismiss(dialog: WaitDialog?) {}
        }
    }

    open fun showWaringDialog(text: String?) {
        TipDialog.show(text, WaitDialog.TYPE.WARNING)
            .setCancelable(true)
            .dialogLifecycleCallback = object : DialogLifecycleCallback<WaitDialog?>() {
            override fun onDismiss(dialog: WaitDialog?) {}
        }
    }

    // 创建 loading、Error statusView
    override fun initStatusView() {
        Logger.w(this.javaClass.simpleName + " ,The status view is initializing.")
        mStatusView = StatusView.newBuilder()
            .with(requireContext())
            .rootView(rootView as ViewGroup?)
            .build()
    }

    open fun isStatusViewNull(): Boolean {
        return null == mStatusView
    }

    open fun showEmptyView() {
        runOnUiThread(Runnable {
            if (isStatusViewNull()) {
                initStatusView()
            }
            mStatusView?.showEmptyView()
        })
    }

    open fun showEmptyView(text: String) {
        runOnUiThread(Runnable {
            if (isStatusViewNull()) {
                initStatusView()
            }
            mStatusView?.showEmptyView(text)
        })
    }

    open fun hideEmptyView() {
        runOnUiThread(object : Runnable {
            override fun run() {
                if (isStatusViewNull()) {
                    Logger.w(this.javaClass.simpleName + " ,The status view is empty.")
                    return
                }
                mStatusView?.hideEmptyView()
            }
        })
    }

    open fun showLoadingView() {
        runOnUiThread(Runnable {
            if (isStatusViewNull()) {
                initStatusView()
            }
            mStatusView?.showLoadingView()
        })
    }


    open fun hideLoadingView() {
        runOnUiThread(object : Runnable {
            override fun run() {
                if (isStatusViewNull()) {
                    Logger.w(this.javaClass.simpleName + " ,The status view is empty.")
                    return
                }
                mStatusView?.hideLoadingView()
            }
        })
    }

    open fun showErrorView() {
        runOnUiThread(Runnable {
            if (isStatusViewNull()) {
                initStatusView()
            }
            mStatusView?.showErrorView()
        })
    }

    open fun showErrorView(text: String?) {
        runOnUiThread(Runnable {
            if (isStatusViewNull()) {
                initStatusView()
            }
            mStatusView!!.showErrorView(text)
        })
    }

    override fun runOnUiThread(action: Runnable?) {
        val activity = activity
        activity?.runOnUiThread(action)
    }

    open fun hideErrorView() {
        runOnUiThread(object : Runnable {
            override fun run() {
                if (isStatusViewNull()) {
                    Logger.w(this.javaClass.simpleName + " ,The status view is empty.")
                    return
                }
                mStatusView?.hideErrorView()
            }
        })
    }

    open fun setOnErrorViewClick(onClickListener: View.OnClickListener) {
        if (isStatusViewNull()) {
            Logger.w(this.javaClass.simpleName + " ,The status view is empty.")
            return
        }
        mStatusView?.setOnErrorViewClick(onClickListener)
    }

    open fun setOnErrorViewClick(listener: (view: View) -> Unit) {
        if (isStatusViewNull()) {
            Logger.w(this.javaClass.simpleName + " ,The status view is empty.")
            return
        }
        mStatusView?.setOnErrorViewClick {
            listener(it)
        }
    }


    // 布局资源ID
    protected abstract fun getLayoutId(): Int


    // 初始化变量 子类需要就重写
    protected abstract fun initVariables(rootView: View?, container: ViewGroup?, savedInstanceState: Bundle?)

    // 初始化监听
    protected abstract fun initListeners()

    override fun onDestroy() {
        //Logger.d(this.getClass().getSimpleName());
        if (!isStatusViewNull()) {
            mStatusView!!.release()
            mStatusView = null
        }
        rootView = null
        super.onDestroy()
    }

    open fun showToastShort(@StringRes resId: Int) {
        showToastShort(getString(resId))
    }

    override fun showToastShort(text: CharSequence) {
        TopToast.INSTANCE.showToast(context, text.toString(), Toast.LENGTH_SHORT)
    }

    open fun getRootView(): ViewGroup? {
        return rootView as ViewGroup?
    }

    override fun getIntent(): Intent? {
        Logger.d(this.javaClass.simpleName)
        return requireActivity().intent
    }

    override fun getUri(): Uri? {
        Logger.d(this.javaClass.simpleName)
        if (null == activity || null == activity?.intent) {
            Logger.w(this.javaClass.simpleName + " ,The activity or intent is empty.")
            return null
        }
        return requireActivity().intent.data
    }

    override fun getUriSite(): String? {
        Logger.d(this.javaClass.simpleName)
        if (null == uri) {
            Logger.w(this.javaClass.simpleName + " ,The uri is empty.")
            return ""
        }
        val uriSite = uri!!.scheme + "://" + uri!!.host + uri!!.path
        Logger.d(this.javaClass.simpleName + " ,UriSite: " + uriSite)
        return uriSite
    }

    override fun getCompatColor(@ColorRes resId: Int): Int {
        return ContextCompat.getColor(requireActivity(), resId)
    }

    override fun getCompatDrawable(@DrawableRes id: Int): Drawable? {
        return ContextCompat.getDrawable(requireActivity(), id)
    }

    open fun Router(uri: String): DcUriRequest {
        Logger.i(this.javaClass.simpleName + " ,The Router : " + uri)
        return DcUriRequest(requireActivity(), uri)
    }

    open fun getUser(): DcLoginUser? {
        if (null == AccountHelper.getInstance().user) {
            Router(AccountConstant.Uri.INDEX)
                .start()
            return null
        }
        return AccountHelper.getInstance().user
    }
}