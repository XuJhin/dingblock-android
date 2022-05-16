package cool.dingstock.appbase.router.interceptor

import com.sankuai.waimai.router.core.UriCallback
import com.sankuai.waimai.router.core.UriInterceptor
import com.sankuai.waimai.router.core.UriRequest
import com.sankuai.waimai.router.core.UriResult
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.lib_base.util.Logger

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/5  11:22
 *  登录拦截器
 */
class DcLoginInterceptor : UriInterceptor{

    override fun intercept(request: UriRequest, callback: UriCallback) {
        Logger.d("router",request.uri.toString())
        //是否需要登录
        if(!LoginUtils.isLoginAndRequestLogin(request.context)){
            //todo 暂时先取消 这个继续的逻辑
//            LoginUtils.registerLoginSuccessListener(object : LoginUtils.LoginSuccessListener() {
//                override fun removeLoginListener() {
//                    callback.onComplete(UriResult.CODE_ERROR)
//                }
//
//                override fun onLoginSuccess() {
//                    callback.onNext()
//                }
//            })
//            return
            callback.onComplete(UriResult.CODE_ERROR)
        }
        callback.onNext()
    }
}