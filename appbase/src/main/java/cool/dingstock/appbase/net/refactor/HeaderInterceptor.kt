package cool.dingstock.appbase.net.refactor

import cool.dingstock.appbase.constant.ServerConstant
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.util.LoginUtils.getCurrentUser
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.util.AppUtils.getVersionCode
import cool.dingstock.lib_base.util.AppUtils.getVersionName
import okhttp3.Interceptor
import okhttp3.Response


/**
 * 请求头部拦截器，统一配置请求参数
 */
class HeaderInterceptor : Interceptor {
    private var versionName = getVersionName(BaseLibrary.getInstance().context)
    private var versionCode = getVersionCode(BaseLibrary.getInstance().context)
    private var channel = MobileHelper.getInstance().currentChannel
    private var brand = MobileHelper.getInstance().brand
    var mode = "light"
    private var currentUser = getCurrentUser()
    private var token: String = currentUser?.sessionToken ?: ""

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("version", ServerConstant.VERSION)
            .addHeader("app", "Android$versionName($versionCode)")
            .addHeader("token", token)
            .addHeader("dc_channel", channel)
            .addHeader("brand", brand)
            .addHeader("interface-style", mode)
            .build();

        return chain.proceed(request)
    }
}