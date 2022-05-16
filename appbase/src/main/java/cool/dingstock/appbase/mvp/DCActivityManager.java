package cool.dingstock.appbase.mvp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.List;

import cool.dingstock.appbase.R;
import cool.dingstock.appbase.entity.event.update.EventUpdateInfo;
import cool.dingstock.appbase.widget.dialog.TipImgDialog;
import cool.dingstock.lib_base.stroage.ConfigSPHelper;
import cool.dingstock.lib_base.util.CollectionUtils;
import cool.dingstock.lib_base.util.Logger;

public class DCActivityManager {

    private static volatile DCActivityManager mInstance;

    private final LinkedList<BaseActivity> activityList;

    private boolean isForeground;

    private DCActivityManager() {
        EventBus.getDefault().register(this);
        activityList = new LinkedList<>();
    }

    public static DCActivityManager getInstance() {
        if (null == mInstance) {
            synchronized (DCActivityManager.class) {
                if (null == mInstance) {
                    mInstance = new DCActivityManager();
                }
            }
        }
        return mInstance;
    }

    public void add(@NonNull BaseActivity activity) {
        synchronized (DCActivityManager.class) {
            if (activityList.contains(activity)) {
                Logger.w("ActivityList have this activity. so remove it.");
                activityList.remove(activity);
            }

            activityList.addFirst(activity);
            Logger.i(activity.getClass().getSimpleName() + " add to the Top.");
        }
    }

    public void remove(@NonNull BaseActivity activity) {
        synchronized (DCActivityManager.class) {
            activityList.remove(activity);
            Logger.i("Remove the ", activity.getClass().getSimpleName());
        }
    }

    public BaseActivity getTopActivity() {
        synchronized (DCActivityManager.class) {
            if(activityList.size() ==0){
                return null;
            }
            Logger.i("The top activity: ", activityList.getFirst().getClass().getSimpleName());
            return activityList.getFirst();
        }
    }

    public LinkedList<BaseActivity> getActivityList(){
        return activityList;
    }

    public boolean isTopActivity(@NonNull String className) {
        synchronized (DCActivityManager.class) {
            Logger.i("The className: ", className, "; The top activity: " + activityList.getFirst().getClass().getName());
            return activityList.getFirst().getClass().getName().equals(className);
        }
    }

    public boolean isTopModule(@NonNull String moduleTag) {
        synchronized (DCActivityManager.class) {
            Logger.i("The top module: ", activityList.getFirst().moduleTag());
            return activityList.getFirst().moduleTag().equals(moduleTag);
        }
    }

    public void finishModule(@NonNull String moduleTag, @NonNull List<Class<? extends Activity>> ignoreActivityList) {
        synchronized (DCActivityManager.class) {
            for (BaseActivity activity : activityList) {
                if (activity.moduleTag().equals(moduleTag) && !ignoreActivityList.contains(activity.getClass())) {
                    activity.finish();
                }
            }
        }
    }

    public void finishActivity(@NonNull List<Class<? extends Activity>> finishActivityList) {
        synchronized (DCActivityManager.class) {
            for (BaseActivity activity : activityList) {
                if (finishActivityList.contains(activity.getClass())) {
                    activity.finish();
                }
            }
        }
    }

    public void finishAllModule() {
        synchronized (DCActivityManager.class) {
            for (BaseActivity activity : activityList) {
                activity.finish();
            }
        }
    }

    public void finishAllIgnoreModule(@NonNull String moduleTag) {
        synchronized (DCActivityManager.class) {
            for (BaseActivity activity : activityList) {
                if (!activity.moduleTag().equals(moduleTag)) {
                    activity.finish();
                }
            }
        }
    }

    public boolean isActivityListEmpty() {
        return CollectionUtils.isEmpty(activityList);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateEvent(EventUpdateInfo update) {
        Logger.i("Receive the update Event.");
        if (!update.getAndroidForceUpdate() && ConfigSPHelper.getInstance().getBoolean(update.getAndroidVersion())) {
            return;
        }
        // TODO: 2019-08-22 不在提醒 按钮
        TipImgDialog.newBuilder(getTopActivity())
                .cancelable(!update.getAndroidForceUpdate())
                .bottomTxt(update.getAndroidForceUpdate() ? "" : "不再提醒", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConfigSPHelper.getInstance().save(update.getAndroidVersion(), true);
                    }
                })
                .title("版本更新")
                .imgres(R.drawable.app_update)
                .content(TextUtils.isEmpty(update.getAndroidUpdateTip())
                        ? getTopActivity().getString(R.string.update_dialog_title)
                        : update.getAndroidUpdateTip())
                .actionBtn("去更新", (view) -> {
                    if (null == getTopActivity()) {
                        return;
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(update.getLink()));
                    getTopActivity().startActivity(intent);
                }).show();
    }

    public void setForeground(boolean foreground) {
        isForeground = foreground;
    }

    public boolean isFistLaunch(){
        for (int i =0;i<activityList.size();i++){
            if(activityList.get(i).getClass().getName().contains("HomeIndexActivity")){
                return false;
            }
        }
        return true;
    }

}
