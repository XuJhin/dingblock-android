package cool.dingstock.appbase.router.handler

import androidx.lifecycle.lifecycleScope
import com.sankuai.waimai.router.core.UriCallback
import com.sankuai.waimai.router.core.UriHandler
import com.sankuai.waimai.router.core.UriRequest
import com.sankuai.waimai.router.core.UriResult
import cool.dingstock.appbase.base.BaseDcActivity
import cool.dingstock.appbase.constant.HomeConstant
import cool.dingstock.appbase.helper.bp.BpInitHelper
import cool.dingstock.appbase.mvp.DCActivityManager
import cool.dingstock.appbase.router.DcRouterUtils
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.ToastUtil
import kotlinx.coroutines.launch

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/5  11:43
 */
class BpUriHandler : UriHandler() {

    override fun handleInternal(request: UriRequest, callback: UriCallback) {
        Logger.d("router", request.uri.toString())
        if (request.uri.toString().contains(DcRouterUtils.DC_BP_HOST)) {
            val link = request.uri.getQueryParameter("link") ?: ""
            if (link.isEmpty()) {
                callback.onComplete(UriResult.CODE_ERROR)
                return
            }
            if (!BpInitHelper.getBpResolveState()) {
                ToastUtil.getInstance()._short(request.context, "资源加载中，请稍后")
                return
            }
            (DCActivityManager.getInstance().topActivity as? BaseDcActivity)?.apply {
                lifecycleScope.launch {
                    showLoadingDialog()
                    val result = BpInitHelper.getBpHelper().parseLink(link)
                    hideLoadingDialog()
                    if (!result.err && result.res != null) {
                        DcRouter(HomeConstant.Uri.BP_GOODS_DETAIL)
                            .putExtra(HomeConstant.UriParam.KEY_GOOD_DETAIL, result.res)
                            .start()
                        callback.onComplete(UriResult.CODE_SUCCESS)
                    } else {
                        ToastUtil.getInstance()._short(request.context, "商品查询失败")
                        callback.onComplete(UriResult.CODE_ERROR)
                    }
                }
            }
            return
        }
        callback.onNext()
    }

    override fun shouldHandle(request: UriRequest): Boolean {
        if (request.uri.toString().contains(DcRouterUtils.DC_BP_HOST)) {
            return true
        }
        return false
    }

}