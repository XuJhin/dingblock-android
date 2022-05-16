package cool.dingstock.appbase.mvp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


import cool.dingstock.appbase.toast.TopToast;
import cool.dingstock.appbase.ut.UTHelper;
import cool.dingstock.lib_base.util.Logger;
import org.jetbrains.annotations.NotNull;

public abstract class BaseFragment extends Fragment {

    protected String pageId;
    private boolean hasPageStart;
    private boolean isStart;
    private boolean isVisible;


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onUserVisible();
        } else {
            onUserInvisible();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger.d(this.getClass().getSimpleName());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        isStart = true;
        onPageStart();
    }

    private void onPageStart() {
        if (ignoreUt()) {
            return;
        }
        if (!isStart || !isVisible) {
            return;
        }
        if (hasPageStart) {
            return;
        }
        if (TextUtils.isEmpty(pageId)) {
            if (TextUtils.isEmpty(tag())) {
                pageId = getClass().getSimpleName();
            }
        }
        UTHelper.onPageStart(pageId);
        hasPageStart = true;
    }

    protected String tag() {
        return null;
    }

    protected boolean ignoreUt() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        isStart = false;
        onPageEnd();
    }

    private void onPageEnd() {
        if (ignoreUt()) {
            return;
        }
        if (!hasPageStart) {
            return;
        }
        if (TextUtils.isEmpty(pageId)) {
            if (TextUtils.isEmpty(tag())) {
                pageId = getClass().getSimpleName();
            }
        }

        UTHelper.onPageEnd(pageId);
        hasPageStart = false;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onUserVisible() {
        isVisible = true;
        onPageStart();
    }

    public void onUserInvisible() {
        isVisible = false;
        onPageEnd();
    }

    public String getUriSite() {
        Logger.d(this.getClass().getSimpleName());
        if (null == getUri()) {
            Logger.w(this.getClass().getSimpleName() + " ,The uri is empty.");
            return "";
        }

        String uriSite = getUri().getScheme() + "://" + getUri().getHost() + getUri().getPath();
        Logger.d(this.getClass().getSimpleName() + " ,UriSite: " + uriSite);

        return uriSite;
    }

    public Uri getUri() {
        Logger.d(this.getClass().getSimpleName());
        if (null == getActivity() || null == getIntent()) {
            Logger.w("The activity or intent is empty.");
            return null;
        }

        return getIntent().getData();
    }

    public Intent getIntent() {
        Logger.d(this.getClass().getSimpleName());
        if (null == getActivity()) {
            return null;
        }

        return getActivity().getIntent();
    }

    public int getCompatColor(@ColorRes int resId) {
        return ContextCompat.getColor(getActivity(), resId);
    }

    public Drawable getCompatDrawable(@DrawableRes int id) {
        return ContextCompat.getDrawable(getActivity(), id);
    }

    public void showToastShort(final CharSequence text) {
        if (!getUserVisibleHint()) {
            Logger.w(this.getClass().getSimpleName() +
                    " ,This fragment is invisible to user, so can't show the toast.");
            return;
        }
        Logger.i(this.getClass().getSimpleName() + " ,The toast context: " + text);
        TopToast.INSTANCE.showToast(getContext(), text.toString(), Toast.LENGTH_SHORT);
    }

    public void showToastLong(final CharSequence text) {
        if (!getUserVisibleHint()) {
            Logger.w(this.getClass().getSimpleName() +
                    " ,This fragment is invisible to user, so can't show the toast.");
            return;
        }

        Logger.i(this.getClass().getSimpleName() + " ,The toast context: " + text);
        TopToast.INSTANCE.showToast(getContext(), text.toString(), Toast.LENGTH_LONG);
    }

    // 创建 loading、Error statusView
    protected void initStatusView() {
        Logger.w(this.getClass().getSimpleName() + " ,The status view is initializing.");
    }

    protected void runOnUiThread(Runnable action) {
        FragmentActivity activity = getActivity();
        if (null != activity) {
            activity.runOnUiThread(action);
        }
    }

    /**
     * 重新加载
     */
    public void reload() {

    }

    public void switchPages(Uri uri) {

    }

}
