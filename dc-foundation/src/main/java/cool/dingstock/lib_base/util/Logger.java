package cool.dingstock.lib_base.util;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cool.dingstock.lib_base.BuildConfig;

public class Logger {
    private static String TAG = "DingChao - %1$s.%2$s(L:%3$d)";
    private static List<String> ignoreClassList = new ArrayList<>();

    private Logger() {
    }

    public static void initialize(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            TAG = tag + " - %1$s.%2$s(L:%3$d)";
        }
    }

    public static void addIgnoreClazz(@NonNull String clazzName) {
        if (!TextUtils.isEmpty(clazzName)) {
            ignoreClassList.add(clazzName);
        }
    }


    public static void v(String... messages) {
        String message = concatMessage(messages);
        int maxLogChars = 1364;

        for (int i = 0; i < message.length(); i += maxLogChars) {
            int end = i + maxLogChars;
            if (end > message.length()) {
                end = message.length();
            }

            Log.v(generateTag(), message.substring(i, end).trim());
        }

    }

    public static void d(String... messages) {
        if (BuildConfig.DEBUG && !isIgnore()) {
            String message = concatMessage(messages);
            int maxLogChars = 1364;

            for (int i = 0; i < message.length(); i += maxLogChars) {
                int end = i + maxLogChars;
                if (end > message.length()) {
                    end = message.length();
                }

                Log.d(generateTag(), message.substring(i, end).trim());
            }

        }
    }

    public static void i(String... messages) {
        String message = concatMessage(messages);
        int maxLogChars = 1364;

        for (int i = 0; i < message.length(); i += maxLogChars) {
            int end = i + maxLogChars;
            if (end > message.length()) {
                end = message.length();
            }

            Log.i(generateTag(), message.substring(i, end).trim());
        }

    }

    public static void w(String... messages) {
        String message = concatMessage(messages);
        int maxLogChars = 1364;

        for (int i = 0; i < message.length(); i += maxLogChars) {
            int end = i + maxLogChars;
            if (end > message.length()) {
                end = message.length();
            }

            Log.w(generateTag(), message.substring(i, end).trim());
        }

    }

    public static void e(String... messages) {
        String message = concatMessage(messages);
        int maxLogChars = 1364;

        for (int i = 0; i < message.length(); i += maxLogChars) {
            int end = i + maxLogChars;
            if (end > message.length()) {
                end = message.length();
            }

            Log.e(generateTag(), message.substring(i, end).trim());
        }

    }

    private static String generateTag() {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        return String.format(Locale.getDefault(), TAG, callerClazzName, caller.getMethodName(), caller.getLineNumber());
    }

    private static boolean isIgnore() {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
        String callerClazzName = caller.getClassName();
        if (ignoreClassList.contains(callerClazzName)) {
            return true;
        } else {
            Iterator var2 = ignoreClassList.iterator();

            String clazzName;
            do {
                if (!var2.hasNext()) {
                    return false;
                }

                clazzName = (String) var2.next();
            } while (!callerClazzName.startsWith(clazzName + "$"));

            return true;
        }
    }

    private static String concatMessage(String... messages) {
        if (null != messages && messages.length >= 1) {
            StringBuilder sb = new StringBuilder();
            String[] var2 = messages;
            int var3 = messages.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                String message = var2[var4];
                sb.append(message);
            }

            return sb.toString();
        } else {
            return "";
        }
    }
}
