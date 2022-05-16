package cool.dingstock.lib_base.util

import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.text.TextUtils

/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/22  11:52
 */
object ActivityUtils {
    /**
     * 判断某个界面是否在前台
     *
     * @param activity 要判断的Activity
     * @return 是否在前台显示
     */
    fun isForeground(activity: Activity): Boolean {
        return isForeground(activity, activity::class.java.name)
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context   Context
     * @param className 界面的类名
     * @return 是否在前台显示
     */
    fun isForeground(context: Context?, className: String): Boolean {
        if (context == null || TextUtils.isEmpty(className)) return false
        val am: ActivityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val list: List<ActivityManager.RunningTaskInfo> = am.getRunningTasks(1)
        if (list.isNotEmpty()) {
            val cpn: ComponentName? = list[0].topActivity
            if (className == cpn?.className) return true
        }
        return false
    }
}