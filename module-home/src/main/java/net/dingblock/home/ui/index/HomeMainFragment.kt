package net.dingblock.home.ui.index

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import cool.dingstock.appbase.entity.bean.home.HomeCategoryBean
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.widget.tablayout.HomeTabScaleImgAdapter
import cool.dingstock.foundation.adapter.vp.VpFragmentAdapter
import cool.dingstock.foundation.ext.getCompatColor
import cool.dingstock.home.R
import cool.dingstock.home.base.HomeViewModel
import cool.dingstock.home.databinding.BlockHomeMainBinding
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.SizeUtils
import net.dingblock.home.ui.h5.BlockHomeH5Fragment
import net.dingblock.home.ui.information.InformationFragment
import net.dingblock.home.ui.raffle.RaffleFragment
import net.dingblock.mobile.base.fragment.BaseBindingFragment
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator

/**
 * 首页信息
 */
class HomeMainFragment : BaseBindingFragment<BlockHomeMainBinding>() {
    val viewModel: HomeViewModel by viewModels()

    val fragment: MutableList<Fragment> = mutableListOf()
    private val titleList: MutableList<HomeTabScaleImgAdapter.TabTitleEntity> = mutableListOf(
        HomeTabScaleImgAdapter.TabTitleEntity().apply {
            title = "发售"
        },
        HomeTabScaleImgAdapter.TabTitleEntity().apply {
            title = "市场"
        }, HomeTabScaleImgAdapter.TabTitleEntity().apply {
            title = "空投"
        }
    )
    private var commonNavigator: CommonNavigator? = null
    private val tabPageAdapter by lazy {
        HomeTabScaleImgAdapter(titleList).apply {
            setStartAndEndTitleSize(16f, 21f)
            setNormalColor(getCompatColor(R.color.color_text_black3))
            setSelectedColor(getCompatColor(R.color.color_text_black1))
            setPadding(SizeUtils.dp2px(10f))
            setIndicatorColor(
                getCompatColor(R.color.color_text_black1),
                10.dp.toInt(),
                SizeUtils.dp2px(1.5f)
            )
            setTabSelectListener { index ->
                viewBinding.vpHome.currentItem = index
            }
//            setTabSelectListener { index: Int ->
//                selIndex = index
//                homeIndexViewModel!!.homeSelIndex = index
//
//                if (headTabCanScroll) {
//                    viewPager!!.currentItem = index
//                    UTHelper.homeLevel1Tab(titleList?.get(index)?.title)
//                }
//            }
        }
    }
    lateinit var tabAdapter: VpFragmentAdapter

    companion object {
        const val TAG = "HomeMainFragment"
        fun instance(tag: String): HomeMainFragment {
            return HomeMainFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewBar()
        if (commonNavigator != null) {
            return
        }
        Logger.d(TAG, "onViewCreated")
        commonNavigator = CommonNavigator(context)
        commonNavigator?.adapter = tabPageAdapter
        with(viewBinding.indicator) {
            navigator = commonNavigator
        }
        tabAdapter = VpFragmentAdapter(parentFragmentManager)
        with(viewBinding.vpHome) {
            adapter = tabAdapter
            addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    viewBinding.indicator.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    viewBinding.indicator.onPageSelected(position)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    viewBinding.indicator.onPageScrollStateChanged(state)
                }
            })
        }

        tabAdapter.fragments.add(0, RaffleFragment())
        tabAdapter.fragments.add(1, InformationFragment())
        BlockHomeH5Fragment.instance(HomeCategoryBean(link = "www.baidu.com")).let { tabAdapter.fragments.add(2, it) }
        tabAdapter.notifyDataSetChanged()


    }

    private fun setupViewBar() {

    }

    override fun onVisibleFirst() {
        super.onVisibleFirst()
        Logger.d(TAG, "onVisibleFirst")
    }

    override fun onVisibleExceptFirst() {
        super.onVisibleExceptFirst()
        Logger.d(TAG, "onVisible")
    }

    override fun onVisible() {
        super.onVisible()
    }

}

