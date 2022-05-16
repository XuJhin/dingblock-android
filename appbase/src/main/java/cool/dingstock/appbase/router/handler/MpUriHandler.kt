package cool.dingstock.appbase.router.handler

import com.sankuai.waimai.router.core.UriCallback
import com.sankuai.waimai.router.core.UriHandler
import com.sankuai.waimai.router.core.UriRequest
import com.sankuai.waimai.router.core.UriResult
import cool.dingstock.appbase.router.DcRouterUtils
import cool.dingstock.appbase.router.MiniRouter
import cool.dingstock.lib_base.util.Logger

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/5  11:43
 */
class MpUriHandler : UriHandler(){

    override fun handleInternal(request: UriRequest, callback: UriCallback) {
        Logger.d("router",request.uri.toString())
        if(request.uri.toString().contains(DcRouterUtils.DC_MINI_HOST)){
            MiniRouter.openMiNi(request.uri)
            callback.onComplete(UriResult.CODE_SUCCESS)
            return
        }
        callback.onNext()
    }

    override fun shouldHandle(request: UriRequest): Boolean {
        if (request.uri.toString().contains(DcRouterUtils.DC_MINI_HOST)) {
            return true
        }
        return false
    }

}