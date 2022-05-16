package cool.dingstock.monitor.adapter.item;

import android.view.View;
import cool.dingstock.appbase.entity.bean.monitor.MonitorCenterRegions;
import cool.dingstock.appbase.ext.ImageViewExtKt;
import cool.dingstock.appbase.widget.recyclerview.item.BaseItem;
import cool.dingstock.appbase.widget.recyclerview.item.BaseViewHolder;
import cool.dingstock.monitor.R;
import cool.dingstock.monitor.databinding.ItemMonitorCenterRegionHeaderBinding;

public class MonitorCenterRegionItem extends BaseItem<MonitorCenterRegions, ItemMonitorCenterRegionHeaderBinding> {

    public MonitorCenterRegionItem(MonitorCenterRegions data) {
        super(data);
    }

    @Override
    public int getViewType() {
        return 10003;
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_monitor_center_region_header;
    }

    @Override
    public void onReleaseViews(BaseViewHolder holder, int sectionKey, int sectionViewPosition) {

    }

    @Override
    public void onSetViewsData(BaseViewHolder holder, int sectionKey, int sectionViewPosition) {
        MonitorCenterRegions data = getData();
        if(data.isAdd()){
            viewBinding.addGroup.setVisibility(View.VISIBLE);
            viewBinding.contentGroup.setVisibility(View.GONE);
            return;
        }
        viewBinding.addGroup.setVisibility(View.GONE);
        viewBinding.contentGroup.setVisibility(View.VISIBLE);
        ImageViewExtKt.load(viewBinding.ivMonitorHeaderRegionCover, data.getSnapshotImageUrl());
        viewBinding.tvMonitorHeaderRegionName.setText(data.getName());
        viewBinding.tvMonitorHeaderRegionCount.setText("" + data.getRaffleCount());
    }
}
