package cool.dingstock.appbase.widget.popwindows;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import java.lang.ref.WeakReference;

import cool.dingstock.appbase.router.DcRouterUtils;
import cool.dingstock.appbase.toast.TopToast;
import cool.dingstock.lib_base.thread.ThreadPoolHelper;
import cool.dingstock.lib_base.util.Logger;

public abstract class BasePopupWindow extends PopupWindow {

    protected WeakReference<Context> mContextWeak;
    protected View mRootView;

    public BasePopupWindow(@NonNull Context context) {
        this.mContextWeak = new WeakReference<>(context);
        if (getLayoutId() <= 0) {
            Logger.e("The Layout id is invalid.");
            return;
        }
        mRootView = View.inflate(context, getLayoutId(), null);
        initBasePopupWindow();
        initViews();
        initListener();
    }

    private void initBasePopupWindow() {
        setContentView(mRootView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setOutsideTouchable(false);
        setBackgroundDrawable(new BitmapDrawable());

        setPopupWindowConfig();
    }

    protected abstract int getLayoutId();

    protected abstract void setPopupWindowConfig();

    protected abstract void initViews();

    protected void initListener() {
    }

    public void showAtLocation(int gravity, int x, int y) {
        if (null == mContextWeak) {
            Logger.w("showAtLocation mContextWeak is null");
            return;
        }
        if (!(mContextWeak.get() instanceof Activity) || ((Activity) mContextWeak.get()).isFinishing()) {
            Logger.e("The context is invalid.");
            return;
        }

        Window window = ((Activity) mContextWeak.get()).getWindow();
        View view = window.peekDecorView();
        // 并透明阴影
        setBackgroundAlpha(0.6f, window);
        super.showAtLocation(view, gravity, x, y);
    }


    public void hide() {
        dismiss();
    }

    @Override
    public void dismiss() {
        if (null == mContextWeak) {
            Logger.w("showAtLocation mContextWeak is null");
            return;
        }
        if (null == mContextWeak.get()) {
            return;
        }
        Window window = ((Activity) mContextWeak.get()).getWindow();
        if (null == window) {
            return;
        }
        setBackgroundAlpha(1.0f, window);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        super.dismiss();
    }

    protected Context getContext() {
        // TODO: 2017/7/12 优化 反射 去拿父类的 context
        return mContextWeak.get();
    }

    public DcRouterUtils.Builder Router(@NonNull String uri) {
        Logger.i(this.getClass().getSimpleName() + " Begin to start activity Uri: " + uri);
        return new DcRouterUtils.Builder(getContext(), uri);
    }

    public void showToastShort(final @StringRes int resId) {
        showToastShort(getString(resId));
    }

    public void showToastLong(final @StringRes int resId) {
        showToastLong(getString(resId));
    }

    /**
     * Please use the new method.
     * See the {@link #showToastShort(int)}
     */
    public void showToastShort(final CharSequence text) {
        Logger.i(this.getClass().getSimpleName() + " The toast context: " + text);
        ThreadPoolHelper.getInstance().runOnUiThread(() -> {
            TopToast.INSTANCE.showToast(getContext(), text.toString(), Toast.LENGTH_SHORT);
        });

    }

    /**
     * Please use the new method.
     * See the {@link #showToastLong(int)}
     */
    public void showToastLong(final CharSequence text) {
        Logger.i(this.getClass().getSimpleName() + " The toast context: " + text);
        ThreadPoolHelper.getInstance().runOnUiThread(() -> TopToast.INSTANCE.showToast(getContext(), text.toString(), Toast.LENGTH_LONG));

    }


    @NonNull
    protected String getString(@StringRes int resId) {
        return null != getContext() ? getContext().getString(resId) : "";
    }

    @NonNull
    protected String getString(@StringRes int resId, Object... formatArgs) {
        return null != getContext() ? getContext().getString(resId, formatArgs) : "";
    }

    protected String[] getStringArray(@ArrayRes int resId) {
        return null != getContext() ? getContext().getResources().getStringArray(resId) : null;
    }

    @NonNull
    protected String getIcon(@StringRes int resId) {
        return null != getContext() ? getContext().getString(resId) : "";
    }

    protected int getColor(@ColorRes int resId) {
        return null != getContext() ? ContextCompat.getColor(getContext(), resId) : -1;
    }

    protected Drawable getDrawable(@DrawableRes int resId) {
        return null != getContext() ? ContextCompat.getDrawable(getContext(), resId) : null;
    }

    protected Resources getResources() {
        return null != getContext() ? getContext().getResources() : null;
    }

    private void setBackgroundAlpha(float bgAlpha, Window window) {
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = bgAlpha;
        window.setAttributes(lp);
    }

}
