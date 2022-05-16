package cool.dingstock.debug.adapter.item;

import android.text.TextUtils;
import android.view.View;
import cool.dingstock.appbase.widget.recyclerview.item.BaseItem;
import cool.dingstock.appbase.widget.recyclerview.item.BaseViewHolder;
import cool.dingstock.debug.R;
import cool.dingstock.debug.bean.DebugIndexBean;
import cool.dingstock.debug.databinding.DebugItemIndexBinding;

public class DebugIndexItem extends BaseItem<DebugIndexBean, DebugItemIndexBinding> {

    public interface ActionListener {
        void onSwitch(boolean checked);
    }

    private ActionListener mListener;

    public void setMListener(ActionListener mListener) {
        this.mListener = mListener;
    }

    public DebugIndexItem(DebugIndexBean data) {
        super(data);
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.debug_item_index;
    }

    @Override
    public void onReleaseViews(BaseViewHolder holder, int sectionKey, int sectionViewPosition) {
        viewBinding.debugItemIndexSwitch.setVisibility(View.GONE);
        viewBinding.debugItemIndexLinkIcon.setVisibility(View.GONE);
    }

    @Override
    public void onSetViewsData(BaseViewHolder holder, int sectionKey, int sectionViewPosition) {
        viewBinding.debugItemIndexTitleTxt.setText(getData().getTitle());
        String actionType = getData().getActionType();
        if (!TextUtils.isEmpty(actionType)) {

            switch (actionType) {
                case "switch":
                    viewBinding.debugItemIndexSwitch.setVisibility(View.VISIBLE);
                    setSwitchStatus(getData().getSwitchStatus());
                    break;
                case "actionSheet":
                case "link":
                case "mpEnv":
                    viewBinding.debugItemIndexLinkIcon.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }


    public void setSwitchStatus(boolean status) {
        viewBinding.debugItemIndexSwitch.setOnCheckedChangeListener(null);
        viewBinding.debugItemIndexSwitch.setChecked(status);
        viewBinding.debugItemIndexSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (null != mListener) {
                mListener.onSwitch(isChecked);
            }
        });
    }
}
