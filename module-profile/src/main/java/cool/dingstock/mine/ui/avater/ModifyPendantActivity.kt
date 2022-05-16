package cool.dingstock.mine.ui.avater

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.adapter.VPLessViewAdapter
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.mine.PendantHomeBean
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.mvp.BaseFragment
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.widget.tablayout.TabScaleAdapter
import cool.dingstock.mine.R
import cool.dingstock.mine.databinding.ActivityModifyPendantBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [MineConstant.Path.MODIFY_PENDANT]
)
class ModifyPendantActivity: VMBindingActivity<ModifyPendantViewModel, ActivityModifyPendantBinding>() {
    private val fragmentList = mutableListOf<BaseFragment>()

    private val tabScaleAdapter by lazy {
        TabScaleAdapter(viewModel.tabNameList).apply {
            setStartAndEndTitleSize(14f, 14f)
            setNormalColor(getCompatColor(R.color.color_text_black4))
            setSelectedColor(getCompatColor(R.color.color_text_black1))
            setPadding(15.dp.toInt())
            setIndicatorColor(
                getCompatColor(R.color.color_text_black1),
                25.dp.toInt(),
                -1
            )
            setIndicatorHeight(2.dp.toInt())
            setTabSelectListener { index ->
                viewBinding.pendantVp.setCurrentItem(index, false)
            }
        }
    }

    private val commonNavigator by lazy {
        CommonNavigator(this).apply {
            adapter = tabScaleAdapter
        }
    }

    override fun moduleTag(): String {
        return ModuleConstant.MINE_MODULE
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        showLoadingView()
        with(viewBinding) {
            pendantVp.apply {
                adapter = VPLessViewAdapter(supportFragmentManager, fragmentList)
            }
        }

        with(viewModel) {
            wearSuccess.observe(this@ModifyPendantActivity) {
                if (it == true) {
                    pendantHomeData.value?.let { pendant ->
                        viewBinding.wearBtn.text = if (pendant.user?.avatarPendantId == selectPendant.value?.id) "取消佩戴" else "立即佩戴"
                    }
                }
            }
            lifecycleScope.launchWhenResumed {
                pendantHomeData.filter {
                    it != null
                }.collectLatest {
                    setData(it!!)
                }
            }

            lifecycleScope.launchWhenResumed {
                selectPendant
                    .filter {
                        it != null
                    }.
                    sample(500)
                    .collectLatest { pendant ->
                        pendantHomeData.value?.let {
                            pendantHomeData.value?.user?.avatarPendantUrl = pendant?.imageUrl
                            viewBinding.avatarIv.setPendantUrl(pendant?.imageUrl)
                            viewBinding.wearBtn.text = if (pendant!!.needVip == true) {
                                if (it.user?.isVip == true) {
                                    if (it.user?.avatarPendantId == pendant.id) "取消佩戴" else "立即佩戴"
                                } else {
                                    "开通会员使用头像挂件"
                                }
                            } else {
                                if (it.user?.avatarPendantId == pendant.id) "取消佩戴" else "立即佩戴"
                            }
                        }
                    }
            }

            lifecycleScope.launch {
                wearResult
                    .filter { it.isNotEmpty() }
                    .collectLatest {
                        hideLoadingDialog()
                        showToastShort(it)
                        wearResult.value = ""
                    }
            }
        }
        viewModel.getPendantHome()
    }

    override fun initListeners() {
        with(viewBinding) {
            pendantVp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    categoryTab.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    categoryTab.onPageSelected(position)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    categoryTab.onPageScrollStateChanged(state)
                }

            })

            wearBtn.setOnShakeClickListener {
                if (viewModel.selectPendant.value?.needVip == true) {
                    if (viewModel.pendantHomeData.value?.user?.isVip == true) {
                        wearPendant()
                    } else {
                        UTHelper.commonEvent(UTConstant.Mine.VipP_ent, "source", "头像挂件页")
                        DcRouter(MineConstant.Uri.VIP_CENTER).start()
                    }
                } else {
                    wearPendant()
                }
            }
        }
    }

    private fun wearPendant() {
        showLoadingDialog()
        viewModel.wearPendant()
    }

    override fun onStatusViewErrorClick() {
        super.onStatusViewErrorClick()
        showLoadingView()
        viewModel.getPendantHome()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        viewModel.tabNameList.clear()
        fragmentList.clear()
        val managerFragments = supportFragmentManager.fragments
        if (managerFragments.isNotEmpty()) {
            val transaction = supportFragmentManager.beginTransaction()
            for (fragment in managerFragments) {
                if (fragment is PendantListFragment) transaction.remove(fragment)
            }
            transaction.commitNowAllowingStateLoss()
        }
    }

    private fun setData(data: PendantHomeBean) {
        var defaultPosition = 0
        var hasWear = false

        data.user?.let {
            viewBinding.avatarIv.apply {
                setAvatarUrl(it.avatarUrl)
                it.avatarPendantUrl?.let { url -> if(url.isNotEmpty()) setPendantUrl(url) }
            }
        }
        data.pendants?.forEachIndexed { index, pendant ->
            viewModel.tabNameList.add(pendant.name ?: "")
            pendant.pendantList?.let {
                for (pendantDetail in it) {
                    if (data.user?.avatarPendantId == pendantDetail.id) {
                        defaultPosition = index
                        pendantDetail.selected = true
                        hasWear = true
                        viewModel.selectPendant.value = pendantDetail
                        break
                    }
                }
            }
        }
        if (!hasWear) {
            data.pendants?.let { pendants ->
                for ((index, pendant) in pendants.withIndex()) {
                    if (!pendant.pendantList.isNullOrEmpty()) {
                        pendant.pendantList!![0].selected = true
                        viewModel.selectPendant.value = pendant.pendantList!![0]
                        defaultPosition = index
                        break
                    }
                }
            }
        }
        data.pendants?.forEach {
            fragmentList.add(PendantListFragment.getInstance(it.pendantList))
        }
        viewBinding.categoryTab.navigator = commonNavigator

        viewBinding.pendantVp.adapter?.notifyDataSetChanged()
        viewBinding.pendantVp.offscreenPageLimit = data.pendants?.size ?: 1
        viewBinding.pendantVp.setCurrentItem(defaultPosition, false)
    }
}