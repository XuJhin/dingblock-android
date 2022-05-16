package cool.dingstock.uicommon.setting.adapter;

import cool.dingstock.appbase.widget.recyclerview.item.BaseFoot;
import cool.dingstock.appbase.widget.recyclerview.item.BaseViewHolder;
import cool.dingstock.uicommon.R;
import cool.dingstock.uicommon.databinding.SettingIndexFootBinding;

public class SettingIndexFoot extends BaseFoot<String, SettingIndexFootBinding> {
	
	public SettingIndexFoot(String data) {
		super(data);
	}
	
	@Override
	public int getLayoutId(int viewType) {
		return R.layout.setting_index_foot;
	}
	
	@Override
	public void onReleaseViews(BaseViewHolder holder, int sectionKey, int sectionFootPosition) {
	
	}
	
	@Override
	public void onSetViewsData(BaseViewHolder holder, int sectionKey, int sectionFootPosition) {
		viewBinding.settingIndexFootTxt.setText(getData());
	}
}
