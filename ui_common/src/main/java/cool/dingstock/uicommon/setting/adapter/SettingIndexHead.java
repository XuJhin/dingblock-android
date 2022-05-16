package cool.dingstock.uicommon.setting.adapter;

import android.text.TextUtils;
import android.view.View;
import cool.dingstock.appbase.widget.recyclerview.item.BaseHead;
import cool.dingstock.appbase.widget.recyclerview.item.BaseViewHolder;
import cool.dingstock.uicommon.R;
import cool.dingstock.uicommon.databinding.SettingIndexHeadBinding;

public class SettingIndexHead extends BaseHead<String, SettingIndexHeadBinding> {

    public SettingIndexHead(String data) {
        super(data);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.setting_index_head;
    }

    @Override
    public void onReleaseViews(BaseViewHolder holder, int sectionKey, int sectionHeadPosition) {

    }

    @Override
    public void onSetViewsData(BaseViewHolder holder, int sectionKey, int sectionHeadPosition) {
        if(TextUtils.isEmpty(getData())){
            viewBinding.settingIndexHeadTxt.setVisibility(View.GONE);
        }else {
            viewBinding.settingIndexHeadTxt.setVisibility(View.VISIBLE);
        }
        viewBinding.settingIndexHeadTxt.setText(getData());
    }
}
