package cool.dingstock.appbase.webview.overridedelegate

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.widget.Toast
import cool.dingstock.appbase.webview.delegate.OverrideUrlDelegate
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.SystemUtils

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/26  10:05
 */
class DHGameDelegate(context: Context) : OverrideUrlDelegate(context){

    val wexPayPre = "wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb?" //https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb?prepay_id=wx261616526037269e59f6db8df1e4f80000&package=3511646060
    val aliPayPre = "mclient.alipay.com/home/exterfaceAssign.htm?" //https://mclient.alipay.com/home/exterfaceAssign.htm?_input_charset=utf-8&subject=%E6%9E%81%E9%99%90%E9%80%83%E4%BA%A1-%E5%AE%89%E5%8D%9310%E5%85%83%E9%A6%96%E5%85%85&sign=6dbeee5e085d9b7f84fcd223fb442610&body=18414094414588547072&notify_url=http%3A%2F%2Fcz.m3guo.com%2Fgamepay_api%2Falipay.aspx&alipay_exterface_invoke_assign_model=cashier&alipay_exterface_invoke_assign_target=mapi_direct_trade.htm&payment_type=1&out_trade_no=18414094414588547072&partner=2088301235410274&alipay_exterface_invoke_assign_sign=l_c_x_x_k%2F%2F_x_uczsuy_b_u_c_qb0%2B_p7fx_b_f5y_a40kwi1_b_k_h_zhc30f_dvo_sesffw%3D%3D&service=alipay.wap.create.direct.pay.by.user&total_fee=10&app_pay=Y&return_url=https%3A%2F%2Fpay.17m3.com%2Fsdk%2Fwap_alipay_callback.aspx&sign_type=MD5&seller_id=2088301235410274&show_url=http%3A%2F%2Fwww.shandw.com%2Fmi%2Fgame%2F1938257615.html%3Fchannel%3D14269%26phone%3D%26_sender_sdw_rfid_%3D2023270047%26v%3D315&alipay_exterface_invoke_assign_client_ip=110.184.69.28

//    override fun shouldOverrideUrlLoading(webView: WebView, url: String?): Boolean {
//        Logger.d("DHGameDelegate shouldOverrideUrlLoading::url=$url")
//        // 如下方案可在非微信内部WebView的H5页面中调出微信支付
//        if (
//                url.startsWith("https://${aliPayPre}")||url.startsWith("http://${aliPayPre}")||url.startsWith(aliPayPre)//目前就 支付宝这方需要处理跳转支付。 微信暂时不处理。可以使用
//        ) {
//            try {
//                val hasApp: Boolean = if (url.startsWith("https://${wexPayPre}") ||url.startsWith("http:${wexPayPre}") ||url.startsWith(wexPayPre)) {
//                    SystemUtils.isInstalled(context?.get(), "com.tencent.mm")//微信的暂时不跳转
//                    false
//                } else {
//                    (SystemUtils.isInstalled(context?.get(), "com.alipay.android.app") || SystemUtils.isInstalled(context?.get(), "com.eg.android.AlipayGphone"))
//                }
//                if (hasApp) {
//                    val intent = Intent()
//                    intent.setAction(Intent.ACTION_VIEW)
//                    intent.setData(Uri.parse(url))
//                    context?.get()?.startActivity(intent)
//                } else {
//                     ToastUtil.getInstance().makeTextAndShow(context?.get(), "客官，请先安装支付App哦~", Toast.LENGTH_SHORT)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                 ToastUtil.getInstance().makeTextAndShow(context?.get(), "客官，请先安装支付App哦~", Toast.LENGTH_SHORT)
//            }
//            if (url.startsWith("https")) {
//                val map = HashMap<String, String>()
//                map["Referer"] = "http://www.shandw.com"
//                webView.loadUrl(url, map)
//                return true
//            }
//        }
//
//        return false
//    }

    override fun shouldOverrideUrlLoading(webView: android.webkit.WebView, url: String): Boolean {
        // 如下方案可在非微信内部WebView的H5页面中调出微信支付
        if (
            url.startsWith("https://${aliPayPre}")||url.startsWith("http://${aliPayPre}")||url.startsWith(aliPayPre)//目前就 支付宝这方需要处理跳转支付。 微信暂时不处理。可以使用
        ) {
            try {
                val hasApp: Boolean = if (url.startsWith("https://${wexPayPre}") ||url.startsWith("http:${wexPayPre}") ||url.startsWith(wexPayPre)) {
                    SystemUtils.isInstalled(context?.get(), "com.tencent.mm")//微信的暂时不跳转
                    false
                } else {
                    (SystemUtils.isInstalled(context?.get(), "com.alipay.android.app") || SystemUtils.isInstalled(context?.get(), "com.eg.android.AlipayGphone"))
                }
                if (hasApp) {
                    val intent = Intent()
                    intent.setAction(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse(url))
                    context?.get()?.startActivity(intent)
                } else {
                    ToastUtil.getInstance().makeTextAndShow(context?.get(), "客官，请先安装支付App哦~", Toast.LENGTH_SHORT)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ToastUtil.getInstance().makeTextAndShow(context?.get(), "客官，请先安装支付App哦~", Toast.LENGTH_SHORT)
            }
            if (url.startsWith("https")) {
                val map = HashMap<String, String>()
                map["Referer"] = "http://www.shandw.com"
                webView.loadUrl(url, map)
                return true
            }
        }

        return false
    }

}