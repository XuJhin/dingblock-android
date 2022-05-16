package cool.dingstock.appbase.adapter.multiadapter.wrapper

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cool.dingstock.appbase.adapter.multiadapter.ICoustomAdapter

class LoadMoreWrapper(val mInnerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), ICoustomAdapter {

    private val ITEM_TYPE_LOAD_MORE = Int.MAX_VALUE - 2
    private var mLoadMoreView: View? = null
    private var mLoadMoreLayoutId = 0
    private fun hasLoadMore(): Boolean {
        return hasLoadMore && (mLoadMoreView != null || mLoadMoreLayoutId != 0)
    }

    interface OnLoadMoreListener {
        fun onLoadMoreRequested()
    }

    private var mOnLoadMoreListener: OnLoadMoreListener? = null
    private var hasLoadMore = true
    private var isLoading = false
    private fun isShowLoadMore(position: Int): Boolean {
        if (mInnerAdapter.itemCount == 0) {
            return true
        }
        return hasLoadMore() && position >= mInnerAdapter.itemCount
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (mInnerAdapter.itemCount == 0) {
            return
        }
        if (isShowLoadMore(position)) {
            if (mOnLoadMoreListener != null && !isLoading) {
                isLoading = true
                mOnLoadMoreListener?.onLoadMoreRequested()
                return
            }
        }
        mInnerAdapter.onBindViewHolder(holder, position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isShowLoadMore(position)) {
            ITEM_TYPE_LOAD_MORE
        } else {
            mInnerAdapter.getItemViewType(position)
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        if (isShowLoadMore(holder.layoutPosition)) {
            setFullSpan(holder)
        } else {
            onViewAttachedToWindow(holder, holder.layoutPosition)
        }
    }

    private fun setFullSpan(holder: RecyclerView.ViewHolder) {
        WrapperUtils.setFullSpan(holder)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        WrapperUtils.onAttachedToRecyclerView(
                mInnerAdapter,
                recyclerView,
                object : WrapperUtils.SpanSizeCallback {
                    override fun getSpanSize(
                            layoutManager: GridLayoutManager?,
                            oldLookup: GridLayoutManager.SpanSizeLookup?,
                            position: Int
                    ): Int {
                        if (isShowLoadMore(position)) {
                            return layoutManager!!.spanCount
                        }
                        if (oldLookup != null) {
                            return oldLookup.getSpanSize(position)
                        }
                        return 1
                    }
                })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TYPE_LOAD_MORE) {
            if (mLoadMoreView != null) {
                ViewHolder.createViewHolder(parent.context, parent, mLoadMoreView!!)
            } else {
                ViewHolder.createViewHolder(parent.context, parent, mLoadMoreLayoutId)
            }
        } else {
            mInnerAdapter.onCreateViewHolder(parent, viewType)
        }
    }

    override fun getItemCount(): Int {
        return mInnerAdapter.itemCount + if (hasLoadMore()) 1 else 0
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder, postion: Int) {
        if (isShowLoadMore(holder.layoutPosition)) {
            setFullSpan(holder)
            return
        }
        if (mInnerAdapter is ICoustomAdapter) {
            mInnerAdapter.onViewAttachedToWindow(holder, postion)
            return
        }
        mInnerAdapter.onViewAttachedToWindow(holder)
    }

    fun setOnLoadMoreListener(loadMoreListener: LoadMoreWrapper.OnLoadMoreListener?): LoadMoreWrapper? {
        if (loadMoreListener != null) {
            mOnLoadMoreListener = loadMoreListener
        }
        return this
    }

    fun setLoadMoreView(loadMoreView: View): LoadMoreWrapper? {
        mLoadMoreView = loadMoreView
        return this
    }

    fun setLoadMoreView(layoutId: Int): LoadMoreWrapper? {
        mLoadMoreLayoutId = layoutId
        return this
    }

    fun loadingComplete() {
        isLoading = false
    }

    fun setLoadMore(b: Boolean) {
        hasLoadMore = b
        if (hasLoadMore) {
            notifyItemInserted(itemCount)
        } else {
            notifyItemRemoved(itemCount)
        }
    }

    fun isLoading(): Boolean {
        return isLoading
    }

    fun resetLoadMore() {
        isLoading = false
        setLoadMore(true)
    }

}