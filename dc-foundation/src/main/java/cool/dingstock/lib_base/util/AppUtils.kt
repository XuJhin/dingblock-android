package cool.dingstock.lib_base.util

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import cool.dingstock.lib_base.BuildConfig
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.net.NetworkInterface
import java.util.*

object AppUtils {


    /**
     * 获取渠道名
     *
     * @param ctx 此处习惯性的设置为activity，实际上context就可以
     * @return 如果没有获取成功，那么返回值为空
     */
    fun getUMChannelName(ctx: Context?): String? {
        if (ctx == null) {
            return null
        }
        var channelName: String? = null
        try {
            val packageManager = ctx.packageManager
            if (packageManager != null) {
                //注意此处为ApplicationInfo 而不是 ActivityInfo,因为友盟设置的meta-data是在application标签中，而不是某activity标签中，所以用ApplicationInfo
                val applicationInfo: ApplicationInfo =
                    packageManager.getApplicationInfo(ctx.packageName, PackageManager.GET_META_DATA)
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        channelName = applicationInfo.metaData.getString("UMENG_CHANNEL")
                    }
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return channelName
    }


    fun getAppMetaData(context: Context?, key: String?): String? {
        if (context == null || TextUtils.isEmpty(key)) {
            return null
        }
        var channelNumber: String? = null
        try {
            val packageManager = context.packageManager
            if (packageManager != null) {
                val applicationInfo =
                    packageManager.getApplicationInfo(
                        context.packageName,
                        PackageManager.GET_META_DATA
                    )
                if (applicationInfo.metaData != null) {
                    channelNumber = applicationInfo.metaData.getString(key)
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return channelNumber
    }

    fun getAppId(context: Context): String {
        val pm = context.packageManager ?: return BuildConfig.LIBRARY_PACKAGE_NAME
        val pi: PackageInfo?
        try {
            pi = pm.getPackageInfo(context.packageName, 0)
            if (pi != null) {
                return pi.packageName
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return BuildConfig.LIBRARY_PACKAGE_NAME
    }

    fun getVersionCode(context: Context): Int {
        var code: Int = 1
        try {
            val manager = context.packageManager
            val info = manager.getPackageInfo(context.packageName, 0)
            info.versionName
            code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.longVersionCode.toInt()
            } else {
                info.versionCode
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return code
    }

    fun getVersionName(context: Context): String {
        var versionName: String = ""
        try {
            val manager = context.packageManager
            val info = manager.getPackageInfo(context.packageName, 0)
            versionName = info.versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return versionName
    }

    fun getProcessName(context: Context): String? {
        // 直接获取 系统目录下的 数据
        var mBufferedReader: BufferedReader? = null
        try {
            val file = File("/proc/" + Process.myPid() + "/" + "cmdline")
            mBufferedReader = BufferedReader(FileReader(file))
            val processName = mBufferedReader.readLine().trim { it <= ' ' }
            if (!TextUtils.isEmpty(processName)) {
                return processName
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            if (null != mBufferedReader) {
                try {
                    mBufferedReader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        // 使用 ActivityManager 获取
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        for (procInfo in runningApps) {
            if (procInfo.pid == Process.myPid()) {
                return procInfo.processName
            }
        }
        return null
    }

    val applicationContext: Application?
        get() {
            try {
                return Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication")
                    .invoke(null, null as Array<Any?>?) as Application
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                val application = Class.forName("android.app.AppGlobals")
                    .getMethod("getInitialApplication")
                    .invoke(null, null as Array<Any?>?) as Application
                return application
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }



    @SuppressLint("HardwareIds")
    fun getDeviceInfo(context: Context): String? {
        try {
            val json = JSONObject()
            val tm = context
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var device_id: String? = null
            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    device_id = tm.meid
                } else {
                    device_id = tm.deviceId
                }
            }
            val mac = getMac(context)
            json.put("mac", mac)
            if (TextUtils.isEmpty(device_id)) {
                device_id = mac
            }
            if (TextUtils.isEmpty(device_id)) {
                device_id = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            }
            json.put("device_id", device_id)
            return json.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getMac(context: Context?): String? {
        var mac: String? = ""
        if (context == null) {
            return mac
        }
        if (Build.VERSION.SDK_INT < 23) {
            mac = getMacBySystemInterface(context)
        } else {
            mac = macByJavaAPI
            if (TextUtils.isEmpty(mac)) {
                mac = getMacBySystemInterface(context)
            }
        }
        return mac
    }

    @get:TargetApi(9)
    private val macByJavaAPI: String?
        private get() {
            try {
                val interfaces = NetworkInterface.getNetworkInterfaces()
                while (interfaces.hasMoreElements()) {
                    val netInterface = interfaces.nextElement()
                    if ("wlan0" == netInterface.name || "eth0" == netInterface.name) {
                        val addr = netInterface.hardwareAddress
                        if (addr == null || addr.isEmpty()) {
                            return null
                        }
                        val buf = StringBuilder()
                        for (b in addr) {
                            buf.append(String.format("%02X:", b))
                        }
                        if (buf.isNotEmpty()) {
                            buf.deleteCharAt(buf.length - 1)
                        }
                        return buf.toString().toLowerCase(Locale.getDefault())
                    }
                }
            } catch (e: Throwable) {
            }
            return null
        }

    @SuppressLint("HardwareIds")
    private fun getMacBySystemInterface(context: Context?): String {
        return if (context == null) {
            ""
        } else try {
            val wifi =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (checkPermission(context, Manifest.permission.ACCESS_WIFI_STATE)) {
                val info = wifi.connectionInfo
                info.macAddress
            } else {
                ""
            }
        } catch (e: Throwable) {
            ""
        }
    }

    fun checkPermission(context: Context?, permission: String): Boolean {
        var result = false
        if (context == null) {
            return result
        }
        if (Build.VERSION.SDK_INT >= 23) {
            result = try {
                val clazz = Class.forName("android.content.Context")
                val method = clazz.getMethod("checkSelfPermission", String::class.java)
                val rest = method.invoke(context, permission) as Int
                rest == PackageManager.PERMISSION_GRANTED
            } catch (e: Throwable) {
                false
            }
        } else {
            val pm = context.packageManager
            if (pm.checkPermission(
                    permission,
                    context.packageName
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                result = true
            }
        }
        return result
    }
}