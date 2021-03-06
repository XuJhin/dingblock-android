package cool.dingstock.appbase.util;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import cool.dingstock.appbase.R;

import static android.content.Context.NOTIFICATION_SERVICE;

//删除
public class DifferentNotifications {
    private static String OSName=null;
    private static String SYSTEM_XIAOMI="XIAOMI";
    private static String SYSTEM_SAMSUNG="SAMSUNG";
    private static String SYSTEM_HUAWEI_HONOR="HONOR";
    private static String SYSTEM_HUAWEI="HUAWEI";
    private static String SYSTEM_NOVA="NOVA";
    private static String SYSTEM_SONY="SONY";
    private static String SYSTEM_VIVO="VIVO";
    private static String SYSTEM_OPPO="OPPO";
    private static String SYSTEM_LG="LG";
    private static String SYSTEM_ZUK="ZUK";
    private static String SYSTEM_HTC="HTC";


    public static int  NOTIFI_ID = 10101;
    private static Notification notification = null;

    public DifferentNotifications(){
        init();
    }

    public static void init(){
        OSName=android.os.Build.BRAND.trim().toUpperCase();
    }

    //显示Bubble
    public static void showBubble(Context context,Notification notification,int NOTIFI_ID,int num) {

        OSName = android.os.Build.BRAND.trim().toUpperCase();

        if(notification!=null) {

            if (num < 0) num = 0;
            if (num > 99) num = 99;

            Log.e("system_name", OSName);
            if (OSName != null) {

                //小米
                if (OSName.equals(SYSTEM_XIAOMI)) {
                    setBadgeOfXiaomi(context,notification,NOTIFI_ID,num);

                    //三星和LG
                } else if (OSName.equals(SYSTEM_SAMSUNG) || OSName.equals(SYSTEM_LG)) {
                    setBadgeOfSamsung(context, notification,NOTIFI_ID,num);

                    //华为荣耀和华为
                } else if (OSName.equals(SYSTEM_HUAWEI_HONOR) || OSName.equals(SYSTEM_HUAWEI)) {
                    setBadgeOfHuaWei(context,notification,NOTIFI_ID, num);

                    //索尼
                } else if (OSName.equals(SYSTEM_SONY)) {
                    setBadgeOfSony(context,notification,NOTIFI_ID, num);

                    //VIVO
                } else if (OSName.equals(SYSTEM_VIVO)) {
                    setBadgeOfVIVO(context,notification,NOTIFI_ID,num);

                    //OPPO
                } else if (OSName.equals(SYSTEM_OPPO)) {
                    setBadgeOfOPPO(context,notification,NOTIFI_ID,num);

                    //ZUK
                } else if (OSName.equals(SYSTEM_ZUK)) {
                    setBadgeOfZUK(context,notification,NOTIFI_ID,num);

                    //HTC
                } else if (OSName.equals(SYSTEM_HTC)) {
                    setBadgeOfHTC(context,notification,NOTIFI_ID,num);

                    //NOVA
                } else if (OSName.equals(SYSTEM_NOVA)) {
                    setBadgeOfNOVA(context,notification,NOTIFI_ID,num);

                    //其他的
                } else {
                    setBadgeOfDefault(context,notification,NOTIFI_ID,num);
                }


            }


        }


    }


    public static void showBubble(Context context,int relCount,int showCount){
        notification = getNotification(context,relCount);
        showBubble(context,notification,NOTIFI_ID,showCount);
    }

    /**
     * 显示通知栏
     * @param context 上下文对象
     */
    private static Notification getNotification(Context context,int relCount) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // 兼容 8.0 系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = createNotificationChannel(context, nm);
            NotificationCompat.Builder builder = createNotificationCompatBuilder(context,relCount);
            builder.setChannelId(notificationChannel.getId());
            return builder.build();
        }
        NotificationCompat.Builder builder = createNotificationCompatBuilder(context,relCount);
        return builder.build();
    }

    @NonNull
    private static NotificationCompat.Builder createNotificationCompatBuilder(Context context,int relCount) {
        // 通知栏点击接收者
        Intent i = new Intent();
        String title = "盯链";
        String content = "你有"+( relCount < 99 ? (relCount+"") : "99+")+"条新消息";
//        i.setAction(OnPushClickRecevier.ACTION);
//        i.putExtra("data", pushMsg);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
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
    private static NotificationChannel createNotificationChannel(Context context, NotificationManager notificationManager) {
        // 通知渠道
        NotificationChannel mChannel = new NotificationChannel("ding_stock_message", "消息通知", NotificationManager.IMPORTANCE_HIGH);
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

    //小米
    private static void setBadgeOfXiaomi(final Context context,final Notification notification,final int NOTIFI_ID,final int  num){


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {


                try {

                    Field field = notification.getClass().getDeclaredField("extraNotification");

                    Object extraNotification = field.get(notification);

                    Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);

                    method.invoke(extraNotification, num);

                } catch (Exception e) {

                    e.printStackTrace();
                    Log.e("Xiaomi" + " Badge error", "set Badge failed");

                }

//                mNotificationManager.notify(0,notification);
                NotificationManager notifyMgr = (NotificationManager)(context.getSystemService(NOTIFICATION_SERVICE));
                if(num!=0)notifyMgr.notify(NOTIFI_ID, notification);else notifyMgr.cancel(NOTIFI_ID);


            }
        },550);

    }

    //三星
    private static void setBadgeOfSamsung(Context context,Notification notification, int NOTIFI_ID,int num) {
        // 获取你当前的应用
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }

        try {
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", num);
            intent.putExtra("badge_count_package_name", context.getPackageName());
            intent.putExtra("badge_count_class_name", launcherClassName);
            context.sendBroadcast(intent);

            NotificationManager notifyMgr = (NotificationManager)(context.getSystemService(NOTIFICATION_SERVICE));
            if(num!=0)notifyMgr.notify(NOTIFI_ID, notification);else notifyMgr.cancel(NOTIFI_ID);


        }catch (Exception e){
            e.printStackTrace();
            Log.e("SAMSUNG" + " Badge error", "set Badge failed");
        }
    }

    //华为 荣耀系列
    private static void setBadgeOfHuaWei(Context context, Notification notification,int NOTIFI_ID,int num) {


        //检测EMUI版本是否支持
//        boolean isSupportedBade=false;
//        try {
//            PackageManager manager = context.getPackageManager();
//            PackageInfo info = manager.getPackageInfo("com.huawei.android.launcher", 0);
//            if(info.versionCode>=63029){
//                isSupportedBade = true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if(!isSupportedBade){
//            Log.i("badgedemo", "not supported badge!");
//            return;
//        }

        try{
            Bundle localBundle = new Bundle();
            localBundle.putString("package", context.getPackageName());
            localBundle.putString("class", getLauncherClassName(context));
            localBundle.putInt("badgenumber", num);
            context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, localBundle);

            NotificationManager notifyMgr = (NotificationManager)(context.getSystemService(NOTIFICATION_SERVICE));
            if(num!=0)notifyMgr.notify(NOTIFI_ID, notification);else notifyMgr.cancel(NOTIFI_ID);
        }catch(Exception e){
            e.printStackTrace();
            Log.e("HUAWEI" + " Badge error", "set Badge failed");
        }




    }

    //索尼
    private static void setBadgeOfSony(Context context,Notification notification,int NOTIFI_ID, int num){
        String numString="";
        String activityName = getLauncherClassName(context);
        if (activityName == null){
            return;
        }
        Intent localIntent = new Intent();
//        int numInt = Integer.valueOf(num);
        boolean isShow = true;
        if (num < 1){
            numString = "";
            isShow = false;
        }else if (num > 99){
            numString = "99";
        }

        try {
            localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", isShow);
            localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
            localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", activityName);
            localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", numString);
            localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());
            context.sendBroadcast(localIntent);

            NotificationManager notifyMgr = (NotificationManager)(context.getSystemService(NOTIFICATION_SERVICE));
            if(num!=0)notifyMgr.notify(NOTIFI_ID, notification);else notifyMgr.cancel(NOTIFI_ID);


        }catch (Exception e){
            e.printStackTrace();
            Log.e("SONY" + " Badge error", "set Badge failed");
        }
    }

    //VIVO
    private static void setBadgeOfVIVO(Context context,Notification notification,int NOTIFI_ID,int num){
        try {
            Intent localIntent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
            localIntent.putExtra("packageName", context.getPackageName());
            localIntent.putExtra("className", getLauncherClassName(context));
            localIntent.putExtra("notificationNum", num);
            context.sendBroadcast(localIntent);

            NotificationManager notifyMgr = (NotificationManager)(context.getSystemService(NOTIFICATION_SERVICE));
            if(num!=0)notifyMgr.notify(NOTIFI_ID, notification);else notifyMgr.cancel(NOTIFI_ID);

        }catch (Exception e){
            e.printStackTrace();
            Log.e("VIVO" + " Badge error", "set Badge failed");
        }
    }

    //OPPO *只支持部分机型
    private static void setBadgeOfOPPO(Context context, Notification notification,int NOTIFI_ID,int num){
        try {

            if (num == 0) {
                num = -1;
            }

            Intent intent = new Intent("com.oppo.unsettledevent");
            intent.putExtra("pakeageName", context.getPackageName());
            intent.putExtra("number", num);
            intent.putExtra("upgradeNumber", num);
            if (canResolveBroadcast(context, intent)) {
                context.sendBroadcast(intent);
            } else {

                try {
                    Bundle extras = new Bundle();
                    extras.putInt("app_badge_count", num);
                    context.getContentResolver().call(Uri.parse("content://com.android.badge/badge"), "setAppBadgeCount", null, extras);

                    NotificationManager notifyMgr = (NotificationManager)(context.getSystemService(NOTIFICATION_SERVICE));
                    if(num!=0)notifyMgr.notify(NOTIFI_ID, notification);else notifyMgr.cancel(NOTIFI_ID);


                } catch (Throwable th) {
                    Log.e("OPPO" + " Badge error", "unable to resolve intent: " + intent.toString());
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            Log.e("OPPO" + " Badge error", "set Badge failed");
        }
    }

    //ZUK
    private static void setBadgeOfZUK(Context context,Notification notification,int NOTIFI_ID, int num){
        final Uri CONTENT_URI = Uri.parse("content://com.android.badge/badge");
        try {
            Bundle extra = new Bundle();
            extra.putInt("app_badge_count", num);
            context.getContentResolver().call(CONTENT_URI, "setAppBadgeCount", null, extra);

            NotificationManager notifyMgr = (NotificationManager)(context.getSystemService(NOTIFICATION_SERVICE));
            if(num!=0)notifyMgr.notify(NOTIFI_ID, notification);else notifyMgr.cancel(NOTIFI_ID);


        }catch (Exception e){
            e.printStackTrace();
            Log.e("ZUK" + " Badge error", "set Badge failed");
        }

    }

    //HTC
    private static void setBadgeOfHTC(Context context,Notification notification,int NOTIFI_ID,int num){

        try {
            Intent intent1 = new Intent("com.htc.launcher.action.SET_NOTIFICATION");
            intent1.putExtra("com.htc.launcher.extra.COMPONENT", context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).getComponent().flattenToShortString());
            intent1.putExtra("com.htc.launcher.extra.COUNT", num);

            Intent intent = new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");
            intent.putExtra("packagename", context.getPackageName());
            intent.putExtra("count", num);

            if (canResolveBroadcast(context, intent1) || canResolveBroadcast(context, intent)) {
                context.sendBroadcast(intent1);
                context.sendBroadcast(intent);
            } else {
                Log.e("HTC" + " Badge error", "unable to resolve intent: " + intent.toString());
            }

            NotificationManager notifyMgr = (NotificationManager)(context.getSystemService(NOTIFICATION_SERVICE));
            if(num!=0)notifyMgr.notify(NOTIFI_ID, notification);else notifyMgr.cancel(NOTIFI_ID);


        }catch (Exception e){
            e.printStackTrace();
            Log.e("HTC" + " Badge error", "set Badge failed");
        }

    }

    //NOVA
    private static void setBadgeOfNOVA(Context context,Notification notification,int NOTIFI_ID,int num){
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put("tag", context.getPackageName()+ "/" +getLauncherClassName(context));
            contentValues.put("count", num);
            context.getContentResolver().insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"), contentValues);

            NotificationManager notifyMgr = (NotificationManager)(context.getSystemService(NOTIFICATION_SERVICE));
            if(num!=0)notifyMgr.notify(NOTIFI_ID, notification);else notifyMgr.cancel(NOTIFI_ID);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("NOVA" + " Badge error", "set Badge failed");
        }
    }

    //其他
    private static void setBadgeOfDefault(Context context,Notification notification,int NOTIFI_ID,int num){

        try {
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", num);
            intent.putExtra("badge_count_package_name", context.getPackageName());
            intent.putExtra("badge_count_class_name", getLauncherClassName(context));
            if (canResolveBroadcast(context, intent)) {
                context.sendBroadcast(intent);
            } else {
                Log.e("Default" + " Badge error", "unable to resolve intent: " + intent.toString());
            }

            NotificationManager notifyMgr = (NotificationManager)(context.getSystemService(NOTIFICATION_SERVICE));
            if(num!=0)notifyMgr.notify(NOTIFI_ID, notification);else notifyMgr.cancel(NOTIFI_ID);


        }catch (Exception e){
            e.printStackTrace();
            Log.e("Default" + " Badge error", "set Badge failed");
        }

    }





    //获取类名
    public static String getLauncherClassName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setPackage(context.getPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (info == null) {
            info = packageManager.resolveActivity(intent, 0);
        }
        return info.activityInfo.name;
    }



    //隐藏Bubble
    public static void hideBubble(Context context) {
        if(notification!=null){
            showBubble(context,notification,NOTIFI_ID,0);
        }
    }



    //广播
    public static boolean canResolveBroadcast(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> receivers = packageManager.queryBroadcastReceivers(intent, 0);
        return receivers != null && receivers.size() > 0;
    }


}