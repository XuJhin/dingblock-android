package cool.dingstock.home.ui.fashion.index

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseBinderAdapter
import com.shuyu.gsyvideoplayer.GSYVideoManager
import cool.dingstock.appbase.constant.CircleConstant
import cool.dingstock.appbase.constant.HomeBusinessConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.RequestState
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.bean.home.HomeCategoryBean
import cool.dingstock.appbase.entity.bean.home.fashion.FashionEntity
import cool.dingstock.appbase.layoutmanager.CenterLayoutManager
import cool.dingstock.appbase.mvp.lazy.VmBindingLazyFragment
import cool.dingstock.appbase.mvvm.status.PageLoadState
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.util.isWhiteMode
import cool.dingstock.appbase.viewmodel.HomeIndexViewModel
import cool.dingstock.appbase.widget.CommonEmptyView
import cool.dingstock.home.R
import cool.dingstock.home.adapter.NewSaleAdapter
import cool.dingstock.home.adapter.item.AllFashionBrandItem
import cool.dingstock.home.adapter.item.FashionBrandItem
import cool.dingstock.home.adapter.item.NewSaleItem
import cool.dingstock.home.databinding.AllFashionBrandLayoutBinding
import cool.dingstock.home.databinding.HomeFragmentFashionLayoutBinding
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.post.helper.setVideoAutoPlay

class HomeFashionFragment: VmBindingLazyFragment<FashionViewModel, HomeFragmentFashionLayoutBinding>() {
    companion object {
        @JvmStatic
        fun getInstance(homeCategoryBean: HomeCategoryBean?): HomeFashionFragment? {
            if (null == homeCategoryBean) {
                return null
            }
            val fragment = HomeFashionFragment()
            val bundle = Bundle()
            bundle.putParcelable(HomeBusinessConstant.KEY_CATEGORY, homeCategoryBean)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            if (it.isWhiteMode()) StatusBarUtil.setDarkMode(it)
        }
    }

    override fun onPause() {
        super.onPause()
        activity?.let {
            if (it.isWhiteMode()) StatusBarUtil.setLightMode(it)
        }
    }

    private var fashionList: MutableList<Any> = arrayListOf()
    private val brandAdapter by lazy {
        BaseBinderAdapter(fashionList)
    }

    private val brandBinder by lazy {
        FashionBrandItem()
    }

    private val allBrandAdapter by lazy {
        BaseBinderAdapter(fashionList)
    }

    private val allBrandBinder by lazy {
        AllFashionBrandItem()
    }

    private val allBrandPop by lazy {
        initAllBrandPop()
    }

    private val brandLayoutManager by lazy {
        CenterLayoutManager(context, CenterLayoutManager.HORIZONTAL, false)
    }

    val postList: ArrayList<Any> = arrayListOf()
    private val newSaleAdapter by lazy {
        NewSaleAdapter(postList).apply {
            registerDynamicReload(lifecycle)
        }
    }

    private val newSaleBinder by lazy {
        NewSaleItem()
    }

    private var bgHeight = 0
    private var firstBgUrl: String? = null

    private val homeIndexViewModel by lazy { ViewModelProvider(requireActivity())[HomeIndexViewModel::class.java] }

    override fun initVariables(
        rootView: View?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        viewBinding.fashionRefresh.apply {
            val lp = layoutParams as FrameLayout.LayoutParams
            lp.topMargin = SizeUtils.dp2px(40f) + SizeUtils.getStatusBarHeight(requireContext())
            layoutParams = lp
        }
        viewBinding.rvFashionBrand.post {
            val location = IntArray(2)
            viewBinding.rvFashionPost.getLocationOnScreen(location)
            bgHeight = location[1]
            viewBinding.ivBg.layoutParams.height = bgHeight
            viewBinding.bgMask.layoutParams.height = bgHeight
        }
        initRv()
        showLoadingView()
        with(viewModel) {
            takeId.observe(viewLifecycleOwner) {
                viewModel.refresh()
            }
            refreshResult.observe(viewLifecycleOwner) {
                hideLoadingView()
                setBrandList(it)
            }
            liveDataPostRefresh.observe(viewLifecycleOwner) {
                updateData(it)
            }
            liveDataPostLoadMore.observe(viewLifecycleOwner) {
                loadMoreData(it)
            }
            liveDataEndLoadMore.observe(viewLifecycleOwner) {
                if (it) {
                    endLoadMore()
                }
            }
            pageLoadState.observe(viewLifecycleOwner) {
                when (it) {
                    is PageLoadState.Loading -> {
                    }
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
                            viewBinding.rvFashionPost.scrollToPosition(0)
                        }
                    }
                    is PageLoadState.Empty -> {
                        if (it.isRefresh) {
                            showRvEmpty()
                            finishRefresh()
                        } else {
                            finishLoadMore()
                        }
                    }
                }
            }
            requestState.observe(viewLifecycleOwner) {
                finishRefresh()
                when (it) {
                    is RequestState.Error -> {
                        showErrorView()
                        showErrorView(it.errorMsg)
                    }
                    else -> { }
                }
            }
        }
        homeIndexViewModel.fashionScrollTop.observe(viewLifecycleOwner) {
            if (!it || !super.getPageVisible()) {
                return@observe
            }
            tryToScrollToTop()
        }

    }

    override fun initListeners() {
        viewBinding.fashionRefresh.setOnRefreshListener {
            if (GSYVideoManager.instance().playTag == viewBinding.rvFashionPost.adapter?.hashCode()?.toString() &&
                GSYVideoManager.instance().playPosition >= 0) {
                GSYVideoManager.releaseAllVideos()
            }
            refreshData(true)
        }
        setOnErrorViewClick {
            if (viewModel.requestState.value is RequestState.Error) {
                showLoadingView()
                viewModel.fetchAllFashionBrand()
            } else {
                refreshData(false)
            }
        }
        viewBinding.allBrand.setOnClickListener {
            if (fashionList.isEmpty()) {
                viewModel.fetchAllFashionBrand()
            }
            if (!allBrandPop.isShowing) {
                allBrandPop.showAtLocation(it, Gravity.TOP, 0, 0)
                requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                setWindowAlpha(1f, 0.5f)
                StatusBarUtil.setLightMode(requireActivity())
            }
        }
    }

    override fun onLazy() {
        showLoadingView()
        viewModel.fetchAllFashionBrand()
    }

    private fun initRv() {
        viewBinding.run {
            brandBinder.setOnItemClickListener { _, _, position ->
                with(fashionList[position] as FashionEntity) {
                    UTHelper.commonEvent(UTConstant.Fashion.TrendyListP_click_Brand, name)
                    viewModel.takeId.postValue(talkId)
                    viewBinding.tvIntroduce.text = simpleDesc
                    viewBinding.ivBg.let { Glide.with(requireContext()).load(bgUrl).into(it) }
                }
                for ((pos, item) in fashionList.withIndex()) {
                    (item as FashionEntity).selected = pos == position
                }
                brandAdapter.notifyDataSetChanged()
                brandLayoutManager.smoothScrollToPosition(
                    viewBinding.rvFashionBrand,
                    RecyclerView.State(),
                    position
                )
            }
            brandAdapter.addItemBinder(FashionEntity::class.java, brandBinder)
            rvFashionBrand.layoutManager = brandLayoutManager
            rvFashionBrand.adapter = brandAdapter

            newSaleBinder.setOnItemClickListener { adapter, _, position ->
                val entity = adapter.getItem(position)
                if (entity is CircleDynamicBean) {
                    navigationToPostDetail(entity)
                }
            }
            newSaleAdapter.headerWithEmptyEnable = true
            newSaleAdapter.loadMoreModule.isEnableLoadMore = true
            newSaleAdapter.loadMoreModule.setOnLoadMoreListener {
                viewModel.fetchMorePost()
            }
            newSaleAdapter.addItemBinder(CircleDynamicBean::class.java, newSaleBinder)
            rvFashionPost.layoutManager = LinearLayoutManager(requireContext())
            rvFashionPost.adapter = newSaleAdapter
            rvFashionPost.setVideoAutoPlay(lifecycle)
            initScroll()
        }
    }

    private fun initScroll() {
        viewBinding.rvFashionPost.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lm = viewBinding.rvFashionPost.layoutManager as? LinearLayoutManager
                homeIndexViewModel.fashionIsSHowRocket = (lm?.findFirstVisibleItemPosition()?:0)>0
            }
        })
    }

    private fun initAllBrandPop(): PopupWindow {
        return PopupWindow().apply {
            setBackgroundDrawable(null)
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            width = ViewGroup.LayoutParams.MATCH_PARENT
            isClippingEnabled = false //入侵标题栏
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                setIsLaidOutInScreen(true)
            }
            isOutsideTouchable = true
            isFocusable = true
            update()
            animationStyle = R.style.top_dialog_animation
            val popBinding = AllFashionBrandLayoutBinding.inflate(layoutInflater).apply {
                contentCl.setPadding(0, SizeUtils.getStatusBarHeight(requireContext()), 0, SizeUtils.dp2px(20f))
                ivClose.setOnClickListener {
                    dismiss()
                }
                allBrandBinder.setOnItemClickListener { _, _, position ->
                    with(fashionList[position] as FashionEntity) {
                        dismiss()
                        UTHelper.commonEvent(UTConstant.Fashion.TrendyListP_click_Brand, name)
                        viewModel.takeId.postValue(talkId)
                        viewBinding.tvIntroduce.text = simpleDesc
                        viewBinding.ivBg.let { Glide.with(requireContext()).load(bgUrl).into(it) }
                        for ((pos, item) in fashionList.withIndex()) {
                            (item as FashionEntity).selected = pos == position
                        }
                        brandLayoutManager.smoothScrollToPosition(
                            viewBinding.rvFashionBrand,
                            RecyclerView.State(),
                            position
                        )
                        brandAdapter.notifyDataSetChanged()
                    }
                }
                allBrandAdapter.addItemBinder(FashionEntity::class.java, allBrandBinder)
                rvAllBrand.layoutManager = GridLayoutManager(requireContext(), 5, RecyclerView.VERTICAL, false)
                rvAllBrand.adapter = allBrandAdapter
            }
            setOnDismissListener {
                requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                setWindowAlpha(0.5f, 1f)
                StatusBarUtil.setDarkMode(requireActivity())
            }
            contentView = popBinding.root
        }
    }

    private fun setWindowAlpha(start: Float, end: Float) {
        ValueAnimator.ofFloat(start, end).apply {
            duration = 300
            addUpdateListener {
                requireActivity().window.attributes = requireActivity().window.attributes.apply {
                    alpha = it.animatedValue as Float
                }
            }
        }.start()
    }

    private fun tryToScrollToTop() {
        viewBinding.rvFashionPost.stopScroll()
        val layoutManager = viewBinding.rvFashionPost.layoutManager ?: return
        layoutManager.scrollToPosition(0)
    }

    private fun refreshData(isRefresh: Boolean) {
        if (!isRefresh) {
            showLoadingView()
        }
        viewModel.refresh()
    }

    private fun setBrandList(brandList: MutableList<FashionEntity>?) {
        fashionList.clear()
        brandList?.let { fashionList.addAll(it) }
        brandAdapter.notifyDataSetChanged()
        for ((i, item) in fashionList.withIndex()) {
            if ((item as FashionEntity).selected) {
                viewBinding.tvIntroduce.text = item.simpleDesc
                firstBgUrl = item.bgUrl
                firstBgUrl?.let {
                    Glide.with(requireContext()).load(it).into(viewBinding.ivBg)
                    firstBgUrl = null
                }
                brandLayoutManager.smoothScrollToPosition(
                    viewBinding.rvFashionBrand,
                    RecyclerView.State(), i
                )
            }
        }
        allBrandAdapter.notifyDataSetChanged()
    }

    private fun finishRefresh() {
        viewBinding.fashionRefresh.finishRefresh()
    }

    private fun finishLoadMore() {
        newSaleAdapter.loadMoreModule.apply {
            loadMoreComplete()
            loadMoreEnd(false)
        }
    }

    private fun showRvEmpty() {
        newSaleAdapter.setList(emptyList())
        context?.let {
            newSaleAdapter.setEmptyView(CommonEmptyView(it).apply {
                tv.setTextColor(Color.WHITE)
            })
        }
    }

    private fun updateData(list: MutableList<CircleDynamicBean>) {
        if (list.isNullOrEmpty()) {
            return
        }
        postList.clear()
        newSaleAdapter.apply {
            setList(list)
            loadMoreModule.loadMoreComplete()
            loadMoreModule.isEnableLoadMore = true
        }
    }

    private fun loadMoreData(list: List<CircleDynamicBean>) {
        if (list.isNullOrEmpty()) {
            newSaleAdapter.loadMoreModule.loadMoreComplete()
            endLoadMore()
            return
        }
        newSaleAdapter.apply {
            val insertStart = postList.size
            addData(insertStart, list)
            loadMoreModule.loadMoreComplete()
        }
    }

    private fun endLoadMore() {
        newSaleAdapter.loadMoreModule.loadMoreComplete()
        newSaleAdapter.loadMoreModule.isEnableLoadMore = false
    }

    private fun navigationToPostDetail(entity: CircleDynamicBean) {
        UTHelper.commonEvent(UTConstant.Fashion.TrendyListP_click_Allcard)
        DcUriRequest(requireContext(), CircleConstant.Uri.CIRCLE_DETAIL)
            .putUriParameter(CircleConstant.UriParams.ID, entity.id ?: "")
            .putExtra(CircleConstant.UriParams.DETAIL_FROM_FASHION, entity.isFashion)
            .putExtra(CircleConstant.UriParams.DETAIL_FROM_FASHION_LIST, true)
            .start()
    }
}