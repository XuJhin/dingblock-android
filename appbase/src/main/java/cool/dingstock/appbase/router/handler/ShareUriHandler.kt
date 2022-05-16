package cool.dingstock.appbase.router.handler

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.sankuai.waimai.router.core.UriCallback
import com.sankuai.waimai.router.core.UriHandler
import com.sankuai.waimai.router.core.UriRequest
import com.sankuai.waimai.router.core.UriResult
import cool.dingstock.appbase.R
import cool.dingstock.appbase.mvp.DCActivityManager
import cool.dingstock.appbase.router.DcRouterUtils
import cool.dingstock.appbase.share.ShareParams
import cool.dingstock.appbase.share.ShareServiceHelper
import cool.dingstock.appbase.share.ShareType
import cool.dingstock.lib_base.thread.ThreadPoolHelper
import cool.dingstock.lib_base.util.Logger

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/5  11:31
 *
 *  路由分享
 *
 */
class ShareUriHandler : UriHandler() {

    override fun handleInternal(request: UriRequest, callback: UriCallback) {
        Logger.d("router",request.uri.toString())
        if (request.uri.toString().startsWith(DcRouterUtils.DC_SHARE_LINK)) {
//            if(request.context.javaClass.toString().contains("PopupActivity")){
//
//            }
            ThreadPoolHelper.getInstance().runOnUiThread { shareLink(request.context,request.uri) }
            callback.onComplete(UriResult.CODE_SUCCESS)
            return
        }
        if (request.uri.toString().startsWith(DcRouterUtils.DC_SHARE_IMAGE)) {
            ThreadPoolHelper.getInstance().runOnUiThread { shareImg(request.context,request.uri) }
            callback.onComplete(UriResult.CODE_SUCCESS)
            return
        }
        if (request.uri.toString().startsWith(DcRouterUtils.DC_SHARE_MP)) {
            ThreadPoolHelper.getInstance().runOnUiThread { shareMp(request.context,request.uri) }
            callback.onComplete(UriResult.CODE_SUCCESS)
            return
        }
        callback.onNext()
    }

    override fun shouldHandle(request: UriRequest): Boolean {
        if(request.uri.toString().startsWith(DcRouterUtils.DC_SHARE)) {
            return true
        }
        return false
    }

    /**
     * 分享链接
     */
    private fun shareLink(context: Context, uri: Uri) {
        var title: String? = uri.getQueryParameter("title")
        if (TextUtils.isEmpty(title)) {
            title = context.getString(R.string.app_name)
        }
        var content: String? = uri.getQueryParameter("body")
        if (TextUtils.isEmpty(content)) {
            content = context.getString(R.string.common_dc_slogan)
        }
        var url: String? = uri.getQueryParameter("url")
        if (TextUtils.isEmpty(url)) {
            url = ""
        }
        var imgUrl: String? = uri.getQueryParameter("imageUrl")
        if (TextUtils.isEmpty(imgUrl)) {
            imgUrl = ""
        }
        val platformStr: String? = uri.getQueryParameter("platforms")
        val pwdUrl: String? = uri.getQueryParameter("pwdUrl")
        val type = ShareType.Link
        val params = ShareParams()
        params.title = title
        params.content = content
        params.imageUrl = imgUrl
        params.link = url
        if (!TextUtils.isEmpty(platformStr)) {
            params.platformStr = platformStr
        }
        if (!TextUtils.isEmpty(pwdUrl)) {
            params.pwdUrl = pwdUrl
        }
        type.params = params
        ShareServiceHelper.share(context,type)
//        val shareDialog = cool.mobile.account.share.ShareDialog(context)
//        shareDialog.shareType = type
//        shareDialog.show()
    }

    /**
     * 分享小程序
     */
    private fun shareMp(context: Context, uri: Uri) {
        var shareUrl: String? = uri.getQueryParameter("url")
        var title: String? = uri.getQueryParameter("title")
        if (TextUtils.isEmpty(title)) {
            title = context.getString(R.string.app_name)
        }
        var content: String? = uri.getQueryParameter("body")
        if (TextUtils.isEmpty(content)) {
            content = context.getString(R.string.common_dc_slogan)
        }
        var path: String? = uri.getQueryParameter("path")
        if (TextUtils.isEmpty(content)) {
            path = ""
        }
        var imageUrl: String? = uri.getQueryParameter("imageUrl")
        var platformStr: String? = uri.getQueryParameter("platforms")
        var pwdUrl: String? = uri.getQueryParameter("pwdUrl")
        var type = ShareType.Mp
        var params = ShareParams()
        params.title = title
        params.mpPath = path
        params.imageUrl = imageUrl
        params.link = shareUrl
        if (!TextUtils.isEmpty(platformStr)) {
            params.platformStr = platformStr
        }
        if (!TextUtils.isEmpty(pwdUrl)) {
            params.pwdUrl = pwdUrl
        }
        type.params = params
        ShareServiceHelper.share(context,type)
//        val shareDialog = cool.mobile.account.share.ShareDialog(context)
//        shareDialog.shareType = type
//        shareDialog.show()
    }

    /**
     * 分享图片
     */
    private fun shareImg(context: Context, uri: Uri) {
        var shareUrl: String? = uri.getQueryParameter("url")
        var title: String? = uri.getQueryParameter("title")
        if (TextUtils.isEmpty(title)) {
            title = context.getString(R.string.app_name)
        }
        var content: String? = uri.getQueryParameter("body")
        if (TextUtils.isEmpty(content)) {
            content = context.getString(R.string.common_dc_slogan)
        }
        var imageUrl: String? = uri.getQueryParameter("imageUrl")
        var platformStr: String? = uri.getQueryParameter("platforms")
        var pwdUrl: String? = uri.getQueryParameter("pwdUrl")
        var type = ShareType.Image
        var params = ShareParams()
        params.title = title
        params.imageUrl = imageUrl
        params.link = shareUrl
        if (!TextUtils.isEmpty(platformStr)) {
            params.platformStr = platformStr
        }
        if (!TextUtils.isEmpty(pwdUrl)) {
            params.pwdUrl = pwdUrl
        }
        type.params = params
        ShareServiceHelper.share(context,type)
//        val shareDialog = cool.mobile.account.share.ShareDialog(context)
//        shareDialog.shareType = type
//        shareDialog.show()
    }

}