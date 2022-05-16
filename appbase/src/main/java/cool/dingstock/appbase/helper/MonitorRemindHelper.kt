package cool.dingstock.appbase.helper

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import cool.dingstock.appbase.dagger.AppBaseApiHelper
import cool.dingstock.appbase.entity.bean.config.RemindTimeEntity
import cool.dingstock.appbase.util.CalendarReminderUtils
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.appbase.dialog.remind.MonitorRemindTimeDialog
import cool.dingstock.appbase.entity.bean.home.AlarmFromWhere
import cool.dingstock.appbase.entity.bean.home.AlarmRefreshEvent
import cool.dingstock.appbase.exception.DcException
import cool.dingstock.appbase.net.api.common.CommonApi
import cool.dingstock.appbase.util.NotificationsUtils
import cool.dingstock.lib_base.util.TimeUtils
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class MonitorRemindHelper(
    private val fromWhere: AlarmFromWhere,
    private val pos: Int,
    private val productId: String?
) {

    @Inject
    lateinit var commonApi: CommonApi

    val oneMinute = 60 * 1 * 1000L
    val fiveMinute = 60 * 5 * 1000L
    val tenMinute = 60 * 10 * 1000L
    val fifteenMinute = 60 * 15 * 1000L

    val remindTypeCalendar = "calendar"
    val remindTypeDcPush = "dcPush"

    private val permissionCalendar = arrayOf(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR
    )

    private var mRequestCode = 101
    private var isFromDialog = false
    private var isDialogChooseDefault = true

    init {
        AppBaseApiHelper.appBaseComponent.inject(this)
    }

    private fun getRemindConfig(): RemindTimeEntity {
        return RemindConfigRefreshHelper.remindConfig
    }

    fun setRemind(
        context: Context,
        fragmentManager: FragmentManager?,
        raffleId: String,
        desc: String,
        notifyData: Long
    ) {
        isFromDialog = false
        isDialogChooseDefault = true
        if (!RemindConfigRefreshHelper.isRefreshRemindConfig) {
            fetchRemindSetting(context, fragmentManager, raffleId, desc, notifyData)
        } else {
            requestPermissionAndSetRemind(context, fragmentManager, raffleId, desc, notifyData)
        }
    }

    private fun requestPermissionAndSetRemind(
        context: Context,
        fragmentManager: FragmentManager?,
        raffleId: String,
        desc: String,
        notifyData: Long
    ) {
        val remindConfig = getRemindConfig()
        if (remindConfig.isSetDefault) {
            if (remindConfig.isCalendar) {
                if (remindConfig.isChoose1 || remindConfig.isChoose2 || remindConfig.isChoose3 || remindConfig.isChoose4) {
                    requestCalendarPermission(context, raffleId, desc, remindConfig, notifyData)
                }
            } else if (remindConfig.isAppPush) {
                if (NotificationsUtils.isNotificationEnabled(context)) {
                    lightAlarm(context, raffleId, remindConfig)
                } else {
                    openSystemSettingPage(context)
                }
            }
        } else {
            if (fragmentManager != null) {
                val remindDialog = MonitorRemindTimeDialog()
                remindDialog.mContext = context
                remindDialog.mRemindTimeConfig = remindConfig
                remindDialog.raffleId = raffleId
                remindDialog.desc = desc
                remindDialog.fromWhere = fromWhere
                remindDialog.pos = pos
                remindDialog.productId = productId.toString()
                remindDialog.notifyData = notifyData
                remindDialog.show(fragmentManager, "remindTimeDialog")
            }
        }
    }

    fun cancelRemind(
        context: Context,
        raffleId: String,
        remindDesc: String
    ) {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                permissionCalendar,
                ++mRequestCode
            )
        } else {
            CalendarReminderUtils.deleteCalendarEvent(context, remindDesc)
            cancelRemindPush(context, raffleId)
        }
    }


    fun setRemindInDialog(
        remindConfig: RemindTimeEntity,
        context: Context,
        raffleId: String,
        desc: String,
        isChooseDefault: Boolean,
        notifyData: Long
    ): Boolean {
        isDialogChooseDefault = isChooseDefault
        isFromDialog = true
        if (remindConfig.isCalendar) {
            if (remindConfig.isChoose1 || remindConfig.isChoose2 || remindConfig.isChoose3 || remindConfig.isChoose4) {
                return requestCalendarPermission(context, raffleId, desc, remindConfig, notifyData)
            }
        } else if (remindConfig.isAppPush) {
            return if (NotificationsUtils.isNotificationEnabled(context)) {
                lightAlarm(context, raffleId, remindConfig)
                true
            } else {
                openSystemSettingPage(context)
                false
            }
        }
        return true
    }


    fun initRemindApiData(remindConfig: RemindTimeEntity): ArrayList<Long> {
        val arrayList: ArrayList<Long> = arrayListOf()
        if (remindConfig.isChoose1) {
            arrayList.add(oneMinute)
        }
        if (remindConfig.isChoose2) {
            arrayList.add(fiveMinute)
        }
        if (remindConfig.isChoose3) {
            arrayList.add(tenMinute)
        }
        if (remindConfig.isChoose4) {
            arrayList.add(fifteenMinute)
        }
        if (arrayList.isEmpty()) return arrayListOf()
        return arrayList
    }

    private fun requestCalendarPermission(
        context: Context,
        raffleId: String,
        desc: String,
        remindConfig: RemindTimeEntity,
        notifyData: Long
    ): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                permissionCalendar,
                ++mRequestCode
            )
            return false
        } else {
            var isShowToast = 0
            if (remindConfig.isChoose1) {
                val showToast = setCalendarRemind(context, desc, notifyData - oneMinute)
                if (showToast) isShowToast += 1
            }
            if (remindConfig.isChoose2) {
                val showToast = setCalendarRemind(context, desc, notifyData - fiveMinute)
                if (showToast) isShowToast += 1
            }
            if (remindConfig.isChoose3) {
                val showToast = setCalendarRemind(context, desc, notifyData - tenMinute)
                if (showToast) isShowToast += 1
            }
            if (remindConfig.isChoose4) {
                val showToast =
                    setCalendarRemind(context, desc, notifyData - fifteenMinute)
                if (showToast) isShowToast += 1
            }
            if (isShowToast != 0) {
                lightAlarm(context, raffleId, remindConfig)
            }
            return true
        }
    }

    private fun eventRefreshAlarmState(isLightUpAlarm: Boolean) {
        EventBus.getDefault().post(
            AlarmRefreshEvent(
                pos = pos,
                isLightUpAlarm = isLightUpAlarm,
                fromWhere = fromWhere,
                productId = productId
            )
        )
    }

    fun generateRemindMsg(
        goodName: String,
        shopName: String,
        saleTime: Long,
        saleWay: String,
        salePrize: String
    ): String {
        val reallySaleTime = TimeUtils.formatTimestampS2(saleTime)
        return """
            $goodName
            $shopName
            $reallySaleTime
            $saleWay
            $salePrize
            """.trimIndent()
    }

    private fun setCalendarRemind(context: Context, desc: String, remindTime: Long): Boolean {
        val notifyData: Long = if (0 > System.currentTimeMillis()) {
            0
        } else {
            remindTime
        }
        return CalendarReminderUtils.addCalendarEvent(context, desc, "", notifyData)
    }

    private fun lightAlarm(context: Context, raffleId: String, remindConfig: RemindTimeEntity) {
        val remindType = if (remindConfig.isCalendar) {
            remindTypeCalendar
        } else {
            remindTypeDcPush
        }
        val isDefault = remindConfig.isSetDefault

        commonApi.setRemind(raffleId, remindType, isDefault, initRemindApiData(remindConfig))
            .subscribe({
                if (!it.err) {
                    eventRefreshAlarmState(isLightUpAlarm = true)

                    val toastMsg = if (isFromDialog)
                        if (isDialogChooseDefault) "设置提醒成功，通过监控-设置可修改提醒设置"
                        else
                            if (remindConfig.isCalendar) "设置成功,将通过日历提醒您"
                            else "设置成功,将通过APP推送提醒您"
                    else
                        if (remindConfig.isCalendar) "设置成功,将通过日历提醒您"
                        else "设置成功,将通过APP推送提醒您"

                    ToastUtil.getInstance()
                        .makeTextAndShow(
                            context,
                            toastMsg,
                            Toast.LENGTH_SHORT
                        )
                } else {
                    ToastUtil.getInstance()
                        .makeTextAndShow(context, it.msg, Toast.LENGTH_SHORT)
                }
            }, {
                ToastUtil.getInstance()
                    .makeTextAndShow(context, (it as DcException).msg, Toast.LENGTH_SHORT)
            })
    }

    private fun cancelRemindPush(context: Context, raffleId: String) {
        commonApi.cancelRemind(raffleId)
            .subscribe({
                if (!it.err) {
                    eventRefreshAlarmState(isLightUpAlarm = false)
                    ToastUtil.getInstance()
                        .makeTextAndShow(
                            context,
                            "取消提醒成功",
                            Toast.LENGTH_SHORT
                        )
                } else {
                    ToastUtil.getInstance()
                        .makeTextAndShow(context, it.msg, Toast.LENGTH_SHORT)
                }
            }, {
                ToastUtil.getInstance()
                    .makeTextAndShow(context, (it as DcException).msg, Toast.LENGTH_SHORT)
            })
    }

    fun updateRemindSetting(
        context: Context,
        remindType: String,
        remindAt: ArrayList<Long>
    ) {
        commonApi.updateRemindSetting(remindType, remindAt)
            .subscribe({
                if (!it.err) {
                    RemindConfigRefreshHelper.isRefreshRemindConfig = false
                } else {
                    ToastUtil.getInstance().makeTextAndShow(context, it.msg, Toast.LENGTH_SHORT)
                }
            }, {
                ToastUtil.getInstance()
                    .makeTextAndShow(context, (it as DcException).msg, Toast.LENGTH_SHORT)
            })
    }

    private fun fetchRemindSetting(
        context: Context,
        fragmentManager: FragmentManager?,
        raffleId: String,
        desc: String,
        notifyData: Long
    ) {
        var isCalendar = false
        var isAppPush = false

        var choose1 = false
        var choose2 = false
        var choose3 = false
        var choose4 = false
        var isDefault = true

        commonApi.fetchRemindSetting()
            .subscribe({
                if (!it.err) {
                    if (it.res?.remindType == remindTypeCalendar) {
                        isCalendar = true
                    } else {
                        isAppPush = true
                    }
                    it.res?.remindAt?.forEach { time ->
                        if (time == oneMinute) choose1 = true
                        if (time == fiveMinute) choose2 = true
                        if (time == tenMinute) choose3 = true
                        if (time == fifteenMinute) choose4 = true
                    }
                    if (!choose1 && !choose2 && !choose3 && !choose4) {
                        choose1 = true
                        isCalendar = true
                        isAppPush = false
                        isDefault = false
                    }
                    RemindConfigRefreshHelper.isRefreshRemindConfig = true
                    RemindConfigRefreshHelper.remindConfig = RemindTimeEntity(
                        choose1,
                        choose2,
                        choose3,
                        choose4,
                        isCalendar,
                        isAppPush,
                        isDefault
                    )
                    requestPermissionAndSetRemind(
                        context,
                        fragmentManager,
                        raffleId,
                        desc,
                        notifyData
                    )
                } else {
                    ToastUtil.getInstance().makeTextAndShow(context, it.msg, Toast.LENGTH_SHORT)
                }
            }, {
                ToastUtil.getInstance()
                    .makeTextAndShow(context, (it as DcException).msg, Toast.LENGTH_SHORT)
            })
    }

    fun openSystemSettingPage(context: Context) {
        val intent = Intent()
        try {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS

            //8.0及以后版本使用这两个extra.  >=API 26
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.applicationInfo.packageName)
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.applicationInfo.uid)

            //5.0-7.1 使用这两个extra.  <= API 25, >=API 21
            intent.putExtra("app_package", context.applicationInfo.packageName)
            intent.putExtra("app_uid", context.applicationInfo.uid)

            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()

            //其他低版本或者异常情况，走该节点。进入APP设置界面
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.putExtra("package", context.applicationInfo.packageName)
            context.startActivity(intent)
        }
    }
}