package cool.dingstock.appbase.widget.recyclerview.item;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import cool.dingstock.appbase.R;
import cool.dingstock.appbase.annotations.LoadMoreStatus;
import cool.dingstock.appbase.databinding.CommonRecyclerLoadMoreBinding;

/**
 * Description: TODO
 * Author: Shper
 * Version: V0.1 2017/7/15
 */
public class DefaultLoadMore extends BaseLoadMore<Integer, CommonRecyclerLoadMoreBinding> {

    public static final int PULL_LOAD_TYPE = 3300001;

    public static final int LOAD_MORE_TYPE = 3300002;

    private ProgressBar mLoadingView;
    private TextView mEndTextView;

    private @LoadMoreStatus
    int mLoadMoreStatus;

    public DefaultLoadMore(Integer viewType) {
        super(viewType);
    }

    @Override
    public int getViewType() {
        return getData();
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.common_recycler_load_more;
    }

    @Override
    public void onSetViewsData(BaseViewHolder holder) {

        if (null == mLoadingView) {
            // 初始化 loading 布局
            mLoadingView = ((ViewGroup) holder.getItemView()).findViewById(R.id.loading_view);
        }

        if (null == mEndTextView) {
            mEndTextView = ((ViewGroup) holder.getItemView()).findViewById(R.id.end_tv);
        }

        switch (mLoadMoreStatus) {
            case LoadMoreStatus.END:
                end();
                break;
            case LoadMoreStatus.IDLE:
                holder.getItemView().setVisibility(View.GONE);
                break;
            case LoadMoreStatus.LOADING:
                startAnim();
                break;
        }
    }

    /**
     * Loading 状态 动画转起来
     */
    @Override
    public void startAnim() {
        if (null == mLoadingView) {
            return;
        }

        mLoadMoreStatus = LoadMoreStatus.LOADING;
        mLoadingView.setVisibility(View.VISIBLE);
        mEndTextView.setVisibility(View.GONE);
    }


    /**
     * 停止动画 并且状态为idle 隐藏视图
     */
    @Override
    public void stopAnim() {
        if (null == mLoadingView) {
            return;
        }

        mLoadMoreStatus = LoadMoreStatus.IDLE;
        mLoadingView.setVisibility(View.GONE);
    }

    /**
     * 分页结束的情况
     */
    @Override
    public void end() {
        if (null == mEndTextView) {
            return;
        }
        mLoadMoreStatus = LoadMoreStatus.END;
        mEndTextView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.GONE);
    }
}
