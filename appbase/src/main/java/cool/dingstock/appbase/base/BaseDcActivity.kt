package cool.dingstock.appbase.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import com.kongzue.dialogx.interfaces.BaseDialog
import cool.dingstock.appbase.R
import cool.dingstock.appbase.constant.AccountConstant
import cool.dingstock.appbase.entity.bean.account.DcLoginUser
import cool.dingstock.appbase.mvp.BaseActivity
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.updater.UpdaterService
import cool.dingstock.appbase.widget.stateview.StatusView
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.SizeUtils
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable


abstract class BaseDcActivity : BaseActivity() {

    private val mCompositeDisposable = CompositeDisposable()
    private val mDialogProgressList = arrayListOf<WaitDialog>()

    protected var mStatusView: StatusView? = null
    fun routeTo(link: String) {
        DcRouter(link).start()
    }

    /**
     * 增加方法,设置状态栏的高度
     */
    protected open fun resetStatusBarHeight() {
        val statusView = findViewById<View>(R.id.fake_status_bar) ?: return
        statusView.layoutParams.height = SizeUtils.getStatusBarHeight(this)
    }

    open fun changeStatusBar() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        Logger.d("base create")
        super.onCreate(savedInstanceState)
        // 分解 onCreate 使其更符合 单一职能原则
        changeStatusBar()
        if (getLayoutId() != 0) {
            setContentView(getLayoutId())
            // 获取 RootView
            initRootView()
            initStatusView()
            // 初始化变量
            initVariables(savedInstanceState)
            // 初始化监听器
            initListeners()
            //初始化presenter
            if (isCheckUpdate()) {
                startCheck()
            }
            resetStatusBarHeight()
        }
    }

    open fun showLoadingDialog(text: String?) {
        val tipDialog = WaitDialog
            .setMessage(text)
            .setCancelable(true)
        mDialogProgressList.add(tipDialog)
    }

    open fun showLoadingDialog(@StringRes resId: Int) {
        val loadingString = resources.getString(resId)
        showLoadingDialog(loadingString)
    }

    open fun showLoadingDialog() {
        showLoadingDialog(R.string.common_loading_tip)
    }

    open fun hideLoadingDialog() {
        for (tipDialog in mDialogProgressList) {
            if (tipDialog.isShow) {
                tipDialog.doDismiss()
            }
        }
        mDialogProgressList.clear()
//        WaitDialog.dismiss()
    }

    open fun showLoadingDialog(dialog: TipDialog) {
        mDialogProgressList.add(dialog)
    }


    open fun showSuccessDialog(text: String?) {
        TipDialog.show(text, WaitDialog.TYPE.SUCCESS)
            .setCancelable(true)

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
        TipDialog.show(text, WaitDialog.TYPE.ERROR)
            .setCancelable(false)
            .show()

    }

    open fun showWaringDialog(@StringRes resId: Int) {
        val waringString = resources.getString(resId)
        showWaringDialog(waringString)
    }

    open fun showWaringDialog(text: String?) {
        TipDialog.show(text, WaitDialog.TYPE.WARNING)
            .setCancelable(false)
    }

    private fun startCheck() {
        UpdaterService.startCheck(this)
    }

    protected open fun isCheckUpdate(): Boolean {
        return false
    }


    // 创建 loading Error statusView
    protected open fun initStatusView() {
        if (mStatusView == null) {
            mStatusView = StatusView.newBuilder()
                .with(this)
                .rootView(mRootView)
                .build()
        }
    }

    private fun isStatusViewNull(): Boolean {
        return null == mStatusView
    }

    open fun showLoadingView() {
        runOnUiThread {
            if (isStatusViewNull()) {
                initStatusView()
            }
            mStatusView!!.showLoadingView()
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
                mStatusView!!.hideEmptyView()
            }
        })
    }

    open fun showErrorView() {
        runOnUiThread {
            if (isStatusViewNull()) {
                initStatusView()
            }
            mStatusView!!.showErrorView()
        }
    }

    open fun showErrorView(text: String?) {
        runOnUiThread {
            if (isStatusViewNull()) {
                initStatusView()
            }
            mStatusView!!.showErrorView(text)
        }
    }

    open fun hideErrorView() {
        runOnUiThread(object : Runnable {
            override fun run() {
                if (isStatusViewNull()) {
                    Logger.w(this.javaClass.simpleName + " ,The status view is empty.")
                    return
                }
                mStatusView!!.hideErrorView()
            }
        })
    }

    open fun setOnErrorViewClick(onClickListener: View.OnClickListener) {
        if (isStatusViewNull()) {
            Logger.w(this.javaClass.simpleName + " ,The status view is empty.")
            return
        }
        mStatusView!!.setOnErrorViewClick(onClickListener)
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
        if (!isStatusViewNull()) {
            mStatusView!!.release()
        }
        mCompositeDisposable.clear()
        hideLoadingDialog()
        super.onDestroy()
    }

    fun getAccount(): DcLoginUser? {
        val user = AccountHelper.getInstance().user
        if (null == user) {
            DcRouter(AccountConstant.Uri.INDEX)
                .start()
            return null
        }
        return user
    }

    override fun onResume() {
        super.onResume()
        mStatusView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mStatusView?.onPause()
    }

    protected open fun addDisposable(disposable: Disposable?) {
        mCompositeDisposable.add(disposable!!)
    }

//    override fun finish() {
//        super.finish()
//        overridePendingTransition(R.anim.on_activity_close_enter, R.anim.on_activity_close_exit)
//    }
//
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        overridePendingTransition(R.anim.on_activity_close_enter, R.anim.on_activity_close_exit)
//    }

}