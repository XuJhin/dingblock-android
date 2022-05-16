package cool.dingstock.appbase.mvp.lazy;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;


import java.util.List;

import com.kongzue.dialogx.dialogs.TipDialog;
import com.kongzue.dialogx.dialogs.WaitDialog;
import cool.dingstock.appbase.R;
import cool.dingstock.appbase.constant.AccountConstant;
import cool.dingstock.appbase.entity.bean.account.DcLoginUser;
import cool.dingstock.appbase.mvp.BaseFragment;
import cool.dingstock.appbase.net.api.account.AccountHelper;
import cool.dingstock.appbase.router.DcUriRequest;
import cool.dingstock.appbase.toast.TopToast;
import cool.dingstock.appbase.widget.dialog.RKAlertDialog;
import cool.dingstock.appbase.widget.stateview.StatusView;
import cool.dingstock.lib_base.util.Logger;
import cool.dingstock.lib_base.util.ScreenUtils;
import org.jetbrains.annotations.NotNull;

public abstract class LazyDcFragment<P extends DCLazyFragmentPresenter> extends BaseFragment {

    protected View rootView;
    protected P mPresenter;
    protected StatusView mStatusView;
    private boolean mIsFirstVisible = true;
    private boolean isViewCreated = false;
    private boolean currentVisibleState = false;
    protected boolean isAttach = false;

    public boolean getPageVisible() {
        return currentVisibleState;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewCreated = true;
        // !isHidden() 默认为 true  在调用 hide show 的时候可以使用
        if (!isHidden() && getUserVisibleHint()) {
            dispatchUserVisibleHint(true);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initVariables(view, null, savedInstanceState);
        // 初始化监听
        initListeners();
        // 初始化P层
        mPresenter = initPresenter();
        if (null != mPresenter) {
            Logger.i("Presenter: " + mPresenter.getClass().getSimpleName());
            // 调用P层onCreateView
            mPresenter.onCreateView();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        dispatchUserVisibleHint(!hidden);
    }


    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger.d(this.getClass().getSimpleName());
        if (rootView == null) {
            if (getLayoutId() != 0) {
                rootView = inflater.inflate(getLayoutId(), container, false);
            }
        }
        return rootView;
    }

    protected int getStatusBarHeight() {
        return ScreenUtils.getStatusHeight(requireContext());
    }

    /**
     * 增加方法,设置状态栏的高度
     */
    protected void resetStatusBarHeight() {
        if (rootView != null) {
            View statusView = rootView.findViewById(R.id.fake_status_bar);
            if (statusView == null) {
                return;
            }
            statusView.getLayoutParams().height = getStatusBarHeight();
        }
    }

    public AlertDialog.Builder makeAlertDialog() {
        return RKAlertDialog.make(getActivity());
    }

    public void showLoadingDialog(String text) {
        TipDialog.show(text)
                .setCancelable(true);
    }

    public void showLoadingDialog(@StringRes int resId) {

        TipDialog.show(resId)
                .setCancelable(true);
    }

    public void showLoadingDialog() {
        TipDialog.show(R.string.common_loading_tip)
                .setCancelable(true);
    }

    public void hideLoadingDialog() {
        TipDialog.dismiss();
    }

    public void showSuccessDialog(String text) {
        TipDialog.show(text, WaitDialog.TYPE.ERROR)
                .setCancelable(true);
    }

    public void showSuccessDialog(@StringRes int resId) {
        TipDialog.show(resId, WaitDialog.TYPE.SUCCESS)
                .setCancelable(true);
    }

    public void showFailedDialog(@StringRes int resId) {
        TipDialog.show(resId, WaitDialog.TYPE.ERROR)
                .setCancelable(true);
    }


    public void showFailedDialog(String text) {
        TipDialog.show(text, WaitDialog.TYPE.ERROR)
                .setCancelable(true);
    }

    public void showWaringDialog(@StringRes int resId) {
        TipDialog.show(resId, WaitDialog.TYPE.WARNING)
                .setCancelable(true);
    }

    public void showWaringDialog(String text) {
        TipDialog.show(text, WaitDialog.TYPE.WARNING)
                .setCancelable(true);
    }

    // 创建 loading、Error statusView
    protected void initStatusView() {
        Logger.w(this.getClass().getSimpleName() + " ,The status view is initializing.");
        if (mStatusView == null) {
            mStatusView = StatusView.newBuilder()
                    .with(requireContext())
                    .rootView((ViewGroup) rootView)
                    .build();
        }
    }

    private boolean isStatusViewNull() {
        return null == this.mStatusView;
    }

    public void showEmptyView() {
        runOnUiThread(() -> {
            if (isStatusViewNull()) {
                initStatusView();
            }
            mStatusView.showEmptyView();
        });
    }


    public void showEmptyView(String text) {
        runOnUiThread(() -> {
            if (isStatusViewNull()) {
                initStatusView();
            }
            mStatusView.showEmptyView(text);
        });
    }

    public void hideEmptyView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isStatusViewNull()) {
                    Logger.w(this.getClass().getSimpleName() + " ,The status view is empty.");
                    return;
                }
                mStatusView.hideEmptyView();
            }
        });
    }

    public void showLoadingView() {
        runOnUiThread(() -> {
            if (isStatusViewNull()) {
                initStatusView();
            }
            mStatusView.showLoadingView();
        });
    }


    public void hideLoadingView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isStatusViewNull()) {
                    Logger.w(this.getClass().getSimpleName() + " ,The status view is empty.");
                    return;
                }
                mStatusView.hideLoadingView();
            }
        });
    }

    public void showErrorView() {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        runOnUiThread(() -> {
            if (isStatusViewNull()) {
                initStatusView();
            }
            mStatusView.showErrorView();
        });
    }

    public void showErrorView(String text) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        runOnUiThread(() -> {
            if (isStatusViewNull()) {
                initStatusView();
            }
            mStatusView.showErrorView(text);
        });
    }

    public void hideErrorView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isStatusViewNull()) {
                    Logger.w(this.getClass().getSimpleName() + " ,The status view is empty.");
                    return;
                }
                mStatusView.hideErrorView();
            }
        });
    }

    public void setOnErrorViewClick(@NonNull View.OnClickListener onClickListener) {
        if (isStatusViewNull()) {
            Logger.w(this.getClass().getSimpleName() + " ,The status view is empty.");
            initStatusView();
        }
        mStatusView.setOnErrorViewClick(onClickListener);
    }

    // 布局资源ID
    protected abstract int getLayoutId();

    // 初始化P层
    protected abstract P initPresenter();

    // 初始化变量 子类需要就重写
    protected abstract void initVariables(View rootView, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    // 初始化监听
    protected abstract void initListeners();

    @Override
    public void onStart() {
        //Logger.d(this.getClass().getSimpleName());
        super.onStart();
        if (null != mPresenter) {
            mPresenter.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mPresenter) {
            mPresenter.onResume();
        }
        if (mStatusView != null) {
            mStatusView.onResume();
        }
        if (!mIsFirstVisible) {
            if (!isHidden() && !currentVisibleState && getUserVisibleHint()) {
                dispatchUserVisibleHint(true);
            }
        }
    }


    @Override
    public void onPause() {
        //Logger.d(this.getClass().getSimpleName());
        super.onPause();
        if (null != mPresenter) {
            mPresenter.onPause();
        }
//        if (currentVisibleState && getUserVisibleHint()) {
//            dispatchUserVisibleHint(false);
//        }
        if (mStatusView != null) {
            mStatusView.onPause();
        }
        dispatchChildVisibleState(false);

    }

    /**
     * 统一处理 显示隐藏
     *
     * @param visible
     */
    private void dispatchUserVisibleHint(boolean visible) {
        //当前 Fragment 是 child 时候 作为缓存 Fragment 的子 fragment getUserVisibleHint = true
        //但当父 fragment 不可见所以 currentVisibleState = false 直接 return 掉
        // 这里限制则可以限制多层嵌套的时候子 Fragment 的分发
        if (visible && isParentInvisible()) return;

        //此处是对子 Fragment 不可见的限制，因为 子 Fragment 先于父 Fragment回调本方法 currentVisibleState 置位 false
        // 当父 dispatchChildVisibleState 的时候第二次回调本方法 visible = false 所以此处 visible 将直接返回
        if (currentVisibleState == visible) {
            return;
        }

        currentVisibleState = visible;

        if (visible) {
            if (isAdded()) {
                if (mIsFirstVisible) {
                    mIsFirstVisible = false;
                    onFragmentFirstVisible();
                }
                onFragmentResume();
                dispatchChildVisibleState(true);
            }
        } else {
            if (isAdded()) {
                dispatchChildVisibleState(false);
                onFragmentPause();
            }
        }
    }

    /**
     * 用于分发可见时间的时候父获取 fragment 是否隐藏
     *
     * @return true fragment 不可见， false 父 fragment 可见
     */
    private boolean isParentInvisible() {
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof LazyDcFragment) {
            LazyDcFragment fragment = (LazyDcFragment) parentFragment;
            return !fragment.isSupportVisible();
        } else {
            return false;
        }
    }

    public boolean isSupportVisible() {
        return currentVisibleState;
    }

    /**
     * 当前 Fragment 是 child 时候 作为缓存 Fragment 的子 fragment 的唯一或者嵌套 VP 的第一 fragment 时 getUserVisibleHint = true
     * 但是由于父 Fragment 还进入可见状态所以自身也是不可见的， 这个方法可以存在是因为庆幸的是 父 fragment 的生命周期回调总是先于子 Fragment
     * 所以在父 fragment 设置完成当前不可见状态后，需要通知子 Fragment 我不可见，你也不可见，
     * <p>
     * 因为 dispatchUserVisibleHint 中判断了 isParentInvisible 所以当 子 fragment 走到了 onActivityCreated 的时候直接 return 掉了
     * <p>
     * 当真正的外部 Fragment 可见的时候，走 setVisibleHint (VP 中)或者 onActivityCreated (hide show) 的时候
     * 从对应的生命周期入口调用 dispatchChildVisibleState 通知子 Fragment 可见状态
     *
     * @param visible
     */
    private void dispatchChildVisibleState(boolean visible) {
        FragmentManager childFragmentManager = getChildFragmentManager();
        List<Fragment> fragments = childFragmentManager.getFragments();
        if (!fragments.isEmpty()) {
            for (Fragment child : fragments) {
                if (child instanceof LazyDcFragment && !child.isHidden() && child.getUserVisibleHint()) {
                    ((LazyDcFragment) child).dispatchUserVisibleHint(visible);
                }
            }
        }
    }

    /**
     * 第一次对用户课件
     */
    public void onFragmentFirstVisible() {
//        Log.e(super.getTAG(),  "  对用户第一次可见");
//        mPresenter.

    }

    /**
     * 对用户可见
     */
    public void onFragmentResume() {
        if (null == mPresenter) {
            return;
        }
        mPresenter.onUserVisible();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        isAttach = true;
    }

    /**
     * 对用户不可见
     */
    public void onFragmentPause() {
        if (null == mPresenter) {
            return;
        }
        mPresenter.onUserInvisible();
    }

    @Override
    public void onStop() {
        //Logger.d(this.getClass().getSimpleName());
        super.onStop();
        currentVisibleState = false;
        if (null != mPresenter) {
            mPresenter.onStop();
        }
    }


    @Override
    public void onDestroy() {
        //Logger.d(this.getClass().getSimpleName());
        if (null != mPresenter) {
            mPresenter.onDestroy();
            mPresenter = null;
        }
        rootView = null;
        if (!isStatusViewNull()) {
            mStatusView.release();
        }
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewCreated = false;
        mIsFirstVisible = true;
        if (null != mPresenter) {
            mPresenter.onDestroyView();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 对于默认 tab 和 间隔 checked tab 需要等到 isViewCreated = true 后才可以通过此通知用户可见
        // 这种情况下第一次可见不是在这里通知 因为 isViewCreated = false 成立,等从别的界面回到这里后会使用 onFragmentResume 通知可见
        // 对于非默认 tab mIsFirstVisible = true 会一直保持到选择则这个 tab 的时候，因为在 onActivityCreated 会返回 false
        if (isViewCreated) {
            if (isVisibleToUser && !currentVisibleState) {
                dispatchUserVisibleHint(true);
            } else if (!isVisibleToUser && currentVisibleState) {
                dispatchUserVisibleHint(false);
            }
        }
    }

    public ViewGroup getRootView() {
        return (ViewGroup) this.rootView;
    }

    public P getPresenter() {
        Logger.d(this.getClass().getSimpleName());
        return mPresenter;
    }


    public int getCompatColor(@ColorRes int resId) {
        return ContextCompat.getColor(requireActivity(), resId);
    }

    public Drawable getCompatDrawable(@DrawableRes int id) {
        return ContextCompat.getDrawable(requireActivity(), id);
    }


    public DcUriRequest DcRouter(@NonNull String uri) {
        Logger.i(this.getClass().getSimpleName() + " ,The Router : " + uri);
        return new DcUriRequest(requireActivity(), uri);
    }

    public DcLoginUser getUser() {
        if (null == AccountHelper.getInstance().getUser()) {
            DcRouter(AccountConstant.Uri.INDEX)
                    .start();
            return null;
        }
        return AccountHelper.getInstance().getUser();
    }

    public Intent getIntent() {
        Logger.d(this.getClass().getSimpleName());
        if (null == getActivity()) {
            return null;
        }

        return getActivity().getIntent();
    }

    public Uri getUri() {
        Logger.d(this.getClass().getSimpleName());
        if (null == getActivity() || null == getIntent()) {
            Logger.w("The activity or intent is empty.");
            return null;
        }

        return getIntent().getData();
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

    public void showToastShort(final int id) {
        showToastShort(getString(id));
    }

    public void showToastShort(final CharSequence text) {
        if (!getUserVisibleHint()) {
            Logger.w(this.getClass().getSimpleName() +
                    " ,This fragment is invisible to user, so can't show the toast.");
            return;
        }
        if (TextUtils.isEmpty(text)) {
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


    protected void runOnUiThread(Runnable action) {
        FragmentActivity activity = getActivity();
        if (null != activity) {
            activity.runOnUiThread(action);
        }
    }
}

