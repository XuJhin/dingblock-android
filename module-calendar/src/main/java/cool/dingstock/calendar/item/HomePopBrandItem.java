package cool.dingstock.calendar.item;

import cool.dingstock.appbase.entity.bean.home.HomeBrandBean;
import cool.dingstock.appbase.widget.recyclerview.item.BaseItem;
import cool.dingstock.appbase.widget.recyclerview.item.BaseViewHolder;
import cool.dingstock.calendar.R;
import cool.dingstock.calendar.databinding.HomeItemPopBrandBinding;

public class HomePopBrandItem extends BaseItem<HomeBrandBean, HomeItemPopBrandBinding> {

	public HomePopBrandItem(HomeBrandBean data) {
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
		viewBinding.homeItemPopBrandTxt.setText(getData().getName());
		viewBinding.homeItemPopBrandTxt.setSelected(getData().isSelected());
		holder.getItemView().setOnClickListener(v -> {
			mData.setSelected(!mData.isSelected());
			viewBinding.homeItemPopBrandTxt.setSelected(mData.isSelected());
		});
	}
	
	public boolean isSelected() {
		return mData.isSelected();
	}
}
