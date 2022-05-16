package cool.dingstock.post.list

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.chad.library.adapter.base.BaseBinderAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.permissionx.guolindev.PermissionX
import cool.dingstock.appbase.adapter.itembinder.OnItemClickListener
import cool.dingstock.appbase.base.BaseVPLazyFragment
import cool.dingstock.appbase.constant.HomeConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.bean.circle.HomeNearbyLocationEntity
import cool.dingstock.appbase.entity.bean.circle.LocationEntityPermission
import cool.dingstock.appbase.entity.bean.home.HomePostData
import cool.dingstock.appbase.entity.event.circle.EventRefresh
import cool.dingstock.appbase.entity.event.relation.EventFollowChange
import cool.dingstock.appbase.mvvm.status.PageLoadState
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LocationHelper
import cool.dingstock.appbase.util.OnLocationResultListener
import cool.dingstock.appbase.viewmodel.HomeIndexViewModel
import cool.dingstock.appbase.widget.DynamicEmptyView
import cool.dingstock.lib_base.stroage.ConfigSPHelper
import cool.dingstock.lib_base.util.CollectionUtils
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.StringUtils
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.post.adapter.DynamicBinderAdapter
import cool.dingstock.post.adapter.holder.DynamicItemViewHolder
import cool.dingstock.post.databinding.HomeFragmentRecommendPostBinding
import cool.dingstock.post.helper.setVideoAutoPlay
import cool.dingstock.post.item.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomePostListFragment : BaseVPLazyFragment<PostViewModel, HomeFragmentRecommendPostBinding>() {

    private lateinit var homeVm: HomeIndexViewModel

    private lateinit var permissionLaunch: ActivityResultLauncher<Intent>

    var postType: String? = null
    private var dyAdapter: DynamicBinderAdapter? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var dynamicItemBinder: DynamicItemBinder? = null
    var postData: HomePostData? = null
    var dataListObserver: Observer<MutableList<CircleDynamicBean>>? = null

    var header: View? = null
    var basicItemClick: (() -> Unit)? = null
    var onNearbyLoadCompleteFun: ((list: ArrayList<HomeNearbyLocationEntity>?) -> Unit)? = null

    var isInitData = false
    var isBasic: Boolean = false

    override fun lazyInit() {
        super.lazyInit()
        // 如果已经加载过数据了不用再次加载了，为了避免 HomeDingChaoFragment刷新的时候
        // 调用 ViewPagerAdapter.notifyDataChange()方法导致本Fragment从新走lazyInit的问题
        // 本来这个fragment不会被重建 ，但是还是调用的lazyInit
        if (isInitData) {
            return
        }
        dyAdapter?.data?.clear()
        //如果首页接口 传过来了数据那就不用再次 请求接口 防止请求两次推荐
        if (postData != null && postData?.posts != null && postData?.posts?.size != 0) {
            viewModel.nextKey = postData?.nextKey
            viewModel.loadType = postData?.type
            postData?.posts?.let {
                newPostList(it)
            }
        } else {
            refresh()
        }
    }

    private fun refresh() {
        if (postType?.equals(PostConstant.PostType.Nearby) == true) {
            requestLocationPermission()
        } else {
            viewModel.loadData()
        }
    }

    override fun reload() {
        super.reload()

        isInitData = true
        if (postData != null && postData?.posts != null && postData?.posts?.size != 0) {
            viewModel.nextKey = postData?.nextKey
            postData?.posts?.let {
                newPostList(it)
            }
        } else {
            refresh()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        homeVm = ViewModelProvider(requireActivity())[HomeIndexViewModel::class.java]
        arguments?.let { bundle ->
            postType = bundle.getString(TYPE)
            pageId = bundle.getString(javaClass.name + TYPE)
            viewModel.type = postType
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewAndEvent()
        asyncUI()
        mStatusView?.setOnErrorViewClick {
            mStatusView?.showLoadingView()
            refresh()
        }
    }

    private fun requestLocationPermission() {
        PermissionX.init(this)
                .permissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                .request { allGranted, _, _ ->
                    if (allGranted) {
                        startLocation()
                    } else {
                        mStatusView?.hideLoadingView()
                        onNearbyLoadCompleteFun?.invoke(arrayListOf())
                        dyAdapter?.setList(arrayListOf(LocationEntityPermission()))
                        dyAdapter?.loadMoreModule?.apply {
                            loadMoreComplete()
                            loadMoreEnd(true)
                        }
                    }
                }
    }

    private fun startLocation() {
        mStatusView?.showLoadingView()
        LocationHelper(requireContext(), object : OnLocationResultListener {
            override fun onLocationResult(location: Location?) {
                viewModel.updateLocation(location)
                viewModel.loadData()
            }

            override fun onLocationChange(location: Location?) {
            }

            override fun onLocationFail() {
                mStatusView?.hideLoadingView()
                mStatusView?.showErrorView("获取定位失败")
                onNearbyLoadCompleteFun?.invoke(arrayListOf())
            }
        }).startLocation()
    }


    private fun asyncUI() {
        viewModel.apply {
            nearbyHeaderLiveData.observe(this@HomePostListFragment) {
                onNearbyLoadCompleteFun?.invoke(it)
            }
            dataListObserver = androidx.lifecycle.Observer {
                newPostList(it)
            }
            dataListObserver?.let {
                dataList.observeForever(it)
            }
            loadmoreList.observe(this@HomePostListFragment) {
                dyAdapter?.loadMoreModule?.loadMoreComplete()
                if (it == null || it.size == 0) {
                    dyAdapter?.loadMoreModule?.loadMoreEnd(false)
                }
                it?.let {
                    addPostList(it)
                }
            }
            pageLoadStateLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is PageLoadState.Empty -> {
                        if (it.isRefresh) {
                            showEmpty()
                        } else {
                            loadMoreComplete()
                        }
                    }
                    is PageLoadState.Error -> {
                        if (it.isRefresh) {
                            Logger.e("testFragment:${mStatusView}")
                            mStatusView?.showErrorView(it.msg)
                        } else {
                            loadMoreComplete()
                            showToast(it.msg)
                        }
                    }
                    else -> {
                    }
                }
            }
        }
        homeVm.homeScrollTop.observe(viewLifecycleOwner) {
            if (it && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                tryToScrollTop()
            }
        }
    }

    private fun loadMoreComplete() {
        dyAdapter?.loadMoreModule?.apply {
            loadMoreEnd(false)
            loadMoreComplete()
            isEnableLoadMoreIfNotFullPage = false
        }
    }

    private fun showEmpty() {
        dyAdapter?.loadMoreModule?.apply {
            isEnableLoadMore = true
            isEnableLoadMoreIfNotFullPage = false
        }
        mStatusView?.showEmptyView("没有数据哦~")
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun refresh(eventRefresh: EventRefresh) {
        if (postType == eventRefresh.type && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            viewModel.loadData()
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        dataListObserver?.let {
            viewModel.dataList.removeObserver(it)
        }
        (rootView?.parent as? ViewGroup)?.removeView(rootView)
        super.onDestroy()
    }

    private fun tryToScrollTop() {
        viewBinding.homeFragmentSecondRv.stopScroll()
        viewBinding.homeFragmentSecondRv.stopNestedScroll()
        val layoutManager = viewBinding.homeFragmentSecondRv.layoutManager
        layoutManager?.scrollToPosition(0)
    }

    private fun initViewAndEvent() {
        viewModel.type = postType
        if (dyAdapter != null) {
            dyAdapter?.registerDynamicReload(lifecycle)
            dyAdapter?.removeAllHeaderView()
            header?.let { dyAdapter?.addHeaderView(it) }
            return
        }
        dyAdapter = DynamicBinderAdapter(arrayListOf())
        dyAdapter?.removeAllHeaderView()
        header?.let { dyAdapter?.addHeaderView(it) }
        dyAdapter?.registerDynamicReload(lifecycle)
        dyAdapter?.setEmptyView(
                DynamicEmptyView(
                        requireContext(),
                        noBg = true,
                        fullParent = true
                )
        )
        mLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        dynamicItemBinder = DynamicItemBinder(requireContext())
        when {
            isBasic -> {
                dynamicItemBinder?.updateShowWhere(PostItemShowWhere.BASIC)
            }
            postType == HomeConstant.Constant.POST_TYPE_RECOMMEND -> {
                dynamicItemBinder?.updateShowWhere(PostItemShowWhere.HomeRecommend)
            }
            postType == PostConstant.PostType.Talk -> {
                dynamicItemBinder?.updateShowWhere(PostItemShowWhere.TalkHomePage)
                dynamicItemBinder?.hideTalkInfo()
            }
            postType == PostConstant.PostType.Deal -> {
                dynamicItemBinder?.updateShowWhere(PostItemShowWhere.DealHomePage)
                dynamicItemBinder?.hideTalkInfo()
            }
            postType == PostConstant.PostType.Followed -> {
                dynamicItemBinder?.updateShowWhere(PostItemShowWhere.FollowHomePage)
            }

            else -> {
                dynamicItemBinder?.updateShowWhere(PostItemShowWhere.Index)
            }
        }
        dyAdapter?.addItemBinder(CircleDynamicBean::class.java, dynamicItemBinder!!)
        dyAdapter?.addItemBinder(
                LocationEntityPermission::class.java,
                LocationPermissionItemBinder().apply {
                    onItemClickListener = object : OnItemClickListener {
                        override fun onItemClick(
                                adapter: BaseBinderAdapter,
                                holder: BaseViewHolder,
                                position: Int
                        ) {
                            val entity = adapter.data[position]
                            if (entity is LocationEntityPermission) {
                                openLocationPermission()
                            }
                        }
                    }
                })
        if (HomeConstant.Constant.POST_TYPE_RECOMMEND == postType) {
            dynamicItemBinder?.onConvertListener =
                    object : OnConvertListener<CircleDynamicBean, DynamicItemViewHolder> {
                        override fun onConvert(h: DynamicItemViewHolder, t: CircleDynamicBean) {
                            //被展示，判断上一个 是否有东西，如果有东西那么久 判定上一个划过了线，被完全展示
                            val currentPosition = dyAdapter?.getDataPosition(h) ?: -1
                            if (currentPosition > 0) {
                                val item =
                                        dyAdapter?.getItem(currentPosition - 1)
                                when (item) {
                                    is CircleDynamicBean -> {
                                        item.let { entity ->
                                            if (entity.isSticky && !entity.isUpDataExposure && !StringUtils.isEmpty(
                                                            entity.id
                                                    )
                                            ) {
                                                viewModel.uploadExposure(entity.id!!)
                                                entity.isUpDataExposure = true
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
        }

        viewBinding.homeFragmentSecondRv.apply {
            (this.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            layoutManager = mLayoutManager
            adapter = dyAdapter
            setVideoAutoPlay(lifecycle)
        }
        dyAdapter?.loadMoreModule?.setOnLoadMoreListener {
            viewModel.loadPost(isLoadMore = true)
        }
        dynamicItemBinder?.topicClickAction = {
            UTHelper.commonEvent(UTConstant.Circle.EnterSource_TopicDetailsP, "position", "首页动态列表")
        }
        if (isBasic) {
            dynamicItemBinder?.basicClickAction = {
                basicItemClick?.invoke()
            }
        }
        initStatusView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        permissionLaunch =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (lacksPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        UTHelper.commonEvent(UTConstant.Home.HomeP_click_Nearby_GoOpen, "result", "失败")
                    } else {
                        UTHelper.commonEvent(UTConstant.Home.HomeP_click_Nearby_GoOpen, "result", "成功")
                    }
                    refresh()
                }
    }

    private fun lacksPermission(permission: String): Boolean {
        return context?.let {
            ContextCompat.checkSelfPermission(
                    it,
                    permission
            )
        } == PackageManager.PERMISSION_DENIED
    }

    /**
     * 打开权限请求
     */
    private fun openLocationPermission() {
        val intent = Intent()
        intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.parse("package:" + requireActivity().packageName)
        permissionLaunch.launch(intent)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventRelationFollow(eventFollowChange: EventFollowChange?) {
        if (dyAdapter?.data.isNullOrEmpty() && postType == PostConstant.PostType.Followed) {
            viewModel.loadData()
        }
    }

    /**
     * 加载更多
     */
    private fun addPostList(data: List<CircleDynamicBean>) {
        mStatusView?.hideLoadingView()
        if (CollectionUtils.isEmpty(data)) {
            return
        }
        dyAdapter?.addData(data)
        dyAdapter?.loadMoreModule?.loadMoreComplete()
    }

    /**
     *刷新数据
     */
    private fun newPostList(data: List<CircleDynamicBean>) {
        dyAdapter?.setList(data)
        dyAdapter?.loadMoreModule?.loadMoreComplete()
        mStatusView?.hideLoadingView()
    }

    private fun showToast(tips: String?) {
        ToastUtil.getInstance().makeTextAndShow(requireContext(), tips, Toast.LENGTH_SHORT)
    }

    fun addHeader(header: View) {
        this.header = header
    }

    fun setTopicId(topicId: String) {
        viewModel.topicId = topicId
    }

    fun setOnNearbyLoadSuccess(onSuccess: (list: ArrayList<HomeNearbyLocationEntity>?) -> Unit) {
        onNearbyLoadCompleteFun = onSuccess
    }

    fun scroll2Top() {
        viewBinding.homeFragmentSecondRv.scrollToPosition(0)
    }

    companion object {
        /**
         * 实例方法传递数据
         *
         * @param postType fragment 类型
         * @see PostConstant
         * @return
         */
        fun getInstance(postType: String?): HomePostListFragment {
            val param = HomePostListFragment()
            val bundle = Bundle()
            bundle.putString(TYPE, postType)
            param.arguments = bundle
            return param
        }

        const val TYPE = "type"
    }
}
