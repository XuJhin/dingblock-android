package cool.dingstock.appbase.list.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import cool.dingstock.appbase.R
import cool.dingstock.appbase.adapter.LoadMoreBinderAdapter
import cool.dingstock.appbase.lazy.LazyFragment
import cool.dingstock.appbase.list.AbsListViewModel
import cool.dingstock.appbase.mvp.lazy.VmBindingLazyFragment

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/10/21 14:12
 * @Version:         1.1.0
 * @Description:
 */
abstract class AbsListFragment<VM : AbsListViewModel, VB : ViewBinding> : VmBindingLazyFragment<VM, VB>() {

    val pageAdapter: LoadMoreBinderAdapter by lazy {
        LoadMoreBinderAdapter()
    }

    override fun initVariables(rootView: View?, container: ViewGroup?, savedInstanceState: Bundle?) {
        setUpAdapterLoadMore()
        setUpAdapter()
    }

    abstract fun setUpAdapter()

    /**
     * 设置AdapterLoadMore
     */
    protected fun setUpAdapterLoadMore() {
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

    protected fun showRvEmpty() {
        pageAdapter.setList(emptyList())
        pageAdapter.headerWithEmptyEnable = true
        pageAdapter.loadMoreModule.isEnableLoadMore = false
        pageAdapter.setEmptyView(R.layout.common_recycler_empty)
    }

    protected fun showRvEmpty(view: View) {
        pageAdapter.setList(emptyList())
        pageAdapter.headerWithEmptyEnable = true
        pageAdapter.loadMoreModule.isEnableLoadMore = false
        pageAdapter.setEmptyView(view)
    }

    abstract fun fetchMoreData()

}