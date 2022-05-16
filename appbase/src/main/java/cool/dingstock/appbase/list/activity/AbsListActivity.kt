package cool.dingstock.appbase.list.activity

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import cool.dingstock.appbase.R
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.list.AbsListViewModel
import cool.dingstock.appbase.mvvm.activity.VMActivity
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.mvvm.status.PageLoadState


/**
 * 列表Activity的简单封装
 *已处理好加载更多等逻辑
 */
abstract class AbsListActivity<VM : AbsListViewModel,VB:ViewBinding> : VMBindingActivity<VM,VB>() {

    lateinit var pageAdapter: DcBaseBinderAdapter

    open fun setupAdapter() {
        pageAdapter = DcBaseBinderAdapter(ArrayList())
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        setupAdapter()
        setUpAdapterLoadMore()
        bindItemView()
        initPageStateObserver()
    }

    private fun initPageStateObserver() {
        viewModel.pageLoadStateLiveData.observe(this, Observer { viewStatus ->
            when (viewStatus) {
                is PageLoadState.Loading -> {
                    if (viewStatus.isRefresh) {
                        mStatusView?.hideEmptyView()
                        mStatusView?.hideErrorView()
                        mStatusView?.showLoadingView()
                    } else {
                        return@Observer
                    }
                }
                is PageLoadState.Success -> {
                    mStatusView?.hideLoadingView()
                    mStatusView?.hideEmptyView()
                    mStatusView?.hideErrorView()
                }
                is PageLoadState.Empty -> {
                    if (viewStatus.isRefresh) {
                        mStatusView?.hideLoadingView()
                        mStatusView?.hideErrorView()
                        mStatusView?.showEmptyView(viewStatus.msg)
                        showRvEmpty()
                    } else {
                        finishLoadMore()
                    }
                }
                is PageLoadState.Error -> {
                    if (viewStatus.isRefresh) {
                        mStatusView?.hideEmptyView()
                        mStatusView?.hideLoadingView()
                        mStatusView?.showErrorView(viewStatus.msg)
                        mStatusView?.setOnErrorViewClick {
                            onStatusViewErrorClick()
                        }
                    } else {
                        loadMoreError()
                    }
                }
            }
        })
    }

    /**
     * 初始化Adapter的加载更多
     *
     */
    private fun setUpAdapterLoadMore() {
        pageAdapter.apply {
            loadMoreModule.apply {
                isAutoLoadMore = true
                isEnableLoadMoreIfNotFullPage = true
                preLoadNumber = 5
                setOnLoadMoreListener {
                    fetchMoreData()
                }
            }
        }
    }

    /**
     * 更新recycle数据
     * @param list 列表数据
     * @param isLoadMore 是否时加载更多
     */
    fun updateDataList(list: Collection<Any>, isLoadMore: Boolean) {
        if (isLoadMore) {
            if (list.isNullOrEmpty()) {
                finishLoadMore()
            } else {
                pageAdapter.addData(list)
                pageAdapter.loadMoreModule.loadMoreComplete()
            }
        } else {
            if (list.isNullOrEmpty()) {
                showRvEmpty()
                return
            }
            pageAdapter.setList(list)
            completeLoadMore()
        }
    }

    /**
     * 加载更多失败
     */
    protected fun loadMoreError() {
        pageAdapter.loadMoreModule.loadMoreFail()
    }

    /**
     * 结束加载更多
     */
    protected fun finishLoadMore() {
        pageAdapter.loadMoreModule.apply {
            loadMoreComplete()
            loadMoreEnd(false)
        }
    }

    /**
     * 完成本次loadMore
     */
    protected fun completeLoadMore() {
        pageAdapter.loadMoreModule.apply {
            isEnableLoadMore = true
            loadMoreComplete()
        }
    }

    open fun showRvEmpty() {
        pageAdapter.setList(emptyList())
        pageAdapter.setEmptyView(R.layout.common_recycler_empty)
    }

    /**
     *绑定数据item
     */
    abstract fun bindItemView()
    abstract fun fetchMoreData()

}