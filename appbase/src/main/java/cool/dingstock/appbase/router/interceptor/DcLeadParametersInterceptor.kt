package cool.dingstock.appbase.router.interceptor

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.widget.Toast
import com.sankuai.waimai.router.Router
import com.sankuai.waimai.router.core.UriCallback
import com.sankuai.waimai.router.core.UriInterceptor
import com.sankuai.waimai.router.core.UriRequest
import com.sankuai.waimai.router.core.UriResult
import cool.dingstock.appbase.R
import cool.dingstock.appbase.constant.AccountConstant
import cool.dingstock.appbase.dagger.AppBaseApiHelper
import cool.dingstock.appbase.mvp.BaseActivity
import cool.dingstock.appbase.mvp.DCActivityManager
import cool.dingstock.appbase.net.api.mine.MineApi
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.appbase.router.DcRouterUtils
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.thread.ThreadPoolHelper
import cool.dingstock.lib_base.util.AppUtils
import cool.dingstock.lib_base.util.KtStringUtils.compareVersion
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.StringUtils
import cool.dingstock.lib_base.util.ToastUtil
import javax.inject.Inject

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/5  10:28
 *
 *  前置参数拦截器
 *
 */
class DcLeadParametersInterceptor : UriInterceptor {
    val net by lazy {
        Net()
    }

    override fun intercept(request: UriRequest, callback: UriCallback) {
        Logger.d("router", request.uri.toString())
        //是否需要登录
        if (!checkUriParameterLogin(request.uri)) {
            Router.startUri(request.context, AccountConstant.Uri.INDEX)
            //todo 暂时取消这个逻辑

            //            LoginUtils.registerLoginSuccessListener(object : LoginUtils.LoginSuccessListener() {
            //                override fun removeLoginListener() {
            //                    callback.onComplete(UriResult.CODE_ERROR)
            //                }
            //
            //                override fun onLoginSuccess() {
            //                    callback.onNext()
            //                }
            //            })
            callback.onComplete(UriResult.CODE_ERROR)
            return
        }
        if (request.uri.toString()
                .contains(DcRouterUtils.DC_HOST) && !checkUriVersion(request.uri)
        ) {
            ThreadPoolHelper.getInstance().runOnUiThread {
                ToastUtil.getInstance()
                    .makeTextAndShow(request.context, "当前版本不支持", Toast.LENGTH_SHORT)
            }
            showUpdateTipDialog()
            callback.onComplete(UriResult.CODE_ERROR)
            return
        }
        //如果是登录路由 且 已经登录了 就不跳转登录
        if (request.uri.toString().startsWith(AccountConstant.Uri.INDEX) ||
            request.uri.toString().startsWith(AccountConstant.Uri.INDEX1)
        ) {
            if (LoginUtils.isLogin()) {
                callback.onComplete(UriResult.CODE_ERROR)
                return
            }
        }
        if (checkTaskId(request.uri)) {
            queryUriParameter(request.uri, DcRouterUtils.TASK_ID)?.let { taskId ->
                net.minApi.detectTask(taskId).subscribe({
                    Logger.e("路由触发taskId", taskId, " ", it.msg)
                }, {
                    Logger.e("路由触发taskId", taskId, " 失败", it.message)
                })
            }
        }
        callback.onNext()
    }

    private fun checkUriVersion(uri: Uri?): Boolean {
        if (null == uri) {
            return false
        }
        //兼容 minVer 和 minver
        val urlVersion = queryUriParameter(uri, DcRouterUtils.MIN_VER)
        if (TextUtils.isEmpty(urlVersion)) {
            return true
        }
        return compareVersion(
            urlVersion!!,
            AppUtils.getVersionName(BaseLibrary.getInstance().context)
        ) <= 0
    }

    private fun checkUriParameterLogin(uri: Uri): Boolean {
        try {
            var requireAuth: String? = queryUriParameter(uri, DcRouterUtils.NEED_LOGIN)
            if (!StringUtils.isEmpty(requireAuth)) {
                if (!checkoutLogin(requireAuth)) return false
            }
            requireAuth = queryUriParameter(uri, DcRouterUtils.REQUIRE_AUTH)
            if (!checkoutLogin(requireAuth)) return false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    private fun checkoutLogin(requireAuth: String?): Boolean {
        if (DcRouterUtils.NEED_AUTH.equals(requireAuth, ignoreCase = true) || "true".equals(
                requireAuth,
                ignoreCase = true
            )
        ) { //需要登录
            if (!LoginUtils.isLogin()) {
                return false
            }
        }
        return true
    }

    private fun checkTaskId(uri: Uri): Boolean {
        try {
            var requireAuth: String? = queryUriParameter(uri, DcRouterUtils.TASK_ID)
            if (!StringUtils.isEmpty(requireAuth)) {
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    /**
     * 兼容大小写查询参数
     */
    private fun queryUriParameter(uri: Uri, key: String): String? {
        var queryParameter: String? = uri.getQueryParameter(key)
        if (TextUtils.isEmpty(queryParameter)) {
            queryParameter = uri.getQueryParameter(key.toLowerCase())
        }
        return queryParameter
    }

    private fun showUpdateTipDialog() {
        val topActivity = (DCActivityManager.getInstance().topActivity ?: return) as? BaseActivity
            ?: return
        topActivity.makeAlertDialog().setMessage("当前版本暂不支持该功能，请前往应用商店更新")
            .setNegativeButton(R.string.common_cancel) { dialog, which -> dialog.dismiss() }
            .setPositiveButton(
                "去下载",
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                    MobileHelper.getInstance().getDownLoadUrl(object : ParseCallback<String?> {
                        override fun onSucceed(data: String?) {
                            if (TextUtils.isEmpty(data)) {
                                return
                            }
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                topActivity.startActivity(intent)
                            } catch (e: Exception) {
                            }
                        }

                        override fun onFailed(errorCode: String, errorMsg: String) {}
                    })
                }).show()
    }

    class Net {
        @Inject
        lateinit var minApi: MineApi

        init {
            AppBaseApiHelper.appBaseComponent.inject(this)
        }
    }
}
