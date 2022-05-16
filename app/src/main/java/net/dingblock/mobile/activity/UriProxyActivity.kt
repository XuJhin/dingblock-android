package net.dingblock.mobile.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sankuai.waimai.router.common.DefaultUriRequest
import com.sankuai.waimai.router.core.OnCompleteListener
import com.sankuai.waimai.router.core.UriRequest
import cool.dingstock.appbase.constant.BoxConstant
import cool.dingstock.appbase.constant.PushConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.mvp.DCActivityManager
import cool.dingstock.lib_base.json.JSONHelper
import cool.dingstock.lib_base.util.Logger

/**
 * Scheme页面，接收dingstock://,进行处理，交友mainActivity处理
 */
class UriProxyActivity : AppCompatActivity() {
    companion object {
        const val PAY_HOST = "backfromalipay"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(null)
        handRoute()
    }

    /**
     * 处理路由分发
     */
    private fun handRoute() {
        val intent: Intent? = intent
        if (intent == null) {
            this.finish()
            return
        }
        val data = intent.data
        if (data == null) {
            this.finish()
            return
        }
        val dataString = intent.dataString
        if (dataString.isNullOrEmpty()) {
            finish()
            return
        }

        val isAppLive = !DCActivityManager.getInstance().isActivityListEmpty
        //处理支付返回数据
        val parse = Uri.parse(dataString)
        val query = parse.query.toString()

        Logger.d("TestPay", " errCode = " + query);
        val errCode = parse.getQueryParameter("errCode") ?: ""
        val errMsg = parse.getQueryParameter("errStr") ?: ""
        if (parse.host.equals(PAY_HOST)) {
            if (isAppLive) {
                val bundle = Bundle()

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
            } else {
                val launchIntent = Intent(this, BootActivity::class.java)
                startActivity(launchIntent)
                finish()
            }
        }
        val linkUrl: String
        when {
            //以dingstock://app.dingstock.net 开头时替换dingstock为https
            dataString.startsWith("${RouterConstant.DC_SCHEME}://${RouterConstant.HOST}") -> {
                linkUrl =
                    dataString.replace(RouterConstant.DC_SCHEME, RouterConstant.SCHEME, true)
            }
            //以dingstock://***path** 开头时替换dingstock为dingstock://app.dingstock.net
            dataString.startsWith("${RouterConstant.DC_SCHEME}://") -> {
                linkUrl = dataString.replace(
                    "${RouterConstant.DC_SCHEME}://",
                    "${RouterConstant.getSchemeHost()}/",
                    true
                )
            }
            else -> {
                finish()
                return
            }
        }


        if (isAppLive) {
            Logger.e("UriProxyActivity is isAppLive true  $linkUrl")
            DefaultUriRequest(this, linkUrl)
                .putExtras(intent.extras)
                .onComplete(object : OnCompleteListener {
                    override fun onSuccess(request: UriRequest) {
                        finish()
                    }

                    override fun onError(request: UriRequest, resultCode: Int) {
                        finish()
                    }
                })
                .start();
        } else {
            Logger.e("UriProxyActivity is isAppLive false  $linkUrl")
            val launchIntent = Intent(this, BootActivity::class.java)
            launchIntent.putExtra(PushConstant.Key.KEY_URL, linkUrl)
            launchIntent.putExtra(PushConstant.Key.KEY_ACTION_VIEW, true)
            launchIntent.putExtra(PushConstant.Key.KEY_IGNORE_AD, true)
            startActivity(launchIntent)
            finish()
        }
    }

}