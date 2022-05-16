package cool.dingstock.appbase.router.handler

import com.sankuai.waimai.router.core.UriCallback
import com.sankuai.waimai.router.core.UriHandler
import com.sankuai.waimai.router.core.UriRequest
import com.sankuai.waimai.router.core.UriResult
import cool.dingstock.appbase.constant.WebviewConstant
import cool.dingstock.appbase.router.DcRouterUtils
import cool.dingstock.appbase.router.DcUriRequest
import java.lang.Exception

class InnerUriHandler : UriHandler() {

    override fun handleInternal(request: UriRequest, callback: UriCallback) {
        try {
            DcUriRequest(request.context,WebviewConstant.Uri.INDEX)
                    .putUriParameter("url",request.uri.toString())
                    .start()
            callback.onComplete(UriResult.CODE_SUCCESS)
            return
        }catch (e:Exception){
            callback.onComplete(UriResult.CODE_ERROR)
        }
        callback.onNext()
    }

    override fun shouldHandle(request: UriRequest): Boolean {
        val b = (request.uri.toString().startsWith("https://")||request.uri.toString().startsWith("http://"))
                &&!request.uri.toString().startsWith(DcRouterUtils.DC_HOST)
        return b
    }

}