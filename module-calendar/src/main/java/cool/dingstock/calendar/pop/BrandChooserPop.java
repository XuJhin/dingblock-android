package cool.dingstock.calendar.pop;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cool.dingstock.appbase.adapter.CommonSpanItemDecoration;
import cool.dingstock.appbase.entity.bean.home.HomeBrandBean;
import cool.dingstock.appbase.entity.bean.home.HomeTypeBean;
import cool.dingstock.appbase.widget.recyclerview.adapter.BaseRVAdapter;
import cool.dingstock.calendar.R;
import cool.dingstock.calendar.item.HomePopBrandItem;
import cool.dingstock.calendar.item.HomePopTypeItem;
import cool.dingstock.lib_base.util.CollectionUtils;
import cool.dingstock.lib_base.util.ToastUtil;

public class BrandChooserPop extends Dialog {
	private final List<HomeBrandBean> filterList = new ArrayList<>();
	private final List<HomeTypeBean> typeList = new ArrayList<>();
	private View mRootView;
	private RecyclerView brandRv;
	private TextView confirmTxt;
	private BaseRVAdapter<HomePopBrandItem> rvAdapter;
	private List<HomeBrandBean> homeBrandBeanList;
	private DisMissListener dismissListener;

	private BaseRVAdapter<HomePopTypeItem> typeAdapter;
	private List<HomeTypeBean> homeTypeBeanList;
	
	public BrandChooserPop(Context context) {
		super(context);
		initViews();
		
	}
	
	protected void initViews() {
		mRootView = LayoutInflater.from(getContext()).inflate(R.layout.home_pop_brand_choose, null, false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		getWindow().setWindowAnimations(R.style.DC_bottom_dialog_animation);
		setContentView(mRootView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		brandRv = mRootView.findViewById(R.id.home_pop_brand_choose_rv);
		confirmTxt = mRootView.findViewById(R.id.home_pop_brand_choose_confirm_txt);
		rvAdapter = new BaseRVAdapter<>();
		brandRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
		brandRv.addItemDecoration(new CommonSpanItemDecoration(3, 10, 10, false));
		brandRv.setAdapter(rvAdapter);

		RecyclerView typeRv = mRootView.findViewById(R.id.home_pop_type_choose_rv);
		typeAdapter = new BaseRVAdapter<>();
		typeRv.setAdapter(typeAdapter);
		typeRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
		typeRv.addItemDecoration(new CommonSpanItemDecoration(3, 10, 10, false));

		confirmTxt.setOnClickListener(v -> {
			if (rvAdapter.getAllItemList().isEmpty()) {
				return;
			}
			filterList.clear();
			typeList.clear();
			for (HomePopBrandItem homePopBrandItem : rvAdapter.getItemList()) {
				if (homePopBrandItem.isSelected()) {
					filterList.add(homePopBrandItem.getData());
				}
			}
			for (HomePopBrandItem homePopBrandItem : rvAdapter.getItemList()) {
				HomeBrandBean data = homePopBrandItem.getData();
				data.setSelected(homePopBrandItem.isSelected());
			}
			for (HomePopTypeItem homePopTypeItem : typeAdapter.getItemList()) {
				if (homePopTypeItem.isSelected()) {
					typeList.add(homePopTypeItem.getData());
				}
			}
			for (HomePopTypeItem homePopTypeItem : typeAdapter.getItemList()) {
				HomeTypeBean data = homePopTypeItem.getData();
				data.setSelected(homePopTypeItem.isSelected());
			}
			
			if (null != dismissListener) {
				if (typeList.isEmpty()) {
					ToastUtil.getInstance()._short(this.getContext(), "请至少选择一个类型");
					return;
				}
				if (filterList.isEmpty()) {
					ToastUtil.getInstance()._short(this.getContext(), "请至少选择一个品牌");
					return;
				}
				dismissListener.onFilterBrandList(filterList, typeList);
				dismiss();
			}
		});
		mRootView.setOnClickListener(v -> dismiss());
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
	}
	
	public static Builder newBuilder(Context context) {
		return new Builder(context);
	}
	
	public void setDataList() {
		if (CollectionUtils.isEmpty(homeBrandBeanList)) {
			return;
		}
		List<HomePopBrandItem> itemList = new ArrayList<>();
		for (HomeBrandBean homeBrandBean : homeBrandBeanList) {
			itemList.add(new HomePopBrandItem(homeBrandBean));
		}
		rvAdapter.setItemViewList(itemList);
	}

	public void setTypeList() {
		if (CollectionUtils.isEmpty(homeTypeBeanList)) {
			return;
		}
		List<HomePopTypeItem> itemList = new ArrayList<>();
		for (HomeTypeBean homeTypeBean : homeTypeBeanList) {
			itemList.add(new HomePopTypeItem(homeTypeBean));
		}
		typeAdapter.setItemViewList(itemList);
	}
	
	@Override
	public void show() {
		super.show();
		WindowManager.LayoutParams attributes = getWindow().getAttributes();
		attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
		attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
		getWindow().setAttributes(attributes);
		getWindow().setGravity(Gravity.BOTTOM);
	}
	
	public interface DisMissListener {
		void onFilterBrandList(List<HomeBrandBean> filterList, List<HomeTypeBean> typeList);
	}
	
	public static class Builder {
		
		private final BrandChooserPop actionSheet;
		
		private Builder(@NonNull Context context) {
			this.actionSheet = new BrandChooserPop(context);
		}
		
		public Builder dismissListener(DisMissListener listener) {
			this.actionSheet.dismissListener = listener;
			return this;
		}
		
		public Builder brands(List<HomeBrandBean> homeBrandBeanList) {
			this.actionSheet.homeBrandBeanList = homeBrandBeanList;
			this.actionSheet.setDataList();
			return this;
		}

		public Builder types(List<HomeTypeBean> homeTypeBeanList) {
			this.actionSheet.homeTypeBeanList = homeTypeBeanList;
			this.actionSheet.setTypeList();
			return this;
		}
		
		public BrandChooserPop build() {
			return this.actionSheet;
		}
	}
	
}
