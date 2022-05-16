package cool.dingstock.appbase.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import cool.dingstock.appbase.R;


public class LoadingView extends FrameLayout {

    private List<ScaleAnimation> scaleAnimationList = new ArrayList<>();

    private ImageView dot1;
    private ImageView dot2;
    private ImageView dot3;

    private boolean outStatus;
    private boolean isRunning;

    public LoadingView(@NonNull Context context) {
        this(context, null);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (outStatus) {
            startAnim();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAnim(false);
    }

    private void initView() {
        View rootView = View.inflate(getContext(), R.layout.common_layout_loading, null);
        dot1 = rootView.findViewById(R.id.common_layout_dot1);
        dot2 = rootView.findViewById(R.id.common_layout_dot2);
        dot3 = rootView.findViewById(R.id.common_layout_dot3);
        addView(rootView);
    }


    public void startAnim() {
        outStatus = true;
        if (isRunning) {
            return;
        }
        isRunning = true;
        startAnim(dot1, 0);
        startAnim(dot2, 200);
        startAnim(dot3, 400);
    }


    private void startAnim(View view, long delay) {
        postDelayed(() -> {
            ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0f, 1f, 0f,
                    ScaleAnimation.ABSOLUTE, view.getWidth() / 2f,
                    ScaleAnimation.ABSOLUTE, view.getHeight() / 2f);
            scaleAnimation.setDuration(600);
            scaleAnimation.setRepeatCount(ScaleAnimation.INFINITE);
            scaleAnimation.setRepeatMode(ScaleAnimation.REVERSE);
            view.startAnimation(scaleAnimation);
            scaleAnimationList.add(scaleAnimation);
        }, delay);
    }


    public void endAnim() {
        cancelAnim(true);
    }

    public void cancelAnim(boolean byUser) {
        if (byUser) {
            outStatus = false;
        }
        isRunning = false;
        for (ScaleAnimation scaleAnimation : scaleAnimationList) {
            scaleAnimation.cancel();
        }
        scaleAnimationList.clear();
        dot1.clearAnimation();
        dot2.clearAnimation();
        dot3.clearAnimation();
    }


}
