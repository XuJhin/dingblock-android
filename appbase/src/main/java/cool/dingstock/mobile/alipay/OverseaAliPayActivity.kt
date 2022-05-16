package cool.dingstock.mobile.alipay

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
//import com.transfer.wcpaylibrary.util.PayUtil
import cool.dingstock.mobile.PayCallback

class OverseaAliPayActivity : AppCompatActivity() {

    companion object {
        val ALI_PAY_PARAMETER = "aliPayParameter"
        var payCallback: PayCallback? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var aliPayParameter = intent.getStringExtra(ALI_PAY_PARAMETER)
//        PayUtil.aliPayTerritoryPay(this, aliPayParameter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val errorCode = data!!.getIntExtra("errorCode", 100)
        when (errorCode) {
            0 -> {
                //支付成功
                payCallback?.onSucceed()
            }
            -1 -> {
                //支付失败
                payCallback?.onFailed("-1", "支付失败")
            }
            -2 -> {
                //用户取消
                payCallback?.onCancel()
            }
            -4 -> {
                //支付结果处理中
                payCallback?.onFailed("${errorCode}", "支付结果处理中")

            }
            else -> {
                //其它错误
                payCallback?.onFailed("${errorCode}", "其它错误")


            }
        }
    }


}