package cool.dingstock.appbase.adapter;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import cool.dingstock.appbase.mvp.BaseFragment;

public class MyFragmentPageAdapter extends FragmentStatePagerAdapter {

    private List<BaseFragment> mFragments;
    private FragmentManager fragmentManager;

    public MyFragmentPageAdapter(FragmentManager fm, List<BaseFragment> fragments) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mFragments = fragments;
        this.fragmentManager=fm;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    public Object instantiateItem(ViewGroup container, int position) {
        //拿到缓存的fragment，如果没有缓存的，就新建一个，新建发生在fragment的第一次初始化时
        Fragment f = (Fragment) super.instantiateItem(container, position);
        String fragmentTag = f.getTag();
        if (f != getItem(position)) {
            //如果是新建的fragment，f 就和getItem(position)是同一个fragment，否则进入下面
            FragmentTransaction ft =fragmentManager.beginTransaction();
            //移除旧的fragment
            ft.remove(f);
            //换成新的fragment
            f =getItem(position);
            //添加新fragment时必须用前面获得的tag
            ft.add(container.getId(), f, fragmentTag);
            ft.attach(f);
            ft.commitAllowingStateLoss();
        }
        return f;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
