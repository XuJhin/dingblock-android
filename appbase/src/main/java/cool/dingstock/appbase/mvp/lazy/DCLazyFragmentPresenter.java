package cool.dingstock.appbase.mvp.lazy;

import android.content.Intent;
import android.net.Uri;

import java.lang.ref.WeakReference;

import cool.dingstock.appbase.mvp.BaseFragment;
import cool.dingstock.lib_base.util.Logger;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public abstract class DCLazyFragmentPresenter<F extends BaseFragment> {

    private WeakReference<F> mFragmentWeak;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    protected void addDisposable(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }


    public DCLazyFragmentPresenter(F fragment) {
        // 绑定 Fragment
        this.mFragmentWeak = new WeakReference<>(fragment);
    }

    protected void onCreateView() {
        Logger.d(this.getClass().getSimpleName());
        onLoadData();
    }

    protected void onStart() {
    }

    protected void onResume() {
    }

    protected void onUserVisible() {
    }

    protected void onUserInvisible() {
    }

    protected void onPause() {
    }

    protected void onStop() {
    }

    protected void onDestroyView() {
    }

    /**
     * 解除全部绑定
     */
    protected void onDestroy() {
        mFragmentWeak.clear();
        mFragmentWeak = null;
        if(mCompositeDisposable!=null){
            mCompositeDisposable.clear();
        }
    }

    public void onLoadData() {
        Logger.d(this.getClass().getSimpleName());
    }

    public Intent getIntent() {
        return null != mFragmentWeak && mFragmentWeak.get() != null ? mFragmentWeak.get().getIntent() : null;
    }

    /**
     * 获取绑定的 Fragment
     */
    public F getFragment() {
        if (null == mFragmentWeak) {
            return null;
        }
        return mFragmentWeak.get();
    }

    /**
     * 检测是否 View 已经绑定
     */
    public boolean isFragmentBind() {
        return mFragmentWeak != null && mFragmentWeak.get() != null;
    }


    public Uri getUri() {
        return this.isFragmentBind() ? mFragmentWeak.get().getUri() : null;
    }

    public String getQueryParameter(String key) {
        return null != this.getUri() ? this.getUri().getQueryParameter(key) : null;
    }

}
