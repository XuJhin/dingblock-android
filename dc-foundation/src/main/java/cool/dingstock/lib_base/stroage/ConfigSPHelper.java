package cool.dingstock.lib_base.stroage;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import cool.dingstock.lib_base.BaseLibrary;

public class ConfigSPHelper {

    private static final String FILE_NAME = "dc_config";
    private static volatile ConfigSPHelper mInstance;
    private final SharedPreferences mPreferences;

    private ConfigSPHelper() {
        mPreferences = BaseLibrary.getInstance().getContext().
                getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public static ConfigSPHelper getInstance() {
        if (null == mInstance) {
            synchronized (ConfigSPHelper.class) {
                if (null == mInstance) {
                    mInstance = new ConfigSPHelper();
                }
            }
        }

        return mInstance;
    }


    public String getString(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        return mPreferences.getString(key, "");
    }

    public String getString(String key, String defaultStr) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        return mPreferences.getString(key, defaultStr);
    }

    public boolean getBoolean(String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        return mPreferences.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        return mPreferences.getBoolean(key, defaultValue);
    }


    public int getInt(String key) {
        if (TextUtils.isEmpty(key)) {
            return -1;
        }
        return mPreferences.getInt(key, -1);
    }


    public int getInt(String key, int defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        return mPreferences.getInt(key, defaultValue);
    }

    public Long getLong(String key, long defValue) {
        return mPreferences.getLong(key, defValue);
    }


    public void save(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(key, value);
        edit.apply();
    }


    public void save(String key, int value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putInt(key, value);
        edit.apply();
    }


    public void save(String key, long value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putLong(key, value);
        edit.apply();
    }


    public void save(String key, boolean value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }


    public boolean remove(String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.remove(key);
        return edit.commit();
    }


}
