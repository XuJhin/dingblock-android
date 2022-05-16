package cool.dingstock.appbase.widget.recyclerview.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cool.dingstock.appbase.R;
import cool.dingstock.appbase.annotations.LoadMoreStatus;
import cool.dingstock.appbase.databinding.CommonRecyclerEmptyBinding;
import cool.dingstock.appbase.databinding.CommonRecyclerErrorBinding;
import cool.dingstock.appbase.exception.RvAdapterException;
import cool.dingstock.appbase.util.KtExtendUtilsKt;
import cool.dingstock.appbase.widget.CommonEmptyView;
import cool.dingstock.appbase.widget.recyclerview.item.BaseEmpty;
import cool.dingstock.appbase.widget.recyclerview.item.BaseError;
import cool.dingstock.appbase.widget.recyclerview.item.BaseFoot;
import cool.dingstock.appbase.widget.recyclerview.item.BaseHead;
import cool.dingstock.appbase.widget.recyclerview.item.BaseItem;
import cool.dingstock.appbase.widget.recyclerview.item.BaseLoadMore;
import cool.dingstock.appbase.widget.recyclerview.item.BaseViewHolder;
import cool.dingstock.appbase.widget.recyclerview.item.DefaultLoadMore;
import cool.dingstock.lib_base.util.CollectionUtils;
import cool.dingstock.lib_base.util.Logger;

/**
 * RecyclerView 的 BaseRVAdapter
 * Author: Shper
 * Version: V0.1 2017/4/20
 */
public class BaseRVAdapter<I extends BaseItem> extends RecyclerView.Adapter<BaseViewHolder> implements ItemTouchable {

    private BaseEmpty mEmptyView;
    private BaseError mErrorView;
    private BaseLoadMore mLoadMoreView;
    private BaseLoadMore mPullLoadView;

    private boolean isShowEmptyView;
    private boolean isShowErrorView;

    private @LoadMoreStatus
    int mLoadMoreStatus;
    private boolean isOpenLoadMoreView;

    private boolean isShowPullLoadView;
    private boolean isOpenPullLoadView;

    private int mLastVisiblePosition = -1;
    private int mFirstVisiblePosition = -1;

    private SectionList<I> mSectionList = new SectionList<>();

    private OnItemViewClickListener<I> mOnItemViewClickListener;
    private onItemViewLongClickListener<I> mOnItemViewLongClickListener;
    private OnItemChangeListener mOnItemChangeListener;
    private OnLoadMoreListener mOnLoadMoreListener;
    private onPullLoadListener mOnPullLoadListener;
    private RecyclerView.LayoutManager layoutManager;

    private Set<Integer> fullSpanTypeList = new HashSet<>();


    public BaseRVAdapter() {
    }

    public BaseRVAdapter(@NonNull List<I> itemList) {
        if (itemList.isEmpty()) {
            return;
        }
        this.mSectionList.setItemViewList(SectionList.SECTION_KEY_DEFAULT, itemList);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // State View
        if (null != mEmptyView && mEmptyView.getViewType() == viewType) {
            return mEmptyView.onCreateViewHolder(parent, viewType);
        }
        if (null != mErrorView && mErrorView.getViewType() == viewType) {
            return mErrorView.onCreateViewHolder(parent, viewType);
        }
        if (null != mLoadMoreView && mLoadMoreView.getViewType() == viewType) {
            return mLoadMoreView.onCreateViewHolder(parent, viewType);
        }
        if (null != mPullLoadView && mPullLoadView.getViewType() == viewType) {
            return mPullLoadView.onCreateViewHolder(parent, viewType);
        }
        // Header & Foot
        if (this.mSectionList.isHeadViewViewType(viewType)) {
            return this.mSectionList.getHeadViewByViewType(viewType).onCreateViewHolder(parent, viewType);
        }
        if (this.mSectionList.isFootViewViewType(viewType)) {
            return this.mSectionList.getFootViewByViewType(viewType).onCreateViewHolder(parent, viewType);
        }

        I item = this.mSectionList.getItemViewByViewType(viewType);
        if (null == item) {
            throw new RvAdapterException("error viewType");
        }

        final BaseViewHolder viewHolder = item.onCreateViewHolder(parent, viewType);

        // Click
        KtExtendUtilsKt.setOnShakeClickListener(viewHolder.getItemView(), v -> {
            if (null == mOnItemViewClickListener) {
                return;
            }
            int realPosition = viewHolder.getAdapterPosition() - getPullLoadCount();
            mOnItemViewClickListener.onItemViewClick(
                    mSectionList.getItemView(realPosition),
                    mSectionList.getSectionKey(realPosition),
                    mSectionList.getSectionItemPosition(realPosition));
        });

        // LongClick
        viewHolder.getItemView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null == mOnItemViewLongClickListener) {
                    return false;
                }

                int realPosition = viewHolder.getAdapterPosition() - getPullLoadCount();
                return mOnItemViewLongClickListener.onItemViewLongClick(
                        mSectionList.getItemView(realPosition),
                        mSectionList.getSectionKey(realPosition),
                        mSectionList.getSectionItemPosition(realPosition));
            }
        });

        return viewHolder;
    }

    private void checkStaggeredGridLayout(BaseViewHolder holder, boolean force) {
        if (null == layoutManager) {
            return;
        }
        if (!(layoutManager instanceof StaggeredGridLayoutManager)) {
            return;
        }
        if (force || fullSpanTypeList.contains(holder.getItemViewType())) {
            StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, holder.getItemView().getLayoutParams().height);
            layoutParams.setFullSpan(true);
            holder.itemView.setLayoutParams(layoutParams);
        }
    }


    public void registerFullSpanType(Set<Integer> viewTypeList) {
        if (null == viewTypeList) {
            return;
        }
        fullSpanTypeList.addAll(viewTypeList);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

        if (this.isShowEmptyView) {
            checkStaggeredGridLayout(holder, true);
            mEmptyView.onBindViewHolder(holder, SectionList.SECTION_KEY_DEFAULT, position);
            return;
        }

        if (this.isShowErrorView) {
            checkStaggeredGridLayout(holder, true);
            mErrorView.onBindViewHolder(holder, SectionList.SECTION_KEY_DEFAULT, position);
            return;
        }

        if (isDropLoadPosition(position)) {
            checkStaggeredGridLayout(holder, true);
            mPullLoadView.onBindViewHolder(holder, SectionList.SECTION_KEY_DEFAULT, position);
            return;
        }

        if (isLoadMoreViewPosition(position)) {
            checkStaggeredGridLayout(holder, true);
            mLoadMoreView.onBindViewHolder(holder, SectionList.SECTION_KEY_DEFAULT, position);
            return;
        }

        int realPosition = position - getPullLoadCount();
        if (this.mSectionList.isHeadViewPosition(realPosition)) {
            if (null != this.mSectionList.getHeadView(realPosition)) {
                checkStaggeredGridLayout(holder, true);
                this.mSectionList.getHeadView(realPosition).onBindViewHolder(holder,
                        this.mSectionList.getSectionKey(realPosition),
                        this.mSectionList.getSectionHeadPosition(realPosition));
            }

            return;
        }

        if (this.mSectionList.isFootViewPosition(realPosition)) {
            if (null != this.mSectionList.getFootView(realPosition)) {
                checkStaggeredGridLayout(holder, true);
                this.mSectionList.getFootView(realPosition).onBindViewHolder(holder,
                        this.mSectionList.getSectionKey(realPosition),
                        this.mSectionList.getSectionFootPosition(realPosition));
            }

            return;
        }
        checkStaggeredGridLayout(holder, false);
        this.mSectionList.getItemView(realPosition).onBindViewHolder(holder,
                this.mSectionList.getSectionKey(realPosition),
                this.mSectionList.getSectionItemPosition(realPosition));
    }

    @Override
    public int getItemViewType(int position) {

        if (this.isShowEmptyView) {
            return mEmptyView.getViewType();
        }

        if (this.isShowErrorView) {
            return mErrorView.getViewType();
        }

        if (isDropLoadPosition(position)) {
            return mPullLoadView.getViewType();
        }

        if (isLoadMoreViewPosition(position)) {
            return mLoadMoreView.getViewType();
        }

        int realPosition = position - getPullLoadCount();
        if (this.mSectionList.isHeadViewPosition(realPosition)) {
            return this.mSectionList.getViewType(realPosition);
        }

        if (this.mSectionList.isFootViewPosition(realPosition)) {
            return this.mSectionList.getViewType(realPosition);
        }

        return this.mSectionList.getViewType(realPosition);
    }

    @Override
    public int getItemCount() {
        int itemCount = (this.isShowEmptyView || this.isShowErrorView) ?
                1 : this.mSectionList.getCount() + getLoadMoreCount() + getPullLoadCount();
        return itemCount;
    }


    private RecyclerView mRecyclerView;

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        layoutManager = recyclerView.getLayoutManager();
        Logger.i("onAttachedToRecyclerView layoutManager==" + layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layoutManager instanceof GridLayoutManager) {
                    mLastVisiblePosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                    mFirstVisiblePosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                } else if (layoutManager instanceof LinearLayoutManager) {
                    mLastVisiblePosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                    mFirstVisiblePosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager staggeredGridLayoutManager =
                            (StaggeredGridLayoutManager) layoutManager;
                    int[] lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                    staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                    mLastVisiblePosition = findMax(lastPositions);
                    staggeredGridLayoutManager.findFirstVisibleItemPositions(lastPositions);
                    mFirstVisiblePosition = findMin(lastPositions);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                View firstView = recyclerView.getChildAt(0);
                if (null == firstView) {
                    return;
                }

                if (null == recyclerView.getContext()) {
                    return;
                }

                if (recyclerView.getContext() instanceof Activity
                        && ((Activity) recyclerView.getContext()).isFinishing()) {
                    return;
                }
                //判断RecyclerView 的ItemView是否满屏，如果不满一屏，上拉不会触发加载更多
                boolean isFullScreen = firstView.getTop() <= recyclerView.getPaddingTop();
                Logger.w("fistView top=" + firstView.getTop() + " paddingTop=" + recyclerView.getPaddingTop());
                int itemCount = recyclerView.getLayoutManager().getItemCount();
                //因为LoadMore View  是Adapter的一个Item,显示LoadMore 的时候，Item数量＋1了
                // ，导致 mLastVisiblePosition == itemCount-1
                Logger.w("mLastVisiblePosition+" + mLastVisiblePosition);

                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && mLoadMoreStatus == LoadMoreStatus.IDLE
                        && itemCount != 0
                        && mLastVisiblePosition == itemCount - 1
                        && isOpenLoadMoreView
                        && null != mLoadMoreView
                        && null != mLoadMoreView.getHolder()) {

                    mLoadMoreStatus = LoadMoreStatus.LOADING;
                    mLoadMoreView.getHolder().setVisibility(View.VISIBLE);
                    mLoadMoreView.startAnim();
                    if (null != mOnLoadMoreListener) {
                        mOnLoadMoreListener.onLoading();
                    }
                }

                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && mFirstVisiblePosition == 0
                        && isOpenPullLoadView
                        && !isShowPullLoadView
                        && null != mPullLoadView
                        && null != mPullLoadView.getHolder()) {

                    isShowPullLoadView = true;
                    mPullLoadView.getHolder().setVisibility(View.VISIBLE);
                    mPullLoadView.startAnim();

                    if (null != mOnPullLoadListener) {
                        mOnPullLoadListener.onLoading();
                    }
                }
            }
        });
    }

    @Override
    public void onViewDetachedFromWindow(BaseViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        // 释放资源
        int realPosition = holder.getAdapterPosition() - getPullLoadCount();

        //越界检查
        if (realPosition < 0 || realPosition >= this.mSectionList.getCount() ||
                null == this.mSectionList.getItemView(realPosition)) {
            return;
        }

        this.mSectionList.getItemView(realPosition).releaseResource();
    }

    @Override
    public void onItemViewDismiss(int position) {
        int realPosition = position - getPullLoadCount();

        if (!this.mSectionList.isItemViewPosition(realPosition)) {
            return;
        }

        this.mSectionList.removeItemView(realPosition);

        notifyItemRemoved(realPosition);

        if (null != this.mOnItemChangeListener) {
            this.mOnItemChangeListener.onItemViewDismiss();
        }
    }

    @Override
    public boolean onItemViewMove(int fromPosition, int toPosition) {
        int realFromPosition = fromPosition - getPullLoadCount();
        int realToPosition = toPosition - getPullLoadCount();

        if (!this.mSectionList.isItemViewPosition(realFromPosition) ||
                !this.mSectionList.isItemViewPosition(realToPosition)) {
            return false;
        }

        if (this.mSectionList.itemViewMove(realFromPosition, realToPosition)) {
            if (null != this.mOnItemChangeListener) {
                this.mOnItemChangeListener.onItemViewMove(
                        this.mSectionList.getSectionKey(realFromPosition),
                        this.mSectionList.getSectionItemPosition(realToPosition),
                        this.mSectionList.getSectionItemPosition(realFromPosition)
                );
            }

            notifyItemMoved(realFromPosition, realToPosition);
            return true;
        }

        return false;
    }


    public void showEmptyView() {
        showEmptyView("");
    }


    public void showEmptyView(String text) {
        resetState();
        this.isShowEmptyView = true;
        this.mEmptyView = new BaseEmpty<String, CommonRecyclerEmptyBinding>(text) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.common_recycler_empty;
            }

            @Override
            public void onSetViewsData(BaseViewHolder holder) {
                if (TextUtils.isEmpty(getData())) {
                    return;
                }
                CommonEmptyView emptyTxt = holder.getItemView().findViewById(R.id.commonEmptyView);
                emptyTxt.setText(getData());
            }
        };
        try {
            clearAllSectionViews();
        } catch (Error error) {
            Log.e("error", error.getMessage());
        }

    }


    public void showEmptyView(@NonNull BaseEmpty emptyView) {
        resetState();
        this.isShowEmptyView = true;
        this.mEmptyView = emptyView;
        clearAllSectionViews();
    }


    public void hideEmptyView() {
        this.isShowEmptyView = false;
        notifyDataSetChanged();
    }

    public void showErrorView() {
        showErrorView(null, "");
    }

    public void showErrorView(String text) {
        showErrorView(null, text);
    }

    public void showErrorView(BaseError errorView, String text) {
        resetState();
        this.isShowErrorView = true;
        this.mErrorView = null != errorView ? errorView : new BaseError<String, CommonRecyclerErrorBinding>("") {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.common_recycler_error;
            }

            @Override
            public void onSetViewsData(BaseViewHolder holder) {
                TextView errorTxt = holder.getItemView().findViewById(R.id.common_state_error_txt);
                if (!TextUtils.isEmpty(text)) {
                    errorTxt.setText(text);
                }
            }
        };
        clearAllSectionItemViews();
    }

    public void hideErrorView() {
        this.isShowErrorView = false;
        notifyDataSetChanged();
    }

    /**
     * 打开 Adapter 的 LoadMore 功能，可在 adapter 初始化时，或 有更多数据时 打开
     */
    public void openLoadMore() {
        openLoadMore(null);
    }


    /**
     * 打开 Adapter 的 LoadMore 功能，可在 adapter 初始化时，或 有更多数据时 打开
     */
    public void openLoadMore(BaseLoadMore loadMoreView) {
        if (this.isOpenLoadMoreView) {
            return;
        }

        this.isOpenLoadMoreView = true;
        this.mLoadMoreView = (null != loadMoreView ? loadMoreView : new DefaultLoadMore(DefaultLoadMore.LOAD_MORE_TYPE));

        if (this.mSectionList.getCount() > 0) {
            notifyItemInserted(getItemCount() - 1);
        }
    }


    /**
     * 关闭 Adapter 的 LoadMore 功能，在 无更多数据时，可调用来关闭 此功能
     */
    public void closeLoadMore() {
        hideLoadMoreView();
        if (this.isOpenLoadMoreView) {
            this.isOpenLoadMoreView = false;
            notifyDataSetChanged();
        }

        this.isOpenLoadMoreView = false;
        this.mLoadMoreView = null;
    }

    /**
     * 关闭 Adapter 的 LoadMore 功能，在 无更多数据时，可调用来关闭 此功能
     */
    public void endLoadMore() {
        if (!this.isOpenLoadMoreView
                || null == this.mLoadMoreView
                || null == this.mLoadMoreView.getHolder()
                || null == mRecyclerView) {
            return;
        }
        this.mLoadMoreStatus = LoadMoreStatus.END;
        this.mLoadMoreView.getHolder().setVisibility(View.VISIBLE);
        this.mLoadMoreView.end();
    }

    /**
     * 隐藏 LoadMoreView 视图，在 数据加载完成后，调用
     */
    public void hideLoadMoreView() {
        this.mLoadMoreStatus = LoadMoreStatus.IDLE;
        if (null != this.mLoadMoreView && null != this.mLoadMoreView.getHolder()) {
            this.mLoadMoreView.getHolder().setVisibility(View.GONE);
            this.mLoadMoreView.stopAnim();
        }
    }

    public void openPullLoad() {
        openPullLoad(null);
    }

    public void openPullLoad(BaseLoadMore pullLoadView) {
        if (this.isOpenPullLoadView) {
            return;
        }

        this.isOpenPullLoadView = true;
        this.mPullLoadView = (null != pullLoadView ? pullLoadView : new DefaultLoadMore(DefaultLoadMore.PULL_LOAD_TYPE));

        notifyItemInserted(0);
    }

    public void hidePullLoadView() {

        this.isShowPullLoadView = false;
        if (null != this.mPullLoadView && null != this.mPullLoadView.getHolder()) {
            this.mPullLoadView.getHolder().setVisibility(View.GONE);
            this.mPullLoadView.stopAnim();
        }
    }

    public void closePullLoad() {
        hidePullLoadView();
        if (this.isOpenPullLoadView) {
            notifyItemRemoved(0);
        }

        this.isOpenPullLoadView = false;
        this.mPullLoadView = null;
    }


    @MainThread
    public void addHeadView(@NonNull BaseHead headView) {
        addHeadView(SectionList.SECTION_KEY_DEFAULT, headView);
    }

    @MainThread
    public void addHeadView(@IntRange(from = 0) int sectionKey, @NonNull BaseHead headView) {
        int position = this.mSectionList.addHeadView(sectionKey, headView);
        if (position < 0) {
            return;
        }
        if (this.mSectionList.getCount() == 1) {
            resetState();
            notifyDataSetChanged();
        } else {
            notifyItemInserted(position);
        }
    }

    @MainThread
    public void updateHeadView(@NonNull BaseHead headView) {
        updateHeadView(SectionList.SECTION_KEY_DEFAULT, headView);
    }

    @MainThread
    public void updateHeadView(@IntRange(from = 0) int sectionKey, @NonNull BaseHead headView) {
        int position = this.mSectionList.updateHeadView(sectionKey, headView);
        if (position < 0) {
            return;
        }
        notifyItemChanged(position);
    }

    @MainThread
    public void removeHeadView(@NonNull BaseHead view) {
        removeHeadView(SectionList.SECTION_KEY_DEFAULT, view);
    }

    @MainThread
    public void removeHeadView(@IntRange(from = 0) int sectionKey, @NonNull BaseHead headView) {
        int position = this.mSectionList.removeHeadView(sectionKey, headView);
        if (position < 0) {
            return;
        }
        notifyItemRemoved(position);
    }

    public BaseHead getHeadView(@IntRange(from = 0) int headPosition) {
        return getHeadView(SectionList.SECTION_KEY_DEFAULT, headPosition);
    }

    public BaseHead getHeadView(@IntRange(from = 0) int sectionKey, @IntRange(from = 0) int headPosition) {
        return this.mSectionList.getHeadView(sectionKey, headPosition);
    }

    @MainThread
    public void clearAllHeadView(@IntRange(from = 0) int sectionKey) {
        int headCount = this.mSectionList.getHeadViewList(sectionKey).size();
        if (headCount < 1) {
            return;
        }
        int position = this.mSectionList.clearAllHeadView(sectionKey);

        notifyItemRangeRemoved(position, headCount);
    }

    @MainThread
    public void clearAllSectionHeadView() {
        this.mSectionList.clearAllSectionHeadView();
        notifyDataSetChanged();
    }

    @MainThread
    public void addFootView(@NonNull BaseFoot view) {
        addFootView(SectionList.SECTION_KEY_DEFAULT, view);
    }

    @MainThread
    public void addFootView(@IntRange(from = 0) int sectionKey, @NonNull BaseFoot footView) {
        int position = this.mSectionList.addFootView(sectionKey, footView);
        if (position < 0) {
            return;
        }
        if (this.mSectionList.getCount() == 1) {
            resetState();
            notifyDataSetChanged();
        } else {
            notifyItemInserted(position);
        }
    }

    @MainThread
    public void updateFootView(@NonNull BaseFoot footView) {
        updateFootView(SectionList.SECTION_KEY_DEFAULT, footView);
    }

    @MainThread
    public void updateFootView(@IntRange(from = 0) int sectionKey, @NonNull BaseFoot footView) {

        int position = this.mSectionList.updateFootView(sectionKey, footView);
        if (position < 0) {
            return;
        }

        notifyItemChanged(position);
    }

    @MainThread
    public void removeFootView(@NonNull BaseFoot view) {
        removeFootView(SectionList.SECTION_KEY_DEFAULT, view);
    }

    @MainThread
    public void removeFootView(@IntRange(from = 0) int sectionKey, @NonNull BaseFoot footView) {
        int position = this.mSectionList.removeFootView(sectionKey, footView);
        if (position < 0) {
            return;
        }
        notifyItemRemoved(position);
    }

    public BaseFoot getFootView(@IntRange(from = 0) int footPosition) {
        return getFootView(SectionList.SECTION_KEY_DEFAULT, footPosition);
    }

    public BaseFoot getFootView(@IntRange(from = 0) int sectionKey, @IntRange(from = 0) int footPosition) {
        return this.mSectionList.getFootView(sectionKey, footPosition);
    }

    @MainThread
    public void setItemViewList(@NonNull List<I> itemList) {
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }
        setItemViewList(SectionList.SECTION_KEY_DEFAULT, itemList);
    }

    @MainThread
    public void setItemViewList(@IntRange(from = 0) int sectionKey, @NonNull List<I> itemList) {
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }
        resetState();
        int position = this.mSectionList.setItemViewList(sectionKey, itemList);
        if (position < 0) {
            return;
        }
        notifyDataSetChanged();
    }

    public List<I> getItemList() {
        return getItemList(SectionList.SECTION_KEY_DEFAULT);
    }

    public List<I> getItemList(@IntRange(from = 0) int sectionKey) {
        return this.mSectionList.getItemViewList(sectionKey);
    }

    public I getItemView(int position) {
        return this.mSectionList.getItemView(position);
    }

    public I getItemView(@IntRange(from = 0) int sectionKey, int sectionPosition) {
        return this.mSectionList.getItemViewBySection(sectionKey, sectionPosition);
    }

    public List<I> getAllItemList() {
        return this.mSectionList.getAllItemViewList();
    }


    @MainThread
    public void addItemView(@NonNull I item) {
        addItemView(SectionList.SECTION_KEY_DEFAULT, item);
    }

    @MainThread
    public void addItemView(@IntRange(from = 0) int sectionKey, @NonNull I item) {
        int position = this.mSectionList.addItemView(sectionKey, item);
        if (position < 0) {
            return;
        }

        if (this.mSectionList.getCount() <= 1) {
            resetState();
            notifyDataSetChanged();
        } else {
            notifyItemChanged(position);
        }
    }

    @MainThread
    public void addItemViewList(@NonNull List<I> itemList) {
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }
        addItemViewList(SectionList.SECTION_KEY_DEFAULT, itemList);
    }

    @MainThread
    public void addItemViewList(@IntRange(from = 0) int sectionKey, @NonNull List<I> itemList) {
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }

        int position = this.mSectionList.addItemViewList(sectionKey, itemList);
        if (position < 0) {
            return;
        }

        if (this.mSectionList.getCount() == 1) {
            resetState();
            notifyDataSetChanged();
        } else {
            int realPosition = position + getLoadMoreCount() + getPullLoadCount();
            notifyItemRangeInserted(realPosition, itemList.size());
        }
    }

    @MainThread
    public void insertItemView(@IntRange(from = 0) int index, @NonNull I item) {
        insertItemView(SectionList.SECTION_KEY_DEFAULT, index, item);
    }

    @MainThread
    public void insertItemView(@IntRange(from = 0) int sectionKey, @IntRange(from = 0) int index, @NonNull I item) {
        int position = this.mSectionList.insertItemView(sectionKey, index, item);
        if (position < 0) {
            return;
        }
        if (this.mSectionList.getCount() == 1) {
            resetState();
            notifyDataSetChanged();
        } else {
            int realPosition = position + getPullLoadCount();
            notifyItemInserted(realPosition);
            notifyItemRangeChanged(0, this.mSectionList.getCount());
        }
    }

    @MainThread
    public void insertItemViewList(@IntRange(from = 0) int index, @NonNull List<I> itemList) {
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }
        insertItemViewList(SectionList.SECTION_KEY_DEFAULT, index, itemList);
    }

    @MainThread
    public void insertItemViewList(@IntRange(from = 0) int sectionKey, @IntRange(from = 0) int index, @NonNull List<I> itemList) {
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }

        int position = this.mSectionList.insertItemViewList(sectionKey, index, itemList);
        if (position < 0) {
            return;
        }
        if (this.mSectionList.getCount() == 1) {
            resetState();
            notifyDataSetChanged();
        } else {
            notifyItemRangeInserted(position, itemList.size());
            notifyItemRangeChanged(0, this.mSectionList.getCount());
        }
    }

    @MainThread
    public void updateItemView(@NonNull I item) {
        updateItemView(SectionList.SECTION_KEY_DEFAULT, item);
    }

    @MainThread
    public void updateItemView(@IntRange(from = 0) int sectionKey, @NonNull I item) {
        int position = this.mSectionList.updateItemView(sectionKey, item);
        if (position < 0) {
            return;
        }
        notifyItemChanged(position);
    }

    @MainThread
    public void removeItemView(@NonNull I item) {
        removeItemView(SectionList.SECTION_KEY_DEFAULT, item);
    }

    @MainThread
    public void removeItemView(@IntRange(from = 0) int sectionKey, @NonNull I item) {
        int position = this.mSectionList.removeItemView(sectionKey, item);
        if (position < 0) {
            return;
        }
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, this.mSectionList.getCount());
    }

    @MainThread
    public void removeItemView(@IntRange(from = 0) int index) {
        removeItemView(SectionList.SECTION_KEY_DEFAULT, index);
    }

    @MainThread
    public void removeItemView(@IntRange(from = 0) int sectionKey, @IntRange(from = 0) int index) {
        int position = this.mSectionList.removeItemView(sectionKey, index);
        if (position < 0) {
            return;
        }
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, this.mSectionList.getCount());
    }

    @MainThread
    public void clearAllItemView() {
        clearAllItemView(SectionList.SECTION_KEY_DEFAULT);
    }

    @MainThread
    public void clearAllItemView(@IntRange(from = 0) int sectionKey) {
        int position = this.mSectionList.clearAllItemView(sectionKey);
        if (position < 0) {
            return;
        }
        notifyDataSetChanged();
    }

    @MainThread
    public void clearAllSectionItemViews() {
        this.mSectionList.clearAllSectionItemViews();
        notifyDataSetChanged();
    }

    @MainThread
    public void clearAllSectionViews() {
        this.clearAllSectionItemViews();
        this.clearAllSectionHeadView();
        notifyDataSetChanged();
    }

    public void setOnItemViewClickListener(@NonNull OnItemViewClickListener<I> listener) {
        this.mOnItemViewClickListener = listener;
    }

    public void setOnItemViewLongClickListener(@NonNull onItemViewLongClickListener<I> listener) {
        this.mOnItemViewLongClickListener = listener;
    }

    public void setOnItemChangeListener(@NonNull OnItemChangeListener listener) {
        this.mOnItemChangeListener = listener;
    }

    public void setOnLoadMoreListener(@NonNull OnLoadMoreListener listener) {
        this.mOnLoadMoreListener = listener;
    }

    public void setOnPullLoadListener(@NonNull onPullLoadListener listener) {
        this.mOnPullLoadListener = listener;
    }

    private int getLoadMoreCount() {
        return isOpenLoadMoreView ? 1 : 0;
    }

    private int getPullLoadCount() {
        return isOpenPullLoadView ? 1 : 0;
    }

    private void resetState() {
        this.isShowEmptyView = false;
        this.isShowErrorView = false;

        this.mLoadMoreStatus = LoadMoreStatus.IDLE;
        if (null != this.mLoadMoreView && null != this.mLoadMoreView.getHolder()) {
            this.mLoadMoreView.getHolder().setVisibility(View.GONE);
            this.mLoadMoreView.stopAnim();
        }
    }

    private boolean isLoadMoreViewPosition(int position) {
        return position >= mSectionList.getCount() && isOpenLoadMoreView;
    }

    private boolean isDropLoadPosition(int position) {
        return position == 0 && isOpenPullLoadView;
    }

    // TODO
    public boolean isNormalState() {
        return (!isShowEmptyView || !isShowErrorView);
    }

    public I getItemByRealPosition(int realPosition) {
        return mSectionList.getItemView(realPosition);
    }

    /**
     * 获取组数最大值
     */
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }


    /**
     * 获取组数最大值
     */
    private int findMin(int[] lastPositions) {
        int min = lastPositions[0];
        for (int value : lastPositions) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }


    public interface OnItemViewClickListener<I> {
        void onItemViewClick(I item, int sectionKey, int sectionItemPosition);
    }

    public interface onItemViewLongClickListener<I> {
        boolean onItemViewLongClick(I item, int sectionKey, int sectionItemPosition);
    }

    public interface OnItemChangeListener {

        void onItemViewDismiss();

        void onItemViewMove(int sectionKey, int fromSectionItemPosition, int toSectionItemPosition);

    }

    public interface OnLoadMoreListener {
        void onLoading();
    }

    public interface onPullLoadListener {
        void onLoading();
    }

}
