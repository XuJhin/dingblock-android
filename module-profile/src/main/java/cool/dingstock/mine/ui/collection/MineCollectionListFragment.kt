package cool.dingstock.mine.ui.collection

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.shuyu.gsyvideoplayer.GSYVideoManager
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.event.circle.EventCollectChange
import cool.dingstock.appbase.entity.event.update.EventRefreshMineCollectionAndMinePage
import cool.dingstock.appbase.mvp.lazy.VmBindingLazyFragment
import cool.dingstock.appbase.recyclerview.OffsetLinearLayoutManager
import cool.dingstock.mine.R
import cool.dingstock.mine.databinding.FragmentMineCollectionListBinding
import cool.dingstock.post.adapter.DynamicBinderAdapter
import cool.dingstock.post.helper.setVideoAutoPlay
import cool.dingstock.post.item.DynamicItemBinder
import cool.dingstock.post.item.PostItemShowWhere
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class MineCollectionListFragment : VmBindingLazyFragment<MineCollectionViewModel, FragmentMineCollectionListBinding>() {

    private var mineAdapter: DynamicBinderAdapter? = null
    private var layoutManager: OffsetLinearLayoutManager? = null
    private val itemList: ArrayList<CircleDynamicBean> = arrayListOf()
    private var fragmentState = ""

    override fun onLazy() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun initVariables(rootView: View?, container: ViewGroup?, savedInstanceState: Bundle?) {
        fragmentState = this.arguments?.getString(KEY_ID).toString()
        viewModel.isDeal = (fragmentState == "deal")

        context?.let { ctx ->
            val itemBinder = DynamicItemBinder(ctx)
            itemBinder.updateShowWhere(PostItemShowWhere.MineCollection)
            itemBinder.origin = DynamicBinderAdapter.MINE

            mineAdapter = DynamicBinderAdapter(arrayListOf())
            mineAdapter?.registerDynamicReload(lifecycle)
            mineAdapter?.headerWithEmptyEnable = true
            mineAdapter?.loadMoreModule?.setOnLoadMoreListener { viewModel.loadMorePosts() }
            mineAdapter?.updateShowWhere(PostItemShowWhere.MineCollection)
            mineAdapter?.addItemBinder(CircleDynamicBean::class.java, itemBinder)

            layoutManager = OffsetLinearLayoutManager(ctx)
            viewBinding.rv.layoutManager = layoutManager
            viewBinding.rv.adapter = mineAdapter
            viewBinding.rv.setVideoAutoPlay(lifecycle)
        }

        viewBinding.slRefresh.setOnRefreshListener {
            refresh(false)
        }
        refresh(true)
    }

    override fun initBaseViewModelObserver() {
        viewModel.postsRefreshLiveData.observe(this) {
            finishRequest()
            updateList(it, true)
        }
        viewModel.postsLoadMoreLiveData.observe(this) {
            finishRequest()
            updateList(it, false)
        }
        super.initBaseViewModelObserver()
    }

    private fun finishRequest() {
        hideLoadingView()
        viewBinding.slRefresh.finishRefresh()
    }

    override fun initListeners() {
    }

    fun refresh(isFirstLoad: Boolean) {
        if (isFirstLoad) {
            showLoadingView()
        }
        if (!isFirstLoad && GSYVideoManager.instance().playTag == viewBinding.rv.adapter?.hashCode()?.toString()
            && GSYVideoManager.instance().playPosition >= 0) {
            GSYVideoManager.releaseAllVideos()
        }
        viewModel.refreshData()
    }

    private fun updateList(circleDynamicBeans: List<CircleDynamicBean>, isRefresh: Boolean) {
        if (isRefresh) {
            itemList.clear()
            itemList.addAll(circleDynamicBeans)
            mineAdapter?.setList(itemList)
            if (circleDynamicBeans.isEmpty()) {
                showRvEmpty()
            } else {
                mineAdapter?.loadMoreModule?.isEnableLoadMore = true
            }
        } else {
            if (circleDynamicBeans.isEmpty()) {
                finishLoadMore()
            } else {
                itemList.addAll(circleDynamicBeans)
                mineAdapter?.loadMoreModule?.loadMoreComplete()
                mineAdapter?.loadMoreModule?.isEnableLoadMore = true
                mineAdapter?.setList(itemList)
            }
        }
        mineAdapter?.notifyDataSetChanged()
    }

    private fun showRvEmpty() {
        mineAdapter?.setEmptyView(R.layout.common_empty_layout)
        mineAdapter?.loadMoreModule?.isEnableLoadMore = false
    }

    private fun finishLoadMore() {
        mineAdapter?.loadMoreModule?.loadMoreComplete()
        mineAdapter?.loadMoreModule?.loadMoreEnd(false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefresh(event: EventRefreshMineCollectionAndMinePage) {
        refresh(false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefresh(event: EventCollectChange) {
        refresh(false)
    }

    companion object {
        const val KEY_ID = "mine_collection_list"
        fun newInstance(type: String?): MineCollectionListFragment {
            val fragment = MineCollectionListFragment()
            val args = Bundle()
            args.putString(KEY_ID, type)
            fragment.arguments = args
            return fragment
        }
    }
}