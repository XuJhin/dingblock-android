package cool.dingstock.appbase.widget.stateview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.airbnb.lottie.LottieAnimationView
import cool.dingstock.appbase.R
import cool.dingstock.appbase.util.isDarkMode
import cool.dingstock.appbase.widget.CommonEmptyView
import cool.dingstock.appbase.widget.TitleBar
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.SizeUtils
import java.lang.ref.SoftReference

class StatusView private constructor() {
    private var mContextSoft: SoftReference<Context>? = null
    private var mRootViewSoft: SoftReference<ViewGroup?>? = null
    private var mMarginTop = 0
    private var fullscreen = false
    private var mErrorIvWidth = 0
    private var mErrorIvHeight = 0
    private var mColor = 0
    private var mLoadingView: FrameLayout? = null
    private var mLoadingAniView: LottieAnimationView? = null
    private var mStatusRootView: View? = null
    private var mErrorLayer: View? = null
    private var mEmptyLayer: View? = null
    private var mEmptyTxt: CommonEmptyView? = null
    private var mErrorTxt: TextView? = null
    private var mErrorIv: ImageView? = null
    fun showLoadingView() {
        Logger.d("Show the Loading view.")
        if (mStatusRootView == null) {
            return
        }
        mStatusRootView?.visibility = View.VISIBLE
        mLoadingView?.visibility = View.VISIBLE
        mLoadingAniView?.let {
            if (it.context.isDarkMode()) {
                it.setAnimation("pull_white_refresh_ani.json")
            } else {
                it.setAnimation("pull_refresh_ani.json")
            }
            it.playAnimation()
        }
        mErrorTxt?.text = ""
        mErrorLayer?.visibility = View.GONE
        mEmptyLayer?.visibility = View.GONE
        // 添加
        addStatusRootView()
    }

    /**
     * 设置并添加 Status 样式
     */
    private fun addStatusRootView() {
        if (null == mStatusRootView || null == mRootViewSoft || null == mRootViewSoft?.get()) {
            Logger.w("The root view is empty.")
            return
        }
        if (mRootViewSoft?.get()?.indexOfChild(mStatusRootView)!! > -1) {
            Logger.d("The view has added, so don't add again.")
            //            mRootViewSoft.get().bringChildToFront(mStatusRootView);
            mRootViewSoft?.get()?.removeView(mStatusRootView)
        }
        val params = mStatusRootView?.layoutParams
        var statusRootLp: ViewGroup.MarginLayoutParams? = null
        //获取view的margin设置参数
        statusRootLp = if (params is ViewGroup.MarginLayoutParams) {
            params
        } else {
            //不存在时创建一个新的参数
            //基于View本身原有的布局参数对象
            ViewGroup.MarginLayoutParams(params)
        }
        var haveTitleBar = false
        for (i in 0 until mRootViewSoft?.get()?.childCount!!) {
            val childView = mRootViewSoft?.get()?.getChildAt(i)
            if (childView is TitleBar) {
                haveTitleBar = true
            }
        }
        if (fullscreen) {
            mMarginTop = 0
        } else {
            if (mMarginTop != 0) {
                statusRootLp.topMargin = mMarginTop
            } else {
                statusRootLp.topMargin = if (haveTitleBar) SizeUtils.dp2px(44f) else 0
            }
        }
        // 根据不用的布局 增加到不同的Index
        if (mRootViewSoft?.get() is RelativeLayout || mRootViewSoft?.get() is FrameLayout) {
            Logger.d("RootView is RelativeLayout or FrameLayout, so add to lastIndex")
            mStatusRootView?.layoutParams = statusRootLp
            mRootViewSoft?.get()?.addView(mStatusRootView)
            mStatusRootView?.bringToFront()
        } else if (mRootViewSoft?.get() is LinearLayout) {
            Logger.d("RootView is LinearLayout,so add to firstIndex")
            mStatusRootView?.layoutParams = statusRootLp
            mRootViewSoft?.get()?.addView(mStatusRootView, 0)
        } else if (mRootViewSoft?.get() is LinearLayoutCompat) {
            Logger.d("RootView is LinearLayout,so add to firstIndex")
            mStatusRootView?.layoutParams = statusRootLp
            mRootViewSoft?.get()?.addView(mStatusRootView, 0)
        } else if (mRootViewSoft?.get() is CoordinatorLayout) {
            mRootViewSoft?.get()?.addView(mStatusRootView)
            mStatusRootView?.bringToFront()
        } else if (mRootViewSoft?.get() is ConstraintLayout) {
            val layoutParams = ConstraintLayout.LayoutParams(statusRootLp)
            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            mStatusRootView?.layoutParams = layoutParams
            mRootViewSoft?.get()?.addView(mStatusRootView)
            Logger.e("RootView is RelativeLayout or FrameLayout, so add to lastIndex")
            mStatusRootView?.bringToFront()
        }
    }

    fun hideLoadingView() {
        Logger.d("Hide the Loading view.")
        if (null != mStatusRootView) {
            mStatusRootView?.visibility = View.GONE
        }
        if (null != mLoadingView) {
            mLoadingView?.visibility = View.GONE
            mLoadingAniView?.cancelAnimation()
        }
        // 移除
        removeStatusRootView()
    }

    /**
     * 删除 status
     */
    private fun removeStatusRootView() {
        if (null == mStatusRootView || null == mRootViewSoft || null == mRootViewSoft?.get()) {
            Logger.w("The root view is empty.")
            return
        }
        if (mRootViewSoft?.get()?.indexOfChild(mStatusRootView) == -1) {
            Logger.d("The view no added, so don't remove.")
            return
        }
        Logger.d("Start to remove the status fo rootView.")
        mRootViewSoft?.get()?.removeView(mStatusRootView)
    }

    fun showEmptyView() {
        val context = mContextSoft?.get() ?: return
        showEmptyView(context.getString(R.string.common_status_empty_tips))
    }

    fun showEmptyView(text: String) {
        val context = mContextSoft?.get() ?: return
        mStatusRootView?.visibility = View.VISIBLE
        mStatusRootView?.setOnClickListener { v: View? -> }
        mEmptyTxt?.setText(text)
        mEmptyLayer?.visibility = View.VISIBLE
        mLoadingView?.visibility = View.GONE
        mLoadingAniView?.cancelAnimation()
        // 添加
        addStatusRootView()
    }

    fun hideEmptyView() {
        if (null != mStatusRootView) {
            mStatusRootView?.visibility = View.GONE
        }
        if (mEmptyLayer != null) {
            mEmptyLayer?.visibility = View.GONE
        }
    }

    fun showErrorView() {
        val context = mContextSoft?.get() ?: return
        showErrorView(context.getString(R.string.common_status_error_tips))
    }

    fun showErrorView(text: String?) {
        if ("i/o failure".equals(text, ignoreCase = true)) {
            showErrorView()
            return
        }
        if (null == mStatusRootView) {
            return
        }
        Logger.d("Show the Error view.")
        mStatusRootView?.visibility = View.VISIBLE
        //这里为了屏蔽 除了statusView的所有view的点击事件
        mStatusRootView?.setOnClickListener { v: View? -> }
        mErrorLayer?.visibility = View.VISIBLE
        mErrorTxt?.text = text
        mEmptyLayer?.visibility = View.GONE
        mLoadingView?.visibility = View.GONE
        mLoadingAniView?.cancelAnimation()
        // 添加
        addStatusRootView()
    }

    fun hideErrorView() {
        Logger.d("Hide the Error view.")
        mStatusRootView?.visibility = View.GONE
        mErrorLayer?.visibility = View.GONE
        mErrorTxt?.text = ""
        // 移除
        removeStatusRootView()
    }

    fun onPause() {
        try {
            mLoadingAniView?.pauseAnimation()
        } catch (e: Exception) {
        }
    }

    fun onResume() {
        try {
            mLoadingAniView?.resumeAnimation()
        } catch (e: Exception) {
        }
    }

    fun setOnErrorViewClick(onClickListener: View.OnClickListener) {
        if (null == mErrorLayer) {
            Logger.w("The error view is empty. so can't add the click listener.")
            return
        }
        mErrorLayer?.setOnClickListener(onClickListener)
    }

    fun release() {
        Logger.d("Start to Release the statusView.")
        removeStatusRootView()
        if (null != mContextSoft) {
            mContextSoft?.clear()
            mContextSoft = null
        }
        if (null != mRootViewSoft) {
            mRootViewSoft?.clear()
            mRootViewSoft = null
        }
        if (null != mLoadingView) {
            mLoadingAniView?.cancelAnimation()
            mLoadingView = null
            mLoadingAniView = null
        }
        mStatusRootView = null
        mErrorLayer = null
        mErrorTxt = null
        mEmptyLayer = null
        mEmptyTxt = null
    }

    /**
     * 根据不用 Theme 设置不用的 loading error 样式
     */
    private fun createStatusViews(context: Context?) {
        Logger.d("Start to create status views.")
        // 初始化 状态布局
        mStatusRootView = LayoutInflater.from(context).inflate(
            R.layout.common_layout_status,
            mRootViewSoft?.get(), false
        )
        if (0 != mColor) {
            mStatusRootView?.setBackgroundColor(mColor)
        }
        // 初始化 loading 布局
        mLoadingView = mStatusRootView?.findViewById(R.id.status_layout_loading_layer)
        mLoadingAniView = mStatusRootView?.findViewById(R.id.status_layout_loading_ani_view)
        mLoadingAniView?.repeatCount = Int.MAX_VALUE
        // 初始化 error 布局
        mErrorLayer = mStatusRootView?.findViewById(R.id.status_layout_error_layer)
        mEmptyLayer = mStatusRootView?.findViewById(R.id.status_layout_empty_layer)
        mEmptyTxt = mStatusRootView?.findViewById(R.id.emptyView)
        mErrorTxt = mStatusRootView?.findViewById(R.id.status_layout_error_txt)
        mErrorIv = mStatusRootView?.findViewById(R.id.common_status_error_iv)
        if (mErrorIvHeight != 0 && mErrorIvWidth != 0) {
            val layoutParams = mErrorIv?.layoutParams
            layoutParams?.width = mErrorIvWidth
            layoutParams?.height = mErrorIvHeight
            mErrorIv?.layoutParams = layoutParams
        }
        // resetStatus
        if (mStatusRootView != null) {
            mStatusRootView?.visibility = View.GONE
        }
        if (mLoadingView != null) {
            mLoadingView?.visibility = View.GONE
        }
        if (mEmptyLayer != null) {
            mErrorLayer?.visibility = View.GONE
        }
    }

    class Builder {
        private val mStatusView: StatusView
        fun with(context: Context): Builder {
            mStatusView.mContextSoft = SoftReference(context)
            return this
        }

        fun rootView(rootView: ViewGroup?): Builder {
            mStatusView.mRootViewSoft = SoftReference(rootView)
            return this
        }

        fun marginTop(mMarginTop: Int): Builder {
            mStatusView.mMarginTop = mMarginTop
            return this
        }

        fun fullscreen(fullscreen: Boolean): Builder {
            mStatusView.fullscreen = fullscreen
            return this
        }

        fun errorIvWidthAndHeight(width: Int, height: Int): Builder {
            mStatusView.mErrorIvWidth = width
            mStatusView.mErrorIvHeight = height
            return this
        }

        fun backgroundColor(color: Int): Builder {
            mStatusView.mColor = color
            return this
        }

        fun build(): StatusView {
            mStatusView.createStatusViews(mStatusView.mContextSoft?.get())
            return mStatusView
        }

        init {
            mStatusView = StatusView()
        }
    }

    companion object {
        @JvmStatic
        fun newBuilder(): Builder {
            return Builder()
        }
    }
}