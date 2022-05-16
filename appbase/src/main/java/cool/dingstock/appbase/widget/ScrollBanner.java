package cool.dingstock.appbase.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;

import cool.dingstock.appbase.R;
import cool.dingstock.lib_base.util.Logger;

public class ScrollBanner extends LinearLayout {

    private TextView mBannerTV1;
    private TextView mBannerTV2;
    private LinkedList<String> textList = new LinkedList<>();

    private boolean view1Running;
    private boolean view2Running;
    private Handler handler = new Handler();

    public ScrollBanner(Context context) {
        this(context, null);
    }

    public ScrollBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public ScrollBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.view_scroll_banner, this);
        mBannerTV1 = view.findViewById(R.id.tv_banner1);
        mBannerTV1.setTag(1);
        mBannerTV2 = view.findViewById(R.id.tv_banner2);
        mBannerTV2.setTag(2);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    public void enqueue(String text) {
        textList.add(text);
        if (view2Running && view1Running) {
            Logger.w("all view running so wait ..");
            return;
        }
        dequeue();
    }

    private void dequeue() {
        if (textList.isEmpty()) {
            return;
        }
        String text = textList.pop();
        TextView idleView = getIdleView();
        if (null == idleView) {
            return;
        }
        int pos = (Integer) idleView.getTag();
        switch (pos) {
            case 1:
                view1Running = true;
                break;
            case 2:
                view2Running = true;
                break;
        }

        Logger.d("text = " + text + " running viewTag=" + idleView.getTag());
        handler.postDelayed(() -> {
            idleView.setText(text);
            TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, 0,
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0.8f,
                    1, 0.8f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(translateAnimation);
            animationSet.addAnimation(alphaAnimation);
            animationSet.addAnimation(scaleAnimation);
            animationSet.setDuration(700);
            animationSet.setFillAfter(true);
            animationSet.start();
            idleView.startAnimation(animationSet);
            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (1 == (Integer) idleView.getTag()) {
                        view1Running = false;
                    }
                    if (2 == (Integer) idleView.getTag()) {
                        view2Running = false;
                    }
                    dequeue();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }, view1Running && view2Running ? 50 : 0);
    }


    private TextView getIdleView() {
        if (!view1Running) {
            return mBannerTV1;
        }
        if (!view2Running) {
            return mBannerTV2;
        }
        Logger.w("ALL TEXT RUNNING");
        return null;
    }


    private void stop() {
        view2Running = false;
        view1Running = false;
        mBannerTV2.clearAnimation();
        mBannerTV1.clearAnimation();
        textList.clear();
    }

}