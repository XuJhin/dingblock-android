package cool.dingstock.appbase.widget.banner;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

import cool.dingstock.lib_base.util.CollectionUtils;

/**
 * Created by wangshuwen on 2017/10/21.
 */

public class CommonBannerAdapter extends PagerAdapter {

    private List<View> viewList;

    public CommonBannerAdapter(List<View> viewList) {
        this.viewList = viewList;
    }

    @Override
    public int getCount() {
        if (CollectionUtils.isEmpty(viewList)) {
            return 0;
        }
        if (viewList.size() > 1) {
            return Integer.MAX_VALUE;
        }
        if (viewList.size() == 1) {
            return Integer.MAX_VALUE;
        }
        return 0;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //对ViewPager页号求模取出View列表中要显示的项
        position %= viewList.size();
        if (position < 0) {
            position = viewList.size() + position;
        }
        View view = viewList.get(position);
        ViewParent vp = view.getParent();
        if (vp != null) {
            ViewGroup parent = (ViewGroup) vp;
            parent.removeView(view);
        }
        container.addView(view);
        return view;
    }


}
