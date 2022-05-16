package cool.dingstock.appbase.customerview.expand;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


public class SlideTouchRecyclerView extends RecyclerView {
    private static final String TAG = SlideTouchRecyclerView.class.getName();
    private static final int VERTICAL = 1;
    private static final int HORIZONTAL = 2;
    private static final int UNKNOW = 0;


    private float startX,startY;
    private int orientation;

    public SlideTouchRecyclerView(@NonNull Context context) {
        super(context);
    }

    public SlideTouchRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SlideTouchRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX=e.getX();
                startY=e.getY();
                orientation=UNKNOW;//初始化滑动方向
                break;
            case MotionEvent.ACTION_MOVE://处理滑动事件，先确定滑动方向。如果是水平忽略垂直变化，如果是垂直忽略水平变化
                float offsetX = Math.abs(e.getX()-startX);
                float offsetY = Math.abs(e.getY()-startY);
                if(orientation==UNKNOW){
                    if(offsetX> ViewConfiguration.get(getContext()).getScaledTouchSlop()){
                        orientation=HORIZONTAL;
                    }

                    if(offsetY>ViewConfiguration.get(getContext()).getScaledTouchSlop()){
                        orientation=VERTICAL;
                    }

                }
                switch (orientation){
                    case VERTICAL:
                        e.setLocation(startX,e.getY());
                        break;
                    case HORIZONTAL:
                        e.setLocation(e.getX(),startY);
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                orientation=UNKNOW;//初始化滑动方向
                break;
        }
        return  super.onInterceptTouchEvent(e);

    }


    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (getOnFocusChangeListener() != null) {
            getOnFocusChangeListener().onFocusChange(child, false);
            getOnFocusChangeListener().onFocusChange(focused, true);
        }
    }

    public boolean isScrollTop() {
        return computeVerticalScrollOffset() == 0;
    }

}
