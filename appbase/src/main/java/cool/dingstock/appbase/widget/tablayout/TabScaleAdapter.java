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
import cool.dingstock.lib_base.util.SizeUtils;

public class TabScaleAdapter extends CommonNavigatorAdapter {
    private List<String> titleList;
    private float startTitleSize;
    private float endTitleSize;
    private int indicatorColor;
    private int normalColor;
    private int selectedColor;
    private int indicatorWidth;
    private int indicatorRound = -1;
    private int padding = -1;

    private int indicatorHeight = SizeUtils.dp2px(3);

    private TabSelectListener mTabSelectListener;
    private LinePagerIndicator indicator;

    public void setTabSelectListener(TabSelectListener listener) {
        this.mTabSelectListener = listener;
    }

    public TabScaleAdapter(List<String> titleList) {
        this.titleList = titleList;
    }

    @Override
    public int getCount() {
        return CollectionUtils.isEmpty(titleList) ? 0 : this.titleList.size();
    }

    @Override
    public IPagerTitleView getTitleView(Context context, int i) {
        TabTitleView titleView = new TabTitleView(context);
        titleView.setNormalColor(normalColor != 0 ? normalColor : context.getColor(R.color.color_text_black4));
        titleView.setSelectedColor(selectedColor != 0 ? selectedColor : context.getColor(R.color.color_text_black1));
        titleView.setText(titleList.get(i));
        titleView.setOnClickListener(view -> {
            if (null != mTabSelectListener) {
                mTabSelectListener.onSelected(i);
            }
        });
        titleView.setTextSize(startTitleSize);
        titleView.setStartAndEndTitleSize(startTitleSize, endTitleSize);
        titleView.setPadding(padding != -1 ? padding : SizeUtils.dp2px(20),
                0,
                padding != -1 ? padding : SizeUtils.dp2px(20), 0);
        return titleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        if (hideIndicator) {
            return null;
        }
        indicator = new LinePagerIndicator(context);
        indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
        indicator.setLineWidth(indicatorWidth != 0 ? indicatorWidth : SizeUtils.dp2px(12));
        indicator.setRoundRadius(indicatorRound != -1 ? indicatorRound : SizeUtils.dp2px(2));
        indicator.setLineHeight(indicatorHeight);
        indicator.setColors(indicatorColor != 0 ? indicatorColor :
                Color.parseColor(String.valueOf(R.color.color_text_black1)));
        return indicator;
    }


    public void setPadding(int padding) {
        this.padding = padding;
    }

    public void setStartAndEndTitleSize(float startTitleSize, float endTitleSize) {
        this.startTitleSize = startTitleSize;
        this.endTitleSize = endTitleSize;
    }


    private boolean hideIndicator;

    public void hideIndicator() {
        this.hideIndicator = true;
    }


    public void setIndicatorColor(int color, int width, int round) {
        this.indicatorColor = color;
        this.indicatorWidth = width;
        this.indicatorRound = round;
    }

    public void setNormalColor(int color) {
        this.normalColor = color;
    }


    public void setSelectedColor(int color) {
        this.selectedColor = color;
    }

    public void setIndicatorHeight(int indicatorHeight) {
        this.indicatorHeight = indicatorHeight;
    }

}
