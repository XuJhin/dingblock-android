package cool.dingstock.appbase.base

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import cool.dingstock.appbase.R
import cool.dingstock.appbase.constant.AccountConstant
import cool.dingstock.appbase.entity.bean.account.DcLoginUser
import cool.dingstock.appbase.mvp.BaseFragment
import cool.dingstock.appbase.mvp.lazy.LazyDcFragment
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.toast.TopToast
import cool.dingstock.appbase.widget.dialog.RKAlertDialog
import cool.dingstock.appbase.widget.stateview.StatusView
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.ScreenUtils

abstract class BaseLazyFragment : BaseFragment() {
    protected var rootView: View? = null
    protected var mStatusView: StatusView? = null
    private var mIsFirstVisible = true
    private var isViewCreated = false
    private var currentVisibleState = false

    fun getPageVisible(): Boolean {
        return currentVisibleState
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isViewCreated = true
        // !isHidden() 默认为 true  在调用 hide show 的时候可以使用
        if (!isHidden && userVisibleHint) {
            dispatchUserVisibleHint(true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVariables(view, null, savedInstanceState)
        // 初始化监听
        initListeners()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            dispatchUserVisibleHint(false)
        } else {
            dispatchUserVisibleHint(true)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Logger.d(this.javaClass.simpleName)
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutId(), container, false)
        }
        return rootView
    }

    protected fun getStatusBarHeight(): Int {
        return ScreenUtils.getStatusHeight(requireContext())
    }

    /**
     * 增加方法,设置状态栏的高度
     */
    protected fun resetStatusBarHeight() {
        if (rootView != null) {
            val statusView = rootView!!.findViewById<View>(R.id.fake_status_bar) ?: return
            statusView.layoutParams.height = getStatusBarHeight()
        }
    }

    fun makeAlertDialog(): AlertDialog.Builder? {
        return RKAlertDialog.make(activity)
    }

    fun showLoadingDialog(text: String?) {
        WaitDialog.show(text)
            .setCancelable(true)
    }

    fun showLoadingDialog(@StringRes resId: Int) {
        WaitDialog.show(resId)
            .setCancelable(true)
    }

    fun showLoadingDialog() {
        WaitDialog.show(R.string.common_loading_tip)
            .setCancelable(true)
    }

    fun hideLoadingDialog() {
        WaitDialog.dismiss()
    }

    fun showSuccessDialog(text: String?) {
        TipDialog.show(text,WaitDialog.TYPE.SUCCESS)
            .setCancelable(true)
    }

    fun showSuccessDialog(@StringRes resId: Int) {
        TipDialog.show(resId,WaitDialog.TYPE.SUCCESS)
            .setCancelable(true)
    }

    fun showFailedDialog(@StringRes resId: Int) {
        TipDialog.show(resId,WaitDialog.TYPE.ERROR)
            .setCancelable(true)
    }


    fun showFailedDialog(text: String?) {
        TipDialog.show(text,WaitDialog.TYPE.ERROR)
            .setCancelable(true)
    }

    fun showWaringDialog(@StringRes resId: Int) {
        TipDialog.show(resId,WaitDialog.TYPE.WARNING)
            .setCancelable(true)
    }

    fun showWaringDialog(text: String?) {
        TipDialog.show(text,WaitDialog.TYPE.WARNING)
            .setCancelable(true)
    }

    // 创建 loading、Error statusView
    override fun initStatusView() {
        Logger.w(this.javaClass.simpleName + " ,The status view is initializing.")
        mStatusView = StatusView.newBuilder()
            .with(requireContext())
            .rootView(rootView as ViewGroup?)
            .build()
    }

    private fun isStatusViewNull(): Boolean {
        return null == mStatusView
    }

    fun showEmptyView() {
        runOnUiThread(Runnable {
            if (isStatusViewNull()) {
                initStatusView()
            }
            mStatusView?.showEmptyView()
        })
    }

    fun showEmptyView(text: String) {
        runOnUiThread(Runnable {
            if (isStatusViewNull()) {
                initStatusView()
            }
            mStatusView?.showEmptyView(text)
        })
    }

    fun hideEmptyView() {
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

    fun showLoadingView() {
        runOnUiThread(Runnable {
            if (isStatusViewNull()) {
                initStatusView()
            }
            mStatusView?.showLoadingView()
        })
    }


    fun hideLoadingView() {
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

    fun showErrorView() {
        val activity = activity ?: return
        runOnUiThread(Runnable {
            if (isStatusViewNull()) {
                initStatusView()
            }
            mStatusView?.showErrorView()
        })
    }

    fun showErrorView(text: String?) {
        val activity = activity ?: return
        runOnUiThread(Runnable {
            if (isStatusViewNull()) {
                initStatusView()
            }
            mStatusView!!.showErrorView(text)
        })
    }

    fun hideErrorView() {
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

    fun setOnErrorViewClick(onClickListener: View.OnClickListener) {
        if (isStatusViewNull()) {
            Logger.w(this.javaClass.simpleName + " ,The status view is empty.")
            initStatusView()
        }
        mStatusView!!.setOnErrorViewClick(onClickListener)
    }

    // 布局资源ID
    protected abstract fun getLayoutId(): Int

    // 初始化变量 子类需要就重写
    protected abstract fun initVariables(rootView: View?, container: ViewGroup?, savedInstanceState: Bundle?)

    // 初始化监听
    protected abstract fun initListeners()

    override fun onResume() {
        super.onResume()
        if (!mIsFirstVisible) {
            if (!isHidden && !currentVisibleState && userVisibleHint) {
                dispatchUserVisibleHint(true)
            }
        }
    }


    override fun onPause() {
        //Logger.d(this.getClass().getSimpleName());
        super.onPause()
        if (currentVisibleState && userVisibleHint) {
            dispatchUserVisibleHint(false)
        }
    }

    /**
     * 统一处理 显示隐藏
     *
     * @param visible
     */
    private fun dispatchUserVisibleHint(visible: Boolean) {
        //当前 Fragment 是 child 时候 作为缓存 Fragment 的子 fragment getUserVisibleHint = true
        //但当父 fragment 不可见所以 currentVisibleState = false 直接 return 掉
        // 这里限制则可以限制多层嵌套的时候子 Fragment 的分发
        if (visible && isParentInvisible()) return

        //此处是对子 Fragment 不可见的限制，因为 子 Fragment 先于父 Fragment回调本方法 currentVisibleState 置位 false
        // 当父 dispatchChildVisibleState 的时候第二次回调本方法 visible = false 所以此处 visible 将直接返回
        if (currentVisibleState == visible) {
            return
        }
        currentVisibleState = visible
        if (visible) {
            if (mIsFirstVisible) {
                mIsFirstVisible = false
                onFragmentFirstVisible()
            }
            onFragmentResume()
            dispatchChildVisibleState(true)
        } else {
            dispatchChildVisibleState(false)
            onFragmentPause()
        }
    }

    /**
     * 用于分发可见时间的时候父获取 fragment 是否隐藏
     *
     * @return true fragment 不可见， false 父 fragment 可见
     */
    private fun isParentInvisible(): Boolean {
        val parentFragment = parentFragment
        return if (parentFragment is LazyDcFragment<*>) {
            !parentFragment.isSupportVisible
        } else {
            false
        }
    }

    fun isSupportVisible(): Boolean {
        return currentVisibleState
    }

    /**
     * 当前 Fragment 是 child 时候 作为缓存 Fragment 的子 fragment 的唯一或者嵌套 VP 的第一 fragment 时 getUserVisibleHint = true
     * 但是由于父 Fragment 还进入可见状态所以自身也是不可见的， 这个方法可以存在是因为庆幸的是 父 fragment 的生命周期回调总是先于子 Fragment
     * 所以在父 fragment 设置完成当前不可见状态后，需要通知子 Fragment 我不可见，你也不可见，
     *
     *
     * 因为 dispatchUserVisibleHint 中判断了 isParentInvisible 所以当 子 fragment 走到了 onActivityCreated 的时候直接 return 掉了
     *
     *
     * 当真正的外部 Fragment 可见的时候，走 setVisibleHint (VP 中)或者 onActivityCreated (hide show) 的时候
     * 从对应的生命周期入口调用 dispatchChildVisibleState 通知子 Fragment 可见状态
     *
     * @param visible
     */
    private fun dispatchChildVisibleState(visible: Boolean) {
        val childFragmentManager = childFragmentManager
        val fragments = childFragmentManager.fragments
        if (!fragments.isEmpty()) {
            for (child in fragments) {
                if (child is BaseLazyFragment && !child.isHidden() && child.getUserVisibleHint()) {
                    child.dispatchUserVisibleHint(visible)
                }
            }
        }
    }

    /**
     * 第一次对用户课件
     */
    open fun onFragmentFirstVisible() {
//        Log.e(super.getTAG(),  "  对用户第一次可见");
//        mPresenter.
    }

    /**
     * 对用户可见
     */
    fun onFragmentResume() {
    }

    /**
     * 对用户不可见
     */
    fun onFragmentPause() {
    }

    override fun onDestroy() {
        //Logger.d(this.getClass().getSimpleName());
        rootView = null
        if (!isStatusViewNull()) {
            mStatusView!!.release()
        }
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isViewCreated = false
        mIsFirstVisible = true
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        // 对于默认 tab 和 间隔 checked tab 需要等到 isViewCreated = true 后才可以通过此通知用户可见
        // 这种情况下第一次可见不是在这里通知 因为 isViewCreated = false 成立,等从别的界面回到这里后会使用 onFragmentResume 通知可见
        // 对于非默认 tab mIsFirstVisible = true 会一直保持到选择则这个 tab 的时候，因为在 onActivityCreated 会返回 false
        if (isViewCreated) {
            if (isVisibleToUser && !currentVisibleState) {
                dispatchUserVisibleHint(true)
            } else if (!isVisibleToUser && currentVisibleState) {
                dispatchUserVisibleHint(false)
            }
        }
    }

    fun getRootView(): ViewGroup? {
        return rootView as ViewGroup?
    }

    override fun getCompatColor(@ColorRes resId: Int): Int {
        return ContextCompat.getColor(requireActivity(), resId)
    }

    override fun getCompatDrawable(@DrawableRes id: Int): Drawable? {
        return ContextCompat.getDrawable(requireActivity(), id)
    }

    fun Router(uri: String): DcUriRequest {
        Logger.i(this.javaClass.simpleName + " ,The Router : " + uri)
        return DcUriRequest(requireActivity(), uri)
    }

    fun getUser(): DcLoginUser? {
        if (null == AccountHelper.getInstance().user) {
            Router(AccountConstant.Uri.INDEX)
                .start()
            return null
        }
        return AccountHelper.getInstance().user
    }

    override fun getIntent(): Intent? {
        Logger.d(this.javaClass.simpleName)
        return if (null == activity) {
            null
        } else requireActivity().intent
    }

    override fun getUri(): Uri? {
        Logger.d(this.javaClass.simpleName)
        if (null == activity || null == intent) {
            Logger.w("The activity or intent is empty.")
            return null
        }
        return intent!!.data
    }

    override fun getUriSite(): String? {
        Logger.d(this.javaClass.simpleName)
        if (null == uri) {
            Logger.w(this.javaClass.simpleName + " ,The uri is empty.")
            return ""
        }
        val uriSite = uri?.scheme + "://" + uri!!.host + uri!!.path
        Logger.d(this.javaClass.simpleName + " ,UriSite: " + uriSite)
        return uriSite
    }


    override fun showToastShort(text: CharSequence) {
        if (!userVisibleHint) {
            Logger.w(
                this.javaClass.simpleName +
                        " ,This fragment is invisible to user, so can't show the toast."
            )
            return
        }
        Logger.i(this.javaClass.simpleName + " ,The toast context: " + text)
        TopToast.INSTANCE.showToast(context, text.toString(), Toast.LENGTH_SHORT)
    }

    override fun showToastLong(text: CharSequence) {
        if (!userVisibleHint) {
            Logger.w(
                this.javaClass.simpleName +
                        " ,This fragment is invisible to user, so can't show the toast."
            )
            return
        }
        Logger.i(this.javaClass.simpleName + " ,The toast context: " + text)
        TopToast.INSTANCE.showToast(context, text.toString(), Toast.LENGTH_LONG)
    }

    override fun runOnUiThread(action: Runnable?) {
        val activity = activity
        activity?.runOnUiThread(action)
    }
}