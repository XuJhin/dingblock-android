package cool.dingstock.appbase.widget.tablayout;

import android.content.Context;
import android.graphics.Typeface;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;


public class TabTitleView extends ColorTransitionPagerTitleView {

    private float startTitleSize;
    private float endTitleSize;

    public TabTitleView(Context context) {
        super(context);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        super.onLeave(index, totalCount, leavePercent, leftToRight);
        float scaleX = endTitleSize / startTitleSize - (leavePercent * (endTitleSize / startTitleSize - 1));
        float scaleY = endTitleSize / startTitleSize - (leavePercent * (endTitleSize / startTitleSize - 1));
        setScaleX(scaleX);
        setScaleY(scaleY);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        super.onEnter(index, totalCount, enterPercent, leftToRight);
        float scaleX = enterPercent * (endTitleSize / startTitleSize - 1) + 1;
        float scaleY = enterPercent * (endTitleSize / startTitleSize - 1) + 1;
        setScaleX(scaleX);
        setScaleY(scaleY);
    }

    @Override
    public void onSelected(int index, int totalCount) {
        super.onSelected(index, totalCount);
        setTypeface(Typeface.DEFAULT_BOLD);
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        super.onDeselected(index, totalCount);
        setTypeface(Typeface.DEFAULT);
    }

    public void setStartAndEndTitleSize(float startTitleSize, float endTitleSize) {
        this.startTitleSize = startTitleSize;
        this.endTitleSize = endTitleSize;
    }

}