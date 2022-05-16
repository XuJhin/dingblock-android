package cool.dingstock.appbase.widget.tablayout;

import android.content.Context;
import android.graphics.drawable.Drawable;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import cool.dingstock.appbase.R;

public class TabRadiusTitleView extends SimplePagerTitleView {


    public TabRadiusTitleView(Context context) {
        super(context);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        super.onEnter(index, totalCount, enterPercent, leftToRight);
        Drawable drawable = getResources().getDrawable(R.drawable.common_item_bg_radius_13);
        drawable.setAlpha((int) ((1-enterPercent) * 255));
        setBackground(drawable);
    }


    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        super.onLeave(index, totalCount, leavePercent, leftToRight);
        Drawable drawable = getResources().getDrawable(R.drawable.common_item_bg_radius_13);
        drawable.setAlpha((int) (leavePercent * 255));
        setBackground(drawable);
    }
}
