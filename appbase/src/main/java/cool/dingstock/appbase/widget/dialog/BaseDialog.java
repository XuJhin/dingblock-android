package cool.dingstock.appbase.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import cool.dingstock.lib_base.util.Logger;


public abstract class BaseDialog extends Dialog {

    protected View mRootView;

    protected Context mContext;

    public BaseDialog(@NonNull Context context) {
        this(context, 0);
    }

    public BaseDialog(@NonNull Context context, int theme) {
        super(context, theme);
        mContext = context;
        mRootView = View.inflate(context, getLayoutId(), null);
        setContentView(mRootView);
        initViews();

        Window window = getWindow();
        if (null == window) {
            return;
        }
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setDialogConfig(window);

    }


    public abstract int getLayoutId();

    public abstract void initViews();

    public abstract void setDialogConfig(Window window);

    public void setBackgroundColorInt(int colorId) {
        if (null == getWindow() || null == getWindow().getDecorView()) {
            Logger.w("This window is Empty, so do nothing.");
            return;
        }
        getWindow().getDecorView().setBackgroundColor(colorId);
    }

    public void setBackgroundColor(@ColorRes int resId) {
        if (null == getWindow() || null == getWindow().getDecorView()) {
            Logger.w("This window is Empty, so do nothing.");
            return;
        }
        getWindow().getDecorView().setBackgroundColor(getColor(resId));
    }

    public void setBackground(@DrawableRes int resId) {
        if (null == getWindow() || null == getWindow().getDecorView()) {
            Logger.w("This window is Empty, so do nothing.");
            return;
        }

        getWindow().getDecorView().setBackground(getDrawable(resId));
    }

    protected String getString(@StringRes int resId) {
        return getContext().getString(resId);
    }

    protected int getColor(@ColorRes int resId) {
        return ContextCompat.getColor(getContext(), resId);
    }

    protected Drawable getDrawable(@DrawableRes int resId) {
        return ContextCompat.getDrawable(getContext(), resId);
    }


}
