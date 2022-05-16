package cool.dingstock.appbase.helper.front

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Bundle

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/5/28 10:09
 * @Version:         1.1.0
 * @Description:	app前后台切换监听
 */
class AppFrontBackHelper {
//	fun isAppOnForeground(): Boolean {
//		val activityManager: ActivityManager = getApplicationContext()
//				.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//		val packageName: String = getApplicationContext().packageName
//		/**
//		 * 获取Android设备中所有正在运行的App
//		 */
//		/**
//		 * 获取Android设备中所有正在运行的App
//		 */
//		val appProcesses: List<ActivityManager.RunningAppProcessInfo> = activityManager
//				.runningAppProcesses ?: return false
//		for (appProcess in appProcesses) {
//			// The name of the process that this object is associated with.
//			if (appProcess.processName.equals(packageName)
//					&& appProcess.importance === ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//				return true
//			}
//		}
//		return false
//	}
//
	private var mOnAppStatusListener: OnAppStatusListener? = null
	fun register(application: Application, listener: OnAppStatusListener) {
		this.mOnAppStatusListener = listener
		application.registerActivityLifecycleCallbacks(object :
				Application.ActivityLifecycleCallbacks {
			private var activityStartCount = 0
			override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
			}

			override fun onActivityStarted(activity: Activity) {
				activityStartCount++
				if (activityStartCount == 1) {
					mOnAppStatusListener?.onFront()
				}
			}

			override fun onActivityResumed(activity: Activity) {
			}

			override fun onActivityPaused(activity: Activity) {
				//				if (!isAppOnForeground()) {
				//					mOnAppStatusListener?.onBack()
				//					SplashHelper.getInstance().updateAppToBackState(true)
				//				}
			}

			override fun onActivityStopped(activity: Activity) {
				activityStartCount--
				if (activityStartCount == 0) {
					mOnAppStatusListener?.onBack()
				}
			}

			override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
			}

			override fun onActivityDestroyed(activity: Activity) {
			}
		})
	}
}

interface OnAppStatusListener {
	//app处于前台
	fun onFront()

	//app处于后台
	fun onBack()
}