package cool.dingstock.appbase.widget.popwindows;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;

import cool.dingstock.appbase.R;
import cool.dingstock.lib_base.thread.ThreadPoolHelper;

public abstract class BackgroundDarkPopupWindow extends BasePopupWindow {

    protected View mDarkView;
    private WindowManager mWindowManager;
    private int mScreenWidth, mScreenHeight;
    private int mRightOf, mLeftOf, mBelow, mAbove;
    private int[] mLocationInWindowPosition = new int[2];
    private int mDarkStyle = -1;
    private WeakReference<View> mRightOfPositionView, mLeftOfPositionView, mBelowPositionView,
            mAbovePositionView, mFillPositionView;
    private boolean mIsDarkInvoked = false;

    public BackgroundDarkPopupWindow(Context context) {
        super(context);
        if (mRootView != null) {
            mWindowManager = (WindowManager) mRootView.getContext().getSystemService(Context.WINDOW_SERVICE);
            mDarkView = new View(mRootView.getContext());
            mDarkView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            mDarkView.setBackgroundColor(Color.parseColor("#a0000000"));
            DisplayMetrics dm = new DisplayMetrics();
            mWindowManager.getDefaultDisplay().getMetrics(dm);
            mScreenWidth = dm.widthPixels;
            mScreenHeight = dm.heightPixels;
        }


    }


    /**
     * create dark layout
     *
     * @param token
     * @return
     */
    private WindowManager.LayoutParams createDarkLayout(IBinder token) {
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        p.gravity = Gravity.START | Gravity.TOP;
        p.width = WindowManager.LayoutParams.MATCH_PARENT;
        p.height = WindowManager.LayoutParams.MATCH_PARENT;
        p.format = PixelFormat.TRANSLUCENT;
        p.flags = computeFlags(p.flags);
        p.type = WindowManager.LayoutParams.LAST_SUB_WINDOW;
        p.token = token;
        p.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED;
        return p;
    }

    private int computeFlags(int curFlags) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            curFlags &= ~(
                    WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES |
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                            WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
                            WindowManager.LayoutParams.FLAG_SPLIT_TOUCH);
        } else {
            curFlags &= ~(
                    WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES |
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                            WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }
        curFlags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        curFlags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        return curFlags;
    }

    /**
     * get dark animation
     *
     * @return
     */
    private int computeDarkAnimation() {
        if (mDarkStyle == -1) {
            return R.style.DarkAnimation;
        }
        return mDarkStyle;
    }

    @Override
    public void showAsDropDown(final View anchor, final int xoff, final int yoff) {
        mRootView.clearAnimation();
        mRootView.startAnimation(createInAnimation());
        invokeBgCover(anchor);
        super.showAsDropDown(anchor, xoff, yoff);

    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        invokeBgCover(parent);
        super.showAtLocation(parent, gravity, x, y);
    }

    /**
     * show dark background
     */
    private void invokeBgCover(View view) {
        if (mIsDarkInvoked || isShowing() || getContentView() == null) {
            return;
        }
        checkPosition();
        if (mDarkView != null) {
            WindowManager.LayoutParams darkLP = createDarkLayout(view.getWindowToken());
            computeDarkLayout(darkLP);
            darkLP.windowAnimations = computeDarkAnimation();
            mWindowManager.addView(mDarkView, darkLP);
            mIsDarkInvoked = true;
        }
    }

    /**
     * check whether the position of dark is set
     */
    private void checkPosition() {
        checkPositionLeft();
        checkPositionRight();
        checkPositionBelow();
        checkPositionAbove();
        checkPositionFill();
    }

    /**
     * check whether the left-of-position of dark is set
     */
    private void checkPositionLeft() {
        if (mLeftOfPositionView != null) {
            View leftOfView = mLeftOfPositionView.get();
            if (leftOfView != null && mLeftOf == 0) {
                drakLeftOf(leftOfView);
            }
        }
    }

    /**
     * check whether the right-of-position of dark is set
     */
    private void checkPositionRight() {
        if (mRightOfPositionView != null) {
            View rightOfView = mRightOfPositionView.get();
            if (rightOfView != null && mRightOf == 0) {
                darkRightOf(rightOfView);
            }
        }
    }

    /**
     * check whether the below-position of dark is set
     */
    private void checkPositionBelow() {
        if (mBelowPositionView != null) {
            View belowView = mBelowPositionView.get();
            if (belowView != null && mBelow == 0) {
                darkBelow(belowView);
            }
        }
    }

    /**
     * check whether the above-position of dark is set
     */
    private void checkPositionAbove() {
        if (mAbovePositionView != null) {
            View aboveView = mAbovePositionView.get();
            if (aboveView != null && mAbove == 0) {
                darkAbove(aboveView);
            }
        }
    }

    /**
     * check whether the fill-position of dark is set
     */
    private void checkPositionFill() {
        if (mFillPositionView != null) {
            View fillView = mFillPositionView.get();
            if (fillView != null && (mLeftOf == 0 || mAbove == 0)) {
                drakFillView(fillView);
            }
        }
    }

    /**
     * reset dark position
     */
    private void computeDarkLayout(WindowManager.LayoutParams darkLP) {
        darkLP.x = mRightOf;
        darkLP.y = mBelow;
        darkLP.width = mLeftOf - mRightOf;
        darkLP.height = mAbove - mBelow;
    }


    @Override
    public void dismiss() {
        mRootView.clearAnimation();
        mRootView.startAnimation(createOutAnimation());
        if (mDarkView != null && mIsDarkInvoked) {
            mWindowManager.removeViewImmediate(mDarkView);
            mIsDarkInvoked = false;
        }
        ThreadPoolHelper.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BackgroundDarkPopupWindow.super.dismiss();
            }
        }, ANIMATION_IN_TIME);
    }

    /**
     * set dark color
     *
     * @param color
     */
    public void setDarkColor(int color) {
        if (mDarkView != null) {
            mDarkView.setBackgroundColor(color);
        }
    }

    public void resetDarkPosition() {
        darkFillScreen();
        if (mRightOfPositionView != null) {
            mRightOfPositionView.clear();
        }
        if (mLeftOfPositionView != null) {
            mLeftOfPositionView.clear();
        }
        if (mBelowPositionView != null) {
            mBelowPositionView.clear();
        }
        if (mAbovePositionView != null) {
            mAbovePositionView.clear();
        }
        if (mFillPositionView != null) {
            mFillPositionView.clear();
        }
        mRightOfPositionView = mLeftOfPositionView = mBelowPositionView = mAbovePositionView =
                mFillPositionView = null;
    }

    /**
     * fill screen
     */
    public void darkFillScreen() {
        mRightOf = 0;
        mLeftOf = mScreenWidth;
        mAbove = mScreenHeight;
        mBelow = 0;
    }

    /**
     * dark fill view
     *
     * @param view target view
     */
    public void drakFillView(View view) {
        mFillPositionView = new WeakReference<>(view);
        view.getLocationInWindow(mLocationInWindowPosition);
        mRightOf = mLocationInWindowPosition[0];
        mLeftOf = mLocationInWindowPosition[0] + view.getWidth();
        mAbove = mLocationInWindowPosition[1] + view.getHeight();
        mBelow = mLocationInWindowPosition[1];
    }

    /**
     * dark right of target view
     *
     * @param view
     */
    public void darkRightOf(View view) {
        mRightOfPositionView = new WeakReference<>(view);
        view.getLocationInWindow(mLocationInWindowPosition);
        mRightOf = mLocationInWindowPosition[0] + view.getWidth();
    }

    /**
     * dark left of target view
     *
     * @param view
     */
    public void drakLeftOf(View view) {
        mLeftOfPositionView = new WeakReference<>(view);
        view.getLocationInWindow(mLocationInWindowPosition);
        mLeftOf = mLocationInWindowPosition[0];
    }

    /**
     * dark above target view
     *
     * @param view
     */
    public void darkAbove(View view) {
        mAbovePositionView = new WeakReference<>(view);
        view.getLocationInWindow(mLocationInWindowPosition);
        mAbove = mLocationInWindowPosition[1];
    }

    /**
     * dark below target view
     *
     * @param view
     */
    public void darkBelow(View view) {
        mBelowPositionView = new WeakReference<>(view);
        view.getLocationInWindow(mLocationInWindowPosition);
        mBelow = mLocationInWindowPosition[1] + view.getHeight();
    }

    /**
     * get dark anim style
     *
     * @return
     */
    public int getDarkStyle() {
        return mDarkStyle;
    }

    /**
     * set dark anim style
     *
     * @param darkStyle
     */
    public void setDarkStyle(int darkStyle) {
        this.mDarkStyle = darkStyle;
    }


    public final static int ANIMATION_IN_TIME = 100;


    public Animation createInAnimation() {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 0, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(ANIMATION_IN_TIME);
        animation.setFillAfter(true);

        return animation;
    }

    public Animation createOutAnimation() {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 0, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
        animation.setDuration(ANIMATION_IN_TIME);
        animation.setFillAfter(true);
        return animation;
    }
}