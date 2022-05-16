package cool.dingstock.appbase.custom;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.meiqia.core.MQManager;
import com.meiqia.core.bean.MQMessage;
import com.meiqia.core.callback.AppLifecycleListener;
import com.meiqia.core.callback.OnClientInfoCallback;
import com.meiqia.core.callback.OnInitCallback;
import com.meiqia.core.callback.OnMessageSendCallback;
import com.meiqia.core.callback.OnRegisterDeviceTokenCallback;
import com.meiqia.meiqiasdk.activity.MQConversationActivity;
import com.meiqia.meiqiasdk.callback.MQActivityLifecycleCallback;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.meiqia.meiqiasdk.util.MQIntentBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import cool.dingstock.appbase.entity.bean.account.DcLoginUser;
import cool.dingstock.appbase.entity.event.account.EventIsAuthorized;
import cool.dingstock.appbase.push.DCPushManager;
import cool.dingstock.appbase.util.LoginUtils;

public class CustomerManager {
    private static final String TAG = "meqia";
    private static final String UDESK_DOMAIN = "dingstock.udesk.cn";
    private static final String UDESK_APPID = "3070b042f6dd7fb4";
    private static final String UDESK_APPKEY = "f7a10d499164f4024671751d3b4b021c";
    private static final String MEIQIA_KEY_TEST = "2d65fc4228fb6f8cfb8e5447faf75e53";
    private static final CustomerManager ourInstance = new CustomerManager();
    private Context context;

    public static CustomerManager getInstance() {
        return ourInstance;
    }

//    private UdeskConfig config = null;

    private CustomerManager() {
    }

    public void setup(Context applicationContext) {
        this.context = applicationContext;
        initCustom();
        registerToken();
        initLifecycle();
        configCustomerServiceUser(null);
        EventBus.getDefault().register(this);
    }

    private void initLifecycle() {
        MQManager.setAppLifecycleListener((Application) context, new AppLifecycleListener() {
            @Override
            public void background() {
                MQManager.getInstance(context).closeMeiqiaService();
            }

            @Override
            public void foreground() {

            }
        });
        MQConfig.setActivityLifecycleCallback(new MQActivityLifecycleCallback() {
            @Override
            public void onActivityCreated(MQConversationActivity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(MQConversationActivity activity) {

            }

            @Override
            public void onActivityResumed(MQConversationActivity activity) {
                MQManager.getInstance(context).openMeiqiaService();
            }

            @Override
            public void onActivityPaused(MQConversationActivity activity) {

            }

            @Override
            public void onActivityStopped(MQConversationActivity activity) {
                MQManager.getInstance(context).closeMeiqiaService();
            }

            @Override
            public void onActivitySaveInstanceState(MQConversationActivity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(MQConversationActivity activity) {

            }
        });
    }

    private void registerToken() {
        MQManager.getInstance(context).registerDeviceToken(DCPushManager.getInstance().getTxDeviceToken(),
                new OnRegisterDeviceTokenCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "注册美恰推送成功" + "推送init");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.e(TAG, "注册美恰推送成功" + s);
                    }
                });
    }

    private void initCustom() {
        MQConfig.init(context, MEIQIA_KEY_TEST, new OnInitCallback() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "初始化成功" + s);
            }

            @Override
            public void onFailure(int i, String s) {
                Log.d(TAG, "初始化失败" + s);
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void configCustomerServiceUser(EventIsAuthorized event) {
        HashMap<String, String> info = new HashMap<>();
        DcLoginUser currentUser = LoginUtils.INSTANCE.getCurrentUser();
        if (null == currentUser) {
            info.put(CustomConstant.USER_NAME, DCPushManager.getInstance().getTxDeviceToken());
        } else {
            info.put(CustomConstant.DESCRIPTION, currentUser.getDescription());
            info.put(CustomConstant.USER_NAME, currentUser.getNickName());
            info.put(CustomConstant.USER_AVATAR, currentUser.getAvatarUrl());
            if (!TextUtils.isEmpty(currentUser.getMobilePhoneNumber())) {
                info.put(CustomConstant.CELLPHONE, currentUser.getMobilePhoneNumber());
            }
        }
        MQManager.getInstance(context).setClientInfo(info, new OnClientInfoCallback() {
            @Override
            public void onFailure(int i, String s) {
                Log.e(TAG, "更新用户信息失败 \n reason is:" + s);
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "更新用户信息成功");
            }
        });
    }

    public void showCustomServiceActivity(Context context) {
        Intent intent = new MQIntentBuilder(context).build();
        context.startActivity(intent);
    }


    public void sendTextMessageToCustomer(Context context, String message) {
        Intent intent = new MQIntentBuilder(context).build();
        context.startActivity(intent);
        MQManager.getInstance(context).sendMQTextMessage(message,
                new OnMessageSendCallback() {
                    @Override
                    public void onSuccess(MQMessage mqMessage, int i) {
                        Log.e(TAG, "发送消息成功");
                    }

                    @Override
                    public void onFailure(MQMessage mqMessage, int i, String s) {
                        Log.e(TAG, "发送消息失败");
                    }
                });
    }

}
