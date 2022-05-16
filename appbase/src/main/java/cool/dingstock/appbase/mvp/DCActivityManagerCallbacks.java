package cool.dingstock.appbase.mvp;

import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Process;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import cool.dingstock.appbase.R;
import cool.dingstock.appbase.util.DarkModeUtilKt;
import cool.dingstock.appbase.util.StatusBarUtil;
import cool.dingstock.lib_base.util.Logger;


public class DCActivityManagerCallbacks implements Application.ActivityLifecycleCallbacks {

    private int activityStartCount;

    private static boolean needRestart;

    public static void setNeedRestart(boolean needRestart) {
        DCActivityManagerCallbacks.needRestart = needRestart;
    }

    private void setStatusBar(Activity activity) {
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        if (DarkModeUtilKt.isDarkMode(activity)) {
            StatusBarUtil.setDarkMode(activity);
            StatusBarUtil.setNavigationBarColor(activity, Color.BLACK);
            StatusBarUtil.setColor(activity, ContextCompat.getColor(activity, R.color.color_sec_bg), 1);
        } else {
            StatusBarUtil.setLightMode(activity);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Logger.d("Activity: " + activity.getComponentName().getClassName());
        if (!(activity instanceof BaseActivity)) {
            if (activity.getComponentName().getClassName().equals("io.rong.imkit.picture.PictureSelectorActivity")) {
                setStatusBar(activity);
            }
            Logger.w("This activity is not dc activity, so do noting.");
            return;
        }
        DCActivityManager.getInstance().add((BaseActivity) activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Logger.d("Activity: " + activity.getComponentName().getClassName());
        if (activityStartCount == 0) {
            Logger.i("DC foreground running---");
            DCActivityManager.getInstance().setForeground(true);
        }
        activityStartCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Logger.d("Activity: " + activity.getComponentName().getClassName());
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Logger.d("Activity: " + activity.getComponentName().getClassName());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Logger.d("Activity: " + activity.getComponentName().getClassName());
        activityStartCount--;
        if (activityStartCount == 0) {
            Logger.i("DC background running---");
            DCActivityManager.getInstance().setForeground(false);
            killMySelfIfNeed();
        }
    }


    private void killMySelfIfNeed() {
        if (needRestart) {
            Process.killProcess(Process.myPid());
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Logger.d("Activity: " + activity.getComponentName().getClassName());
    }


    @Override
    public void onActivityDestroyed(Activity activity) {
        Logger.d("Activity: " + activity.getComponentName().getClassName());
        if (!(activity instanceof BaseActivity)) {
            Logger.w("This activity is not dc activity, so do noting.");
            return;
        }
        DCActivityManager.getInstance().remove((BaseActivity) activity);
    }

}
