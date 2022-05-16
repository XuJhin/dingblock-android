package cool.dingstock.appbase.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import cool.dingstock.lib_base.util.Logger;


public class AnimUtils {

    /**
     * 旋转动画
     */
    public static void startDefaultRotateAnim(View view) {
        RotateAnimation rotateAnimation = new RotateAnimation(0,
                360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(400);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setFillAfter(true);
        view.startAnimation(rotateAnimation);
    }

    public static void startObjectRotateAnim(View view) {
        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(view, "rotation", 0f, 360.0f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(-1);//设置动画重复次数
        rotateAnimation.setRepeatMode(ValueAnimator.RESTART);//动画重复模式
        rotateAnimation.start();//开始动画
    }

    public static void fadeOutAnimate(final View view, final int duration,Animator.AnimatorListener listener) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
        anim.setDuration(duration);
        anim.start();
        anim.addListener(listener);
    }

    /**
     * 渐隐渐显动画
     *
     * @param view
     * @param duration
     * @param listener
     */
    public static void fadeInOutAnimate(final View view, final int duration, final fadeInOutListener listener) {
        final AlphaAnimation fadeInAnim = new AlphaAnimation(0, 1);
        fadeInAnim.setDuration(duration);
        fadeInAnim.setFillAfter(true);
        fadeInAnim.setInterpolator(new DecelerateInterpolator(1.5f));
        fadeInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Logger.d("fade in animation end");
                listener.onFadeInEnd();

                AlphaAnimation fadeOutAnim = new AlphaAnimation(1, 0);
                fadeOutAnim.setDuration(duration);
                fadeOutAnim.setFillAfter(true);
                fadeOutAnim.setInterpolator(new AccelerateInterpolator(1.5f));
                fadeOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Logger.d("fade out animation end");
                        listener.onFadeOutEnd();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                view.startAnimation(fadeOutAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Logger.d("begin fade in out animation");
        view.startAnimation(fadeInAnim);
    }

    public interface fadeInOutListener {
        void onFadeInEnd();

        void onFadeOutEnd();
    }

}
