package cool.dingstock.appbase.router.handler

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.sankuai.waimai.router.common.DefaultRootUriHandler
import com.sankuai.waimai.router.core.OnCompleteListener
import com.sankuai.waimai.router.core.UriRequest
import com.sankuai.waimai.router.core.UriResult
import cool.dingstock.appbase.router.interceptor.DcLeadParametersInterceptor
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.ToastUtil

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/4  18:04
 */
class DcUriHandler(context: Context) : DefaultRootUriHandler(context) {
    init {
        addChildHandler(ExternalUriHandler(), 500)
        addChildHandler(ShareUriHandler(), 499)
        addChildHandler(MpUriHandler(), 498)
        addChildHandler(BpUriHandler(), 450)
        addChildHandler(CommonUriHandler(), 400)
        addChildHandler(InnerUriHandler(), 100)
        addInterceptor(DcLeadParametersInterceptor())
        globalOnCompleteListener = object : OnCompleteListener {
            override fun onSuccess(request: UriRequest) {
            }

            override fun onError(request: UriRequest, resultCode: Int) {
                Logger.d(request.errorMessage)
                var text = request.getStringField(UriRequest.FIELD_ERROR_MSG, null)
                if (TextUtils.isEmpty(text)) {
                    text = when (resultCode) {
                        UriResult.CODE_NOT_FOUND -> "不支持的跳转链接"
                        else -> ""
                    }
                }
                if (!TextUtils.isEmpty(text)) {
                    ToastUtil.getInstance()
                        .makeTextAndShow(request.context, text, Toast.LENGTH_LONG)
                }
            }

        }
    }

}