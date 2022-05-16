package cool.dingstock.appbase.service;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import cool.dingstock.appbase.R;
import cool.dingstock.lib_base.util.FileUtils;
import cool.dingstock.lib_base.util.Logger;


public class RecordService extends Service {
    private static final String TAG = RecordService.class.getSimpleName();
    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private MediaRecorder mediaRecorder;
    private VirtualDisplay virtualDisplay;


    private boolean running;
    private int width = 720;
    private int height = 1080;
    private int dpi;
    private String filePath;

    @Override
    public IBinder onBind(Intent intent) {
        return new RecordBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int code = intent.getIntExtra("code", -100);
        Intent data = intent.getParcelableExtra("data");
        if (code == Activity.RESULT_OK) {
            mediaProjection = projectionManager.getMediaProjection(code, data);
            startRecord();
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread serviceThread = new HandlerThread("service_thread",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        serviceThread.start();
        running = false;
        mediaRecorder = new MediaRecorder();
        mStartForegroundService();
    }


    private void mStartForegroundService() {
        Intent activityIntent = new Intent();
        activityIntent.setClassName("cool.dingstock.mobile.activity.index", "HomeIndexActivity");
        activityIntent.setAction("stop");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "001";
            String channelName = "screenRecorder";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (manager != null) {
                manager.createNotificationChannel(channel);
                Notification notification = new Notification.
                        Builder(getApplicationContext(), channelId)
                        .setOngoing(true)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setCategory(Notification.CATEGORY_SERVICE)
                        .setContentTitle("录屏中")
                        .setContentIntent(contentIntent)
                        .build();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    //                    startForeground(100, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
                    startForeground(100, notification);
                } else {
                    startForeground(100, notification);
                }
            }
        } else {
            startForeground(100, new Notification());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestory");
    }

    public void setProjectionManager(MediaProjectionManager projectionManager) {
        this.projectionManager = projectionManager;
    }

    public boolean isRunning() {
        return running;
    }

    public void setConfig(int width, int height, int dpi) {
        this.width = width;
        this.height = height;
        this.dpi = dpi;
    }

    public String startRecord() {
        if (mediaProjection == null || running) {
            return null;
        }

        filePath = initRecorder();
        createVirtualDisplay();
        mediaRecorder.start();
        running = true;
        return filePath;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public String stopRecord() {
        if (!running) {
            return null;
        }
        try {
            running = false;
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            virtualDisplay.release();
            mediaProjection.stop();
        } catch (Exception e) {
        }
        return filePath;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createVirtualDisplay() {
        virtualDisplay = mediaProjection.createVirtualDisplay("MainScreen", width, height, dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), null, null);
    }

    private String initRecorder() {
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setVideoSize(width, height);
        mediaRecorder.setVideoFrameRate(30);
        mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        mediaRecorder.setCaptureRate(60);
        String dirPath = FileUtils.getDCScreenRecorderDirCompat() + "/ScreenRecorde/";
        File dir = new File(dirPath);
        if(!dir.exists()){
            dir.mkdirs();
        }
        String filePath = dirPath + System.currentTimeMillis() + ".mp4";
        mediaRecorder.setOutputFile(filePath);
        try {
            mediaRecorder.prepare();
            Logger.e("prepare success");
        } catch (IOException e) {
            e.printStackTrace();
            Logger.e("prepare error");
        }

        return filePath;
    }

    public String getsaveDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "ScreenRecord" + "/";

            File file = new File(rootDir);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    return null;
                }
            }
            return rootDir;
        } else {
            return null;
        }
    }

    public class RecordBinder extends Binder {
        public RecordService getRecordService() {
            return RecordService.this;
        }
    }
}