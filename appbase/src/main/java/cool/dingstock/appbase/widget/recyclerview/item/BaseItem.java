package cool.dingstock.appbase.widget.recyclerview.item;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import cool.dingstock.appbase.router.DcUriRequest;
import cool.dingstock.appbase.toast.TopToast;
import cool.dingstock.lib_base.util.Logger;

public abstract class BaseItem<D, VB extends ViewBinding> {
    
    public static final int DEFAULT_MIN_STATES_TYPE = 3000000;
    
    protected BaseViewHolder mHolder;
    
    protected D mData;
    
    private int position;

    public VB viewBinding;
    
    public BaseItem() {
    
    }
    
    public int getPosition() {
        return position;
    }
    
    public void setPosition(int position) {
        this.position = position;
    }
    
    public BaseItem(D data) {
        this.mData = data;
    }

    public abstract int getViewType();

    /**
     * Create the View from layout.xml
     */
    public final BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*if (getLayoutId(viewType) <= 0) {
            throw new RvAdapterException("This Layout Id is invalid.");
        }*/
        VB vb = null;
        try {
            ParameterizedType pt = (ParameterizedType) getClass().getGenericSuperclass();
            if (pt != null) {
                Type[] types = pt.getActualTypeArguments();
                Class<VB> cls = (Class<VB>) types[1];
                Method method = cls.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
                vb = (VB)method.invoke(null, LayoutInflater.from(parent.getContext()), parent, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (vb != null) {
            BaseViewHolder vh = new BaseViewHolder(vb.getRoot());
            vh.itemView.setTag(vb);
            return new BaseViewHolder(vb.getRoot());
        } else {
            return null;
        }
    }

    public abstract int getLayoutId(int viewType);

    /**
     * Bind the data to ViewHolder
     */
    public final void onBindViewHolder(BaseViewHolder holder, int sectionKey, int sectionViewPosition) {
        this.mHolder = holder;
        viewBinding = (VB)holder.itemView.getTag();
        onReleaseViews(holder, sectionKey, sectionViewPosition);
        if (null == mData) {
            Logger.e("This data is empty.");
            return;
        }
        onSetViewsData(holder, sectionKey, sectionViewPosition);
    }

    /**
     * 重置 Views 数据，避免重用时 数据冲突
     */
    public abstract void onReleaseViews(BaseViewHolder holder, int sectionKey, int sectionViewPosition);

    /**
     * 设置 Views 数据
     */
    public abstract void onSetViewsData(BaseViewHolder holder, int sectionKey, int sectionViewPosition);

    public void releaseResource() {
    }

    public D getData() {
        return mData;
    }

    public void setData(D data) {
        this.mData = data;
    }

    public BaseViewHolder getHolder() {
        return mHolder;
    }

    public Context getContext() {
        if (null == getHolder()) {
            return null;
        }

        return getHolder().getContext();
    }

    public DcUriRequest Router(@NonNull String uri) {
        return new DcUriRequest(getContext(), uri);
    }

    public String getString(@StringRes int resId) {
        if (null == getContext()) {
            return null;
        }

        return getContext().getString(resId);
    }

    public String getString(@StringRes int resId, Object... formatArgs) {
        if (null == getContext()) {
            return null;
        }

        return getContext().getString(resId, formatArgs);
    }

    public String[] getStringArray(@ArrayRes int resId) {
        if (null == getContext()) {
            return null;
        }

        return getContext().getResources().getStringArray(resId);
    }

    public String getIcon(@StringRes int resId) {
        if (null == getContext()) {
            return null;
        }

        return getContext().getString(resId);
    }

    public int getColor(@ColorRes int resId) {
        return ContextCompat.getColor(getContext(), resId);
    }

    public Drawable getDrawable(@DrawableRes int resId) {
        return ContextCompat.getDrawable(getContext(), resId);
    }

    public Resources getResources() {
        if (null == getContext()) {
            return null;
        }

        return getContext().getResources();
    }


    public void showToastShort(final @StringRes int resId) {
        showToastShort(getString(resId));
    }

    public void showToastShort(final CharSequence text) {
        Logger.i(this.getClass().getSimpleName() + " The toast context: " + text);
        TopToast.INSTANCE.showToast(getContext(), text.toString(), Toast.LENGTH_SHORT);
    }

}
