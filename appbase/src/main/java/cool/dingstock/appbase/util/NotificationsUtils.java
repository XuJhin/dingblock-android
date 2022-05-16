package cool.dingstock.appbase.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import cool.dingstock.appbase.R;

/**
 * 获取通知栏权限是否开启
 */

public class NotificationsUtils {




    public static boolean isNotificationEnabled(Context context) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        return notificationManagerCompat.areNotificationsEnabled();
    }

    /**
     * Jump to mobile phone system notification interface
     * 跳的是应用详情页面 为了兼容大部分手机
     */
    public static void gotoSysNotification(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivity(intent);

    }

    public static Notification createNotification(Context context,String channelId,String title,String content, int res){
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // 兼容 8.0 系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = createNotificationChannel(nm,channelId);
            NotificationCompat.Builder builder = createNotificationCompatBuilder(context,title,content,res);
            builder.setChannelId(notificationChannel.getId());
            return builder.build();
        }
        NotificationCompat.Builder builder = createNotificationCompatBuilder(context,title,content,res);
        return builder.build();
    }



    @NonNull
    private static NotificationCompat.Builder createNotificationCompatBuilder(Context context,String title,String content,@IdRes int res) {
        // 通知栏点击接收者
        Intent i = new Intent();
//        i.setAction(OnPushClickRecevier.ACTION);
//        i.putExtra("data", pushMsg);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(res);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);
//        String stoneRing = "通知声音地址";
//        if (!TextUtils.isEmpty(stoneRing)) {
//            builder.setSound(Uri.parse(stoneRing));
//        }
        return builder;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static NotificationChannel createNotificationChannel(NotificationManager notificationManager,String channelId) {
        // 通知渠道
        NotificationChannel mChannel = new NotificationChannel(channelId, "盯链", NotificationManager.IMPORTANCE_HIGH);
        // 开启指示灯，如果设备有的话。
        mChannel.enableLights(true);
        // 开启震动
        mChannel.enableVibration(true);
        //  设置指示灯颜色
        mChannel.setLightColor(Color.RED);
        // 设置是否应在锁定屏幕上显示此频道的通知
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        // 设置是否显示角标
        mChannel.setShowBadge(true);
        //  设置绕过免打扰模式
        mChannel.setBypassDnd(true);
        // 设置震动频率
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400});
        //最后在notificationmanager中创建该通知渠道
        notificationManager.createNotificationChannel(mChannel);
        return mChannel;
    }



}
