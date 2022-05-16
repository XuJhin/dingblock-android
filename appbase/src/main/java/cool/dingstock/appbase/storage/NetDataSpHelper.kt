package cool.dingstock.appbase.storage

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import cool.dingstock.lib_base.BaseLibrary

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/8  11:36
 */
object NetDataSpHelper {
    val fileName = "dc_net_cache"

    private val mPreferences: SharedPreferences by lazy {
        BaseLibrary.getInstance().context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    }


    fun getString(key: String?): String? {
        return if (TextUtils.isEmpty(key)) {
            null
        } else mPreferences.getString(key, "")
    }

    fun getBoolean(key: String?): Boolean {
        return if (TextUtils.isEmpty(key)) {
            false
        } else mPreferences.getBoolean(key, false)
    }


    fun getInt(key: String?): Int {
        return if (TextUtils.isEmpty(key)) {
            -1
        } else mPreferences.getInt(key, -1)
    }


    fun getInt(key: String?, defaultValue: Int): Int {
        return if (TextUtils.isEmpty(key)) {
            defaultValue
        } else mPreferences.getInt(key, defaultValue)
    }

    fun getLong(key: String?, defValue: Long): Long {
        return mPreferences.getLong(key, defValue)
    }


    fun save(key: String?, value: String?) {
        if (TextUtils.isEmpty(key)) {
            return
        }
        val edit = mPreferences.edit()
        edit.putString(key, value)
        edit.apply()
    }


    fun save(key: String?, value: Int) {
        if (TextUtils.isEmpty(key)) {
            return
        }
        val edit = mPreferences.edit()
        edit.putInt(key, value)
        edit.apply()
    }


    fun save(key: String?, value: Long) {
        if (TextUtils.isEmpty(key)) {
            return
        }
        val edit = mPreferences.edit()
        edit.putLong(key, value)
        edit.apply()
    }


    fun save(key: String?, value: Boolean) {
        if (TextUtils.isEmpty(key)) {
            return
        }
        val edit = mPreferences.edit()
        edit.putBoolean(key, value)
        edit.apply()
    }


    fun remove(key: String?): Boolean {
        if (TextUtils.isEmpty(key)) {
            return false
        }
        val edit = mPreferences.edit()
        edit.remove(key)
        return edit.commit()
    }




}