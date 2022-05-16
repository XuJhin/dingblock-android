package cool.dingstock.post.list.deal

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.base.BaseVPLazyFragment
import cool.dingstock.appbase.constant.CircleConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.circle.HomeHotTradingEntity
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.post.R
import cool.dingstock.post.databinding.FragmentHomeDealPostListBinding
import cool.dingstock.post.databinding.HotProductItemLayoutBinding
import cool.dingstock.post.databinding.PostHotTradingPostHeaderLayoutBinding
import cool.dingstock.post.list.HomePostListFragment
import cool.dingstock.post.list.PostConstant


class HomeDealPostListFragment :
    BaseVPLazyFragment<HomeDealPostListVM, FragmentHomeDealPostListBinding>() {

    private var homePostFragment: HomePostListFragment? = null

    private val hotTradingVb by lazy {
        PostHotTradingPostHeaderLayoutBinding.inflate(LayoutInflater.from(context), null, false)
    }

    private val hotProductAdapter = DcBaseBinderAdapter(arrayListOf())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (childFragmentManager.fragments.size > 0) {
            for (fragment in childFragmentManager.fragments) {
                if (fragment is HomePostListFragment && fragment.postType == PostConstant.PostType.Deal) {
                    homePostFragment = fragment
                    homePostFragment?.reload()
                }
            }
        }
        if (homePostFragment == null) {
            homePostFragment = HomePostListFragment.getInstance(PostConstant.PostType.Deal)
            childFragmentManager.beginTransaction()
                .add(R.id.deal_content_layer, homePostFragment!!)
                .commit()
        }
        homePostFragment?.addHeader(hotTradingVb.root)
        hotTradingVb.root.hide(true)

        initObserver()
        initListener()
        initHotProductAdapter()

    }


    override fun lazyInit() {
        super.lazyInit()
        viewModel.loadTopicList()
    }

    @SuppressLint("SetTextI18n")
    private fun initListener() {
        mStatusView?.setOnErrorViewClick {
            viewModel.loadTopicList()
        }

        hotTradingVb.hotTradingLayer.setOnShakeClickListener {
            context?.let {
                UTHelper.commonEvent(UTConstant.Home.HomeP_click_TransactionTopic_Entrance,"type","全部")
                DcUriRequest(it, CircleConstant.Uri.FIND_TOPIC_DETAIL)
                    .putUriParameter(
                        CircleConstant.UriParams.TOPIC_DETAIL_ID,
                        MobileHelper.getInstance().configData.dealTalkId
                    )
                    .start()
            }
        }

    }


    private fun initObserver() {
        viewModel.loadPostListViewData.observe(viewLifecycleOwner) {
            MobileHelper.getInstance().configData?.dealTalkId?.let {
                homePostFragment?.setTopicId(it)
                homePostFragment?.reload()
            }
        }
        viewModel.hotTradingLiveDate.observe(viewLifecycleOwner) {
            hotProductAdapter.setList(it)
            hotTradingVb.root.hide(hotProductAdapter.data.isEmpty())
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
        hotTradingVb.apply {
            hotProductRv.adapter = hotProductAdapter
            hotProductRv.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        hotProductAdapter.setList(arrayListOf("", "", ""))
    }

    companion object {
        fun newInstance() =
            HomeDealPostListFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}