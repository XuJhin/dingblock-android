package cool.dingstock.monitor.ui.log

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.MonitorConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.entity.bean.monitor.MonitorLogBean
import cool.dingstock.appbase.list.activity.AbsListActivity
import cool.dingstock.monitor.adapter.item.MonitorLogItemBinder
import cool.dingstock.monitor.databinding.ActivityMonitorLogBinding

@RouterUri(
        scheme = RouterConstant.SCHEME,
        host = RouterConstant.HOST,
        path = [MonitorConstant.Path.MONITOR_LOG]
)
class MonitorLogActivity : AbsListActivity<MonitorLogViewModel, ActivityMonitorLogBinding>() {

    private val logItemBinder by lazy {
        MonitorLogItemBinder()
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        super.initViewAndEvent(savedInstanceState)
        initPageStateObserver()
        refresh(true)
    }

    private fun finishRefresh() {
        hideLoadingView()
        viewBinding.refreshLayout.finishRefresh()
    }

    private fun initPageStateObserver() {
        viewModel.listLiveData.observe(this) {
            finishRefresh()
            updateDataList(it, false)
        }
        viewModel.listLoadLiveData.observe(this) {
            updateDataList(it, true)
        }
    }

    override fun initListeners() {
        viewBinding.apply {
            titleBar.setRightOnClickListener {
                viewModel.getCloudUrl("feedback")
            }
            titleBar.setLeftOnClickListener {
                finish()
            }
            refreshLayout.setOnRefreshListener {
                refresh(false)
            }
        }
    }

    private fun refresh(isFirst: Boolean) {
        if (isFirst) {
            showLoadingView()
        }
        viewModel.refresh(true)
    }

    override fun fetchMoreData() {
        viewModel.refresh(false)
    }

    override fun bindItemView() {
        pageAdapter.addItemBinder(MonitorLogBean::class.java, logItemBinder)
        viewBinding.rv.apply {
            layoutManager = LinearLayoutManager(this@MonitorLogActivity)
            adapter = pageAdapter
        }
    }

    override fun moduleTag(): String {
        return ModuleConstant.MONITOR
    }
}