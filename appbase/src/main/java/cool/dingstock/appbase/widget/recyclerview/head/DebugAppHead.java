package cool.dingstock.appbase.widget.recyclerview.head;

import android.view.View;

import cool.dingstock.appbase.BuildConfig;
import cool.dingstock.appbase.R;
import cool.dingstock.appbase.databinding.DebugHeadInfoBinding;
import cool.dingstock.appbase.entity.bean.account.DcLoginUser;
import cool.dingstock.appbase.entity.bean.mine.PhoneInfo;
import cool.dingstock.appbase.util.LoginUtils;
import cool.dingstock.appbase.widget.recyclerview.item.BaseHead;
import cool.dingstock.appbase.widget.recyclerview.item.BaseViewHolder;
import cool.dingstock.lib_base.util.AppUtils;

public class DebugAppHead extends BaseHead<String, DebugHeadInfoBinding> {

    public DebugAppHead(String data) {
        super(data);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.debug_head_info;
    }

    @Override
    public void onReleaseViews(BaseViewHolder holder, int sectionKey, int sectionHeadPosition) {

    }

    @Override
    public void onSetViewsData(BaseViewHolder holder, int sectionKey, int sectionHeadPosition) {
        String channel = String.format("%s_%s", AppUtils.INSTANCE.getAppMetaData(getContext(), "UMENG_CHANNEL"),
                BuildConfig.BUILD_TYPE);
        String appName = String.format("%s (%s)", getString(R.string.app_name), AppUtils.INSTANCE.getAppId(getContext()));
        viewBinding.debugHeadAppTxt.setText(String.format("%s\n%s\n%s\n%s",
                appName,
                channel,
                PhoneInfo.Companion.getInstance().getVersion(),
                PhoneInfo.Companion.getInstance().getVersionCode()));

        viewBinding.debugHeadInfoTxt.setText(String.format("%s\n%s\n%s",
                PhoneInfo.Companion.getInstance().getImei(),
                PhoneInfo.Companion.getInstance().getBrand(),
                PhoneInfo.Companion.getInstance().getRelease()));


        DcLoginUser currentUser = LoginUtils.INSTANCE.getCurrentUser();
        if (null == currentUser) {
            viewBinding.debugHeadPhoneTxt.setVisibility(View.GONE);
            viewBinding.debugHeadPushTxt.setVisibility(View.GONE);
        } else {
            viewBinding.debugHeadPhoneTxt.setVisibility(View.VISIBLE);
            viewBinding.debugHeadPhoneTxt.setText(String.format("手机号： %s", currentUser.getMobilePhoneNumber()));
            viewBinding.debugHeadPushTxt.setVisibility(View.VISIBLE);
            viewBinding.debugHeadPushTxt.setText(String.format("PushId： %s", currentUser.getDeviceId()));
        }
    }
}
