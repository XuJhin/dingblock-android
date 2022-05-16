package cool.dingstock.calendar.sneaker.index.item;

import cool.dingstock.appbase.ext.ImageViewExtKt;
import cool.dingstock.appbase.widget.recyclerview.item.BaseItem;
import cool.dingstock.appbase.widget.recyclerview.item.BaseViewHolder;
import cool.dingstock.appbase.entity.bean.home.CalenderProductEntity;
import cool.dingstock.calendar.R;
import cool.dingstock.calendar.databinding.ItemCalenderRecommendBinding;

public class CalenderRecommendItem extends BaseItem<CalenderProductEntity, ItemCalenderRecommendBinding> {
    public CalenderRecommendItem(CalenderProductEntity data) {
        super(data);
    }

    @Override
    public int getViewType() {
        return 31;
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_calender_recommend;
    }

    @Override
    public void onReleaseViews(BaseViewHolder holder, int sectionKey, int sectionViewPosition) {

    }

    @Override
    public void onSetViewsData(BaseViewHolder holder, int sectionKey, int sectionViewPosition) {
        ImageViewExtKt.load(viewBinding.sivRecommendSneaker,getData().getImageUrl());
    }
}
