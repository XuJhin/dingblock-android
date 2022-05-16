package cool.dingstock.appbase.entity.bean.mine;

import android.os.Build;

import cool.dingstock.lib_base.BuildConfig;
import cool.dingstock.lib_base.util.SystemUtils;



data class PhoneInfo (
    var model  : String? = "Model:",
    var brand  : String? = "厂商:",
    var imei  : String? = "IEMI:",
    var release  : String? = "Android系统版本",
    var version  : String? = "APP",
    var versionCode  : String? = "APP",
){
    companion object{
        val instance = PhoneInfo()
    }
}
