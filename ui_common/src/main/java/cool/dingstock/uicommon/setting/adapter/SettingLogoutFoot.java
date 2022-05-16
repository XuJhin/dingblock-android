package cool.dingstock.uicommon.setting.adapter;

import cool.dingstock.appbase.util.KtExtendUtilsKt;
import cool.dingstock.appbase.widget.recyclerview.item.BaseFoot;
import cool.dingstock.appbase.widget.recyclerview.item.BaseViewHolder;
import cool.dingstock.uicommon.R;
import cool.dingstock.uicommon.databinding.SettingLogoutFootBinding;

public class SettingLogoutFoot extends BaseFoot<String, SettingLogoutFootBinding> {

    private static final int LOGOUT_FOOT_TYPE = 100;

    public interface LogoutListener {
        void onLogoutClick();

    }


    private LogoutListener logoutListener;

    public SettingLogoutFoot(String data) {
        super(data);
    }

    @Override
    public int getViewType() {
        return LOGOUT_FOOT_TYPE;
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.setting_logout_foot;
    }

    @Override
    public void onReleaseViews(BaseViewHolder holder, int sectionKey, int sectionFootPosition) {

    }

    public void setLogoutListener(LogoutListener logoutListener) {
        this.logoutListener = logoutListener;
    }

    @Override
    public void onSetViewsData(BaseViewHolder holder, int sectionKey, int sectionFootPosition) {
        KtExtendUtilsKt.setOnShakeClickListener(holder.getItemView().findViewById(R.id.setting_index_foot_txt), v -> {
            if (null != logoutListener) {
                logoutListener.onLogoutClick();
            }
        });

    }
}
