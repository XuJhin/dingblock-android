package cool.dingstock.appbase;

import android.app.Application;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import javax.inject.Inject;

import cool.dingstock.appbase.dagger.AppBaseApiHelper;
import cool.dingstock.appbase.entity.bean.account.DcLoginUser;
import cool.dingstock.appbase.entity.bean.base.BaseResult;
import cool.dingstock.appbase.entity.event.account.EventSessionInvalid;
import cool.dingstock.appbase.net.api.account.AccountApi;
import cool.dingstock.appbase.net.api.account.AccountHelper;
import cool.dingstock.appbase.net.mobile.MobileHelper;
import cool.dingstock.appbase.push.DCPushManager;
import cool.dingstock.appbase.ut.UTHelper;
import cool.dingstock.appbase.util.LoginUtils;
import cool.dingstock.lib_base.BaseLibrary;
import cool.dingstock.lib_base.util.AppUtils;
import cool.dingstock.lib_base.util.Logger;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

/**
 * @author WhenYoung
 * CreateAt Time 2021/1/12  14:28
 */
public class AppBaseLibrary {
    @Inject
    AccountApi accountApi;

    private static volatile AppBaseLibrary mInstance;

    public static AppBaseLibrary getInstance() {
        if (null == mInstance) {
            synchronized (BaseLibrary.class) {
                if (null == mInstance) {
                    mInstance = new AppBaseLibrary();
                }
            }
        }
        return mInstance;
    }

    AppBaseLibrary() {
        AppBaseApiHelper.INSTANCE.getAppBaseComponent().inject(this);
    }


    public static void initialize(Application application) {
        Logger.d("Start to init the XBase lib.");
        getInstance().refresh();
        DcLoginUser user = AccountHelper.getInstance().getUser();
        if (null != user && user.isVip()) {
            UTHelper.vipLaunch();
        }
    }

    /**
     * 重新初始化
     */
    public void refresh() {
        Logger.d("Start to refresh XBase.");
        //更新配置
        MobileHelper.getInstance().getConfigData();
        final DcLoginUser user = AccountHelper.getInstance().getUser();
        if (null == user) {
            return;
        }
        Disposable disposable = accountApi.getUserByNet().subscribe(res -> {
            DcLoginUser newUser = res.getRes();
            if (null == newUser) {
                return;
            }
            //1.微信未绑定手机号
            if (!user.isSmsAuthenticated()) {
                Logger.w("user webChat login but not bind  so logout");
                accountApi.loginLout();
                LoginUtils.INSTANCE.loginOut();
                return;
            }
            //2.deviceId
            String deviceId = newUser.getDeviceId();
            String pushDeviceId = DCPushManager.getInstance().getTxDeviceToken();
            boolean saveFlag = false;

            if (!TextUtils.isEmpty(deviceId) && !deviceId.equals(pushDeviceId)) {
                saveFlag = true;
            }
            //如果为空 说明之前没有保存成功
            if (TextUtils.isEmpty(deviceId)) {
                saveFlag = true;
                Logger.i("user deviceId is empty so save --");
                newUser.setDeviceId(DCPushManager.getInstance().getTxDeviceToken());
            }
            //3.version
            String versionName = AppUtils.INSTANCE.getVersionName(BaseLibrary.getInstance().getContext());
            int versionCode = AppUtils.INSTANCE.getVersionCode(BaseLibrary.getInstance().getContext());
            String version = "Android" + versionName + "(" + versionCode + ")";
            if (!(version).equals(newUser.getVersion())) {
                saveFlag = true;
                newUser.setVersion(version);
            }
            if (saveFlag) {
                accountApi.setUserParameter2Net("deviceId", DCPushManager.getInstance().getTxDeviceToken())
                        .subscribe(dcLoginUserBaseResult -> {

                        });
            }
        }, err -> {
        });
    }
}
