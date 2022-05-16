package cool.dingstock.mobile.wxapi

import android.content.Intent
import android.os.Bundle
import cn.sharesdk.wechat.utils.WechatHandlerActivity
import com.sankuai.waimai.router.common.DefaultUriRequest
import com.sankuai.waimai.router.core.OnCompleteListener
import com.sankuai.waimai.router.core.UriRequest
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.constants.ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import cool.dingstock.appbase.constant.BoxConstant
import cool.dingstock.appbase.constant.PayConstant
import cool.dingstock.lib_base.R
import cool.dingstock.mobile.BoxPayCallBack
import cool.dingstock.mobile.PayCallback

/**
 * fix 修复使用微信授权无法登录的问题
 */
class WXEntryActivity : WechatHandlerActivity(), IWXAPIEventHandler {
    companion object {
        private val SUCCESS_CODE = 0
        private val FAILED_CODE = -1
        private val CANCEL_CODE = -2
        var ID = PayConstant.WX_APP_ID
        private var mPayCallback: PayCallback? = null
        private var boxPayBack: BoxPayCallBack? = null
        fun setupPayBack(mPayCallback: PayCallback?) {
            this.mPayCallback = mPayCallback
        }

        fun setupBoxCallback(boxPayCallBack: BoxPayCallBack) {
            this.boxPayBack = boxPayCallBack
        }
    }

    override fun onDestroy() {
        mPayCallback = null
        super.onDestroy()
    }


    private var api: IWXAPI? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_activity_empty)
        api = WXAPIFactory.createWXAPI(this, ID)
        api?.handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        api?.handleIntent(intent, this)
    }

    /**
     * 分享请求回调
     */
    override fun onReq(arg0: BaseReq?) {
        when (arg0?.type) {
            ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX -> {
            }
            // 小程序 跳转app
            ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX ->
                openApp((arg0 as ShowMessageFromWX.Req?))
            else -> {
            }
        }

    }

    override fun onResp(resp: BaseResp) {
        if (resp.type == ConstantsAPI.COMMAND_PAY_BY_WX) {
            callback(resp)
            finish()
        } else if (resp.type == COMMAND_LAUNCH_WX_MINIPROGRAM) {
            boxPayCallBack(resp)
        } else {
            finish()
        }
    }

    /**
     * 小程序跳转app
     */
    private fun openApp(showReq: ShowMessageFromWX.Req?) {
        val intent = Intent()
        intent.action = "dingstock_app_boot"
        finish()
        startActivity(intent)
    }

    private fun boxPayCallBack(resp: BaseResp) {
        val launchMiniProResp: WXLaunchMiniProgram.Resp = resp as WXLaunchMiniProgram.Resp
        val extraData: String = launchMiniProResp.extMsg
        val bundle = Bundle()
        bundle.putString(BoxConstant.Parameter.CASH_RESULT, extraData)
        DefaultUriRequest(this, BoxConstant.Uri.BOX_CASHIER)
            .putExtras(bundle)
            .onComplete(object : OnCompleteListener {
                override fun onSuccess(request: UriRequest) {
                    finish()
                }

                override fun onError(request: UriRequest, resultCode: Int) {
                    finish()
                }
            })
            .start();
        return
    }

    private fun callback(resp: BaseResp) {
        when (resp.errCode) {
            SUCCESS_CODE -> {
                mPayCallback?.onSucceed()
            }
            FAILED_CODE -> {
                mPayCallback?.onFailed(resp.errCode.toString(), resp.errStr)
            }
            CANCEL_CODE -> {
                mPayCallback?.onCancel()
            }
            else -> {
                mPayCallback?.onFailed("UNKOWN_ERROR_CODE", "UNKOWN_ERROR_CODE")
            }
        }
        mPayCallback = null
        boxPayBack = null
    }

    fun setApi(api: IWXAPI?) {
        this.api = api
    }


}