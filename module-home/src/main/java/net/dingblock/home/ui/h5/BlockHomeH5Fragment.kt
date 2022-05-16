package net.dingblock.home.ui.h5

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import com.lljjcoder.style.citypickerview.CityPickerView
import cool.dingstock.appbase.base.BaseDcActivity
import cool.dingstock.appbase.constant.HomeBusinessConstant
import cool.dingstock.appbase.delegate.DCWebViewControllerDelegate
import cool.dingstock.appbase.entity.bean.home.HomeCategoryBean
import cool.dingstock.appbase.webview.DCWebView
import cool.dingstock.home.databinding.BlockFragmentH5Binding
import cool.dingstock.home.ui.h5.HomeH5Fragment
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.SizeUtils
import net.dingblock.mobile.base.fragment.BaseBindingFragment

class BlockHomeH5Fragment : BaseBindingFragment<BlockFragmentH5Binding>(), DCWebViewControllerDelegate {
    private var homeCategoryBean: HomeCategoryBean? = null
    private var needMargeTop = false
    private var mPicker: CityPickerView? = null


    fun setNeedMargeTop(needMargeTop: Boolean): BlockHomeH5Fragment {
        this.needMargeTop = needMargeTop
        return this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.apply {
            homeCategoryBean = this.getParcelable(HomeBusinessConstant.KEY_CATEGORY)
        }
        viewBinding.homeH5FragmentWebview.controllerDelegate = this
        val layoutParams = viewBinding.swipeRefreshLayout.layoutParams as FrameLayout.LayoutParams
        viewBinding.swipeRefreshLayout.setOnRefreshListener {
            if (homeCategoryBean != null) {
                viewBinding.homeH5FragmentWebview.clearCache(true)
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

    override fun onVisibleFirst() {
        super.onVisibleFirst()
//        showLoadingView()

        Logger.d("H5", "firstVisible")
        viewBinding.homeH5FragmentWebview.loadUrl("https://www.baidu.com")
    }

    override fun showLoadingView() {

    }

    override fun hideLoadingView() {
    }

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
        fun instance(homeCategoryBean: HomeCategoryBean): HomeH5Fragment {
            val fragment = HomeH5Fragment()
            fragment.apply {
                arguments?.putParcelable(HomeBusinessConstant.KEY_CATEGORY, homeCategoryBean)
            }
            return fragment
        }
    }
}