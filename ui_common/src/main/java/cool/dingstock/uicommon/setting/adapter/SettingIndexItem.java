package cool.dingstock.uicommon.setting.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;

import java.lang.reflect.Field;
import cool.dingstock.appbase.entity.bean.setting.SettingItemBean;
import cool.dingstock.appbase.widget.recyclerview.item.BaseItem;
import cool.dingstock.appbase.widget.recyclerview.item.BaseViewHolder;
import cool.dingstock.uicommon.R;
import cool.dingstock.uicommon.databinding.SettingItemIndexBinding;

public class SettingIndexItem extends BaseItem<SettingItemBean, SettingItemIndexBinding> {

    boolean isStart;
    boolean isEnd;


    public SettingIndexItem(SettingItemBean data) {
        super(data);
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.setting_item_index;
    }

    @Override
    public void onReleaseViews(BaseViewHolder holder, int sectionKey, int sectionViewPosition) {

    }

    @Override
    public void onSetViewsData(BaseViewHolder holder, int sectionKey, int sectionViewPosition) {
        if (getData().getActionType().equals("switch")) {
            viewBinding.settingItemIndexMoreIcon.setVisibility(View.GONE);
            viewBinding.settingItemIndexValueTxt.setVisibility(View.GONE);
            viewBinding.settingItemIndexValueTxt.setText("");
            viewBinding.settingItemIndexSwitch.setVisibility(View.VISIBLE);
            setSwitch(getData().getSwitchOpen());
        } else {
            viewBinding.settingItemIndexMoreIcon.setVisibility(View.VISIBLE);
            viewBinding.settingItemIndexValueTxt.setVisibility(View.VISIBLE);
            viewBinding.settingItemIndexSwitch.setVisibility(View.GONE);
            viewBinding.settingItemIndexValueTxt.setText(getData().getValue());
        }
        viewBinding.settingItemIndexNameTxt.setText(getData().getName());
        if (TextUtils.isEmpty(getData().getDesc())) {
            viewBinding.settingItemIndexDesTxt.setVisibility(View.GONE);
        } else {
            viewBinding.settingItemIndexDesTxt.setVisibility(View.VISIBLE);
            viewBinding.settingItemIndexDesTxt.setText(getData().getDesc());
        }
        viewBinding.iv.setImageResource(getResId(getData().getResId()));
        if (isStart) {
            if (isEnd) {
                viewBinding.rootItem.setBackgroundResource(R.drawable.setting_card_all_bg);
            } else {
                viewBinding.rootItem.setBackgroundResource(R.drawable.setting_card_top_bg);
            }
        } else {
            if (isEnd) {
                viewBinding.rootItem.setBackgroundResource(R.drawable.setting_card_bottom_bg);
            } else {
                viewBinding.rootItem.setBackgroundResource(R.drawable.setting_card_not_bg);
            }
        }
    }

    public interface SwitchListener {
        void onSwitch(SettingIndexItem item, boolean isChecked);
    }


    private SwitchListener switchListener;


    public void setSwitchListener(SwitchListener switchListener) {
        this.switchListener = switchListener;
    }


    CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (null != switchListener) {
                switchListener.onSwitch(SettingIndexItem.this, isChecked);
            }
        }
    };


    public void setSwitch(boolean isCheck) {
        if (viewBinding.settingItemIndexSwitch.getVisibility() == View.VISIBLE) {
            viewBinding.settingItemIndexSwitch.setOnCheckedChangeListener(null);
            viewBinding.settingItemIndexSwitch.setChecked(isCheck);
            viewBinding.settingItemIndexSwitch.setOnCheckedChangeListener(listener);

        }
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    /**
     * 根据资源ID名获取ID值
     * 利用Java反射机制从 R.drawable 类中获取资源ID
     *
     * @param name
     * @return
     */
    private int getResId(String name) {
        try {
            //根据资源ID名获取Filed对象
            Field field = R.drawable.class.getField(name);
            return Integer.parseInt(field.get(null).toString());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
