package cool.dingstock.monitor.ui.recommend

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import cool.dingstock.appbase.adapter.LoadMoreBinderAdapter
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.constant.MonitorConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.monitor.MonitorDivider
import cool.dingstock.appbase.entity.bean.monitor.MonitorRecommendChannelEntity
import cool.dingstock.appbase.entity.bean.monitor.MonitorRecommendEntity
import cool.dingstock.appbase.entity.event.monitor.EventChangeMonitorState
import cool.dingstock.appbase.entity.event.monitor.EventRefreshMonitorPage
import cool.dingstock.appbase.entity.event.monitor.MonitorEventSource
import cool.dingstock.appbase.entity.event.update.EventUserVipChange
import cool.dingstock.appbase.ext.azDp
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.mvp.lazy.VmBindingLazyFragment
import cool.dingstock.appbase.mvvm.status.LoadingDialogStatus
import cool.dingstock.appbase.mvvm.status.ViewStatus
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.monitor.databinding.MonitorFragmentRecommendBinding
import cool.dingstock.monitor.ui.recommend.item.HotRecommendChannelItemBinder
import cool.dingstock.monitor.ui.recommend.item.MonitorChannelDivider
import cool.dingstock.monitor.ui.recommend.item.MonitorChannelItemBinder
import cool.dingstock.monitor.ui.recommend.item.MonitorChannelTitleItemBinder
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * 监控 推荐Fragment
 */
class MonitorRecommendFragment : VmBindingLazyFragment<MonitorRecommendViewModel, MonitorFragmentRecommendBinding>() {
    private val monitorRecommendAdapter: LoadMoreBinderAdapter by lazy { LoadMoreBinderAdapter() }
    private val pool = RecycledViewPool()

    private val hotRecommendAdapter by lazy {
        val adapter = DcBaseBinderAdapter(arrayListOf())
        adapter.addItemBinder(hotRecommendItemBinder)
        return@lazy adapter
    }

    private val hotRecommendItemBinder by lazy {
        HotRecommendChannelItemBinder()
    }

    override fun onLazy() {
        viewModel.postStateLoading()
        viewModel.fetchRecommendMonitor()
    }

    override fun reload() {
        super.reload()
        viewModel.fetchRecommendMonitor()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshVipChange(eventMonitor: EventUserVipChange) {
        if (eventMonitor.isChange) {
            monitorRecommendAdapter.notifyDataSetChanged()
            hotRecommendAdapter.notifyDataSetChanged()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshPage(eventMonitor: EventRefreshMonitorPage?) {
        when (eventMonitor?.eventSource) {
            MonitorEventSource.RECOMMEND -> {
                return
            }
            else -> {
                viewModel.fetchRecommendMonitor()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshTargetChannel(eventChangeMonitorState: EventChangeMonitorState) {
        monitorRecommendAdapter.data.forEachIndexed { index, any ->
            if (any is MonitorRecommendChannelEntity && any.objectId == eventChangeMonitorState.channelId) {
                any.subscribed = eventChangeMonitorState.state
                monitorRecommendAdapter.notifyItemChanged(index)
            }
        }

        hotRecommendAdapter.data.forEachIndexed { index, any ->
            if (any is MonitorRecommendChannelEntity && any.objectId == eventChangeMonitorState.channelId) {
                any.subscribed = eventChangeMonitorState.state
                hotRecommendAdapter.notifyItemChanged(index)
            }
        }
    }

    override fun initVariables(
            rootView: View?,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ) {
        monitorRecommendAdapter.apply {
            val monitorChannelItemBinder = MonitorChannelItemBinder()
            monitorChannelItemBinder.source = "推荐频道监控列表"
            addItemBinder(MonitorRecommendEntity::class.java, MonitorChannelTitleItemBinder())
            addItemBinder(MonitorRecommendChannelEntity::class.java, monitorChannelItemBinder,
                    object : DiffUtil.ItemCallback<MonitorRecommendChannelEntity>() {
                        override fun areItemsTheSame(
                                oldItem: MonitorRecommendChannelEntity,
                                newItem: MonitorRecommendChannelEntity
                        ): Boolean {
                            return oldItem.objectId == newItem.objectId
                        }

                        override fun areContentsTheSame(
                                oldItem: MonitorRecommendChannelEntity,
                                newItem: MonitorRecommendChannelEntity
                        ): Boolean {
                            if (oldItem.subscribed != newItem.subscribed) {
                                return false
                            }
                            return true
                        }
                    })
            addItemBinder(MonitorDivider::class.java, MonitorChannelDivider())
        }
        val gridManager = GridLayoutManager(context, 3)
        viewBinding.layoutMonitorRecommendHeaderLayer.hotRecommendRv.apply {
            layoutManager = gridManager
            adapter = hotRecommendAdapter
            itemAnimator?.changeDuration = 0
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    when ((parent.getChildAdapterPosition(view) + 1) % 3) {
                        1 -> {
                            outRect.right = (22f / 3).azDp.toInt()
                        }
                        2 -> {
                            outRect.left = (22f / 6).azDp.toInt()
                            outRect.right = (22f / 6).azDp.toInt()
                        }
                        0 -> {
                            outRect.left = (22f / 3).azDp.toInt()
                        }
                    }
                }
            })
        }

        val manager = LinearLayoutManager(requireContext())
        manager.recycleChildrenOnDetach = true
        viewBinding.monitorRecommendRv.apply {
            layoutManager = manager
            adapter = monitorRecommendAdapter
            setRecycledViewPool(pool)
            itemAnimator?.changeDuration = 0
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        }
        viewBinding.monitorRecommendRefresh.setOnRefreshListener {
            viewModel.fetchRecommendMonitor()
        }
        viewModelObserver()
    }

    override fun initBaseViewModelObserver() {
        viewModel.loadingDialogLiveData.observe(viewLifecycleOwner) { dialogStatus ->
            when (dialogStatus) {
                is LoadingDialogStatus.Loading -> {
                    showLoadingDialog(dialogStatus.msg)
                }
                is LoadingDialogStatus.Success -> {
                    hideLoadingDialog()
                    showSuccessDialog(dialogStatus.msg)
                }
                is LoadingDialogStatus.Error -> {
                    hideLoadingDialog()
                    showFailedDialog(dialogStatus.msg)
                }
                else -> {
                }
            }
        }
        routerObserver = Observer {
            DcRouter(it).start()
        }
        viewModel.routerLiveData.observe(viewLifecycleOwner, routerObserver)
        viewModel.statusViewLiveData.observe(this) { viewStatus ->
            when (viewStatus) {
                is ViewStatus.Loading -> {
                    mStatusView.hideEmptyView()
                    mStatusView.hideErrorView()
                    mStatusView.showLoadingView()
                }
                is ViewStatus.Success -> {
                    mStatusView.hideLoadingView()
                    mStatusView.hideEmptyView()
                    mStatusView.hideErrorView()
                }
                is ViewStatus.Empty -> {
                    mStatusView.hideLoadingView()
                    mStatusView.hideErrorView()
                    monitorRecommendAdapter.setList(emptyList())
                    monitorRecommendAdapter.setEmptyView(cool.dingstock.appbase.R.layout.common_recycler_empty)
                }
                is ViewStatus.Error -> {
                    mStatusView.hideEmptyView()
                    mStatusView.hideLoadingView()
                    mStatusView.showErrorView(viewStatus.msg)
                    mStatusView.setOnErrorViewClick {
                        onStatusViewErrorClick()
                    }
                }
            }
        }
    }

    override fun onStatusViewErrorClick() {
        super.onStatusViewErrorClick()
        showLoadingView()
        viewModel.fetchRecommendMonitor()
    }

    private fun viewModelObserver() {
        viewModel.recommendMonitorLiveData.observe(this, Observer {
            //过滤不为空的数据
            val resultList: MutableList<Any> = arrayListOf()
            if (it.isNullOrEmpty()) {
                showEmptyView()
                return@Observer
            }
            it.forEach { monitorRecommendEntity ->
                if (!monitorRecommendEntity.channelList.isNullOrEmpty()) {
                    resultList.add(monitorRecommendEntity)
                    monitorRecommendEntity.channelList.last().isTail = true
                    resultList.addAll(monitorRecommendEntity.channelList)
                    resultList.add(MonitorDivider())
                }
            }
            monitorRecommendAdapter.setList(resultList)
        })
        viewModel.finisRefreshLiveData.observe(this,  {
            finishRefresh()
        })
        viewModel.hotRecommendMonitorLiveData.observe(this,  {
            setHotRecommend(it)
        })
    }

    private fun finishRefresh() {
        viewBinding.monitorRecommendRefresh.finishRefresh()
    }

    private fun setHotRecommend(entity: MonitorRecommendEntity) {
        viewBinding.layoutMonitorRecommendHeaderLayer.hotTitleTv.text = entity.name
        if (entity.channelList.size == 0) {
            viewBinding.layoutMonitorRecommendHeaderLayer.hotRecommendLayer.hide(true)
        }
        viewBinding.layoutMonitorRecommendHeaderLayer.hotRecommendLayer.setOnShakeClickListener {
            UTHelper.commonEvent(UTConstant.Monitor.RecommendP_click_ThematicEntrance, "TopicName", entity.name)
            DcUriRequest(requireContext(), MonitorConstant.Uri.MONITOR_TOPIC)
                    .putUriParameter("id", entity.id)
                    .start()
        }
        if (entity.channelList.size > 3) {
            hotRecommendAdapter.setList(entity.channelList.subList(0, 3))
        } else {
            hotRecommendAdapter.setList(entity.channelList)
        }
    }

    override fun initListeners() {
    }
}