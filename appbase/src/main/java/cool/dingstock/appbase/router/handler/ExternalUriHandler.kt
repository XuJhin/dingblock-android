package cool.dingstock.appbase.router.handler

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import com.sankuai.waimai.router.core.UriCallback
import com.sankuai.waimai.router.core.UriHandler
import com.sankuai.waimai.router.core.UriRequest
import com.sankuai.waimai.router.core.UriResult
import cool.dingstock.appbase.custom.CustomerManager
import cool.dingstock.appbase.router.DcRouterUtils
import cool.dingstock.appbase.router.interceptor.DcLeadParametersInterceptor
import cool.dingstock.appbase.util.ClipboardHelper
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.ToastUtil

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/5  10:10
 */
class ExternalUriHandler() : UriHandler() {

    init {
        addInterceptor(DcLeadParametersInterceptor())
    }

    override fun handleInternal(request: UriRequest, callback: UriCallback) {
        val appName = request.uri.getQueryParameter("appname")
        Logger.d("router\t", request.uri.toString())
        //跳转到天猫
        if (request.uri.toString().contains(DcRouterUtils.DC_TAOBAOKE_TAOBAO)) {
            val url: String? = request.uri.getQueryParameter("url")
            if (null == url || url.isEmpty()) {
                callback.onComplete(UriResult.CODE_ERROR)
                return
            }
            callback.onComplete(UriResult.CODE_SUCCESS)
            return
        }
        if (request.uri.toString().contains(DcRouterUtils.DC_TAOBAOKE_TMALL)) {
            val url: String? = request.uri.getQueryParameter("url")
            if (null == url || url.isEmpty()) {
                callback.onComplete(UriResult.CODE_ERROR)
                return
            }
            callback.onComplete(UriResult.CODE_SUCCESS)
            return
        }
        //跳转到京东
        if (request.uri.toString().contains(DcRouterUtils.DC_JD)) {
            val url: String? = request.uri.getQueryParameter("url")
            if (null == url || url.isEmpty()) {
                callback.onComplete(UriResult.CODE_ERROR)
                return
            }
            callback.onComplete(UriResult.CODE_SUCCESS)
            return
        }
        //客服中心
        if (request.uri.toString().contains(DcRouterUtils.DC_CUSTOM)) {
            CustomerManager.getInstance().showCustomServiceActivity(request.context)
            callback.onComplete(UriResult.CODE_SUCCESS)
            return
        }
        //外部的页面
        if (request.uri.toString().contains(DcRouterUtils.EXTERNAL_WEBVIEW_HOST)) {
            try {
                val url = Uri.parse(request.uri.getQueryParameter("url"))
                val intent = Intent()
                intent.data = Uri.parse(url.toString())
                intent.action = Intent.ACTION_VIEW
                if ((Build.MANUFACTURER.equals(
                        "OPPO",
                        ignoreCase = true
                    ) || Build.MANUFACTURER.equals("VIVO", ignoreCase = true))
                    && !DcRouterUtils.isScheme(request.uri)
                ) {
                    val chooser = Intent.createChooser(intent, "浏览器")
                    request.context.startActivity(chooser)
                } else {
                    request.context.startActivity(intent)
                }
                callback.onComplete(UriResult.CODE_SUCCESS)
            } catch (e: Exception) {
                callback.onComplete(UriResult.CODE_ERROR)
            }
            return
        }
        if (isNikeUrl(request.uri.toString())) {
            val uriStr = request.uri.toString()
            try {
                openOut(
                    request.context, uriStr.replace("SNKRS://", "https://www.nike.com/launch/")
                        .replace("snkrs://", "https://www.nike.com/launch/")
                )
                callback.onComplete(UriResult.CODE_SUCCESS)
            } catch (e: java.lang.Exception) {
                if (!TextUtils.isEmpty(appName)) {
                    ToastUtil.getInstance()._short(request.context, "请先安装${appName}")
                } else {
                    ToastUtil.getInstance()._short(request.context, "请先安装第三方app")
                }
                callback.onComplete(UriResult.CODE_ERROR)
            }
            return

        }
        if (DcRouterUtils.isUrlNeedActionView(request.uri.toString())) {
            try {
                openOut(request.context, request.uri.toString())
                callback.onComplete(UriResult.CODE_SUCCESS)
            } catch (e: java.lang.Exception) {
                if (!TextUtils.isEmpty(appName)) {
                    ToastUtil.getInstance()._short(request.context, "请先安装${appName}")
                } else {
                    ToastUtil.getInstance()._short(request.context, "请先安装第三方app")
                }

                callback.onComplete(UriResult.CODE_ERROR)
            }
            return
        }

        if (outOffHttp(request.uri.toString())) {
            try {
                openOut(request.context, request.uri.toString())
                callback.onComplete(UriResult.CODE_SUCCESS)
            } catch (e: java.lang.Exception) {
                if (!TextUtils.isEmpty(appName)) {
                    ToastUtil.getInstance()
                        ._short(BaseLibrary.getInstance().context, "请先安装${appName}")
                } else {
                    ToastUtil.getInstance()._short(BaseLibrary.getInstance().context, "请先安装第三方app")
                }
                callback.onComplete(UriResult.CODE_ERROR)
            }
            return
        }
        callback.onNext()
    }

    override fun shouldHandle(request: UriRequest): Boolean {
        return true
    }


    private fun isNikeUrl(url: String): Boolean {
        if (TextUtils.isEmpty(url)) {
            return false
        }
        return if (url.startsWith(DcRouterUtils.SNKRS) || url.startsWith(DcRouterUtils.snkrs)) {
            true
        } else url.contains("www.nike.com")
    }

    private fun outOffHttp(url: String): Boolean {
        return !(url.startsWith("http://") || url.startsWith("https://"))
    }

    private fun openOut(context: Context, uriStr: String) {
        ClipboardHelper.copyMsg(context, "") {}
        val uri = Uri.decode(uriStr)
        val intent = Intent()
        intent.setAction(Intent.ACTION_VIEW)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.parse(uri)
        context.startActivity(intent)
    }

}