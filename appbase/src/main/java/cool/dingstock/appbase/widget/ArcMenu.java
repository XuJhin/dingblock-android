package cool.dingstock.appbase.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;

import cool.dingstock.appbase.R;
import cool.dingstock.lib_base.util.Logger;


public class ArcMenu extends RelativeLayout {

    private static final int POS_LEFT_TOP = 0;
    private static final int POS_LEFT_BOTTOM = 1;
    private static final int POS_RIGHT_TOP = 2;
    private static final int POS_RIGHT_BOTTOM = 3;

    private static final int STATUS_CLOSE = 0;
    private static final int STATUS_OPEN = 1;

    private int mPosition;
    private int mRadius;
    private int mStatus = STATUS_CLOSE;


    private OnMenuItemClickListener mMenuItemClickListener;

    public void setOnMenuItemClickListener(
            OnMenuItemClickListener l) {
        this.mMenuItemClickListener = l;
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                100, getResources().getDisplayMetrics());

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.ArcMenu, defStyle, 0);
        mPosition = typedArray.getInt(R.styleable.ArcMenu_position,
                POS_RIGHT_BOTTOM);
        mRadius = (int) typedArray.getDimension(R.styleable.ArcMenu_radius,
                mRadius);
        typedArray.recycle();

        Logger.d("mPosition = " + mPosition + ", mRadius = " + mRadius);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcMenu(Context context) {
        this(context, null, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            layoutButton();
        }
    }

    private void layoutButton() {
        int count = getChildCount();
        View childViewIndex0 = getChildAt(0);
        int menuLeft = 0;
        int menuTop = 0;
        int menuWidth = childViewIndex0.getMeasuredWidth();
        int menuHeight = childViewIndex0.getMeasuredHeight();
        switch (mPosition) {
            case POS_LEFT_TOP:
                break;
            case POS_LEFT_BOTTOM:
                menuTop = getMeasuredHeight() - menuHeight;
                break;
            case POS_RIGHT_TOP:
                menuLeft = getMeasuredWidth() - menuWidth;
                break;
            case POS_RIGHT_BOTTOM:
                menuTop = getMeasuredHeight() - menuHeight;
                menuLeft = getMeasuredWidth() - menuWidth;
                break;

        }
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            childView.layout(menuLeft, menuTop, menuLeft + menuWidth, menuTop + menuHeight);
            final int pos = i;
            childView.setVisibility(GONE);
            childView.setOnClickListener(v -> {
                if (mMenuItemClickListener != null) {
                    mMenuItemClickListener.onItemClick(v, pos);
                }
            });
        }
    }

    public interface OnMenuItemClickListener {
        void onItemClick(View view, int position);
    }

    public void toggleMenu(AnimatorListenerAdapter adapter) {
        int itemCount = getChildCount();
        changeStatus();
        for (int i = 0; i < itemCount; i++) {
            final View child = getChildAt(i);
            int x = (int) (mRadius * Math.sin(Math.PI / 2 / (itemCount - 1)
                    * i));
            int y = (int) (mRadius * Math.cos(Math.PI / 2 / (itemCount - 1)
                    * i));

            int xflag = 1;
            int yflag = 1;
            if (mPosition == POS_RIGHT_TOP || mPosition == POS_RIGHT_BOTTOM) {
                xflag = -1;
            }
            if (mPosition == POS_LEFT_BOTTOM || mPosition == POS_RIGHT_BOTTOM) {
                yflag = -1;
            }

            child.clearAnimation();

            ObjectAnimator transXAnimator;
            ObjectAnimator transYAnimator;
            ObjectAnimator rotationAnimator;
            ObjectAnimator alphaAnimator;

            AnimatorSet animatorSet = new AnimatorSet();

            if (mStatus != STATUS_CLOSE) {
                // 设置弹出动画
                transXAnimator = ObjectAnimator.ofFloat(child, "translationX", 0, x * xflag);
                transYAnimator = ObjectAnimator.ofFloat(child, "translationY", 0, y * yflag);
                alphaAnimator = ObjectAnimator.ofFloat(child, "alpha", 0, 1);


            } else {
                // 设置收回的动画
                transXAnimator = ObjectAnimator.ofFloat(child, "translationX", x * xflag, 0);
                transYAnimator = ObjectAnimator.ofFloat(child, "translationY", y * yflag, 0);
                alphaAnimator = ObjectAnimator.ofFloat(child, "alpha", 1, 0);
            }
            rotationAnimator = ObjectAnimator.ofFloat(child, "rotation", 0, 360f);
            animatorSet.playTogether(alphaAnimator, transXAnimator, transYAnimator, rotationAnimator);
            animatorSet.setDuration(400);
            child.setVisibility(View.VISIBLE);

            int finalI = i;

            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (mStatus == STATUS_CLOSE) {
                        child.setVisibility(GONE);
                    }
                    if (null != adapter && finalI == itemCount - 1) {
                        adapter.onAnimationEnd(animation);
                    }
                }
            });
            animatorSet.start();
        }

    }


    private void changeStatus() {
        mStatus = (mStatus == STATUS_CLOSE ? STATUS_OPEN : STATUS_CLOSE);
    }


}