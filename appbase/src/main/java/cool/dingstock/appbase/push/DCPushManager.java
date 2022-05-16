package cool.dingstock.appbase.push;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

import cool.dingstock.appbase.BuildConfig;
import cool.dingstock.appbase.constant.PushConstant;
import cool.dingstock.appbase.entity.bean.setting.PushSoundEnum;
import cool.dingstock.appbase.entity.event.account.EventActivated;
import cool.dingstock.appbase.util.PlatUtils;
import cool.dingstock.lib_base.stroage.ConfigSPHelper;

public class DCPushManager {
    private static final String TAG = "PushManager";
    private static final String KEY_PUSH_SOUND = "pushSoundType";
    private static final String KEY_SYS_PUSH = "sysPush";
    private static final String KEY_PUSH_MESSAGE = "pushMsg";
    private static final String PushSp = "pushSp";

    private volatile static DCPushManager instance;

    private WeakReference<Context> contextReference;

    public static DCPushManager getInstance() {
        if (instance == null) {
            synchronized (DCPushManager.class) {
                if (instance == null) {
                    instance = new DCPushManager();
                }
            }
        }
        return instance;
    }

    public void setup(Application applicationContext) {
        contextReference = new WeakReference<>(applicationContext);
        if (BuildConfig.DEBUG) {
            XGPushConfig.enableDebug(contextReference.get(), true);
        }
        XGPushConfig.setMiPushAppId(applicationContext, PushConstant.XiaoMi.APP_ID);
        XGPushConfig.setMiPushAppKey(applicationContext, PushConstant.XiaoMi.APP_KEY);

        // 注意这里填入的是 Oppo 的 AppKey，不是AppId
        XGPushConfig.setOppoPushAppId(applicationContext, PushConstant.Oppo.APP_KEY);
        // 注意这里填入的是 Oppo 的 AppSecret，不是 AppKey
        XGPushConfig.setOppoPushAppKey(applicationContext, PushConstant.Oppo.APP_SECRET);
        //打开第三方推送
        XGPushConfig.enableOtherPush(applicationContext, true);
        XGPushConfig.setMzPushAppId(applicationContext, PushConstant.MeiZu.APP_ID);
        XGPushConfig.setMzPushAppKey(applicationContext, PushConstant.MeiZu.APP_KEY);
        XGPushManager.registerPush(applicationContext, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                OppoChannelHelper.INSTANCE.onSuccess();
                //token在设备卸载重装的时候有可能会变
                Log.d("TPush", "注册成功，设备token为：" + data);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                OppoChannelHelper.INSTANCE.onFail();
                Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
            }
        });

        adaptAndroidO(applicationContext);
    }

    // Android 8.0推送适配
    private void adaptAndroidO(Application applicationContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager)
                    applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (null == mNotificationManager) {
                return;
            }
            mNotificationManager.deleteNotificationChannel("notification_ding");
            mNotificationManager.deleteNotificationChannel("notification_mario");
            mNotificationManager.deleteNotificationChannel("notification_noise");
            mNotificationManager.deleteNotificationChannel("ding_stock_message");
            if (PlatUtils.isXiaomi()) {
                //重要通知 ！！！！！！！！！！！！！！！
                String id = "pre84";
                // 用户可以看到的通知渠道的名字.
                CharSequence name = "小米专用通道";
                // 用户可以看到的通知渠道的描述
                String description = "打开该选项以接收监控推送";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(id, name, importance);
                // 配置通知渠道的属性
                mChannel.setDescription(description);
                mChannel.enableLights(false);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{1500, 500, 1500});
                mNotificationManager.createNotificationChannel(mChannel);
            } else if (PlatUtils.isHuawei()) {

            } else if (PlatUtils.isOppo()) {

            } else if (PlatUtils.isVivo()) {

            } else {//其他

            }

            //重要通知 ！！！！！！！！！！！！！！！
            String id = "dingstock";
            // 用户可以看到的通知渠道的名字.
            CharSequence name = PlatUtils.isOppo() ? "订阅发售" : "发售监控";
            // 用户可以看到的通知渠道的描述
            String description = "打开该选项以接收监控推送";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            mChannel.enableLights(false);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{1500, 500, 1500});
            mNotificationManager.createNotificationChannel(mChannel);

            //全量推送  非重要通知！！！！！
            String allId = "normal";
            String allChannelName = "发售提醒";
            String allDes = "打开该选项以接收发售提醒";
            int allImportance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channelAll = new NotificationChannel(allId, allChannelName, allImportance);
            channelAll.setDescription(allDes);
            channelAll.enableLights(false);
            channelAll.enableVibration(true);
            channelAll.setVibrationPattern(new long[]{1500, 500, 1500});
            mNotificationManager.createNotificationChannel(channelAll);

            //私信 重要通知！！！！！
            String imId = "im";
            String imChannelName = PlatUtils.isOppo() ? "聊天消息" : "私信消息";
            String imDes = "打开该选项以接收私信消息";
            int imImportance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel imAll = new NotificationChannel(imId, imChannelName, imImportance);
            imAll.setDescription(imDes);
            imAll.enableLights(false);
            imAll.enableVibration(true);
            imAll.setVibrationPattern(new long[]{1500, 500, 1500});
            mNotificationManager.createNotificationChannel(imAll);

        }
    }

    public String getTxDeviceToken() {
        return XGPushConfig.getToken(contextReference.get());
    }


    public void setPushSound(Context context, PushSoundEnum pushSound) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PushSp, 0);
        sharedPreferences.edit().putInt(DCPushManager.KEY_PUSH_SOUND, pushSound.getPushType()).apply();
    }

    public PushSoundEnum getPushSound(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PushSp, 0);
        int type = sharedPreferences.getInt(DCPushManager.KEY_PUSH_SOUND, 0);
        return PushSoundEnum.getPushSoundWithType(type);
    }

    /**
     * Save and Read the push disable time
     */
    public long getPushDisableTime() {
        return ConfigSPHelper.getInstance().getLong(KEY_PUSH_MESSAGE, 0L);
    }

    public void updatePushDisableTime() {
        ConfigSPHelper.getInstance().save(KEY_PUSH_MESSAGE, System.currentTimeMillis());
    }

    public boolean clearPushDisableTime() {
        return ConfigSPHelper.getInstance().remove(KEY_PUSH_MESSAGE);
    }

    /**
     * 收到服务端激活成功的推送消息
     */
    public void onUpdateUserInfoEvent() {
        EventBus.getDefault().post(new EventActivated());
    }

}
