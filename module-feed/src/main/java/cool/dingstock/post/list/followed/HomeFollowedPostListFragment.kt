package cool.dingstock.post.list.followed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.base.BaseVPLazyFragment
import cool.dingstock.appbase.constant.HomeConstant
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.home.RecommendKolUserEntity
import cool.dingstock.appbase.entity.event.circle.EventFollowerChange
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.post.R
import cool.dingstock.post.databinding.FragmentHomeFollowedPostListBinding
import cool.dingstock.post.databinding.HomeFollowedMoreItemLayoutBinding
import cool.dingstock.post.databinding.HomeFollowedPostListHeaderLayoutBinding
import cool.dingstock.post.item.RecommendUserItemBinder
import cool.dingstock.post.list.HomePostListFragment
import cool.dingstock.post.list.PostConstant
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * 类名：HomeFllowedPostListFragment
 * 包名：cool.dingstock.post.list.followed
 * 创建时间：2021/9/27 4:07 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class HomeFollowedPostListFragment :
        BaseVPLazyFragment<HomeFollowedPostListVm, FragmentHomeFollowedPostListBinding>() {

    var homePostFragment: HomePostListFragment? = null
    var header: HomeFollowedPostListHeaderLayoutBinding? = null

    private val moreFooter by lazy {
        HomeFollowedMoreItemLayoutBinding.inflate(
                LayoutInflater.from(requireContext()),
                rootView as ViewGroup,
                false
        )
    }

    private val recommendUserItemBinder by lazy {
        RecommendUserItemBinder()
    }
    private val recommendUserAdapter by lazy {
        DcBaseBinderAdapter(arrayListOf())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            pageId = bundle.getString(javaClass.name)
        }
        EventBus.getDefault().register(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (childFragmentManager.fragments.size > 0) {
            for (fragment in childFragmentManager.fragments) {
                if (fragment is HomePostListFragment && fragment.postType == PostConstant.PostType.Followed) {
                    if (fragment.postType == PostConstant.PostType.Followed) {
                        homePostFragment = fragment
                        header =
                                HomeFollowedPostListHeaderLayoutBinding.inflate(LayoutInflater.from(context))
                        homePostFragment?.addHeader(header!!.root)
                        homePostFragment?.reload()
                    }
                }
            }
        }
        if (homePostFragment == null) {
            homePostFragment = HomePostListFragment.getInstance(PostConstant.PostType.Followed)
            childFragmentManager.beginTransaction()
                    .add(R.id.content_layer, homePostFragment!!)
                    .commit()
            header = HomeFollowedPostListHeaderLayoutBinding.inflate(LayoutInflater.from(context))
            homePostFragment?.addHeader(header!!.root)
        }
        viewModel.recommendKolUserLiveData.observe(viewLifecycleOwner) {
            header?.root?.hide(it.isEmpty())
            recommendUserAdapter.setList(it)
            homePostFragment?.scroll2Top()
        }
        recommendUserItemBinder.setOnFollowClick { id, followed ->
            UTHelper.commonEvent(UTConstant.Home.HomeP_click_Attention_tab_recommendAttention)
            viewModel.switchFollowState(!followed, id)
        }
        recommendUserItemBinder.setOnItemClickListener { adapter, _, position ->
            (adapter.data[position] as? RecommendKolUserEntity)?.let {
                DcUriRequest(requireContext(), MineConstant.Uri.DYNAMIC)
                        .putUriParameter(MineConstant.PARAM_KEY.ID, it.id)
                        .start()
            }
        }
        header?.root?.isNestedScrollingEnabled = false
        viewBinding.contentLayer.isVerticalScrollBarEnabled = false
        mStatusView?.setOnErrorViewClick {
            viewModel.loadRecommendKolUser()
        }
    }

    override fun lazyInit() {
        super.lazyInit()
        recommendUserAdapter.addItemBinder(recommendUserItemBinder)
        header?.rv?.adapter = recommendUserAdapter
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        header?.rv?.layoutManager = layoutManager
        recommendUserAdapter.addFooterView(moreFooter.root, orientation = LinearLayout.HORIZONTAL)
        moreFooter.root.setOnShakeClickListener {
            UTHelper.commonEvent(UTConstant.Home.HomeP_click_Attention_tab_recommendMore)
            navigationToRecommendFollow()
        }
        viewModel.loadRecommendKolUser()

    }

    private fun navigationToRecommendFollow() {
        DcUriRequest(requireContext(), HomeConstant.Uri.RECOMMEND_FOLLOW)
                .dialogBottomAni()
                .start()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFollowChange(event: EventFollowerChange) {
        recommendUserAdapter.data.forEachIndexed { index, it ->
            (it as? RecommendKolUserEntity)?.let {
                if (it.id == event.userId) {
                    it.followed = event.isFollowed
                    recommendUserAdapter.notifyDataItemChanged(index)
                    return
                }
            }
        }
        if (event.source != EventFollowerChange.Source.HomePageFollow && event.followCount == 0) {
            //刷新数据
            viewModel.loadRecommendKolUser()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    companion object {
        fun getInstance(): HomeFollowedPostListFragment {
            return HomeFollowedPostListFragment()
        }

    }
}