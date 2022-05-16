package cool.dingstock.home.ui.h5

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.lljjcoder.style.citypickerview.CityPickerView
import cool.dingstock.appbase.base.BaseDcActivity
import cool.dingstock.appbase.constant.HomeBusinessConstant
import cool.dingstock.appbase.delegate.DCWebViewControllerDelegate
import cool.dingstock.appbase.entity.bean.home.HomeCategoryBean
import cool.dingstock.appbase.mvp.lazy.DCLazyFragmentPresenter
import cool.dingstock.appbase.mvp.lazy.VmBindingLazyFragment
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.webview.DCWebView
import cool.dingstock.home.databinding.HomeFragmentH5LayoutBinding
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.SizeUtils

class HomeH5Fragment : VmBindingLazyFragment<BaseViewModel, HomeFragmentH5LayoutBinding>(),
    DCWebViewControllerDelegate {
    private var homeCategoryBean: HomeCategoryBean? = null
    private var needMargeTop = false
    private var mPicker: CityPickerView? = null
    override fun ignoreUt(): Boolean {
        return true
    }

    fun setNeedMargeTop(needMargeTop: Boolean): HomeH5Fragment {
        this.needMargeTop = needMargeTop
        return this
    }

    override fun initPresenter(): DCLazyFragmentPresenter<*>? {
        return null
    }

    override fun initVariables(
        rootView: View?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        val arguments = arguments
        if (null == arguments) {
            Logger.e(" HomeCategoryBean  null  error")
            return
        }
        homeCategoryBean = arguments[HomeBusinessConstant.KEY_CATEGORY] as HomeCategoryBean?
        if (null == homeCategoryBean || TextUtils.isEmpty(homeCategoryBean?.link)) {
            return
        }
        viewBinding.homeH5FragmentWebview.controllerDelegate = this
        val layoutParams = viewBinding.swipeRefreshLayout.layoutParams as FrameLayout.LayoutParams
        viewBinding.swipeRefreshLayout.setOnRefreshListener {
            if (homeCategoryBean != null) {
                viewBinding.homeH5FragmentWebview.clearCache(true)
                viewBinding.homeH5FragmentWebview.clearCache(false)
                viewBinding.homeH5FragmentWebview.reload()
            }
            viewBinding.swipeRefreshLayout.finishRefresh()
        }
        if (needMargeTop) {
            layoutParams.topMargin =
                SizeUtils.dp2px(40f) + SizeUtils.getStatusBarHeight(requireContext())
        } else {
            layoutParams.topMargin = SizeUtils.dp2px(0f)
        }
        viewBinding.swipeRefreshLayout.layoutParams = layoutParams
        viewBinding.homeH5FragmentWebview.refreshStateListener =
            object : DCWebView.RefreshStateListener {
                override fun refreshState(enable: Boolean) {
                    viewBinding.swipeRefreshLayout.isEnabled = enable
                }
            }
    }

    override fun onLazy() {
        showLoadingView()
        homeCategoryBean?.link?.let { viewBinding.homeH5FragmentWebview.loadUrl(it) }
    }

    override fun initListeners() {}
    override fun setTitleBarTitle(title: String) {}
    override fun setRightTxt(text: String) {}
    override fun onWebViewLoadingFinish() {}
    override fun getCityPickerView(): CityPickerView {
        if (mPicker == null) {
            mPicker = CityPickerView()
            mPicker!!.init(context)
        }
        return mPicker!!
    }

    override fun showCityPickerView() {
        mPicker?.showCityPicker()
    }

    override fun setTitleBarLeft(needHidden: Boolean, backBtnColor: String) {}
    override fun getDCActivity(): BaseDcActivity? {
        val activity = activity
        return if (activity !is BaseDcActivity) {
            null
        } else activity
    }

    override fun onDestroyView() {
        viewBinding.homeH5FragmentWebview.release()
        super.onDestroyView()
    }

    companion object {
        fun getInstance(homeCategoryBean: HomeCategoryBean?): HomeH5Fragment? {
            if (null == homeCategoryBean) {
                return null
            }
            val fragment = HomeH5Fragment()
            val bundle = Bundle()
            bundle.putParcelable(HomeBusinessConstant.KEY_CATEGORY, homeCategoryBean)
            fragment.arguments = bundle
            return fragment
        }
    }
}