package cool.dingstock.appbase.mvp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.umeng.analytics.MobclickAgent;

import cool.dingstock.appbase.BuildConfig;
import cool.dingstock.appbase.R;
import cool.dingstock.appbase.constant.HomeConstant;
import cool.dingstock.appbase.helper.ADTShowTimeHelper;
import cool.dingstock.appbase.helper.PartyVerifyHelper;
import cool.dingstock.appbase.router.DcUriRequest;
import cool.dingstock.appbase.toast.TopToast;
import cool.dingstock.appbase.util.ActivityHook;
import cool.dingstock.appbase.util.DarkModeUtilKt;
import cool.dingstock.appbase.util.StatusBarUtil;
import cool.dingstock.appbase.widget.dialog.RKAlertDialog;
import cool.dingstock.lib_base.thread.ThreadPoolHelper;
import cool.dingstock.lib_base.util.Logger;


public abstract class BaseActivity extends AppCompatActivity {

    private static final int SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT = 0x00000010;
    public ViewGroup mRootView;
    protected ViewGroup mContentView;


    /**
     * Set up the Activity ModuleTag
     */
    public abstract String moduleTag();


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //添加打印日志
//        logStack();
        if (lockOrientation()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        //修复8.0崩溃的问题
        ActivityHook.hookOrientation(this);
        super.onCreate(savedInstanceState);
        if (setSysBar()) {
            setSystemStatusBar();
            setSystemNavigationBar();
        }
    }

    protected boolean lockOrientation() {
        return true;
    }

    private void logStack() {
        if (!BuildConfig.DEBUG) {
            return;
        }
        if (getUri() != null) {
            StackTraceElement ste = null;
            for (StackTraceElement stackTraceElement : new Throwable().getStackTrace()) {
                if (stackTraceElement.getClassName().equals(getClass().getName())) {
                    ste = stackTraceElement;
                    break;
                }
            }
            if (ste != null) {
                StackTraceElement[] arr = new StackTraceElement[]{ste};
                Throwable throwable = new Throwable();
                throwable.setStackTrace(arr);
                String stackTraceString = Log.getStackTraceString(throwable);
                Logger.d("activity create by router===:", stackTraceString + "============");
                Logger.d("activity create by router===:", getUri() + " ----------------------------------------------------");
            }

        }
    }

    public Uri getUri() {
        if (null == getIntent()) {
            Logger.d("The intent is empty.");
            return null;
        }

        return getIntent().getData();
    }

    protected boolean setSysBar() {
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 系统状态栏字体颜色设置
     */
    protected void setSystemStatusBar() {
        setSystemStatusBarMode();
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.white), 0);
    }

    protected void setSystemNavigationBar() {
        setSystemNavigationBarMode();
        StatusBarUtil.setNavigationBarColor(this, ContextCompat.getColor(this, R.color.white));
    }

    protected void setSystemStatusBarMode() {
        if (DarkModeUtilKt.isDarkMode(this)) {
            StatusBarUtil.setDarkMode(this);
        } else {
            StatusBarUtil.setLightMode(this);
        }
    }

    protected void setSystemNavigationBarMode() {
        if (DarkModeUtilKt.isDarkMode(this)) {
            StatusBarUtil.setNavigationBarDarkMode(this);
        } else {
            StatusBarUtil.setNavigationBarLightMode(this);
        }
    }

    public AlertDialog.Builder makeAlertDialog() {
        return RKAlertDialog.make(this);
    }

    protected void initRootView() {
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkShowAd();
    }

    protected boolean ignoreCheckAd() {
        return false;
    }

    /**
     * 强制显示广告
     */
    private void checkShowAd() {
//        if (SplashHelper.INSTANCE.isBack2Font()) {
//            SplashHelper.INSTANCE.updateAppToBackState(false);
//            ADTShowTimeHelper.INSTANCE.updateCheckTime();
//            if (ignoreCheckAd()) {
//                return;
//            }
//            if (!ADTShowTimeHelper.INSTANCE.checkNeedEnterAdtPage()) {
//                return;
//            }
//            Intent intent = new Intent();
//            intent.setClassName(this, "cool.dingstock.mobile.activity.BootActivity");
//            Bundle bundle = new Bundle();
//            bundle.putBoolean(HomeConstant.UriParam.IS_BACK2FONT, true);
//            intent.putExtras(bundle);
//            try {
//                startActivity(intent);
//            } catch (Exception e) {
//            }
//        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && enablePartyVerify()) {
            PartyVerifyHelper.INSTANCE.verify(this);
        }
    }

    @Override
    public void finish() {
        super.finish();
        setupOutAnim();
    }

    protected void setupOutAnim() {
        //移除向左移动的退场动画
        if (isBottomPop()) {
            overridePendingTransition(0, R.anim.dialog_out_bottom);
        } else {
            if (enableEnablePendingTransition()) {
                overridePendingTransition(R.anim.on_activity_close_enter, R.anim.on_activity_close_exit);
            }
        }
    }

    public void finishHide() {
        super.finish();
        overridePendingTransition(0, R.anim.on_activity_close_hide);

    }


    protected boolean enableEnablePendingTransition() {
        return true;
    }

    protected boolean isBottomPop() {
        return false;
    }

    protected boolean enablePartyVerify() {
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        Logger.e("UMLOG_DC", getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);       //统计时长
        MobclickAgent.onPageStart(getClass().getSimpleName());
        Logger.e("UMLOG_DC", getClass().getSimpleName());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public String getUriSite() {
        if (null == getUri()) {
            Logger.w(this.getClass().getSimpleName() + " ,The Uri is empty.");
            return "";
        }

        String uriSite = getUri().getScheme() + "://" + getUri().getHost() + getUri().getPath();
        Logger.d(this.getClass().getSimpleName() + " ,UriSite: " + uriSite);

        return uriSite;
    }

    protected String[] getStringArray(@ArrayRes int id) {
        return getResources().getStringArray(id);
    }

    protected int getCompatColor(@ColorRes int resId) {
        return ContextCompat.getColor(this, resId);
    }

    protected int getCompatColor(String colorStr) {
        return Color.parseColor(colorStr);
    }

    protected Drawable getCompatDrawable(@DrawableRes int id) {
        return ContextCompat.getDrawable(this, id);
    }

    public DcUriRequest DcRouter(@NonNull String uri) {
        Logger.i(this.getClass().getSimpleName() + " Begin to start activity Uri: " + uri);
        return new DcUriRequest(this, uri);
    }

    public void showToastShort(final @StringRes int resId) {
        showToastShort(getString(resId));
    }

    /**
     * Please use the new method. See the {@link #showToastShort(int)}
     */
    public void showToastShort(final CharSequence text) {
        TopToast.INSTANCE.showToast(getContext(), text.toString(), Toast.LENGTH_SHORT);
    }

    // Here is some Resource
    protected Context getContext() {
        return this;
    }

    public void showToastLong(final @StringRes int resId) {
        showToastLong(getString(resId));
    }

    /**
     * Please use the new method. See the {@link #showToastLong(int)}
     */
    public void showToastLong(final CharSequence text) {
        Logger.i(this.getClass().getSimpleName() + " The toast context: " + text);
        ThreadPoolHelper.getInstance().runOnUiThread(() -> TopToast.INSTANCE.showToast(getContext(), text.toString(), Toast.LENGTH_LONG));
    }

    public void showToastShort(final @StringRes int resId, Object... formatArgs) {
        showToastShort(getString(resId, formatArgs));
    }

    public void showToastLong(final @StringRes int resId, Object... formatArgs) {
        showToastLong(getString(resId, formatArgs));
    }

    @Override
    public void onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
        setupOutAnim();

    }
}

