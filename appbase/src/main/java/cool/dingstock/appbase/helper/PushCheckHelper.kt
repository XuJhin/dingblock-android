package cool.dingstock.appbase.helper

import android.app.Activity
import cool.dingstock.appbase.constant.CommonConstant
import cool.dingstock.appbase.push.DCPushManager
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.util.NotificationsUtils

/**
 *
 * 检测位置： App主页、退出我的通知页、退出监控订阅页
 *
 * 显示逻辑：每七天触发一次，
 *          App主页使用一个值判断
 *          通知页和监控页共同使用另外一个值进行判断
 */
class PushCheckHelper {
	private lateinit var mActivity: Activity

	fun with(act: Activity): PushCheckHelper {
		this.mActivity = act
		return this
	}

	fun start() {
		showPushDialog { mActivity.finish() }
	}

	/**
	 * @param closeAction   点击关闭时 执行
	 * @param ignoreAction  检测不弹出对话框时执行
	 */
	private fun showPushDialog(ignoreAction: () -> Unit) {
		if (shouldShowPushDialog()) {
			DcUriRequest(mActivity, CommonConstant.Uri.PUSH_DIALOG)
					.dialogCenterAni()
					.start()
			mActivity.finish()
		} else {
			ignoreAction()
		}
	}

	fun checkPushPermission() {
		if (shouldShowPushDialog()) {
			DcUriRequest(mActivity, CommonConstant.Uri.PUSH_DIALOG)
					.dialogCenterAni()
					.start()
		}
	}

	private fun shouldShowPushDialog(): Boolean {
		if (!::mActivity.isInitialized) {
			throw  IllegalArgumentException("请先初始化context")
		}
		val pushDisableTime = DCPushManager.getInstance().pushDisableTime
		val enabled = NotificationsUtils.isNotificationEnabled(mActivity)
		if (pushDisableTime < 0) {
			return false
		}
		if (enabled && pushDisableTime != 0L) {
			DCPushManager.getInstance().clearPushDisableTime()
		}
		if (0L == pushDisableTime && !enabled) {
			DCPushManager.getInstance().updatePushDisableTime()
			return true
		}
		val sevenDayTime = (7 * 24 * 3600 * 1000).toLong()
		if (System.currentTimeMillis() - pushDisableTime > sevenDayTime && !enabled) {
			DCPushManager.getInstance().updatePushDisableTime()
			return true
		}
		return false
	}
}

