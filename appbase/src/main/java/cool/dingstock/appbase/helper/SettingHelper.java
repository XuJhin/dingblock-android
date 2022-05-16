package cool.dingstock.appbase.helper;

import static cool.dingstock.appbase.config.AppSpConstant.CELLULAR_AUTO_GIF;
import static cool.dingstock.appbase.config.AppSpConstant.CELLULAR_AUTO_PLAY;
import static cool.dingstock.appbase.config.AppSpConstant.DEFAULT_OPEN_SOUND;
import static cool.dingstock.appbase.config.AppSpConstant.DISTURB_END_TIME;
import static cool.dingstock.appbase.config.AppSpConstant.DISTURB_START_TIME;
import static cool.dingstock.appbase.config.AppSpConstant.DISTURB_SWITCH;
import static cool.dingstock.appbase.config.AppSpConstant.MONITOR_DIRECT_LINK;
import static cool.dingstock.appbase.config.AppSpConstant.MONITOR_TAB_KEY;
import static cool.dingstock.appbase.config.AppSpConstant.WIFI_AUTO_GIF;
import static cool.dingstock.appbase.config.AppSpConstant.WIFI_AUTO_PLAY;

import android.text.TextUtils;

import androidx.annotation.NonNull;


import java.util.List;

import javax.inject.Inject;

import cool.dingstock.appbase.dagger.AppBaseApiHelper;
import cool.dingstock.appbase.entity.bean.setting.DisturbBean;
import cool.dingstock.appbase.entity.bean.setting.SettingItemBean;
import cool.dingstock.appbase.entity.bean.setting.SettingSection;
import cool.dingstock.appbase.net.api.account.AccountApi;
import cool.dingstock.appbase.net.mobile.MobileHelper;
import cool.dingstock.appbase.net.parse.ParseCallback;
import cool.dingstock.lib_base.json.JSONHelper;
import cool.dingstock.lib_base.stroage.ConfigFileHelper;
import cool.dingstock.lib_base.stroage.ConfigSPHelper;
import cool.dingstock.lib_base.util.CollectionUtils;
import cool.dingstock.lib_base.util.StringUtils;

public class SettingHelper {

    @Inject
    AccountApi accountApi;

    private static final String MONITOR_STYLE = "monitorStyle";
    private volatile static SettingHelper instance;

    public static SettingHelper getInstance() {
        if (null == instance) {
            synchronized (SettingHelper.class) {
                if (null == instance) {
                    instance = new SettingHelper();
                }
            }
        }
        return instance;
    }

    private SettingHelper() {
        AppBaseApiHelper.INSTANCE.getAppBaseComponent().inject(this);
    }

    public boolean isMonitorTabFirst() {
        return ConfigSPHelper.getInstance().getBoolean(MONITOR_TAB_KEY);
    }

    public boolean isMonitorDirectLink() {
        return ConfigSPHelper.getInstance().getBoolean(MONITOR_DIRECT_LINK);
    }

    public void saveMonitorDirectLink(boolean monitorStyle) {
        ConfigSPHelper.getInstance().save(MONITOR_DIRECT_LINK, monitorStyle);
    }

    public void saveMonitorTabFirst(boolean monitorFirst) {
        ConfigSPHelper.getInstance().save(MONITOR_TAB_KEY, monitorFirst);
    }

    public boolean isWifiAutoPlay() {
        return ConfigSPHelper.getInstance().getBoolean(WIFI_AUTO_PLAY, true);
    }

    public void saveWifiAutoPlay(boolean wifiAutoPlay) {
        ConfigSPHelper.getInstance().save(WIFI_AUTO_PLAY, wifiAutoPlay);
    }

    public boolean isCellularAutoPlay() {
        return ConfigSPHelper.getInstance().getBoolean(CELLULAR_AUTO_PLAY);
    }

    public void saveCellularAutoPlay(boolean cellularAutoPlay) {
        ConfigSPHelper.getInstance().save(CELLULAR_AUTO_PLAY, cellularAutoPlay);
    }

    public boolean isDefaultOpenSound() {
        return ConfigSPHelper.getInstance().getBoolean(DEFAULT_OPEN_SOUND);
    }

    public void saveDefaultOpenSound(boolean defaultOpenSound) {
        ConfigSPHelper.getInstance().save(DEFAULT_OPEN_SOUND, defaultOpenSound);
    }

    public boolean isWifiAutoGif() {
        return ConfigSPHelper.getInstance().getBoolean(WIFI_AUTO_GIF, true);
    }

    public void saveWifiAutoGif(boolean wifiAutoGif) {
        ConfigSPHelper.getInstance().save(WIFI_AUTO_GIF, wifiAutoGif);
    }

    public boolean isCellularAutoGif() {
        return ConfigSPHelper.getInstance().getBoolean(CELLULAR_AUTO_GIF);
    }

    public void saveCellularAutoGif(boolean cellularAutoGif) {
        ConfigSPHelper.getInstance().save(CELLULAR_AUTO_GIF, cellularAutoGif);
    }

    public List<SettingSection> getMonitorSettingList() {
        String settingJson = ConfigFileHelper.getConfigJson("monitor_setting_item.json");
        if (TextUtils.isEmpty(settingJson)) {
            return null;
        }
        List<SettingSection> settingSectionList = JSONHelper.fromJsonList(settingJson, SettingSection.class);
        if (CollectionUtils.isEmpty(settingSectionList)) {
            return null;
        }
        return settingSectionList;
    }

    public List<SettingSection> geSettingItemList() {
        String settingJson = ConfigFileHelper.getConfigJson("setting_item.json");
        if (TextUtils.isEmpty(settingJson)) {
            return null;
        }
        List<SettingSection> settingSectionList = JSONHelper.fromJsonList(settingJson, SettingSection.class);
        if (CollectionUtils.isEmpty(settingSectionList)) {
            return null;
        }

        return settingSectionList;
    }

    public void activate(String code, @NonNull ParseCallback<String> callback) {
        accountApi.activate(code)
                .subscribe(res -> {
                    if (!res.getErr()) {
                        callback.onSucceed(res.getRes());
                    } else {
                        callback.onFailed(res.getCode() + "", res.getMsg());
                    }
                }, err -> {
                    callback.onFailed("", err.getMessage());
                });
    }


    public void saveDisturbStatus(boolean status) {
        ConfigSPHelper.getInstance().save(DISTURB_SWITCH, status);
    }

    public void saveDisturbTime(int start, int end) {
        ConfigSPHelper.getInstance().save(DISTURB_START_TIME, start);
        ConfigSPHelper.getInstance().save(DISTURB_END_TIME, end);
    }

    public DisturbBean getDisturbData() {
        DisturbBean disturbBean = new DisturbBean();
        disturbBean.setStartTime(ConfigSPHelper.getInstance().getInt(DISTURB_START_TIME, 0));
        disturbBean.setEndTime(ConfigSPHelper.getInstance().getInt(DISTURB_END_TIME, 0));
        disturbBean.setSwitchStatus(ConfigSPHelper.getInstance().getBoolean(DISTURB_SWITCH));
        return disturbBean;
    }

}
