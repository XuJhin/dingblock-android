package cool.dingstock.appbase.widget.tablayout;

import android.content.Context;
import android.graphics.Color;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

import java.util.List;

import cool.dingstock.appbase.R;
import cool.dingstock.lib_base.util.CollectionUtils;

public class TabRadiusIndicatorAdapter extends CommonNavigatorAdapter {

    private List<String> titleList;
    private float mIndicatorHeight;
    private float mRadius;

    private TabSelectListener mTabSelectListener;

    public void setTabSelectListener(TabSelectListener listener) {
        this.mTabSelectListener = listener;
    }

    @Override
    public int getCount() {
        return CollectionUtils.isEmpty(titleList) ? 0 : this.titleList.size();
    }

    public TabRadiusIndicatorAdapter(List<String> titleList) {
        this.titleList = titleList;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, int i) {
        TabRadiusTitleView clipPagerTitleView = new TabRadiusTitleView(context);
        clipPagerTitleView.setText(titleList.get(i));
        clipPagerTitleView.setNormalColor(Color.parseColor("#797F8C"));
        clipPagerTitleView.setSelectedColor(Color.WHITE);
        clipPagerTitleView.setBackgroundResource(R.drawable.common_item_bg_radius_13);
        clipPagerTitleView.setOnClickListener(v -> {
            if (null != mTabSelectListener) {
                mTabSelectListener.onSelected(i);
            }
        });
        return clipPagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setLineHeight(mIndicatorHeight);
        indicator.setRoundRadius(mRadius);
        indicator.setColors(Color.parseColor("#FF6C6C"));
        return indicator;
    }


    public void setIndicatorHeight(float indicatorHeight) {
        this.mIndicatorHeight = indicatorHeight;
    }

    public void setIndicatorRadius(float radius) {
        this.mRadius = radius;
    }


}
