package cool.dingstock.appbase.updater;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import cool.dingstock.lib_base.stroage.ConfigSPHelper;
import cool.dingstock.lib_base.util.Logger;

public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d("Receive the DownloadManager Broadcast.");

        if (null == intent || TextUtils.isEmpty(intent.getAction())) {
            Logger.e("This intent can't be null.");
            return;
        }

        String action = intent.getAction();
        Logger.d("Action: " + action);
        if (!DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
            Logger.w("This Action is not Download complete.");
            return;
        }

        long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        long saveDownloadId = ConfigSPHelper.getInstance().getLong(UpdaterService.KEY_DOWNLOAD_ID, -99999L);
        Logger.d("DownloadId: " + downloadId + " ;SaveDownloadId: " + saveDownloadId);
        if (downloadId != saveDownloadId) {
            Logger.w("This is not mine downloadId");
            return;
        }
        // 开始安装
        Logger.i("Start to call the UpdateService to install new version.");
        UpdaterService.startInstall(context);
    }

}
