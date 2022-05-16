package cool.dingstock.appbase.customerview.expand;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class SuspendRecyclerGroup extends FrameLayout {
    View mSuspensionBar;
    RecyclerView recyclerView;

    SuspendBarListener suspendBarListener;


    private int mCurrentPosition;
    int mSuspensionHeight;
    float topY=0;

    private boolean isNeedSuspendBar = true;


    public SuspendRecyclerGroup(@NonNull Context context) {
        super(context);
//        init();
    }

    public SuspendRecyclerGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        init();
    }

    public SuspendRecyclerGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        init();
    }

    public void setSuspendBarListener(SuspendBarListener suspendBarListener) {
        this.suspendBarListener = suspendBarListener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    public void setNeedSuspendBar(boolean needSuspendBar) {
        isNeedSuspendBar = needSuspendBar;
        if(isNeedSuspendBar){
            mSuspensionBar.setVisibility(VISIBLE);
        }else {
            mSuspensionBar.setVisibility(GONE);
        }
    }

    private void init() {
        for (int i=0;i<getChildCount();i++){
            View child = getChildAt(i);
            if(child instanceof RecyclerView){
                recyclerView = (RecyclerView) child;
                continue;
            }
            if(mSuspensionBar == null){
                mSuspensionBar = child;
            }

            if(mSuspensionBar!=null&&recyclerView!=null){
                break;
            }
        }


        mSuspensionBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(suspendBarListener!=null){
                    suspendBarListener.onSuspendionBarOnClickListener(view,mCurrentPosition);
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!isNeedSuspendBar){
                    return;
                }
                if(newState==RecyclerView.SCROLL_STATE_IDLE){//快速滚动有可能会不调用方法，这里进行修正
                    synSuspendBar();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!isNeedSuspendBar){
                    return;
                }
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if(linearLayoutManager==null){
                    return;
                }
                // mSuspensionBar.getHeight()的高度的获取如果是在 onCreate() 或者是
                // 在 RecyclerView.OnScrollListener 被初始化的时候去获取，获得的结果会为 0，因此此时 mSuspensionBar 还没有初始化完成
                mSuspensionHeight = mSuspensionBar.getHeight();
                int firstVisPos = linearLayoutManager.findFirstVisibleItemPosition();

                int nextposition = firstVisPos + 1;
                View nextView = linearLayoutManager.findViewByPosition(firstVisPos + 1);

                //向上滑情况下
                if (dy > 0) {
                    //只有第一个可见的 Item 的下一个 Item 的类型
                    //为 Post类型时才需要动态设置效果
                    if (suspendBarListener!=null&&suspendBarListener.isGroup(nextposition)) {

                        if (nextView.getTop() <= mSuspensionHeight) {
                            //被顶掉的效果
                            mSuspensionBar.setY(-(mSuspensionHeight - nextView.getTop()));
                        } else {
                            //否则就直接回到 Y = 0 的位置
                            mSuspensionBar.setY(topY);
                        }
                    }

                    //判断是否需要更新悬浮条
                    if (mCurrentPosition != firstVisPos &&suspendBarListener!=null&&suspendBarListener.isGroup(firstVisPos)) {
                        mCurrentPosition = firstVisPos;
                        //根据 mCurrentPosition 的值，更新 mSuspensionBar

                        suspendBarListener.updataView(mSuspensionBar,mCurrentPosition);
                        if(!suspendBarListener.isGroup(nextposition)){
                            mSuspensionBar.setY(topY);
                        }
                    }
                } else {//向下滑情况
                    // 1、nextItem -> Post and firstVisibleItem -> Comment       mCurrentPosition = ((Comment) firstVisibleItem).getParentPostPosition()
                    // 2、nextItem -> Post and firstVisibleItem -> Post          mCurrentPosition = firstVisPos
                    // 3、nextItem -> Comment and firstVisibleItem -> Comment    mSuspensionBar 不动
                    // 4、nextItem -> Comment and firstVisibleItem -> Post       mSuspensionBar 不动
                    if (suspendBarListener!=null&&suspendBarListener.isGroup(nextposition)) {
                        mCurrentPosition = firstVisPos;
                        suspendBarListener.updataView(mSuspensionBar,mCurrentPosition);

                        if (nextView.getTop() <= mSuspensionHeight) {
                            //被顶掉的效果
                            mSuspensionBar.setY(-(mSuspensionHeight - nextView.getTop()));
                        } else {
                            mSuspensionBar.setY(topY);
                        }
                    }
                }
            }
        });
        if(suspendBarListener!=null){
            suspendBarListener.updataView(mSuspensionBar,mCurrentPosition);
        }
    }



    public void synSuspendBar() {
        mSuspensionHeight = mSuspensionBar.getHeight();
//        topY=recyclerView.getY();
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if(linearLayoutManager==null){
            return;
        }
        int firstVisPos = linearLayoutManager.findFirstVisibleItemPosition();
//        int nextType = sildeExpandAdapter.getOwnItemViewType(firstVisPos + 1);
        View nextView = linearLayoutManager.findViewByPosition(firstVisPos + 1);
        if(suspendBarListener!=null&&suspendBarListener.isGroup(firstVisPos+1)&&nextView!=null){
            if (nextView.getTop() <= mSuspensionHeight) {
                int y = -(mSuspensionHeight - nextView.getTop());
                if(y>-mSuspensionHeight){
                    mSuspensionBar.setY(y);
                }
                //被顶掉的效果
            } else {
                mSuspensionBar.setY(topY);
            }
        }else {
            mSuspensionBar.setY(topY);
        }
        mCurrentPosition=firstVisPos;
        if(suspendBarListener!=null){
            suspendBarListener.updataView(mSuspensionBar,firstVisPos);
        }
    }

    /**
     * 当recyclerView数据变化。或者有列表关闭时调用，理论上有列表打开也需要调用。但实际情况打开列表不会影响炫富投的位置
     *
     * */
    public void onDataPositionChange(){
        if(recyclerView!=null){
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    synSuspendBar();
                }
            },300);
        }
    }


    public interface SuspendBarListener{

        boolean isGroup(int position);

        void updataView(View mSuspensionBar,int position);

        void onSuspendionBarOnClickListener(View mSuspendionBar,int currentPosition);

    }





}




