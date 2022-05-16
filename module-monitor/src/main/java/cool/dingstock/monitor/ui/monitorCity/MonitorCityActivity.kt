package cool.dingstock.monitor.ui.monitorCity

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseBinderAdapter
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.MonitorConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.entity.bean.monitor.MonitorCitiesEntity
import cool.dingstock.appbase.entity.bean.monitor.MonitorCityEntity
import cool.dingstock.appbase.entity.event.monitor.EventRefreshMonitorOfflineCities
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.monitor.adapter.decoration.MonitorChannelDecoration
import cool.dingstock.monitor.databinding.ActivityMonitorCityBinding
import cool.dingstock.monitor.ui.manager.itemView.MonitorCityItemBinder
import cool.dingstock.monitor.ui.manager.itemView.MonitorProvinceItemBinder
import org.greenrobot.eventbus.EventBus

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [MonitorConstant.Path.MONITOR_CITY]
)
class MonitorCityActivity : VMBindingActivity<MonitorCityViewModel, ActivityMonitorCityBinding>() {

    private var lastSelectedMenu: MonitorCitiesEntity? = null
    private var lastSelectedPosition = -1
    private var mMonitorCityPosition = -1
    private var isRefreshMonitorCities = false

    //left
    private val mMenuAdapter: BaseBinderAdapter by lazy {
        BaseBinderAdapter()
    }

    //right
    private val mChannelAdapter: BaseBinderAdapter by lazy {
        BaseBinderAdapter()
    }

    override fun moduleTag(): String {
        return ModuleConstant.MONITOR
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        val channelId = uri.getQueryParameter(MonitorConstant.UriParam.CHANNEL_ID).toString()
        channelId.let { viewModel.channelId = channelId }
        initAdapterAndRv()
        viewModel.fetchQueryCities(channelId)
    }

    override fun onBackPressed() {
        if (isRefreshMonitorCities) {
            EventBus.getDefault().post(EventRefreshMonitorOfflineCities())
        }
        super.onBackPressed()
    }

    override fun initBaseViewModelObserver() {
        viewModel.apply {
            monitorCitiesLiveData.observe(this@MonitorCityActivity) {
                if (!it.isNullOrEmpty()) {
                    it[0].isSelected = true
                    mMenuAdapter.setList(it)
                    mChannelAdapter.setList(it[0].cities)

                    lastSelectedMenu = it[0]
                    lastSelectedPosition = 0
                    viewModel.currentSelectedTab.value = it[0]
                }
            }
            isUpdateMonitorCitySuccess.observe(this@MonitorCityActivity) {
                isRefreshMonitorCities = it

                if (mChannelAdapter.data.isNotEmpty()
                    && mMonitorCityPosition != -1
                    && mMonitorCityPosition < mChannelAdapter.data.size
                ) {
                    mChannelAdapter.notifyItemChanged(mMonitorCityPosition)
                }
            }
            monitorCityCountLiveData.observe(this@MonitorCityActivity) {
                viewBinding.titleBar.title = "已监控" + it.toString() + "个城市"
            }
        }
        super.initBaseViewModelObserver()
    }

    override fun initListeners() {
        viewBinding.titleBar.setLeftOnClickListener {
            if (isRefreshMonitorCities) {
                EventBus.getDefault().post(EventRefreshMonitorOfflineCities())
            }
            finish()
        }
        mMenuAdapter.setOnItemClickListener { adapter, _, position ->
            if (adapter.data.isEmpty()) {
                return@setOnItemClickListener
            }
            when (val entity = adapter.data[position]) {
                is MonitorCitiesEntity -> {
                    if (entity.isSelected) {
                        return@setOnItemClickListener
                    }
                    if (lastSelectedMenu == null) {
                        lastSelectedMenu = viewModel.currentSelectedTab.value
                    }
                    if (lastSelectedMenu != null) {
                        lastSelectedMenu?.isSelected = false
                    }

                    if (mMenuAdapter.data.isEmpty()) {
                        return@setOnItemClickListener
                    }
                    if (lastSelectedPosition == -1) {
                        (mMenuAdapter.data[0] as MonitorCitiesEntity).isSelected = true
                        lastSelectedPosition = 0
                    }
                    entity.isSelected = !entity.isSelected
                    mMenuAdapter.notifyItemChanged(lastSelectedPosition)
                    lastSelectedMenu = entity
                    lastSelectedPosition = position
                    viewModel.currentSelectedTab.value = entity
                    mMenuAdapter.notifyItemChanged(position)
                    mChannelAdapter.setList(entity.cities)
                }
                else -> {
                }
            }
        }

        mChannelAdapter.setOnItemClickListener { adapter, _, position ->
            if (adapter.data.isEmpty()) {
                return@setOnItemClickListener
            }
            when (val entity = adapter.data[position]) {
                is MonitorCityEntity -> {
                    if (mChannelAdapter.data.isEmpty()) {
                        return@setOnItemClickListener
                    }
                    entity.isSelect = !entity.isSelect
                    mMonitorCityPosition = position
                    viewModel.upDateMonitorCity(entity.name ?: "", entity.isSelect)
                }
                else -> {
                }
            }
        }
    }

    private fun initAdapterAndRv() {
        //left
        mMenuAdapter.apply {
            addItemBinder(MonitorCitiesEntity::class.java, MonitorProvinceItemBinder())
        }
        viewBinding.rvLeft.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mMenuAdapter
        }

        //right
        mChannelAdapter.apply {
            addItemBinder(MonitorCityEntity::class.java, MonitorCityItemBinder())
        }
        //使用GridLayout 通过类型判断占据宽度
        val channelLayoutManager = GridLayoutManager(context, 3)
        channelLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (mChannelAdapter.data.isEmpty()) {
                    return 0
                }
                return when (mChannelAdapter.data[position]) {
                    is MonitorCityEntity -> {
                        1
                    }
                    else -> {
                        3
                    }
                }
            }
        }
        viewBinding.rvRight.apply {
            layoutManager = channelLayoutManager
            adapter = mChannelAdapter
            addItemDecoration(MonitorChannelDecoration())
        }
    }
}