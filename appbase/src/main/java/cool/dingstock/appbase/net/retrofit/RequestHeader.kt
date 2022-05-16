package cool.dingstock.appbase.net.retrofit

import android.content.res.Configuration
import cool.dingstock.appbase.constant.ServerConstant
import cool.dingstock.appbase.mvp.DCActivityManager
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.util.LoginUtils.getCurrentUser
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.util.AppUtils.getVersionCode
import cool.dingstock.lib_base.util.AppUtils.getVersionName

object RequestHeader {
    fun createHeader(): HashMap<String, String> {
        val versionName = getVersionName(BaseLibrary.getInstance().context)
        val versionCode = getVersionCode(BaseLibrary.getInstance().context)
        val currentUser = getCurrentUser()
        val token: String = currentUser?.sessionToken ?: ""
        val channel = MobileHelper.getInstance().currentChannel
        val brand = MobileHelper.getInstance().brand
        var mode = "light"
        if (DCActivityManager.getInstance().topActivity != null) {
            val nightMode =
                DCActivityManager.getInstance().topActivity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            mode = if (nightMode == Configuration.UI_MODE_NIGHT_YES) "dark" else "light"
        }
        return hashMapOf(
            "Content-Type" to "application/json",
            "version" to ServerConstant.VERSION,
            "app" to "Android$versionName($versionCode)",
            "token" to token,
            "dc_channel" to channel,
            "brand" to brand,
            "interface-style" to mode,
            "hasAgree" to "false"
        )
    }
}