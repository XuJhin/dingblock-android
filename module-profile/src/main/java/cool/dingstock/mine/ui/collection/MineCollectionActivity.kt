package cool.dingstock.mine.ui.collection

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.getColorRes
import cool.dingstock.appbase.lazy.FragmentLazyStatePageAdapter
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.widget.tablayout.TabScaleTipTxtAdapter
import cool.dingstock.lib_base.util.CollectionUtils
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.mine.R
import cool.dingstock.mine.databinding.ActivityMineCollectionBinding
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator


@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [MineConstant.Path.COLLECTION]
)
class MineCollectionActivity : VMBindingActivity<BaseViewModel, ActivityMineCollectionBinding>() {

    private val tabs by lazy { CommonNavigator(context) }
    private var list = arrayListOf("动态", "交易")

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        UTHelper.commonEvent(UTConstant.Mine.MyP_click_SetUp_Collect)
        viewBinding.titleBar.title = "我的收藏"
        setTabData(list)
    }

    override fun initBaseViewModelObserver() {
        super.initBaseViewModelObserver()
        viewModel.apply {
        }
    }

    override fun initListeners() {
        viewBinding.titleBar.setLeftOnClickListener {
            finish()
        }
    }

    private fun setTabData(orderTag: MutableList<String>) {
        val defaultSelected = 0
        val fragmentList: MutableList<Fragment> = ArrayList()
        val titleList: MutableList<String> = ArrayList()

        if (CollectionUtils.isNotEmpty(orderTag)) {
            for (tab in orderTag) {
                tab.let {
                    titleList.add(it)
                    var fragment: MineCollectionListFragment? = null
                    when (tab) {
                        "动态" -> {
                            fragment = MineCollectionListFragment.newInstance("dynamic")
                        }
                        "交易" -> {
                            fragment = MineCollectionListFragment.newInstance("deal")
                        }
                        else -> {
                        }
                    }
                    fragment?.let { it1 -> fragmentList.add(it1) }
                }
            }
        }

        val fragmentAdapter =
            FragmentLazyStatePageAdapter(supportFragmentManager, fragmentList, titleList)
        viewBinding.vp.adapter = fragmentAdapter

        val tabPageAdapter = TabScaleTipTxtAdapter(titleList)

        ContextCompat.getColor(context, R.color.color_text_black1).let {
            tabPageAdapter.setIndicatorColor(it, 28.dp.toInt(), 0)
        }
        tabPageAdapter.setStartAndEndTitleSize(14f, 16f)
        tabPageAdapter.setPadding(SizeUtils.dp2px(5f))
        tabPageAdapter.setTabSelectListener { index: Int ->
            viewBinding.vp.currentItem = index
        }
        tabs.isAdjustMode = true
        tabs.adapter = tabPageAdapter
        viewBinding.tabLayout.navigator = tabs
        tabs.onPageSelected(defaultSelected)
        viewBinding.tabLayout.onPageSelected(defaultSelected)

        viewBinding.vp.currentItem = defaultSelected
        ViewPagerHelper.bind(viewBinding.tabLayout, viewBinding.vp)
    }

    override fun moduleTag(): String {
        return ModuleConstant.MINE_MODULE
    }
}