package cool.dingstock.post.list.recommend

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.base.BaseVPLazyFragment
import cool.dingstock.appbase.constant.CircleConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.home.HomePostData
import cool.dingstock.appbase.entity.bean.home.HotRankItemEntity
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.post.R
import cool.dingstock.post.databinding.*
import cool.dingstock.post.list.HomePostListFragment
import cool.dingstock.post.list.PostConstant


class HomeRecommendListFragment :
        BaseVPLazyFragment<HomeRecommendPostListVM, FragmentHomeDealPostListBinding>() {

    private var homePostFragment: HomePostListFragment? = null
    var postData: HomePostData? = null
    private val hotListVb by lazy {
        PostHotListHeaderLayoutBinding.inflate(LayoutInflater.from(context), null, false)
    }

    private val hotRankAdapter = DcBaseBinderAdapter(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (childFragmentManager.fragments.size > 0) {
            for (fragment in childFragmentManager.fragments) {
                if (fragment is HomePostListFragment && fragment.postType == PostConstant.PostType.Recommend) {
                    homePostFragment = fragment
                    homePostFragment?.reload()
                }
            }
        }
        if (homePostFragment == null) {
            homePostFragment = HomePostListFragment.getInstance(PostConstant.PostType.Recommend)

            homePostFragment?.postData = postData

            childFragmentManager.beginTransaction()
                    .add(R.id.deal_content_layer, homePostFragment!!)
                    .commit()
        }
        homePostFragment?.addHeader(hotListVb.root)
        hotListVb.root.hide(true)

        initObserver()
        initListener()
        initHotProductAdapter()
    }

    override fun lazyInit() {
        super.lazyInit()
        viewModel.loadHotRankList()
    }

    @SuppressLint("SetTextI18n")
    private fun initListener() {
        mStatusView?.setOnErrorViewClick {
            viewModel.loadHotRankList()
        }
    }

    private fun initObserver() {
        viewModel.loadPostListViewData.observe(viewLifecycleOwner) {
            MobileHelper.getInstance().configData?.dealTalkId?.let {
                homePostFragment?.setTopicId(it)
                homePostFragment?.reload()
            }
        }
        viewModel.hotRankListLiveData.observe(viewLifecycleOwner) {
            hotListVb.apply {
                root.hide(it.isNullOrEmpty())
                viewBg.hide(it.size <= 2)
                viewLine.hide(it.size <= 2)
                ivSmallLogo.hide(it.size > 2)
                ivBigLogo.hide(it.size <= 2)
            }
            hotRankAdapter.setList(it)
        }
    }

    private fun initHotProductAdapter() {
        hotRankAdapter.addItemBinder(
                HotRankItemEntity::class.java,
                object : BaseViewBindingItemBinder<HotRankItemEntity, PostHotListItemLayoutBinding>() {
                    override fun provideViewBinding(
                            parent: ViewGroup,
                            viewType: Int
                    ): PostHotListItemLayoutBinding {
                        return PostHotListItemLayoutBinding.inflate(
                                LayoutInflater.from(context),
                                parent,
                                false
                        )
                    }

                    override fun onConvert(
                            vb: PostHotListItemLayoutBinding,
                            data: HotRankItemEntity
                    ) {
                        vb.apply {
                            tvName.text = data.title
                            ivLogo.hide(data.icon.isNullOrEmpty())
                            if (!data.icon.isNullOrEmpty()) {
                                ivLogo.load(data.icon)
                            }
                            root.setOnShakeClickListener {
                                UTHelper.commonEvent(
                                        UTConstant.Home.HotList_Details,
                                        "name",
                                        data.title ?: ""
                                )
                                DcUriRequest(context, CircleConstant.Uri.HOT_RANK_DETAIL)
                                        .putUriParameter(CircleConstant.UriParams.ID, data.id)
                                        .start()
                            }
                        }
                    }
                })
        hotListVb.apply {
            rv.adapter = hotRankAdapter
            rv.layoutManager =
                    GridLayoutManager(context, 2)
        }
    }

    companion object {
        fun newInstance() =
                HomeRecommendListFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}