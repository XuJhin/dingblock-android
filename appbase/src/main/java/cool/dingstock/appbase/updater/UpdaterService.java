package cool.dingstock.appbase.updater;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.File;

import cool.dingstock.appbase.R;
import cool.dingstock.lib_base.stroage.ConfigSPHelper;
import cool.dingstock.lib_base.util.Logger;

public class UpdaterService extends IntentService {

    public static final String KEY_DOWNLOAD_ID = "DownloadId";
    private static final String KEY_FILE_NAME = "FileName";
    private static final String FORMAT_FILE_NAME = "dc_%1$s.apk";

    private static final String NAME = UpdaterService.class.getSimpleName();

    private static final String INTENT_CHECK_UPDATE = "checkUpdate";
    private static final String INTENT_DOWNLOAD = "download";
    private static final String INTENT_INSTALL = "install";

    private static final String TITLE = "dc";
    private static final String MIME_TYPE = "application/vnd.android.package-archive";
	
	private static final boolean isPost = false;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UpdaterService() {
        super(NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else {
            startForeground(1, new Notification());
        }
    }

    public static void startCheck(Context context) {
        Logger.i("Start Call the UpdaterService to checkUpdate.");
        Intent intent = new Intent(context, UpdaterService.class);
        intent.setAction(INTENT_CHECK_UPDATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "dingstockUpdater";
        String channelName = "盯链";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        NotificationCompat.Builder notificationBuilder
                = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    public static void startDownload(Context context) {
        Logger.i("Start Call the  UpdaterService  to download.");
        Intent intent = new Intent(context, UpdaterService.class);
        intent.setAction(INTENT_DOWNLOAD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void startInstall(Context context) {
        Logger.i("Start Call the UpdaterService  to install.");
        Intent intent = new Intent(context, UpdaterService.class);
        intent.setAction(INTENT_INSTALL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Logger.i("This service be called.");

        if (null == intent || TextUtils.isEmpty(intent.getAction())) {
            Logger.w("Intent is null,so do nothing.");
            return;
        }

        Logger.d("This Action: " + intent.getAction());
        switch (intent.getAction()) {
            case INTENT_CHECK_UPDATE:
                checkUpdate();
                break;
            case INTENT_DOWNLOAD:
                download();
                break;
            case INTENT_INSTALL:
                install();
                break;
            default:
                Logger.d("The action is unknown.");
                break;
        }
    }


    private void checkUpdate() {
//        Logger.i("Start to checkUpdate");
//        ConfigData configData = MobileHelper.getInstance().getConfigData();
//        if (null == configData) {
//            return;
//        }
//        UpdateVerEntity version = configData.getUpdateConfig();
//        if (null == version) {
//            return;
//        }
//        String androidVersion = version.getAndroidVersion();
//        if (TextUtils.isEmpty(androidVersion)) {
//            Logger.w("VersionCode is empty.");
//            return;
//        }
//        if (TextUtils.isEmpty(version.getAndroidUpdateLink())) {
//            Logger.w("DownloadUrl is empty.");
//            return;
//        }
//        Logger.d("isPost: " + isPost + " ;ForceUpdate: " + version.getAndroidForceUpdate());
//        if (isPost) {
//            Logger.d("Updater has started.");
//            return;
//        }
//        String localVersion = BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")";
//        Logger.d("LocalVersion: " + localVersion + " ;ServiceVersionCode: " + androidVersion);
//        if (localVersion.compareTo(androidVersion) >= 0) {
//            Logger.i("The current version is the latest.");
//            return;
//        }
//        isPost = true;
//        Logger.d("Send the update Event.");
//        EventUpdateInfo event = new EventUpdateInfo(version.getAndroidVersion(),version.getAndroidForceUpdate(),version.getAndroidUpdateLink(),version.getAndroidUpdateTip());
//        EventBus.getDefault().post(event);
    }

    private void download() {
//        Logger.i("Start to download");
//        ConfigData configData = MobileHelper.getInstance().getConfigData();
//        if (null == configData) {
//            return;
//        }
//        ConfigVersion version = configData.getVersion();
//        if (null == version) {
//            return;
//        }
//        String androidUpdateLink = version.getAndroidUpdateLink();
//        if (TextUtils.isEmpty(androidUpdateLink)) {
//            return;
//        }
//        String fileName = String.format(FORMAT_FILE_NAME, version.getAndroidVersion());
//
//        Uri uri = Uri.parse(androidUpdateLink);
//        DownloadManager.Request request = new DownloadManager.Request(uri);
//        request.setTitle(TITLE);
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setMimeType(MIME_TYPE);
//        // 设置下载存放的文件夹和文件名字
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
//
//        // TODO 只允许 WIFI 模式下 下载APP 打开此功能 需要 dialog 提示用户切换到 wifi
//        // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
//
//        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//        if (null == downloadManager) {
//            Logger.w("Not found the downloadManager.");
//            return;
//        }
//
//        long downloadId = downloadManager.enqueue(request);
//
//        // 存储下载信息
//        ConfigSPHelper.getInstance().save(KEY_DOWNLOAD_ID, downloadId);
//        ConfigSPHelper.getInstance().save(KEY_FILE_NAME, fileName);
    }

    private void install() {
        Logger.i("Start to install.");

        long downloadId = ConfigSPHelper.getInstance().getLong(KEY_DOWNLOAD_ID, -99999L);
        if (-99999L == downloadId) {
            Logger.w("Not found the downloadId.");
            return;
        }
        String fileName = ConfigSPHelper.getInstance().getString(KEY_FILE_NAME);
        if (TextUtils.isEmpty(fileName)) {
            Logger.w("Not found the file.");
            return;
        }

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (null == downloadManager) {
            Logger.w("Not found the downloadManager.");
            return;
        }

        Uri downloadFileUri = downloadManager.getUriForDownloadedFile(downloadId);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Logger.d("This SDK >= N(7.0)");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            Logger.d("This SDK < N(7.0)");
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    fileName);
            downloadFileUri = Uri.fromFile(file);
        }

        Logger.i("DownloadFileUri: " + downloadFileUri);
        intent.setDataAndType(downloadFileUri, MIME_TYPE);
        startActivity(intent);
        Logger.d("Start to call the system install view.");
    }

}
