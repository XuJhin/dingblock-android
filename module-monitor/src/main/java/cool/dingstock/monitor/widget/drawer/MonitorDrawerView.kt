package cool.dingstock.monitor.widget.drawer

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import cool.dingstock.appbase.constant.MonitorConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.customerview.expand.expandablerecyclerview.models.ExpandableGroup
import cool.dingstock.appbase.entity.bean.monitor.SubChannelGroupEntity
import cool.dingstock.appbase.entity.bean.monitor.SubChannelItemEntity
import cool.dingstock.appbase.entity.event.monitor.EventChannelFilter
import cool.dingstock.appbase.ext.doRequest
import cool.dingstock.appbase.net.api.monitor.MonitorApi
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.widget.stateview.StatusView
import cool.dingstock.lib_base.util.TimeUtils
import cool.dingstock.monitor.dagger.MonitorApiHelper
import cool.dingstock.monitor.databinding.MonitorDrawerViewLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

/**
 * 类名：MonitorDrawerView
 * 包名：cool.dingstock.monitor.widget
 * 创建时间：2021/8/30 5:26 下午
 * 创建人： WhenYoung
 * 描述：
 **/
@SuppressLint("ViewConstructor")
class MonitorDrawerView(context: Activity) : FrameLayout(context) {

    @Inject
    lateinit var api: MonitorApi

    var scope: CoroutineScope? = null

    var onClick: (() -> Unit)? = null


    val stateView by lazy {
        StatusView.newBuilder()
                .with(context)
                .rootView(vb.root)
                .build()
    }

    init {
        MonitorApiHelper.apiMonitorComponent.inject(this)
    }

    val vb: MonitorDrawerViewLayoutBinding =
            MonitorDrawerViewLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    var currentExpandableGroup: ExpandableGroup<*, *>? = null
    var currentHeaderPosition = -1

    val mAdapter = DrawerMonitorExpandAdapter(context, arrayListOf())


    init {
        initAdapter()
        initListener()
        stateView.showLoadingView()
    }

    fun initAdapter() {
        vb.apply {
            rv.layoutManager = LinearLayoutManager(context)
            rv.adapter = mAdapter
            rv.itemAnimator?.changeDuration = 0
        }
    }

    fun initListener() {
        mAdapter.setOnGroupClickListener {
            mAdapter.notifyItemChanged(it)
            return@setOnGroupClickListener false
        }
        stateView.setOnErrorViewClick { loadData() }
        mAdapter.setOnChildClick { posotion, it ->
            EventBus.getDefault().post(EventChannelFilter(it.id, it.name, it.customRuleEffective, it.maintaining))
            onClick?.invoke()
        }
        vb.allLayer.setOnShakeClickListener {
            EventBus.getDefault().post(EventChannelFilter(null, "全部频道", isAll = true))
            onClick?.invoke()
        }
        vb.managerLayer.setOnShakeClickListener {
            UTHelper.commonEvent(UTConstant.Monitor.MyMonitorP_click_AddMonitor)
            DcUriRequest(context, MonitorConstant.Uri.MONITOR_MENU_ON_LINE).start()
        }
        vb.monitorLogCard.setOnShakeClickListener {
            UTHelper.commonEvent(UTConstant.Monitor.MonitorP_Log)
            DcUriRequest(context, MonitorConstant.Uri.MONITOR_LOG).start()
        }
        vb.monitorLogLl.setOnShakeClickListener {
            UTHelper.commonEvent(UTConstant.Monitor.MonitorP_Log)
            DcUriRequest(context, MonitorConstant.Uri.MONITOR_LOG).start()
        }
        vb.refreshLayout.setOnRefreshListener {
            loadData()
        }
    }

    fun loadData() {
        scope = CoroutineScope(Dispatchers.Main)
        scope?.doRequest({
            api.getSubChannel()
        }, {
            vb.refreshLayout.isRefreshing = false
            if (!it.err && it.res != null) {
                stateView.hideLoadingView()
                val list =
                        arrayListOf<ExpandableGroup<SubChannelGroupEntity, SubChannelItemEntity>>()
                it.res?.groups?.forEach { groupEntity ->
                    val group = ExpandableGroup(groupEntity.name, groupEntity.channels, groupEntity)
                    group.isExpand = groupEntity.isExpanded == true
                    list.add(group)
                }
                it.res?.devLogs?.let { log ->
                    vb.logExplainTv.text = log.content
                    vb.logTimeTv.text = TimeUtils.formatTimestampM(log.createdAt)
                }
                mAdapter.list.clear()
                mAdapter.list.addAll(list)
                mAdapter.notifyDataSetChanged()
            } else {
                stateView.showErrorView(it.msg)
            }
        }, {
            vb.refreshLayout.isRefreshing = false
            stateView.showErrorView(it.message)
        })
    }

    fun onClose() {
        scope?.cancel()
    }

    fun setOnFilterClick(onClick: () -> Unit) {
        this.onClick = onClick
    }


}