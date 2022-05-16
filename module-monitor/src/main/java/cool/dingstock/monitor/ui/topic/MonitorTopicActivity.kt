package cool.dingstock.monitor.ui.topic

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseBinderAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.appbar.AppBarLayout
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.adapter.LoadMoreBinderAdapter
import cool.dingstock.appbase.adapter.itembinder.OnItemClickListener
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.MonitorConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.monitor.MonitorRecommendChannelEntity
import cool.dingstock.appbase.entity.bean.monitor.MonitorRecommendEntity
import cool.dingstock.appbase.entity.bean.monitor.RegionChannelBean
import cool.dingstock.appbase.entity.event.account.EventIsAuthorized
import cool.dingstock.appbase.entity.event.monitor.EventChangeMonitorState
import cool.dingstock.appbase.entity.event.monitor.EventRefreshMonitorPage
import cool.dingstock.appbase.entity.event.monitor.MonitorEventSource
import cool.dingstock.appbase.entity.event.update.EventUserVipChange
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.util.isDarkMode
import cool.dingstock.appbase.util.isWhiteMode
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.monitor.R
import cool.dingstock.monitor.databinding.ActivityMonitorTopicBinding
import cool.dingstock.monitor.ui.topic.item.SubjectDetailChannelItemBinder
import io.cabriole.decorator.LinearDividerDecoration
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.absoluteValue
import kotlin.math.min

/**
 * 监控话题
 */
@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [MonitorConstant.Path.MONITOR_TOPIC]
)
class MonitorTopicActivity :
    VMBindingActivity<MonitorTopicViewModel, ActivityMonitorTopicBinding>() {
    companion object {
        private val TOTAL_DISTANCE: Float = 107.dp
    }

    private var subjectId: String = ""
    private val monitorAdapter: LoadMoreBinderAdapter by lazy { LoadMoreBinderAdapter() }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeMonitorState(eventChangeMonitorState: EventChangeMonitorState) {
        if (eventChangeMonitorState.item == null) return
        val position = (eventChangeMonitorState.item as RecyclerView.ViewHolder).adapterPosition
        val list = monitorAdapter.data
        val targetData = list[position]
        if (targetData is MonitorRecommendChannelEntity) {
            targetData.subscribed = !targetData.subscribed
            monitorAdapter.notifyItemChanged(position)
        } else if (targetData is RegionChannelBean) {
            list.forEachIndexed { index, item ->
                if (item is RegionChannelBean
                    && item.objectId == eventChangeMonitorState.channelId
                ) {
                    item.subscribed = !item.subscribed
                    monitorAdapter.notifyItemChanged(index)
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshPage(eventMonitor: EventRefreshMonitorPage?) {
        when (eventMonitor?.eventSource) {
            MonitorEventSource.SUBJECT -> {
                return
            }
            else -> {
                refresh(false)
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshVipChange(eventMonitor: EventUserVipChange) {
        monitorAdapter.notifyDataSetChanged()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun userLogin(event: EventIsAuthorized) {
        monitorAdapter.notifyDataSetChanged()
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        viewBinding.appBar.apply {
            val totalDistance = TOTAL_DISTANCE
            val topBarColor = getCompatColor(R.color.white)
            val iconBack = getCompatColor(R.color.color_text_black1)
            addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                val scrollY = verticalOffset.absoluteValue
                val scale = scrollY / totalDistance
                val alpha = (scale * 255).toInt()
                val alphaColor =
                    Color.argb(
                        min(alpha, 255),
                        topBarColor.red,
                        topBarColor.green,
                        topBarColor.blue
                    )
                val backColor = Color.argb(
                    min(alpha, 255),
                    iconBack.red,
                    iconBack.green,
                    iconBack.blue
                )
                viewBinding.layoutToolbar.setBackgroundColor(alphaColor)
                if (isWhiteMode()) {
                    viewBinding.titleBar.setLeftIconColor(backColor)
                }
                when {
                    scrollY > totalDistance -> {
                        viewBinding.titleBar.titleView.alpha = 1f
                    }
                    scrollY <= 0 -> {
                        viewBinding.titleBar.setLeftIconColorRes(R.color.color_text_absolutely_white)
                        viewBinding.titleBar.setTitleAlpha(0f)
                        viewBinding.titleBar.titleView.alpha = 0f
                    }
                    else -> {
                        viewBinding.titleBar.titleView.alpha = scale
                    }
                }
            })
        }
        if (isWhiteMode()) {
            viewBinding.titleBar.setLeftIconColorRes(R.color.white)
        }
        viewBinding.layoutToolbar.setBackgroundResource(R.color.transparent)
        val itemBinder = SubjectDetailChannelItemBinder()
        itemBinder.source = "专题页频道列表"
        itemBinder.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(
                adapter: BaseBinderAdapter,
                holder: BaseViewHolder,
                position: Int
            ) {
                if (!LoginUtils.isLoginAndRequestLogin(context)) {
                    return
                }
                (adapter.data[position] as? MonitorRecommendChannelEntity)?.let {
                    DcRouter(MonitorConstant.Uri.MONITOR_DETAIL)
                        .putUriParameter(
                            MonitorConstant.UriParam.CHANNEL_ID, it.objectId
                                ?: it.id
                        )
                        .start()
                    UTHelper.commonEvent(
                        UTConstant.Monitor.ChannelP_ent,
                        "source",
                        "推荐主页",
                        "ChannelName",
                        it.name
                    )
                }
            }
        }
        monitorAdapter.addItemBinder(MonitorRecommendChannelEntity::class.java, itemBinder)
        //添加一个footer
        val height = SizeUtils.getNavigationBarHeight() + 10.dp.toInt()
        val view = View(context)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        monitorAdapter.addFooterView(view)
        viewBinding.monitorTopicRv.apply {
            adapter = monitorAdapter
            layoutManager = LinearLayoutManager(this@MonitorTopicActivity)
            addItemDecoration(
                LinearDividerDecoration.create(
                    color = ContextCompat.getColor(context, R.color.transparent),
                    size = SizeUtils.dp2px(9f),
                    orientation = RecyclerView.VERTICAL
                )
            )
        }
        viewBinding.monitorTopicRefresh.setOnRefreshListener {
            refresh(false)
        }
        viewModelObserver()
        refresh(isFirstLoad = true)
    }

    private fun viewModelObserver() {
        viewModel.subjectDetailLiveData.observe(this, Observer {
            updateUI(it)
            finishRefresh()
            if (it.channelList.isNullOrEmpty()) {
                monitorAdapter.setList(emptyList())
                monitorAdapter.setEmptyView(R.layout.common_recycler_empty)
                return@Observer
            }
            val testList = arrayListOf<MonitorRecommendChannelEntity>()
            testList.addAll(it.channelList)
            monitorAdapter.setList(testList)
        })
    }

    private fun finishRefresh() {
        viewBinding.monitorTopicRefresh.finishRefresh()
    }

    private fun updateUI(monitorRecommendEntity: MonitorRecommendEntity?) {
        monitorRecommendEntity?.let {
            viewBinding.recommendMonitorSubjectTitle.text = it.name
            viewBinding.recommendMonitorSubjectDesc.text = it.desc
            viewBinding.titleBar.title = it.name
            viewBinding.topicIvBg.load(it.imageUrl)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun initBundleData() {
        super.initBundleData()
        subjectId = intent.data?.getQueryParameter("id") ?: ""
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun setSystemStatusBar() {
        StatusBarUtil.transparentStatus(this)
        if (isDarkMode()) {
            StatusBarUtil.setDarkMode(this)
        } else {
            StatusBarUtil.setLightMode(this)
        }
    }

    override fun fakeStatusView(): View {
        return viewBinding.fakeLayout.fakeStatusBar
    }

    override fun initListeners() {
    }

    override fun moduleTag(): String {
        return ModuleConstant.MONITOR
    }

    override fun onStatusViewErrorClick() {
        refresh(true)
    }

    fun refresh(isFirstLoad: Boolean = false) {
        if (isFirstLoad) {
            viewModel.postStateLoading()
        }
        viewModel.fetchSubjectDetail(subjectId)
    }
}