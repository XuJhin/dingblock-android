package cool.dingstock.post.list.topic

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.base.BaseVPLazyFragment
import cool.dingstock.appbase.constant.CircleConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.circle.HomeHotTradingEntity
import cool.dingstock.appbase.entity.bean.circle.HomeTopicEntity
import cool.dingstock.appbase.ext.azDp
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.viewmodel.HomeIndexViewModel
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.post.R
import cool.dingstock.post.databinding.FragmentHomeTopicPostListBinding
import cool.dingstock.post.databinding.HotProductItemLayoutBinding
import cool.dingstock.post.item.HomeHotTopicTitleItemBinder
import cool.dingstock.post.list.HomePostListFragment
import cool.dingstock.post.list.PostConstant
import kotlin.math.max
import kotlin.math.min


class HomeTopicPostListFragment :
    BaseVPLazyFragment<HomeTopicPostListVM, FragmentHomeTopicPostListBinding>() {

    private var homePostFragment: HomePostListFragment? = null
    private val homeIndexViewMode: HomeIndexViewModel by activityViewModels()
    private val staggeredGridLayoutManager: StaggeredGridLayoutManager by lazy {
        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
    }

    private val hotTopicTitleItemBinder: HomeHotTopicTitleItemBinder by lazy {
        HomeHotTopicTitleItemBinder()
    }

    private val hotTopicAdapter by lazy {
        val adapter = DcBaseBinderAdapter(arrayListOf())
        adapter.addItemBinder(hotTopicTitleItemBinder)
        adapter
    }

    private val hotProductAdapter = DcBaseBinderAdapter(arrayListOf())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        viewModel.selTopicId = homeIndexViewMode.homePageTopicSelId
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (childFragmentManager.fragments.size > 0) {
            for (fragment in childFragmentManager.fragments) {
                if (fragment is HomePostListFragment && fragment.postType == PostConstant.PostType.Talk) {
                    homePostFragment = fragment
                    homePostFragment?.reload()
                }
            }
        }
        if (homePostFragment == null) {
            homePostFragment = HomePostListFragment.getInstance(PostConstant.PostType.Talk)
            childFragmentManager.beginTransaction()
                .add(R.id.content_layer, homePostFragment!!)
                .commit()
        }
        viewBinding.rv.adapter = hotTopicAdapter
        viewBinding.rv.layoutManager = staggeredGridLayoutManager
        viewBinding.rv.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                val position = parent.getChildLayoutPosition(view)
                if (position == 0 || position == 1) {
                    outRect.left = 12.azDp.toInt()
                } else {
                    outRect.left = 0
                }
            }
        })

        initObserver()
        initAllHeader()
        initListener()
        initHotProductAdapter()

    }


    override fun lazyInit() {
        super.lazyInit()
        viewModel.loadTopicList()
    }

    private fun initAllHeader() {
        viewBinding.topicLayer.leftContent = viewBinding.scrollLayout
        viewBinding.topicLayer.rightContent = viewBinding.allHeaderLayer
    }

    @SuppressLint("SetTextI18n")
    private fun initListener() {
        mStatusView?.setOnErrorViewClick {
            viewModel.loadTopicList()
        }
        viewBinding.topicLayer.setOnShowedRightTrigger {
            context?.let {
                UTHelper.commonEvent(UTConstant.Home.HomeP_click_Topic_all)
                DcUriRequest(it, CircleConstant.Uri.FIND_TOPIC_ALL)
                    .putUriParameter(CircleConstant.UriParams.ID, "N")
                    .start()
            }
        }

        viewBinding.topicLayer.setOnTextListener { isShow ->
            if (isShow) {
                viewBinding.tvShowAll.text = "释\n放\n跳\n转"
            } else {
                viewBinding.tvShowAll.text = "查\n看\n全\n部"
            }
        }

        viewBinding.topicLayer.setOnRotateListener { angleTo ->
            viewBinding.ivShowAll.rotation = angleTo
        }

        hotTopicTitleItemBinder.setOnItemClickListener { adapter, _, position ->
            (adapter.data[position] as? HomeTopicEntity)?.let {
                if (!it.isSelect) {
                    homePostFragment?.setTopicId(it.id)
                    homePostFragment?.reload()
                    kotlin.run {
                        adapter.data.forEachIndexed { index, any ->
                            (any as? HomeTopicEntity)?.let {
                                if (it.isSelect) {
                                    it.isSelect = false
                                    adapter.notifyItemChanged(index)
                                    return@run
                                }
                            }
                        }
                    }
                    it.isSelect = true
                    homeIndexViewMode.homePageTopicSelId = it.id
                    homeIndexViewMode.homePageSelTopicEntity = it
                    viewModel.selTopicId = it.id
                    adapter.notifyItemChanged(position)
                    scroll2Position(position)
                }
            }
        }

    }


    private fun initObserver() {
        viewModel.topicListLiveData.observe(viewLifecycleOwner) {
            hotTopicAdapter.setList(it)
            viewBinding.topicLayer.hide(it.isEmpty())
        }
        viewModel.loadPostListViewData.observe(viewLifecycleOwner) {
            homeIndexViewMode.homePageTopicSelId = it.id
            homeIndexViewMode.homePageSelTopicEntity = it
            homePostFragment?.setTopicId(it.id)
            homePostFragment?.reload()
        }
        viewModel.scroll2Topic.observe(viewLifecycleOwner) {
            viewBinding.rv.postDelayed({
                scroll2Position(it)
            }, 0)
        }
        viewModel.hotTradingLiveDate.observe(viewLifecycleOwner) {
            hotProductAdapter.setList(it)
        }
    }

    private fun initHotProductAdapter() {
        hotProductAdapter.addItemBinder(
            HomeHotTradingEntity::class.java,
            object : BaseViewBindingItemBinder<HomeHotTradingEntity, HotProductItemLayoutBinding>() {
                override fun provideViewBinding(
                    parent: ViewGroup,
                    viewType: Int
                ): HotProductItemLayoutBinding {
                    return HotProductItemLayoutBinding.inflate(
                        LayoutInflater.from(context),
                        parent,
                        false
                    )
                }

                override fun onConvert(vb: HotProductItemLayoutBinding, data: HomeHotTradingEntity) {
                    vb.iv.load(data.imageUrl)
                    vb.iv.setOnShakeClickListener {
                        UTHelper.commonEvent(UTConstant.Home.HomeP_click_TransactionTopic_Entrance,"type",data.name)
                        DcUriRequest(context, CircleConstant.Uri.FIND_TOPIC_DETAIL)
                            .putUriParameter(CircleConstant.UriParams.PRODUCT_ID, data.id)
                            .putUriParameter(CircleConstant.UriParams.KEYWORD, data.name)
                            .putUriParameter(
                                CircleConstant.UriParams.TOPIC_DETAIL_ID,
                                MobileHelper.getInstance().configData.dealTalkId
                            )
                            .start()
                    }
                }
            })
        hotProductAdapter.setList(arrayListOf("", "", ""))
    }

    private fun scroll2Position(index: Int) {
        val h = viewBinding.rv.findViewHolderForAdapterPosition(index)
        val x = (h?.itemView?.x) ?: 0f
        val width = h?.itemView?.width ?: 0
        val offsetX = (SizeUtils.getWidth() - width) / 2
        val scrollX = max(0f, min(x - offsetX, viewBinding.scrollLayout.maxScrollAmount.toFloat()))
        viewBinding.scrollLayout.scrollTo(scrollX.toInt(), 0)
    }


    override fun onResume() {
        super.onResume()
        hotTopicAdapter.data.forEachIndexed { index, any ->
            (any as? HomeTopicEntity)?.let {
                if (it.isSelect) {
                    scroll2Position(index)
                    return
                }
            }
        }
    }

    companion object {
        fun newInstance() =
            HomeTopicPostListFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}