package cool.dingstock.monitor.ui.manager

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.chad.library.adapter.base.BaseBinderAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.permissionx.guolindev.PermissionX
import cool.dingstock.appbase.adapter.itembinder.OnItemClickListener
import cool.dingstock.appbase.constant.MonitorConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.monitor.ChannelHeaderEntity
import cool.dingstock.appbase.entity.bean.monitor.MonitorMenuItemEntity
import cool.dingstock.appbase.entity.bean.monitor.MonitorRecommendChannelEntity
import cool.dingstock.appbase.entity.bean.monitor.RegionChannelBean
import cool.dingstock.appbase.entity.event.monitor.EventChangeMonitorState
import cool.dingstock.appbase.entity.event.monitor.EventMonitorRuleSetting
import cool.dingstock.appbase.entity.event.monitor.EventRefreshMonitorPage
import cool.dingstock.appbase.entity.event.monitor.MonitorEventSource
import cool.dingstock.appbase.mvp.lazy.VmLazyFragment
import cool.dingstock.appbase.mvvm.status.ViewStatus
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LocationHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.OnLocationResultListener
import cool.dingstock.appbase.widget.CommonEmptyView
import cool.dingstock.appbase.widget.index.IndexSideBar
import cool.dingstock.monitor.R
import cool.dingstock.monitor.adapter.decoration.MonitorChannelDecoration
import cool.dingstock.monitor.ui.manager.itemView.*
import cool.dingstock.monitor.utils.TopSmoothScroller
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * todo 重构
 */
class MonitorManagerFragment : VmLazyFragment<MonitorManagerViewModel>() {
    var channelRv: RecyclerView? = null
    var menuRv: RecyclerView? = null
    var indexBar: IndexSideBar? = null
    private var emptyView: View? = null
    private var errorView: View? = null
    private val sectionList: MutableList<String> = arrayListOf()
    var fragmentPageSource: String? = "default"
    private val indexMap: HashMap<String, Int> = hashMapOf()

    //menu使用的Adapter
    private val mMenuAdapter: BaseBinderAdapter by lazy {
        BaseBinderAdapter()
    }

    //channel使用的Adapter
    private val mChannelAdapter: BaseBinderAdapter by lazy {
        BaseBinderAdapter()
    }
    val channelItemBinder = ChannelOnLineItemBinder()


    private var isTouchIndexSideBar = false

    companion object {
        const val SUBSCRIBED_ID = "subscribed"
        const val PAGE_SOURCE = "PAGE_SOURCE"
        const val TYPE_CODE = "typeCode"
        fun instance(source: String?, typeCode: String?): MonitorManagerFragment {
            val monitorManagerFragment = MonitorManagerFragment()
            source?.let {
                monitorManagerFragment.apply {
                    val bundle = Bundle()
                    bundle.putString(PAGE_SOURCE, source)
                    bundle.putString(TYPE_CODE, typeCode)
                    arguments = bundle
                }
            }
            return monitorManagerFragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.monitor_fragment_edit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        fragmentPageSource = arguments?.getString(PAGE_SOURCE, "default")
        viewModel.typeCode = arguments?.getString(TYPE_CODE)
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshPage(eventMonitor: EventRefreshMonitorPage?) {
        when (eventMonitor?.eventSource) {
            MonitorEventSource.MANAGER -> {
                return
            }
            else -> {
                viewModel.fetchSelectedMenu(viewModel.lastSelectedMenu)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeMonitorState(eventChangeMonitorState: EventChangeMonitorState) {
        if (eventChangeMonitorState.item == null) return
        val position = (eventChangeMonitorState.item as RecyclerView.ViewHolder).adapterPosition
        val list = mChannelAdapter.data
        val targetData = list[position]
        if (targetData is MonitorRecommendChannelEntity) {
            targetData.subscribed = !targetData.subscribed
            mChannelAdapter.notifyItemChanged(position)
        } else if (targetData is RegionChannelBean) {
            list.forEachIndexed { index, item ->
                if (item is RegionChannelBean
                    && item.objectId == eventChangeMonitorState.channelId
                ) {
                    item.subscribed = !item.subscribed
                    mChannelAdapter.notifyItemChanged(index)
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateChannel(ruleSetting: EventMonitorRuleSetting) {
        for ((i, channel) in mChannelAdapter.data.withIndex()) {
            if (channel is MonitorRecommendChannelEntity && channel.id == ruleSetting.channelId) {
                channel.customRuleEffective =
                    ruleSetting.keywordSetting == 1 || ruleSetting.sizeSetting == 1
                mChannelAdapter.notifyItemChanged(i)
                break
            }
        }
    }

    override fun initVariables(
        rootView: View?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        channelRv = rootView?.findViewById(R.id.monitor_edit_channel_rv)
        menuRv = rootView?.findViewById(R.id.monitor_edit_area_rv)
        indexBar = rootView?.findViewById(R.id.monitor_index_bar)
        setupScroll()
        initAdapterAndRv()
        showLoadingView()
        initLocation()
        viewModelObserver()
    }

    private fun setupScroll() {
        indexBar?.setOnSelectIndexItemListener(object : IndexSideBar.OnSelectIndexItemListener {
            override fun onSelectIndexItem(index: String?) {
                val position = indexMap[index]
                scrollToPosition(position)
            }

            override fun onTouch(isTouch: Boolean) {
                isTouchIndexSideBar = isTouch
            }
        })
        channelRv?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mChannelAdapter.data.isNullOrEmpty()) {
                    return
                }
                val layoutManager = recyclerView.layoutManager ?: return
                if (layoutManager is GridLayoutManager) {
                    val lastItemPosition = layoutManager.findLastVisibleItemPosition()
                    val firstItemPosition = layoutManager.findFirstVisibleItemPosition()
                    if (firstItemPosition < 0) {
                        return
                    }
                    val positionEntity = mChannelAdapter.data[firstItemPosition]
                    when (positionEntity) {
                        is ChannelHeaderEntity -> {
                            val char: String = (positionEntity.title?.first() ?: return).toString()
                            if (indexBar?.visibility == View.VISIBLE && !isTouchIndexSideBar) {
                                indexBar?.setSelectedLetter(char)
                            }
                        }
                        is RegionChannelBean -> {
                            if (positionEntity.sectionName.isEmpty()) {
                                return
                            }
                            val char: String = positionEntity.sectionName.first().toString()
                            if (indexBar?.visibility == View.VISIBLE && !isTouchIndexSideBar) {
                                indexBar?.setSelectedLetter(char)
                            }
                        }
                        else -> return
                    }
                }
            }
        })
    }

    private fun scrollToPosition(position: Int?) {
        val mScroller = TopSmoothScroller(requireContext())
        mScroller.targetPosition = position ?: 0
        val layoutManager = channelRv?.layoutManager as GridLayoutManager
        layoutManager.scrollToPositionWithOffset(position ?: 0, 0)
    }

    private fun viewModelObserver() {
        //监控menu同步数据
        viewModel.monitorMenuLiveData.observe(this, androidx.lifecycle.Observer {
            if (it.isNullOrEmpty()) {
                showEmptyView()
                return@Observer
            }
            val menuList = arrayListOf<Any>()
            it.forEach { menuItem ->
                menuItem?.let {
                    if (menuItem.list.isNotEmpty()) {
                        if (menuItem.isShowHeader) {
                            menuList.add(menuItem.header ?: "")
                        }
                        menuList.addAll(menuItem.list)
                    }
                }
            }
            mMenuAdapter.setList(menuList)
        })
        viewModel.userSubscribeLiveData.observe(this, androidx.lifecycle.Observer {
            val groupList = arrayListOf<Any>()
            if (it.isNullOrEmpty()) {
                showChannelEmpty()
                return@Observer
            }
            it.forEach { subscribeItem ->
                subscribeItem.let {
                    if (subscribeItem.data.isNullOrEmpty()) {
                        return@forEach
                    }
                    groupList.add(ChannelHeaderEntity(subscribeItem.title, subscribeItem.type))
                    //区分是线上或者线下
                    for (subscribeItemBean in subscribeItem.data) {
                        //线下订阅
                        groupList.add(subscribeItemBean.filter())
                    }
                }
            }
            showChannelList()
            mChannelAdapter.setList(groupList)
        })
        viewModel.offlineListLiveData.observe(this, androidx.lifecycle.Observer {
            if (it.isNullOrEmpty()) {
                return@Observer
            }
            val groupList = arrayListOf<Any>()
            clearScrollTag()
            it.forEach { monitorOfflineEntity ->
                if (monitorOfflineEntity.data.isNullOrEmpty()) {
                    return@forEach
                }
                val letter = monitorOfflineEntity.title.first().toString()
                sectionList.add(letter)
                groupList.add(
                    ChannelHeaderEntity(
                        monitorOfflineEntity.title,
                        monitorOfflineEntity.type
                    )
                )
                indexMap[letter] = groupList.size - 1
                groupList.addAll(monitorOfflineEntity.data)
            }
            if (viewModel.lastSelectedMenu?.code == MonitorManagerViewModel.CHINA_REGION) {
                indexBar?.setIndexItems(sectionList.toTypedArray())
                showChannelList()
            }
            mChannelAdapter.setList(groupList)
        })
        viewModel.onLineListLiveData.observe(this, androidx.lifecycle.Observer {
            if (it.isNullOrEmpty()) {
                return@Observer
            }
            val groupList = arrayListOf<Any>()
            it.forEach { monitorOnLineEntity ->
                if (monitorOnLineEntity.data.isNullOrEmpty()) {
                    return@forEach
                }
                groupList.add(
                    ChannelHeaderEntity(
                        monitorOnLineEntity.title,
                        monitorOnLineEntity.type
                    )
                )
                groupList.addAll(monitorOnLineEntity.data)
            }
            showChannelList()
            mChannelAdapter.setList(groupList)
        })
        viewModel.indexBarState.observe(this, androidx.lifecycle.Observer {
            if (it) {
                indexBar?.visibility = View.VISIBLE
            } else {
                indexBar?.visibility = View.GONE
            }
        })
        viewModel.groupState.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is ViewStatus.Loading -> {
                }
                is ViewStatus.Error -> {
                    setAdapterEmptyVisibility(false)
                    setChannelErrorVisibility(true)
                }
                is ViewStatus.Empty -> {
                    val selectedMenu = viewModel.lastSelectedMenu
                    if (selectedMenu == null) {
                        setAdapterEmptyVisibility(true)
                        setChannelErrorVisibility(false)
                        return@Observer
                    }
                    if (selectedMenu.type == MonitorManagerViewModel.MENU_TYPE_SUBSCRIBED) {
                        showSubjectEmpty()
                    } else {
                        setAdapterEmptyVisibility(true)
                        setChannelErrorVisibility(false)
                    }
                }
                is ViewStatus.Success -> {
                    showChannelList()
                }
            }
        })
    }

    /**
     * 显示订阅为空页面
     */
    private fun showSubjectEmpty() {
        setAdapterEmptyVisibility(false)
        setChannelErrorVisibility(false)
        mChannelAdapter.setList(emptyList())
        val emptyView = LayoutInflater.from(requireContext())
            .inflate(R.layout.layout_empty_monitor_subject, null, false)
        val commonEmptyView = emptyView.findViewById<CommonEmptyView>(R.id.commonEmptyView)
        commonEmptyView.setText("暂无监控")
        commonEmptyView.getTextView().setTextColor(resources.getColor(R.color.color_text_black1))
        val addMonitor = emptyView.findViewById<TextView>(R.id.empty_add_monitor)
        addMonitor.setOnClickListener {
            val menuList = mMenuAdapter.data
            if (menuList.isNullOrEmpty()) {
                return@setOnClickListener
            }
            var performClickPosition = -1
            run loop@{
                menuList.forEachIndexed { index, item ->
                    if (item is MonitorMenuItemEntity) {
                        if (item.code != MonitorManagerViewModel.MENU_TYPE_SUBSCRIBED) {
                            performClickPosition = index
                            return@loop
                        }
                    }
                }
            }
            if (performClickPosition == -1) {
                return@setOnClickListener
            }
            menuRv?.getChildAt(performClickPosition)?.performClick()
        }
        mChannelAdapter.setEmptyView(emptyView)
    }

    override fun onStatusViewErrorClick() {
        showLoadingView()
        refresh()
    }

    private fun showChannelList() {
        setAdapterEmptyVisibility(false)
        setChannelErrorVisibility(false)
    }

    private fun clearScrollTag() {
        indexMap.clear()
        sectionList.clear()
    }

    /**
     * 关闭默认局部刷新动画
     */
    private fun closeMenuAnimator() {
        menuRv?.itemAnimator?.apply {
            changeDuration = 0
            (this as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }

    private fun initAdapterAndRv() {
        //Menu
        mMenuAdapter.apply {
            addItemBinder(String::class.java, MenuHeaderItemBinder())
            addItemBinder(MonitorMenuItemEntity::class.java, MenuChildItemBinder())
        }
        menuRv?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mMenuAdapter
        }
        //Menu 详情
        mChannelAdapter.apply {
            addItemBinder(ChannelHeaderEntity::class.java, ChannelHeaderItemBinder())
            addItemBinder(RegionChannelBean::class.java, ChannelOfflineItemBinder())
            channelItemBinder.source = "全部频道监控按钮"
            addItemBinder(MonitorRecommendChannelEntity::class.java, channelItemBinder)
        }
        //使用GridLayout 通过类型判断占据宽度
        val channelLayoutManager = GridLayoutManager(requireContext(), 3)
        channelLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (mChannelAdapter.data.isNullOrEmpty()) {
                    return 0
                }
                val item = mChannelAdapter.data[position]
                return when (item) {
                    is RegionChannelBean -> {
                        1
                    }
                    else -> {
                        3
                    }
                }
            }
        }
        channelRv?.apply {
            layoutManager = channelLayoutManager
            adapter = mChannelAdapter
            addItemDecoration(MonitorChannelDecoration())
        }
        closeMenuAnimator()
    }

    private fun initLocation() {
        PermissionX.init(this)
            .permissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
//				.permissions(Manifest.permission.ACCESS_COARSE_LOCATION)
            .onExplainRequestReason { scope, deniedList, _ ->
                scope.showRequestReasonDialog(
                    deniedList, "定位权限是此功能正常运行所依赖的权限，请授予",
                    "明白", "取消"
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "是否前往设置页定位权限",
                    "前往",
                    "取消"
                )
            }
            .request { allGranted, _, _ ->
                if (allGranted) {
                    LocationHelper(requireContext(), object : OnLocationResultListener {
                        override fun onLocationFail() {}
                        override fun onLocationResult(location: Location?) {
                            viewModel.updateLocation(location)
                        }

                        override fun onLocationChange(location: Location?) {
                            viewModel.updateLocation(location)
                        }
                    }).bind(viewLifecycleOwner)
                        .startLocation()
                } else {
                    showToastShort("已拒绝盯链获取定位")
                }
            }
    }

    override fun initListeners() {
        mMenuAdapter.setOnItemClickListener { adapter, view, position ->
            if (adapter.data.isNullOrEmpty()) {
                return@setOnItemClickListener
            }
            val entity = adapter.data[position]
            when (entity) {
                is String -> {
                    return@setOnItemClickListener
                }
                is MonitorMenuItemEntity -> {
                    val selectBean = entity
                    if (selectBean.isSelected) {
                        return@setOnItemClickListener
                    }
                    if (viewModel.lastSelectedMenu == null) {
                        viewModel.lastSelectedMenu = selectBean
                    }

                    if (mMenuAdapter.data.isEmpty()) {
                        return@setOnItemClickListener
                    }
                    if (viewModel.lastSelectedPosition == -1) {
                        run looper@{
                            mMenuAdapter.data.forEachIndexed { index, any ->
                                if (any is MonitorMenuItemEntity && any.code.equals(viewModel.lastSelectedMenu?.code)) {
                                    viewModel.lastSelectedPosition = index
                                    return@looper
                                }
                            }
                        }
                    }
                    (mMenuAdapter.data[viewModel.lastSelectedPosition] as MonitorMenuItemEntity).isSelected =
                        false
                    mMenuAdapter.notifyItemChanged(viewModel.lastSelectedPosition)

                    selectBean.isSelected = !selectBean.isSelected
                    viewModel.lastSelectedMenu = selectBean
                    viewModel.lastSelectedPosition = position
                    mMenuAdapter.notifyItemChanged(position)
                    viewModel.fetchSelectedMenu(selectBean)
                    mMenuAdapter.notifyItemChanged(viewModel.lastSelectedPosition)
                }
                else -> {
                }
            }
        }
        channelItemBinder.onItemClickListener =
            object : OnItemClickListener {
                override fun onItemClick(
                    adapter: BaseBinderAdapter,
                    holder: BaseViewHolder,
                    position: Int
                ) {
                    (adapter.data.get(position) as? MonitorRecommendChannelEntity)?.let {
                        if (LoginUtils.isLoginAndRequestLogin(requireContext())) {
                            UTHelper.commonEvent(
                                UTConstant.Monitor.ChannelP_ent,
                                "source",
                                viewModel.lastSelectedMenu?.name ?: "默认选中",
                                "ChannelName",
                                it.name
                            )
                            UTHelper.commonEvent(
                                UTConstant.Monitor.AllChannelP_click_ChannelList,
                                "ChannelName",
                                it.name
                            )
                            DcUriRequest(requireContext(), MonitorConstant.Uri.MONITOR_DETAIL)
                                .putUriParameter(MonitorConstant.UriParam.CHANNEL_ID, it.objectId)
                                .start()
                        }
                    }
                }
            }
    }

    private fun refresh() {
        if (fragmentPageSource.isNullOrEmpty()
            || fragmentPageSource.equals("default")
        ) {
            viewModel.fetchMonitorMenu(null)
        } else {
            viewModel.fetchMonitorMenu(fragmentPageSource)
        }
    }

    /**
     * 显示订阅为空
     */
    fun setAdapterEmptyVisibility(isVisible: Boolean) {
        if (emptyView == null) {
            emptyView = (rootView.findViewById<View>(R.id.view_empty) as ViewStub).inflate()
        }
        if (isVisible) {
            mChannelAdapter.setList(emptyList())
            emptyView?.visibility = View.VISIBLE
        } else {
            emptyView?.visibility = View.INVISIBLE
        }
    }

    fun setChannelErrorVisibility(isVisible: Boolean) {
        if (errorView == null) {
            errorView = (rootView.findViewById<View>(R.id.view_error) as ViewStub).inflate()
        }
        if (isVisible) {
            mChannelAdapter.setList(emptyList())
            errorView?.visibility = View.VISIBLE
        } else {
            errorView?.visibility = View.INVISIBLE
        }
    }

    fun showChannelEmpty() {
        setAdapterEmptyVisibility(true)
    }

    override fun onLazy() {
        refresh()
    }
}