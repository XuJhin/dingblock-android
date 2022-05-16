package cool.dingstock.appbase.net.mobile;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.fm.openinstall.model.AppData;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import cool.dingstock.appbase.constant.CommonConstant;
import cool.dingstock.appbase.dagger.AppBaseApiHelper;
import cool.dingstock.appbase.entity.bean.config.BizAdEntity;
import cool.dingstock.appbase.entity.bean.config.ConfigData;
import cool.dingstock.appbase.entity.bean.config.EmojiConfig;
import cool.dingstock.appbase.entity.bean.config.MonitorConfig;
import cool.dingstock.appbase.entity.bean.config.ShareConfig;
import cool.dingstock.appbase.entity.bean.config.TopOnConfigEntity;
import cool.dingstock.appbase.entity.bean.mine.MineVipChargeEntity;
import cool.dingstock.appbase.helper.ADHelper;
import cool.dingstock.appbase.helper.ADTShowTimeHelper;
import cool.dingstock.appbase.net.api.common.CommonApi;
import cool.dingstock.appbase.net.parse.ParseCallback;
import cool.dingstock.appbase.net.retrofit.exception.DcError;
import cool.dingstock.appbase.router.DcUriRequest;
import cool.dingstock.appbase.util.PlatUtils;
import cool.dingstock.imagepicker.helper.launcher.PLauncher;
import cool.dingstock.lib_base.BaseLibrary;
import cool.dingstock.lib_base.json.JSONHelper;
import cool.dingstock.lib_base.stroage.ConfigFileHelper;
import cool.dingstock.lib_base.stroage.ConfigSPHelper;
import cool.dingstock.lib_base.util.DeviceInfoUtils;
import cool.dingstock.lib_base.util.Logger;

public class MobileHelper {

    private static final String DEFAULT_ASSETS_FILE_NAME = "app_config.json";
    public static final String APP_CONFIG_KEY = "app_config";
    private static final String SELECT_CHANNEL_KEY = "selectChannelId";
    private static final int BETWEEN_TIME = 5 * 60 * 1000;
    private volatile static MobileHelper instance;
    private final AtomicLong mLastUpdateTime = new AtomicLong();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicLong mEmojiLastUpdateTime = new AtomicLong();
    private final AtomicBoolean emojiRunning = new AtomicBoolean(false);
    private final AtomicLong mShareLastUpdateTime = new AtomicLong();
    private final AtomicBoolean shareRunning = new AtomicBoolean(false);
    @Inject
    CommonApi commonApi;
    private ConfigData configData;

    private List<EmojiConfig> emojiConfigs;

    private List<ShareConfig> shareConfigs;

    private String currentChannel;

    private String brand;

    private MobileHelper() {
        AppBaseApiHelper.INSTANCE.getAppBaseComponent().inject(this);
    }

    public static MobileHelper getInstance() {
        if (null == instance) {
            synchronized (MobileHelper.class) {
                if (null == instance) {
                    instance = new MobileHelper();
                }
            }
        }
        return instance;
    }

    public void installation(AppData appData) {
        commonApi.openinstall(appData)
                .subscribe(res -> {
                }, err -> {
                });
    }

    /**
     * 获取上次显示的channelId
     */
    public String getSelectedChannelId() {
        return ConfigSPHelper.getInstance().getString(SELECT_CHANNEL_KEY);
    }

    /**
     * 存储上次显示的channelId
     *
     * @param channelId 监控channelId
     */
    public void saveSelectedChannelId(String channelId) {
        ConfigSPHelper.getInstance().save(SELECT_CHANNEL_KEY, channelId);
    }

    /**
     * 获取APP下载地址
     */
    public void getDownLoadUrl(ParseCallback<String> callback) {
        commonApi.commonInfo("downloadUrl")
                .subscribe(res -> {
                    if (!res.getErr() && res.getRes() != null && res.getRes().getUrl() != null) {
                        callback.onSucceed(res.getRes().getUrl());
                    } else {
                        callback.onFailed(res.getCode() + "", res.getMsg());
                    }
                }, err -> {
                    callback.onFailed(DcError.UNKNOW_ERROR_CODE + "", err.getMessage());
                });
    }

    /**
     * 获取配置数据
     */
    public ConfigData getConfigData() {
        //内存上获取不到 sp获取
        if (null == configData) {
            this.configData = getAppConfigFormSp();
        }
        //sp获取不到 default里面获取
        if (null == configData) {
            this.configData = getDefaultConfigData();
        }
        //后台更新
        getConfigForService();
        return this.configData;
    }

    public List<EmojiConfig> getEmojiConfigs() {
        //后台更新
        emojiConfig();
        return this.emojiConfigs;
    }

    public List<ShareConfig> getShareConfigs() {
        //后台更新
        shareConfig();
        return this.shareConfigs;
    }

    /**
     * 获取通用链接
     *
     * @param type     链接类型
     * @param callback 回调
     */
    public void getCloudUrl(String type, @NonNull ParseCallback<MineVipChargeEntity> callback) {
        commonApi.commonInfo(type)
                .subscribe(res -> {
                    if (!res.getErr() && res.getRes() != null) {
                        MineVipChargeEntity entity = new MineVipChargeEntity(res.getRes().getUrl(), false);
                        callback.onSucceed(entity);
                    } else {
                        callback.onFailed(res.getCode() + "", res.getMsg());
                    }
                }, err -> {
                    callback.onFailed(DcError.UNKNOW_ERROR_CODE + "", err.getMessage());
                });
    }

    /**
     * 获取通用链接并跳转
     *
     * @param type 链接类型
     */
    public void getCloudUrlAndRouter(Context context, String type, String utEventId) {
        commonApi.commonInfo(type)
                .subscribe(res -> {
                    if (!res.getErr() && res.getRes() != null) {
                        new DcUriRequest(context, res.getRes().getUrl())
                                .putUriParameter(CommonConstant.UriParams.UT_EVENT_ID, utEventId)
                                .start();
                    } else {
                    }
                }, err -> {
                });
    }

    /**
     * 获取通用链接并跳转
     *
     * @param type 链接类型
     */
    public void getCloudUrlAndRouter(Context context, String type) {
        commonApi.commonInfo(type)
                .subscribe(res -> {
                    if (!res.getErr() && res.getRes() != null) {
                        new DcUriRequest(context, res.getRes().getUrl())
                                .start();
                    } else {
                    }
                }, err -> {
                });
    }

    /**
     * 获取通用链接并跳转
     *
     * @param type 链接类型
     */
    public void getCloudUrlAndPutValueRouter(Context context, String type, String key, String value) {
        commonApi.commonInfo(type)
                .subscribe(res -> {
                    if (!res.getErr() && res.getRes() != null) {
                        String oldUrl = res.getRes().getUrl();
                        Set<String> params = Uri.parse(oldUrl).getQueryParameterNames();
                        String newUri = Uri.parse(oldUrl).buildUpon().clearQuery().toString();
                        for (String param : params) { // 重新添加或替换query
                            if (Objects.equals(param, "url")) {
                                String url = Uri.parse(oldUrl).getQueryParameter("url");
                                String childUrl = Uri.parse(url).buildUpon().appendQueryParameter(key, value)
                                        .build().toString();
                                newUri = Uri.parse(newUri).buildUpon().appendQueryParameter("url", childUrl).toString();

                            } else {
                                newUri = Uri.parse(newUri).buildUpon().appendQueryParameter(param, Uri.parse(oldUrl).getQueryParameter(param)).toString();
                            }
                        }
                        new DcUriRequest(context, newUri)
                                .start();
                    } else {
                    }
                }, err -> {
                });
    }

    public void getDingUrl(String type, @NonNull ParseCallback<String> callback) {
        commonApi.commonInfo(type)
                .subscribe(res -> {
                    if (!res.getErr() && res.getRes() != null) {
                        callback.onSucceed(res.getRes().getUrl());
                    } else {
                        callback.onFailed(res.getCode() + "", res.getMsg());
                    }
                }, err -> {
                    callback.onFailed(DcError.UNKNOW_ERROR_CODE + "", err.getMessage());
                });
    }

    /**
     * 获取兜底配置
     */
    private ConfigData getDefaultConfigData() {
        String defaultJson = ConfigFileHelper.getDefaultJson(DEFAULT_ASSETS_FILE_NAME);
        if (TextUtils.isEmpty(defaultJson)) {
            return null;
        }
        return JSONHelper.fromJson(defaultJson, ConfigData.class);
    }

    public void getConfigs() {
        emojiConfig();
        shareConfig();
    }

    private void emojiConfig() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mEmojiLastUpdateTime.get() <= BETWEEN_TIME) {
            Logger.w("emojiConfig in  BETWEEN_TIME so do nothing");
            return;
        }
        if (emojiRunning.get()) {
            Logger.w("emojiConfig in  running  so do nothing");
            return;
        }
        emojiRunning.set(true);
        commonApi.emojiConfig()
                .subscribe(res -> {
                    mEmojiLastUpdateTime.set(System.currentTimeMillis());
                    emojiRunning.set(false);
                    List<EmojiConfig> emojiConfigs = res.getRes();
                    if (emojiConfigs != null) {
                        this.emojiConfigs = emojiConfigs;

                    }
                },err -> {
                    emojiRunning.set(false);
                    mEmojiLastUpdateTime.set(0);
                }
            );
    }

    private void shareConfig() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mShareLastUpdateTime.get() <= BETWEEN_TIME) {
            Logger.w("shareConfig in  BETWEEN_TIME so do nothing");
            return;
        }
        if (shareRunning.get()) {
            Logger.w("shareConfig in  running  so do nothing");
            return;
        }
        shareRunning.set(true);
        commonApi.shareConfig()
                .subscribe(res -> {
                    mShareLastUpdateTime.set(System.currentTimeMillis());
                    shareRunning.set(false);
                    List<ShareConfig> shareConfigs = res.getRes();
                    if (shareConfigs != null) {
                        this.shareConfigs = shareConfigs;

                    }
                },err -> {
                    shareRunning.set(false);
                    mShareLastUpdateTime.set(0);
                }
            );
    }

    /**
     * 获取服务端的配置
     */
    private synchronized void getConfigForService() {
        long currentTime = System.currentTimeMillis();
        Logger.d(" CALL END --------------time=" + currentTime + " lastTime=" + mLastUpdateTime.get());
        if (currentTime - mLastUpdateTime.get() <= BETWEEN_TIME) {
            Logger.w("getConfigForService in  BETWEEN_TIME so do nothing");
            return;
        }
        if (running.get()) {
            Logger.w("getConfigForService in  running  so do nothing");
            return;
        }
        running.set(true);
        commonApi.appConfig()
                .subscribe(result -> {
                    mLastUpdateTime.set(System.currentTimeMillis());
                    running.set(false);
                    ConfigData data = result.getRes();
                    String lastImaUrl = "";
                    String lastDownloadUrl = "";

                    if (configData.getBizAD() != null) {
                        BizAdEntity bizAD = configData.getBizAD();
                        if (bizAD != null) {
                            if (bizAD.getMediaUrl() != null) {
                                lastImaUrl = bizAD.getMediaUrl();
                            }
                            if (bizAD.getLocalPath() != null) {
                                lastDownloadUrl = bizAD.getLocalPath();
                            }
                        }
                    }
                    if (data != null) {
                        this.configData = data;
                        ADHelper.INSTANCE.updateCache(data, lastImaUrl, lastDownloadUrl);
                    }
                }, err -> {
                    running.set(false);
                    mLastUpdateTime.set(0);
                });
    }

    private void saveAppConfigToSp(ConfigData configData) {
        if (null == configData) {
            return;
        }
        ConfigSPHelper.getInstance().save(APP_CONFIG_KEY, JSONHelper.toJson(configData));
    }

    private ConfigData getAppConfigFormSp() {
        String appConfigStr = ConfigSPHelper.getInstance().getString(APP_CONFIG_KEY);
        if (TextUtils.isEmpty(appConfigStr)) {
            return null;
        }
        return JSONHelper.fromJson(appConfigStr, ConfigData.class);
    }


    public String getCurrentChannel() {
        if (currentChannel == null) {
            Context context = BaseLibrary.getInstance().getContext();
            if (context != null) {
                try {
                    ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                    currentChannel = applicationInfo.metaData.getString("DC_CHANNEL_NAME");
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return currentChannel;
    }

    public String getBrand() {
        if (brand == null) {
            brand = PlatUtils.getPlat();
        }
        return brand;
    }
}
