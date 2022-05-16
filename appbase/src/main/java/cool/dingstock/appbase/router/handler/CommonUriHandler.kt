package cool.dingstock.appbase.router.handler

import com.sankuai.waimai.router.core.UriCallback
import com.sankuai.waimai.router.core.UriHandler
import com.sankuai.waimai.router.core.UriRequest
import com.sankuai.waimai.router.core.UriResult
import cool.dingstock.appbase.router.DcRouterUtils
import cool.dingstock.appbase.router.MiniRouter
import cool.dingstock.appbase.util.ClipboardHelper
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.StringUtils
import cool.dingstock.lib_base.util.ToastUtil

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/5  11:43
 */
class CommonUriHandler : UriHandler(){

    override fun handleInternal(request: UriRequest, callback: UriCallback) {
        Logger.d("router",request.uri.toString())
        if(request.uri.toString().contains(DcRouterUtils.DC_URL_COMMON)){
            if(request.uri.toString().contains(DcRouterUtils.DC_URL_COMMON_COPY)){
                callback.onComplete(UriResult.CODE_SUCCESS)
                val value = request.uri.getQueryParameter("value")
                val showToast = request.uri.getQueryParameter("showToast")
                if(!StringUtils.isEmpty(value)){
                    ClipboardHelper.copyMsg(request.context,value?:""){
                        if(!StringUtils.isEmpty(showToast)){
                            ToastUtil.getInstance()._short(BaseLibrary.getInstance().context,showToast)
                        }
                    }
                }
                return
            }
        }
        callback.onNext()
    }

    override fun shouldHandle(request: UriRequest): Boolean {
        if (request.uri.toString().contains(DcRouterUtils.DC_URL_COMMON)) {
            return true
        }
        return false
    }

}