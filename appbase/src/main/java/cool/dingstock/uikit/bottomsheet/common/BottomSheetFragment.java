package cool.dingstock.uikit.bottomsheet.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import cool.dingstock.uikit.bottomsheet.ViewTransformer;

public class BottomSheetFragment extends Fragment implements BottomSheetFragmentInterface {

    private BottomSheetFragmentDelegate delegate;

    public BottomSheetFragment() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show(FragmentManager manager, @IdRes int bottomSheetLayoutId) {
        getDelegate().show(manager, bottomSheetLayoutId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int show(FragmentTransaction transaction, @IdRes int bottomSheetLayoutId) {
        return getDelegate().show(transaction, bottomSheetLayoutId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dismiss() {
        getDelegate().dismiss();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dismissAllowingStateLoss() {
        getDelegate().dismissAllowingStateLoss();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTransformer getViewTransformer() {
        return null;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getDelegate().onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getDelegate().onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDelegate().onCreate(savedInstanceState);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
        return getDelegate().getLayoutInflater(savedInstanceState, super.getLayoutInflater(savedInstanceState));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDelegate().onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDelegate().onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getDelegate().onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        getDelegate().onDestroyView();
        super.onDestroyView();
    }

    private BottomSheetFragmentDelegate getDelegate() {
        if (delegate == null) {
            delegate = BottomSheetFragmentDelegate.create(this);
        }
        return delegate;
    }
}