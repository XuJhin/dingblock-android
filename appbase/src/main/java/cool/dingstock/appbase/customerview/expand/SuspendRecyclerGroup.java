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
                if(newState==RecyclerView.SCROLL_STATE_IDLE){//????????????????????????????????????????????????????????????
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
                // mSuspensionBar.getHeight()?????????????????????????????? onCreate() ?????????
                // ??? RecyclerView.OnScrollListener ?????????????????????????????????????????????????????? 0??????????????? mSuspensionBar ????????????????????????
                mSuspensionHeight = mSuspensionBar.getHeight();
                int firstVisPos = linearLayoutManager.findFirstVisibleItemPosition();

                int nextposition = firstVisPos + 1;
                View nextView = linearLayoutManager.findViewByPosition(firstVisPos + 1);

                //??????????????????
                if (dy > 0) {
                    //???????????????????????? Item ???????????? Item ?????????
                    //??? Post????????????????????????????????????
                    if (suspendBarListener!=null&&suspendBarListener.isGroup(nextposition)) {

                        if (nextView.getTop() <= mSuspensionHeight) {
                            //??????????????????
                            mSuspensionBar.setY(-(mSuspensionHeight - nextView.getTop()));
                        } else {
                            //????????????????????? Y = 0 ?????????
                            mSuspensionBar.setY(topY);
                        }
                    }

                    //?????????????????????????????????
                    if (mCurrentPosition != firstVisPos &&suspendBarListener!=null&&suspendBarListener.isGroup(firstVisPos)) {
                        mCurrentPosition = firstVisPos;
                        //?????? mCurrentPosition ??????????????? mSuspensionBar

                        suspendBarListener.updataView(mSuspensionBar,mCurrentPosition);
                        if(!suspendBarListener.isGroup(nextposition)){
                            mSuspensionBar.setY(topY);
                        }
                    }
                } else {//???????????????
                    // 1???nextItem -> Post and firstVisibleItem -> Comment       mCurrentPosition = ((Comment) firstVisibleItem).getParentPostPosition()
                    // 2???nextItem -> Post and firstVisibleItem -> Post          mCurrentPosition = firstVisPos
                    // 3???nextItem -> Comment and firstVisibleItem -> Comment    mSuspensionBar ??????
                    // 4???nextItem -> Comment and firstVisibleItem -> Post       mSuspensionBar ??????
                    if (suspendBarListener!=null&&suspendBarListener.isGroup(nextposition)) {
                        mCurrentPosition = firstVisPos;
                        suspendBarListener.updataView(mSuspensionBar,mCurrentPosition);

                        if (nextView.getTop() <= mSuspensionHeight) {
                            //??????????????????
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
                //??????????????????
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
     * ???recyclerView???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
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




