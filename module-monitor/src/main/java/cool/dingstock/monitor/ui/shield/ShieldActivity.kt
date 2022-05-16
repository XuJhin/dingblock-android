package cool.dingstock.monitor.ui.shield

import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.MonitorConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.entity.bean.monitor.MonitorProductBean
import cool.dingstock.appbase.entity.event.im.EventBlockGoods
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.widget.CommonEmptyView
import cool.dingstock.monitor.adapter.item.MonitorShieldItemBinder
import cool.dingstock.monitor.databinding.ActivityShieldBinding
import org.greenrobot.eventbus.EventBus

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [MonitorConstant.Path.MONITOR_SHIELD]
)
class ShieldActivity : VMBindingActivity<ShieldViewModel, ActivityShieldBinding>() {

    private val shieldAdapter by lazy {
        DcBaseBinderAdapter(arrayListOf())
    }

    private val shieldItemBinder by lazy {
        MonitorShieldItemBinder()
    }

    override fun moduleTag(): String {
        return ModuleConstant.MONITOR
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        initViewAndEvent()
        showLoadingView()
        syncUI()
        loadData()
    }

    private fun initViewAndEvent() {
        viewBinding.titleBar.apply {
            title = "已屏蔽内容"
        }
        viewBinding.refreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
        initRvAdapter()
        viewBinding.rvShield.apply {
            (itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
            layoutManager = LinearLayoutManager(this@ShieldActivity)
            adapter = shieldAdapter
        }
    }

    override fun initListeners() {
    }

    private fun initRvAdapter() {
        shieldItemBinder.setClickListener { id, block, pos ->
            bizAction(id, block, pos)
        }
        shieldAdapter.addItemBinder(MonitorProductBean::class.java, shieldItemBinder)

        viewBinding.rvShield.apply {
            layoutManager = LinearLayoutManager(this@ShieldActivity)
            adapter = shieldAdapter
        }
        val tv = CommonEmptyView(this)
        tv.setText("暂无监控数据")
        shieldAdapter.setEmptyView(tv)
    }

    private fun loadData() {
        showLoadingView()
        viewModel.refresh()
    }

    private fun finishRefresh() {
        viewBinding.refreshLayout.isRefreshing = false
    }

    private fun syncUI() {
        viewModel.apply {
            stateEmpty.observe(this@ShieldActivity) {
                showEmptyView()
                finishRefresh()
            }
            stateLoadMore.observe(this@ShieldActivity) {
                when (it) {
                    ShieldViewModel.LOAD_MORE_COMPLETED -> shieldAdapter.loadMoreModule.loadMoreEnd()
                    ShieldViewModel.LOAD_MORE_FAILED -> shieldAdapter.loadMoreModule.loadMoreFail()
                }
            }
            stateError.observe(this@ShieldActivity) {
                finishRefresh()
                showErrorView(it)
            }
            loadMoreList.observe(this@ShieldActivity) {
                setItemList(it, false)
                shieldAdapter.loadMoreModule.loadMoreComplete()
            }
            dataList.observe(this@ShieldActivity) {
                finishRefresh()
                setItemList(it, true)
            }
        }
    }

    private fun setItemList(
        monitorProductBeanList: MutableList<MonitorProductBean>,
        isRefresh: Boolean
    ) {
        if (isRefresh) {
            hideLoadingView()
            shieldAdapter.setList(monitorProductBeanList)
        } else {
            shieldAdapter.addData(monitorProductBeanList)
            shieldAdapter.loadMoreModule.loadMoreComplete()
        }
    }

    private fun bizAction(bizId: String, blocked: Boolean, position: Int) {
        if (shieldAdapter.data.isNullOrEmpty()) {
            return
        }
        val bean = shieldAdapter.data[position] as MonitorProductBean
        viewModel.bizAction(bizId, blocked, success = {
            bean.blocked = blocked
            showToastShort(if (blocked) "屏蔽成功" else "取消屏蔽成功")
            try {
                shieldAdapter.notifyItemChanged(position + shieldAdapter.headerLayoutCount)
                EventBus.getDefault().post(EventBlockGoods(bizId, blocked))
            } catch (e: java.lang.Exception) {
            }
        }) { msg -> showToastShort(msg) }
    }
}
