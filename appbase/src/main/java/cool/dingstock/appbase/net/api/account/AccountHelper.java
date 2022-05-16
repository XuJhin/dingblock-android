package cool.dingstock.appbase.net.api.account;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import cool.dingstock.appbase.dagger.AppBaseApiHelper;
import cool.dingstock.appbase.entity.bean.account.CountryBean;
import cool.dingstock.appbase.entity.bean.account.DcLoginUser;
import cool.dingstock.appbase.net.parse.ParseCallback;
import cool.dingstock.appbase.net.retrofit.exception.DcError;
import cool.dingstock.appbase.push.DCPushManager;
import cool.dingstock.appbase.util.LoginUtils;
import cool.dingstock.lib_base.json.JSONHelper;
import cool.dingstock.lib_base.stroage.ConfigFileHelper;
import cool.dingstock.lib_base.stroage.ConfigSPHelper;
import cool.dingstock.lib_base.util.CollectionUtils;
import cool.dingstock.lib_base.util.Logger;

public class AccountHelper {

    @Inject
    AccountApi accountApi;

    private volatile static AccountHelper instance;

    private static final String COUNTRY_FILE_NAME = "country.json";

    private static final String KEY_DC_USER = "uc_user_phone";

    public static AccountHelper getInstance() {
        if (null == instance) {
            synchronized (AccountHelper.class) {
                if (null == instance) {
                    instance = new AccountHelper();
                }
            }
        }
        return instance;
    }

    private AccountHelper() {
        AppBaseApiHelper.INSTANCE.getAppBaseComponent().inject(this);
    }

    /**
     * 获取国家码
     *
     * @return 国家码列表
     */
    public List<CountryBean> getCountryList() {
        String defaultJson = ConfigFileHelper.getDefaultJson(COUNTRY_FILE_NAME);
        if (TextUtils.isEmpty(defaultJson)) {
            return null;
        }
        return JSONHelper.fromJsonList(defaultJson, CountryBean.class);
    }

    /**
     * 获取当前user
     *
     * @return user
     */
    public DcLoginUser getUser() {
        return LoginUtils.INSTANCE.getCurrentUser();
    }

    /**
     * 获取当前userId
     *
     * @return userId
     */
    public String getUserId() {
        if (null == getUser()) {
            return "";
        }
        return getUser().getId();
    }


    public void getSmsCodeWithCaptcha(
            String zone,
            String phone,
            @Nullable String captchaAppId,
            @Nullable String captchaTicket,
            @Nullable String captchaRandStr,
            @NonNull ParseCallback<String> callback) {
        accountApi.getSmsCode(zone, phone, captchaAppId, captchaTicket, captchaRandStr)
                .subscribe(res -> {
                    if (!res.getErr()) {
                        callback.onSucceed(res.getRes());
                    } else {
                        callback.onFailed(res.getCode() + "", res.getMsg());
                    }
                }, err -> {
                    callback.onFailed(DcError.UNKNOW_ERROR_CODE + "", err.getMessage() + "");
                });
    }

    /**
     * 获取短信验证码
     *
     * @param zone     国家code
     * @param phone    手机号
     * @param callback 回调
     */
    public void getSmsCode(String zone, String phone, @NonNull ParseCallback<String> callback) {
        accountApi.getSmsCode(zone, phone, null, null, null)
                .subscribe(res -> {
                    if (!res.getErr()) {
                        callback.onSucceed(res.getRes());
                    } else {
                        callback.onFailed(res.getCode() + "", res.getMsg());
                    }
                }, err -> {
                    callback.onFailed(DcError.UNKNOW_ERROR_CODE + "", err.getMessage() + "");
                });
    }



    /**
     * 获取人机验证信息
     *
     * @param callback 回调
     */
    public void getCaptchaRequirement(ParseCallback<Boolean> callback) {
        accountApi.getCaptchaRequirement().subscribe(res -> {
            if(!res.getErr()&&true == res.getRes()){
                callback.onSucceed(true);
            }else {
                callback.onFailed(res.getCode()+"",res.getMsg());
            }
        }, err -> {
            callback.onFailed(DcError.UNKNOW_ERROR_CODE+"",err.getMessage());
        });
    }

    /**
     * 获取最后一次登录成功的手机号
     *
     * @return 手机号
     */
    public String getSaveUserPhone() {
        return ConfigSPHelper.getInstance().getString(KEY_DC_USER);
    }

    /**
     * 存储最后一次登录成功的手机号
     *
     * @param phoneNum 手机号
     */
    public void saveUserPhone(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            return;
        }
        if (phoneNum.contains("+")) {
            phoneNum = phoneNum.substring(3);
        }
        ConfigSPHelper.getInstance().save(KEY_DC_USER, phoneNum);
    }


    /**
     * 当前用户有无订阅
     *
     * @return true/false
     */
    public boolean haveSubscribe() {
        return null != getUser() && CollectionUtils.isNotEmpty(getUser().getChannels());
    }

    /**
     * 存储和用户绑定的pushId
     */
    public void saveDeviceId() {
        if (null == getUser()) {
            return;
        }
        String deviceId = DCPushManager.getInstance().getTxDeviceToken();
        if (TextUtils.isEmpty(deviceId)) {
            Logger.e("saveDeviceId  deviceId empty");
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("deviceId", deviceId);
        accountApi.setUserParameter2Net(map);
    }

}
