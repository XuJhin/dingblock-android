package cool.dingstock.calendar.item;

import cool.dingstock.appbase.entity.bean.home.HomeTypeBean;
import cool.dingstock.appbase.widget.recyclerview.item.BaseItem;
import cool.dingstock.appbase.widget.recyclerview.item.BaseViewHolder;
import cool.dingstock.calendar.R;
import cool.dingstock.calendar.databinding.HomeItemPopBrandBinding;

public class HomePopTypeItem extends BaseItem<HomeTypeBean, HomeItemPopBrandBinding> {

	public HomePopTypeItem(HomeTypeBean data) {
		super(data);
	}
	
	@Override
	public int getViewType() {
		return 0;
	}
	
	@Override
	public int getLayoutId(int viewType) {
		return R.layout.home_item_pop_brand;
	}
	
	@Override
	public void onReleaseViews(BaseViewHolder holder, int sectionKey, int sectionViewPosition) {
	
	}
	
	@Override
	public void onSetViewsData(BaseViewHolder holder, int sectionKey, int sectionViewPosition) {
		viewBinding.homeItemPopBrandTxt.setText(mData.getName());
		viewBinding.homeItemPopBrandTxt.setSelected(mData.getSelected());
		holder.getItemView().setOnClickListener(v -> {
			mData.setSelected(!mData.getSelected());
			viewBinding.homeItemPopBrandTxt.setSelected(mData.getSelected());
		});
	}
	
	public boolean isSelected() {
		return mData.getSelected();
	}
}
