package cool.dingstock.monitor.ui.rule

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.MonitorConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.monitor.ChannelBean
import cool.dingstock.appbase.entity.event.monitor.EventMonitorRuleSetting
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.mvvm.status.PageLoadState
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.widget.CommonEmptyView
import cool.dingstock.monitor.adapter.item.MonitorRuleChannelItem
import cool.dingstock.monitor.databinding.ActivityMonitorSelectChannelBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@RouterUri(scheme = RouterConstant.SCHEME, host = RouterConstant.HOST, path = [MonitorConstant.Path.MONITOR_SELECT_CHANNEL])
class MonitorSelectChannelActivity: VMBindingActivity<SelectChannelViewModule, ActivityMonitorSelectChannelBinding>() {
    private val channelAdapter by lazy {
        DcBaseBinderAdapter(arrayListOf())
    }

    private val channelItem by lazy {
        MonitorRuleChannelItem()
    }

    override fun moduleTag(): String {
        return ModuleConstant.MONITOR
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        viewBinding.refreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
        with(viewBinding.channelRv) {
            channelItem.setOnItemClickListener { _, _, position ->
                val data = channelAdapter.data[position] as ChannelBean
                UTHelper.commonEvent(UTConstant.Monitor.CustomizeMonitorP_Channel, "ChannelName",data.name)
                DcRouter(MonitorConstant.Uri.MONITOR_SETTING_RULE)
                    .putUriParameter("channel_id", data.id).start()
            }
            channelAdapter.loadMoreModule.isEnableLoadMore = true
            channelAdapter.addItemBinder(ChannelBean::class.java, channelItem)
            channelAdapter.loadMoreModule.setOnLoadMoreListener { 
                viewModel.loadData()
            }
            adapter = channelAdapter
            layoutManager = LinearLayoutManager(this@MonitorSelectChannelActivity)
        }
        with(viewModel) {
            liveDataPostRefresh.observe(this@MonitorSelectChannelActivity) {
                hideLoadingView()
                updateData(it)
            }
            liveDataPostLoadMore.observe(this@MonitorSelectChannelActivity) {
                loadMoreData(it)
            }
            liveDataEndLoadMore.observe(this@MonitorSelectChannelActivity) {
                if (it) {
                    endLoadMore()
                }
            }
            pageLoadState.observe(this@MonitorSelectChannelActivity) {
                hideLoadingView()
                when (it) {
                    is PageLoadState.Error -> {
                        if (it.isRefresh) {
                            showErrorView(it.msg)
                            finishRefresh()
                        } else {
                            finishLoadMore()
                        }
                    }
                    is PageLoadState.Success -> {
                        if (it.isRefresh) {
                            finishRefresh()
                        }
                    }
                    is PageLoadState.Empty -> {
                        if (it.isRefresh) {
                            hideLoadingView()
                            showRvEmpty()
                            finishRefresh()
                        } else {
                            finishLoadMore()
                        }
                    }
                    else -> Unit
                }
            }
        }
        showLoadingView()
        viewModel.refresh()
    }

    override fun initListeners() {
        setOnErrorViewClick {
            showLoadingView()
            viewModel.refresh()
        }
    }

    private fun finishRefresh() {
        viewBinding.refreshLayout.finishRefresh()
    }

    private fun finishLoadMore() {
        channelAdapter.loadMoreModule.apply {
            loadMoreComplete()
            loadMoreEnd(false)
        }
    }

    private fun showRvEmpty() {
        channelAdapter.setList(emptyList())
        channelAdapter.setEmptyView(CommonEmptyView(this).apply {
            mRootView.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        })
    }

    private fun updateData(list: MutableList<ChannelBean>) {
        if (list.isNullOrEmpty()) {
            return
        }
        channelAdapter.apply {
            setList(list)
            loadMoreModule.loadMoreComplete()
        }
    }

    private fun loadMoreData(list: List<ChannelBean>) {
        channelAdapter.loadMoreModule.loadMoreComplete()
        if (list.isNullOrEmpty()) {
            endLoadMore()
            return
        }
        channelAdapter.apply {
            val insertStart = channelAdapter.data.size
            addData(insertStart, list)
        }
    }

    private fun endLoadMore() {
        channelAdapter.loadMoreModule.loadMoreComplete()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateChannel(ruleSetting: EventMonitorRuleSetting) {
        for ((i, channel) in channelAdapter.data.withIndex()) {
            if (channel is ChannelBean && channel.id == ruleSetting.channelId) {
                channel.keywordsSetting = ruleSetting.keywordSetting
                channel.sizesSetting = ruleSetting.sizeSetting
                channelAdapter.notifyItemChanged(i)
                break
            }
        }
    }
}