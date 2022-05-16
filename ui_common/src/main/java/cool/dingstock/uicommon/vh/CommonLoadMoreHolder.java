package cool.dingstock.uicommon.vh;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import cool.dingstock.appbase.annotations.LoadMoreStatus;
import cool.dingstock.appbase.widget.stickyheaders.SectioningAdapter;

public class CommonLoadMoreHolder extends SectioningAdapter.ItemViewHolder {

    private ProgressBar mLoadingView;
    private TextView mEndTextView;

    private @LoadMoreStatus
    int mLoadMoreStatus;

    public CommonLoadMoreHolder(View itemView) {
        super(itemView);
        if (null == mLoadingView) {
            // 初始化 loading 布局
            mLoadingView = ((ViewGroup) itemView).findViewById(cool.dingstock.appbase.R.id.loading_view);
        }

        if (null == mEndTextView) {
            mEndTextView = ((ViewGroup) itemView).findViewById(cool.dingstock.appbase.R.id.end_tv);
        }
    }

    public void bind(int mLoadMoreStatus) {
        this.mLoadMoreStatus = mLoadMoreStatus;
        switch (this.mLoadMoreStatus) {
            case LoadMoreStatus.END:
                end();
                break;
            case LoadMoreStatus.IDLE:
                itemView.setVisibility(View.GONE);
                break;
            case LoadMoreStatus.LOADING:
                startAnim();
                break;
        }
    }

    /**
     * Loading 状态 动画转起来
     */
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
    public void end() {
        if (null == mEndTextView) {
            return;
        }
        mLoadMoreStatus = LoadMoreStatus.END;
        mEndTextView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.GONE);
    }

}
