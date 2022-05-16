package cool.dingstock.appbase.mvvm

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.StringRes
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import cool.dingstock.appbase.R
import cool.dingstock.appbase.constant.AccountConstant
import cool.dingstock.appbase.entity.bean.account.DcLoginUser
import cool.dingstock.appbase.mvp.BaseActivity
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.updater.UpdaterService
import cool.dingstock.appbase.widget.stateview.StatusView
import cool.dingstock.lib_base.util.Logger

abstract class StateActivity : BaseActivity() {
    protected var mStatusView: StatusView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 分解 onCreate 使其更符合 单一职能原则
        setContentView(getLayoutId())
        // 获取 RootView
        initRootView()
        // 初始化变量
        initVariables(savedInstanceState)
        // 初始化监听器
        initListeners()
        //初始化presenter
        if (isCheckUpdate()) {
            startCheck()
        }
    }

    open fun showLoadingDialog(text: String?) {
        TipDialog.show(text)
            .setCancelable(false)
    }

    open fun showLoadingDialog(@StringRes resId: Int) {
        val loadingString = resources.getString(resId)
        showLoadingDialog(loadingString)
    }

    open fun showLoadingDialog() {
        showLoadingDialog(R.string.common_loading_tip)
    }

    open fun hideLoadingDialog() {
        WaitDialog.dismiss()
    }

    open fun showSuccessDialog(text: String?) {
        TipDialog.show(text,WaitDialog.TYPE.SUCCESS)
            .setCancelable(true)
            .show()
    }

    open fun showSuccessDialog(@StringRes resId: Int) {
        val successString = resources.getString(resId)
        showSuccessDialog(successString)
    }

    open fun showFailedDialog(@StringRes resId: Int) {
        val failedString = resources.getString(resId)
        showFailedDialog(failedString)
    }

    open fun showFailedDialog(text: String?) {
        TipDialog.show(text,WaitDialog.TYPE.ERROR)
            .setCancelable(true)
            .show()
    }

    open fun showWaringDialog(@StringRes resId: Int) {
        val waringString = resources.getString(resId)
        showWaringDialog(waringString)
    }

    open fun showWaringDialog(text: String?) {
        TipDialog.show(text, WaitDialog.TYPE.WARNING).isCancelable = true

    }

    open fun startCheck() {
        UpdaterService.startCheck(this)
    }

    protected open fun isCheckUpdate(): Boolean {
        return false
    }


    // 创建 loading Error statusView
    protected open fun initStatusView() {
        mStatusView = StatusView.newBuilder()
            .with(this)
            .rootView(mRootView)
            .build()
    }

    open fun isStatusViewNull(): Boolean {
        return null == mStatusView
    }

    open fun showLoadingView() {
        runOnUiThread {
            if (isStatusViewNull()) {
                initStatusView()
            }
            mStatusView?.showLoadingView()
        }
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

    open fun showEmptyView() {
        runOnUiThread {
            if (isStatusViewNull()) {
                initStatusView()
            }
            mStatusView?.showEmptyView()
        }
    }

    open fun showEmptyView(text: String) {
        runOnUiThread {
            if (isStatusViewNull()) {
                initStatusView()
            }
            mStatusView?.showEmptyView(text)
        }
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

    open fun showErrorView() {
        runOnUiThread {
            if (isStatusViewNull()) {
                initStatusView()
            }
            mStatusView?.showErrorView()
        }
    }

    open fun showErrorView(text: String?) {
        runOnUiThread {
            if (isStatusViewNull()) {
                initStatusView()
            }
            mStatusView?.showErrorView(text)
        }
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

    protected abstract fun getLayoutId(): Int


    protected abstract fun initVariables(savedInstanceState: Bundle?)

    protected abstract fun initListeners()

    override fun initRootView() {
        // 获得 ContentView mRootView
        mContentView = findViewById(Window.ID_ANDROID_CONTENT)
        mRootView = mContentView.getChildAt(0) as ViewGroup
        initStatusView()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (!isStatusViewNull()) {
            mStatusView?.release()
        }
        hideLoadingDialog()
    }


    open fun getDcAccount(): DcLoginUser? {
        val user = AccountHelper.getInstance().user
        if (null == user) {
            DcRouter(AccountConstant.Uri.INDEX)
                .start()
            return null
        }
        return user
    }

}

