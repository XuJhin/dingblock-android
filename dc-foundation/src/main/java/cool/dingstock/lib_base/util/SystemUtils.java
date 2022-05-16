package cool.dingstock.lib_base.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import cool.dingstock.lib_base.BaseLibrary;

public class SystemUtils {

    public static final String SYSTEM_ROM_TYPE_EMUI = "Emui";
    public static final String SYSTEM_ROM_TYPE_MIUI = "Miui";
    public static final String SYSTEM_ROM_TYPE_FLYME = "Flyme";
    public static final String SYSTEM_ROM_TYPE_ANDROID = "Android";

    //EMUI标识
    private static final String KEY_EMUI_VERSION_CODE = "ro.build.version.emui";
    private static final String KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level";
    private static final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";

    //MIUI标识
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    //Flyme标识
    private static final String KEY_FLYME_ID_FALG_KEY = "ro.build.display.id";
    private static final String KEY_FLYME_ID_FALG_VALUE_KEYWORD = "Flyme";
    private static final String KEY_FLYME_ICON_FALG = "persist.sys.use.flyme.icon";
    private static final String KEY_FLYME_SETUP_FALG = "ro.meizu.setupwizard.flyme";
    private static final String KEY_FLYME_PUBLISH_FALG = "ro.flyme.published";

    private static final String IMEI_FILE_NAME = "ROKID_IMEI";

    private static Properties prop = null;

    public static String currentTimeMillisStr() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static int randomInt() {
        return new Random(Integer.MAX_VALUE).nextInt();
    }

    @SuppressLint("MissingPermission")
    public synchronized static String getIMEI() {
        // 先拿存储的结果，如果没有进行生成
        File file = new File(BaseLibrary.getInstance().getContext().getFilesDir(), IMEI_FILE_NAME);
        String imei = FileUtils.readFile(file);
        if (!TextUtils.isEmpty(imei)) {
            Logger.d("This phone IMEI: " + imei);
            return imei;
        }

        // 有些机型 可能没有权限
        try {
            TelephonyManager telephonyManager = (TelephonyManager)
                    BaseLibrary.getInstance().getContext().getSystemService(Activity.TELEPHONY_SERVICE);
            if (null != telephonyManager) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    imei = telephonyManager.getDeviceId();
                } else {
                    imei = telephonyManager.getImei();
                }
            }
        } catch (Exception e) {
            imei = "";
        }

        // 没有权限拿到 识别码时，随机生成 一个
        if (TextUtils.isEmpty(imei)) {
            imei = UUIDUtils.generateUUID();
        }

        // 存储到文件中
        FileUtils.writeFile(imei, file);

        Logger.d("This phone IMEI: " + imei);
        return imei;
    }

    // 判断微信是否可用
    public static boolean isWeixinAvilible() {
        final PackageManager packageManager = BaseLibrary.getInstance().getContext().getPackageManager();
        // 获取 PackageManager
        List<PackageInfo> packageInfo = packageManager.getInstalledPackages(0);
        // 获取所有已安装程序的包信息
        if (packageInfo != null) {
            for (int i = 0; i < packageInfo.size(); i++) {
                String pn = packageInfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * return ROM_TYPE ROM类型的枚举 description获取ROM类型: MIUI_ROM, FLYME_ROM, EMUI_ROM, OTHER_ROM
     */

    public static boolean isMiui() {
        return isPropertiesExist(KEY_EMUI_VERSION_CODE);
    }

    private static boolean isPropertiesExist(String... keys) {
        if (keys == null || keys.length == 0) {
            return false;
        }
        try {
            BuildProperties properties = BuildProperties.newInstance();
            for (String key : keys) {
                String value = properties.getProperty(key);
                if (value != null)
                    return true;
            }
            return false;
        } catch (IOException e) {
            Log.e("brand", e.toString());
            return false;
        }
    }

    public static String getRomType() {
        // 华为
        if (containsKey(KEY_EMUI_VERSION_CODE)
                || containsKey(KEY_EMUI_API_LEVEL)
                || containsKey(KEY_EMUI_CONFIG_HW_SYS_VERSION)) {
            return SYSTEM_ROM_TYPE_EMUI;
        }
        // 小米
        if (containsKey(KEY_MIUI_VERSION_CODE)
                || containsKey(KEY_MIUI_VERSION_NAME)
                || containsKey(KEY_MIUI_INTERNAL_STORAGE)) {
            return SYSTEM_ROM_TYPE_MIUI;
        }
        // 魅族
        if (containsKey(KEY_FLYME_ICON_FALG)
                || containsKey(KEY_FLYME_SETUP_FALG)
                || containsKey(KEY_FLYME_PUBLISH_FALG)) {
            return SYSTEM_ROM_TYPE_FLYME;
        }
        // 魅族
        String romName = getProperty(KEY_FLYME_ID_FALG_KEY);
        if (!TextUtils.isEmpty(romName) && romName.contains(KEY_FLYME_ID_FALG_VALUE_KEYWORD)) {
            return SYSTEM_ROM_TYPE_FLYME;
        }
        // 其他Android系统
        return SYSTEM_ROM_TYPE_ANDROID;
    }

    private static String getProperty(String key) {
        try {
            if (prop == null) {
                prop = new Properties();
                prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
            }

            return prop.getProperty(key);
        } catch (Exception e) {
            return "";
        }
    }

    private static boolean containsKey(String key) {
        try {
            if (prop == null) {
                prop = new Properties();
                prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
            }
            return prop.getProperty(key) != null;
        } catch (Exception e) {
            return isHaveInGetProp(key);
        }
    }

    private static boolean isHaveInGetProp(String name) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + name);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Logger.e("Unable to read prop " + name);
            return false;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return !TextUtils.isEmpty(line);
    }

    public static boolean isEmulator() {
        String url = "tel:" + "123456";
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        intent.setAction(Intent.ACTION_DIAL);
        // 是否可以处理跳转到拨号的 Intent
        Application context = BaseLibrary.getInstance().getContext();
        boolean canResolveIntent = intent.resolveActivity(context.getPackageManager()) != null;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.toLowerCase().contains("vbox")
                || Build.FINGERPRINT.toLowerCase().contains("test-keys")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.SERIAL.equalsIgnoreCase("unknown")
                || Build.SERIAL.equalsIgnoreCase("android")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT)
                || null == telephonyManager
                || telephonyManager.getNetworkOperatorName().toLowerCase().equals("android")
                || !canResolveIntent;
    }

    /**
     * 判断手机是否安装某个应用
     *
     * @param appPackageName 应用包名
     * @return true：安装，false：未安装
     */
    public static boolean isInstalled(Context context, String appPackageName) {
        if (context == null) {
            return false;
        }
        PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        for (PackageInfo pkif : pinfo) {
            String pn = pkif.packageName;
            if (appPackageName.equals(pn)) {
                return true;
            }
        }
        return false;
    }

    private static final class BuildProperties {

        private final Properties properties;

        private BuildProperties() throws IOException {
            properties = new Properties();
            // 读取系统配置信息build.prop类
            properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
        }

        public static BuildProperties newInstance() throws IOException {
            return new BuildProperties();
        }

        public boolean containsKey(final Object key) {
            return properties.containsKey(key);
        }

        public boolean containsValue(final Object value) {
            return properties.containsValue(value);
        }

        public Set<Map.Entry<Object, Object>> entrySet() {
            return properties.entrySet();
        }

        public String getProperty(final String name) {
            return properties.getProperty(name);
        }

        public String getProperty(final String name, final String defaultValue) {
            return properties.getProperty(name, defaultValue);
        }

        public boolean isEmpty() {
            return properties.isEmpty();
        }

        public Enumeration<Object> keys() {
            return properties.keys();
        }

        public Set<Object> keySet() {
            return properties.keySet();
        }

        public int size() {
            return properties.size();
        }

        public Collection<Object> values() {
            return properties.values();
        }
    }

}
