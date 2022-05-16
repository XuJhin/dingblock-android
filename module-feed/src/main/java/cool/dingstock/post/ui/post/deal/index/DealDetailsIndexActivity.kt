package cool.dingstock.post.ui.post.deal.index

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.chad.library.adapter.base.BaseBinderAdapter
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.adapter.CommonSpanItemDecoration
import cool.dingstock.appbase.adapter.VPLessViewAdapter
import cool.dingstock.appbase.adapter.itembinder.SizeItemBinder
import cool.dingstock.appbase.constant.*
import cool.dingstock.appbase.entity.bean.circle.GoodsBean
import cool.dingstock.appbase.entity.bean.sku.Size
import cool.dingstock.appbase.entity.event.circle.EventCircleChange
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.helper.TimeTagHelper
import cool.dingstock.appbase.mvp.BaseFragment
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.util.transactionEditDialogShow
import cool.dingstock.appbase.widget.tablayout.TabScaleAdapter
import cool.dingstock.lib_base.json.JSONHelper
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.post.R
import cool.dingstock.post.databinding.ActivityDealDetailsIndexBinding
import cool.dingstock.post.ui.post.deal.defects.DealDefectsFragment
import cool.dingstock.post.ui.post.deal.newdeal.DealNewFragment
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.atomic.AtomicBoolean

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [CircleConstant.Path.DEAL_DETAILS]
)
class DealDetailsIndexActivity :
    VMBindingActivity<DealDetailsIndexVM, ActivityDealDetailsIndexBinding>() {

    private val sizeBinder by lazy {
        SizeItemBinder(true)
    }

    private val sizeAdapter by lazy {
        BaseBinderAdapter().apply {
            addItemBinder(Size::class.java, sizeBinder)
        }
    }


    val fragments = arrayListOf<BaseFragment>()
    private val vpAdapter = VPLessViewAdapter(supportFragmentManager, fragments)
    private val isInit = AtomicBoolean(false)

    override fun moduleTag(): String = "POST"

    override fun fakeStatusView(): View = viewBinding.fakeStatusBar

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        val id = uri.getQueryParameter(CircleConstant.UriParams.ID)
        if (id.isNullOrEmpty()) {
            finish()
            return
        }
        StatusBarUtil.transparentStatus(this)
        viewModel.id = id
        initSizeRv()
        initView()
        initObserver()
        viewModel.loadData()
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun initListeners() {
        viewBinding.publishLayer.setOnShakeClickListener {
            if (!LoginUtils.isLoginAndRequestLogin(this)) {
                return@setOnShakeClickListener
            }
            UTHelper.commonEvent(UTConstant.Deal.TransactionDetailsP_click_Release)
            val entity = viewModel.dealProductEntity.value ?: return@setOnShakeClickListener
            supportFragmentManager.transactionEditDialogShow(
                GoodsBean(
                    viewModel.id,
                    entity.product?.name,
                    entity.product?.sku,
                    entity.product?.imageUrl,
                    entity.product?.goodsType
                )
            )
        }
        viewBinding.smartRefreshLayout.setOnRefreshListener {
            viewModel.loadData(false)
        }
        viewBinding.commentLayer.setOnShakeClickListener {
            UTHelper.commonEvent(UTConstant.Deal.TransactionDetailsP_click_Comment)
            val entity = viewModel.dealProductEntity.value ?: return@setOnShakeClickListener
            DcRouter(HomeConstant.Uri.REGION_COMMENT)
                .dialogBottomAni()
                .putUriParameter(CircleConstant.UriParams.ID, viewModel.id)
                .putExtra(
                    CalendarConstant.DataParam.KEY_PRODUCT,
                    JSONHelper.toJson(entity.product?.sketch())
                )
                .start()
        }
        sizeBinder.setOnItemClickListener { _, _, position ->
            (sizeAdapter.data[position] as? Size).let { size ->
                if (size?.size == viewModel.currentSize) {
                    return@setOnItemClickListener
                }
                UTHelper.commonEvent(UTConstant.Deal.TransactionDetailsP_click_Size)
                viewModel.currentSize = size?.size ?: ""
                sizeAdapter.data.forEach {
                    (it as? Size)?.let { size1 ->
                        size1.selected = size1.size == viewModel.currentSize
                    }
                }
                sizeAdapter.notifyDataSetChanged()
                fragments.forEach {
                    it.reload()
                }
            }
        }
        viewBinding.courierLayer.setOnShakeClickListener {
            if (LoginUtils.isLoginAndRequestLogin(this)) {
                UTHelper.commonEvent(UTConstant.Common.ExpressP_click_Ent, "source", "交易详情页")
                MobileHelper.getInstance().getCloudUrlAndRouter(this, "dcExpress")
            }
        }
    }

    override fun onStatusViewErrorClick() {
        super.onStatusViewErrorClick()
        viewModel.loadData()
    }

    private fun initSizeRv() {
        viewBinding.sizeRv.adapter = sizeAdapter
        viewBinding.sizeRv.layoutManager =
            GridLayoutManager(this, 6, RecyclerView.VERTICAL, false)
        viewBinding.sizeRv.addItemDecoration(CommonSpanItemDecoration(6, 8, 10, false))
        viewBinding.sizeRv.itemAnimator?.changeDuration = 0
    }

    private fun initTab() {
        if (isInit.get()) {
            return
        }
        isInit.set(true)
        val commonNavigator = CommonNavigator(this)
        viewModel.tabList.clear()
        viewModel.tabList.addAll(arrayListOf("全新无瑕", "轻微瑕疵"))
        MobileHelper.getInstance().configData.dealConfig?.typeList?.forEach {
            if (it.name == viewModel.dealProductEntity.value?.product?.goodsType) {
                if (it.qualityList.size >= 0) {
                    viewModel.tabList.clear()
                    it.qualityList.forEach { name ->
                        viewModel.tabList.add(name)
                    }
                }
            }
        }
        val tabPageAdapter = TabScaleAdapter(viewModel.tabList)
        tabPageAdapter.hideIndicator()
        tabPageAdapter.apply {
            setStartAndEndTitleSize(14f, 14f)
            setNormalColor(getCompatColor(R.color.color_text_black4))
            setSelectedColor(getCompatColor(R.color.color_text_black1))
            setPadding(SizeUtils.dp2px(12f))
            setTabSelectListener { index ->
                UTHelper.commonEvent(
                    UTConstant.Deal.TransactionDetailsP_click_Type, "type", when (index) {
                        0 -> viewModel.tabList[0]
                        1 -> viewModel.tabList[1]
                        else -> viewModel.tabList[0]
                    }
                )
                viewBinding.viewPager.setCurrentItem(index, false)
            }
        }
        commonNavigator.adapter = tabPageAdapter
        viewBinding.homePostTab.navigator = commonNavigator
        viewBinding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                viewBinding.homePostTab.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                viewBinding.homePostTab.onPageScrolled(
                    position,
                    positionOffset,
                    positionOffsetPixels
                )
            }

            override fun onPageSelected(position: Int) {
                viewBinding.homePostTab.onPageSelected(position)
            }
        })

        fragments.clear()
        val managerFragments = supportFragmentManager.fragments
        if (managerFragments.isNotEmpty()) {
            val transaction = supportFragmentManager.beginTransaction()
            for (fragment in managerFragments) {
                transaction.remove(fragment)
            }
            transaction.commitNowAllowingStateLoss()
        }
        viewModel.tabList.forEach {
            when (it) {
                "全新无瑕" -> fragments.add(DealNewFragment.newInstance(it))
                "轻微瑕疵" -> fragments.add(DealDefectsFragment.newInstance(it))
            }
        }
        viewBinding.viewPager.adapter = vpAdapter

    }

    private fun initView() {
        var enableShowTips = false
        if (MobileHelper.getInstance().configData.expressEntryList?.calendarDealEntry?.enabled == true) {
            val timeShow = TimeTagHelper.checkTimeTag(
                TimeTagHelper.DealDetailsExpressTips,
                TimeTagHelper.TimeTag.ONCE_DAY
            )
            if (timeShow) {
                enableShowTips = true
                TimeTagHelper.updateTimeTag(
                    TimeTagHelper.DealDetailsExpressTips,
                    System.currentTimeMillis()
                )
            }
        }
        viewBinding.courierTipsLayer.hide(!enableShowTips)
        viewBinding.courierTipsTv.text =
            if (LoginUtils.getCurrentUser()?.isVip() == false)
                MobileHelper.getInstance().configData.expressEntryList?.calendarDealEntry?.tip
            else
                MobileHelper.getInstance().configData.expressEntryList?.calendarDealEntry?.vipTip

        viewBinding.courierLayer.hide(MobileHelper.getInstance().configData.expressEntryList?.calendarDealEntry?.enabled != true)

    }

    private fun initObserver() {
        viewModel.apply {
            dealProductEntity.observe(this@DealDetailsIndexActivity) {
                if (it.shoesSize.isNullOrEmpty()) {
                    viewBinding.commonEmptyView.hide(false)
                } else {
                    viewBinding.commonEmptyView.hide(true)
                }
                viewBinding.smartRefreshLayout.finishRefresh()
                viewBinding.productIv.load(it.product?.imageUrl)
                viewBinding.productNameTv.text = it.product?.name
                viewBinding.skuTv.text = it.product?.sku
                viewBinding.commentTv.text = if (it.product?.commentCount ?: 0 > 0)
                    "讨论(${it.product?.commentCount ?: 0})" else "讨论"
                val sizes = arrayListOf<Size>()
                it.shoesSize?.forEachIndexed { index, s ->
                    if (currentSize == "") {
                        if (index == 0) {
                            viewModel.currentSize = s
                        }
                    }
                    sizes.add(Size(viewModel.currentSize == s, s, false))
                }
                sizeAdapter.setList(sizes)
                if (it.shoesSize.isNullOrEmpty()) {
                    viewBinding.sizeRv.hide(true)
                } else {
                    viewBinding.sizeRv.hide(false)
                }
                initTab()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onDealChange(event: EventCircleChange) {
        viewModel.loadData(false)
    }


}